///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS com.fasterxml.jackson.core:jackson-databind:2.18.2
//JAVA 21+

// Cache webmention author avatars locally so the site has no runtime dependency on
// unavatar.io (which rate-limits and serves full-size images).
//
// Usage:  UNAVATAR_TOKEN=xxxx jbang FetchAvatars.java
//     or: jbang FetchAvatars.java --token xxxx
//
// Idempotent / cache-first: only handles that are not already cached
// (public/avatars/<h>.jpg) and not known-dead (public/avatars/.dead) are fetched.
// Afterwards it rewrites _data/mentions.json photo URLs to the local path, or drops
// them so those faces fall back to the initials circle. Commit public/avatars/ and
// the updated _data/mentions.json. Re-run anytime to fill gaps / pick up new data.

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.stream.*;

public class FetchAvatars {
    static final Path MENTIONS = Path.of("_data/mentions.json");
    static final Path AVADIR   = Path.of("public/avatars");
    static final Path DEAD     = AVADIR.resolve(".dead");
    static final String[] CATS = {"likes", "reposts", "links", "replies"};
    static final ObjectMapper M = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        String token = System.getenv("UNAVATAR_TOKEN");
        for (int i = 0; i < args.length - 1; i++)
            if (args[i].equals("--token")) token = args[i + 1];
        if (token == null || token.isBlank())
            System.out.println("WARN: no token (set UNAVATAR_TOKEN or --token); unauthenticated calls may be rate-limited.");

        Files.createDirectories(AVADIR);
        if (!Files.exists(DEAD)) Files.writeString(DEAD, "");
        Set<String> dead = new TreeSet<>(Files.readAllLines(DEAD).stream()
                .map(String::trim).filter(s -> !s.isEmpty()).toList());

        JsonNode root = M.readTree(MENTIONS.toFile());

        // unique twitter handles from author_url
        Set<String> handles = new TreeSet<>();
        forEachEntry(root, e -> { String h = handle(text(e, "author_url")); if (h != null) handles.add(h); });

        List<String> todo = handles.stream()
                .filter(h -> !Files.exists(AVADIR.resolve(h + ".jpg")) && !dead.contains(h))
                .toList();
        System.out.printf("%d handles, %d to fetch (%d cached/dead)%n",
                handles.size(), todo.size(), handles.size() - todo.size());

        HttpClient http = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS)
                .connectTimeout(Duration.ofSeconds(20)).build();
        int ok = 0, gone = 0, fail = 0, n = 0;
        for (String h : todo) {
            n++;
            String url = "https://unavatar.io/twitter/" + URLEncoder.encode(h, StandardCharsets.UTF_8)
                    + "?fallback=false" + (token != null && !token.isBlank() ? "&token=" + token : "");
            int status = fetchResizeSave(http, url, AVADIR.resolve(h + ".jpg"));
            if (status == 200) ok++;
            else if (status == 404) { dead.add(h); gone++; }
            else fail++;
            if (n % 10 == 0 || n == todo.size())
                System.out.printf("  %d/%d (ok=%d dead=%d fail=%d)%n", n, todo.size(), ok, gone, fail);
            Thread.sleep(token != null && !token.isBlank() ? 150 : 1200);
        }
        Files.writeString(DEAD, String.join("\n", dead) + (dead.isEmpty() ? "" : "\n"));

        // rewrite photo URLs from local cache
        int withPhoto = 0, withInitials = 0;
        for (JsonNode post : iterable(root))
            for (String cat : CATS)
                if (post.has(cat))
                    for (JsonNode e : post.get(cat)) {
                        ObjectNode o = (ObjectNode) e;
                        String hh = handle(text(e, "author_url"));
                        if (hh != null && Files.exists(AVADIR.resolve(hh + ".jpg"))) {
                            o.put("photo", "/avatars/" + hh + ".jpg"); withPhoto++;
                        } else { o.remove("photo"); withInitials++; }
                    }
        M.writerWithDefaultPrettyPrinter().writeValue(MENTIONS.toFile(), root);
        long cached = Files.list(AVADIR).filter(p -> p.toString().endsWith(".jpg")).count();
        System.out.printf("done: cached=%d, faces with photo=%d, faces using initials=%d%n",
                cached, withPhoto, withInitials);
    }

    /** @return HTTP status; on 200 writes a 96x96 center-cropped JPEG. */
    static int fetchResizeSave(HttpClient http, String url, Path out) {
        for (int attempt = 0; attempt < 4; attempt++) {
            try {
                HttpResponse<byte[]> r = http.send(
                        HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(25)).build(),
                        HttpResponse.BodyHandlers.ofByteArray());
                int sc = r.statusCode();
                if (sc == 429) { Thread.sleep(5000L * (attempt + 1)); continue; }
                String ct = r.headers().firstValue("content-type").orElse("");
                if (sc == 200 && ct.startsWith("image/") && !ct.contains("svg")) {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(r.body()));
                    if (img == null) return 0;
                    ImageIO.write(cover(img, 96), "jpg", out.toFile());
                    return 200;
                }
                return sc;
            } catch (Exception ex) {
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            }
        }
        return 0;
    }

    static BufferedImage cover(BufferedImage src, int size) {
        int w = src.getWidth(), h = src.getHeight();
        double scale = Math.max((double) size / w, (double) size / h);
        int nw = (int) Math.round(w * scale), nh = (int) Math.round(h * scale);
        BufferedImage out = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src.getScaledInstance(nw, nh, Image.SCALE_SMOOTH), (size - nw) / 2, (size - nh) / 2, null);
        g.dispose();
        return out;
    }

    // --- helpers ---
    static String handle(String authorUrl) {
        if (authorUrl == null) return null;
        try {
            URI u = URI.create(authorUrl.trim());
            String host = u.getHost() == null ? "" : u.getHost().replaceFirst("^www\\.", "").toLowerCase();
            if (!host.equals("twitter.com") && !host.equals("x.com")) return null;
            String[] segs = Arrays.stream(u.getPath().split("/")).filter(s -> !s.isEmpty()).toArray(String[]::new);
            return segs.length > 0 ? segs[0] : null;
        } catch (Exception e) { return null; }
    }

    static String text(JsonNode e, String f) { return e.has(f) ? e.get(f).asText() : null; }
    static Iterable<JsonNode> iterable(JsonNode o) { return () -> o.elements(); }
    static void forEachEntry(JsonNode root, java.util.function.Consumer<JsonNode> fn) {
        for (JsonNode post : iterable(root))
            for (String cat : CATS)
                if (post.has(cat)) for (JsonNode e : post.get(cat)) fn.accept(e);
    }
}
