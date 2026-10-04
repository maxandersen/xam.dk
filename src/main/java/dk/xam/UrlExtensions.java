package dk.xam;

import io.quarkiverse.roq.frontmatter.runtime.model.Page;
import io.quarkiverse.roq.frontmatter.runtime.model.RoqUrl;
import io.quarkus.qute.TemplateExtension;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;

/**
 * Jekyll-style {@code relativize_url} for Roq: make a link relative to the current
 * page so the site is portable under any base path (and file://), with no
 * site.path-prefix needed. The current page is the receiver, so templates read:
 *
 *   {=page.rel('/assets/main.css')}      -> ../../assets/main.css  (on a /blog/x/ page)
 *   {=page.rel(post.url)}                 -> relative link to another page
 *   {=page.rel(page.data.background)}
 *
 * Every Roq page is emitted as {@code <path>/index.html} and accessed with a
 * trailing slash, so the page URL path is treated as the base directory. The
 * actual up-navigation ("../") is computed by java.nio.file.Path.relativize.
 * External / mailto / anchor targets are returned unchanged.
 */
@TemplateExtension
public class UrlExtensions {

    public static String rel(Page page, String target) {
        return relativize(target, page == null || page.url() == null ? "/" : page.url().path());
    }

    public static String rel(Page page, RoqUrl target) {
        if (target == null) return null;
        if (target.isExternal()) return target.toString();
        return rel(page, target.path());
    }

    /** package-private for the self-test */
    static String relativize(String target, String fromUrl) {
        if (target == null) return null;
        if (isExternalOrAnchor(target)) return target;
        if (!target.startsWith("/")) return target;                 // already relative
        String from = (fromUrl == null || fromUrl.isEmpty()) ? "/" : fromUrl;
        boolean dirTarget = target.equals("/") || target.endsWith("/");
        String baseStr = (from.length() > 1 && from.endsWith("/")) ? from.substring(0, from.length() - 1) : from;
        // base is the page's directory; Path.relativize yields the "../" up-navigation
        String r = Path.of(baseStr).relativize(Path.of(target)).toString().replace(File.separatorChar, '/');
        if (r.isEmpty()) r = ".";
        if (dirTarget && !r.endsWith("/")) r = r + "/";
        return r;
    }

    private static boolean isExternalOrAnchor(String s) {
        return s.startsWith("http://") || s.startsWith("https://") || s.startsWith("//")
                || s.startsWith("mailto:") || s.startsWith("tel:") || s.startsWith("data:") || s.startsWith("#");
    }

    // Self-check: relativize, then resolve as a browser would (base = page url with
    // trailing slash) must return the original absolute target.
    public static void main(String[] args) {
        String[][] cases = {
                {"/assets/main.css", "/blog/first-thought/"},
                {"/assets/main.css", "/"},
                {"/about", "/blog/first-thought/"},
                {"/blog/", "/blog/first-thought/"},
                {"/avatar.jpg", "/about/"},
                {"/", "/blog/first-thought/"},
                {"/blog/x/", "/blog/y/"},
                {"/sub/assets/a.css", "/sub/blog/p/"},
                {"https://x.com/a", "/blog/p/"},
                {"mailto:max@xam.dk", "/about/"},
        };
        for (String[] c : cases) {
            String target = c[0], fromUrl = c[1];
            String rel = relativize(target, fromUrl);
            if (isExternalOrAnchor(target)) {
                check(rel.equals(target), target + " should pass through, got " + rel);
                System.out.println(pad(fromUrl) + " -> " + pad(target) + " rel=" + rel + " (passthrough)");
                continue;
            }
            String base = fromUrl.endsWith("/") ? fromUrl : fromUrl + "/";
            String resolved = URI.create("http://h" + base).resolve(rel).getPath();
            check(resolved.equals(target), fromUrl + " -> " + target + " : rel=" + rel + " resolved=" + resolved);
            System.out.println(pad(fromUrl) + " -> " + pad(target) + " rel=" + rel);
        }
        System.out.println("all ok");
    }

    private static void check(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }
    private static String pad(String s) { return String.format("%-28s", s); }
}
