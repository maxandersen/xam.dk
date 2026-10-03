# Jekyll → Quarkus Roq migration plan

Source: Jekyll 4.3 site (clean-blog theme), 99 posts, AsciiDoc + Markdown + HTML,
pagination, webmentions (mostly disabled), disqus (disabled), feed.atom.

Roq 2.1.9 / Quarkus 3.40.1 / Java 25. Base theme (port existing design).

## Directory mapping
| Jekyll | Roq |
|--------|-----|
| `_posts/` | `content/posts/` |
| root `*.md/.adoc/.html` pages | `content/` |
| `_layouts/` | `templates/layouts/` |
| `_includes/` | `templates/partials/` |
| `_data/` | kept in place via `quarkus.roq.data.dir=_data` |
| `_sass/`, `assets/main.scss` | `web/` (web bundler) |
| static (`img images js qrcode userscripts avatar.jpg favicon.ico assets/vendor` etc.) | `public/` |
| self-contained microsites (`anabelle benjamin asylum`) | `public/` as-is |
| microsite pages (`cape5 hibern8ide house maxandersen surprise`) | `content/` |
| `_config.yml` | `config/application.properties` + `data/siteConfig.yml` |
| `_plugins/relativze.rb` | removed (Roq URLs are site-relative) |

## URL preservation
Jekyll permalink `/blog/:categories/:title/`; posts have no categories →
effective `/blog/<slug>/`. Roq: `site.collections.posts.link=/blog/:slug/`.

## Phases
- **A Foundation**: scaffold (pom, mvnw, config), core template chain
  (default/home/page/post + head/navbar/footer/scripts/post-meta partials),
  index + about + posts rendering. Gate: `roq generate` succeeds, pages render.
- **B Styling + scale**: SCSS→web/, static dirs→public/, microsites, all content.
- **C Full site**: blog pagination page, RSS feed, aliases/redirects, sitemap,
  tagging, remove Jekyll artifacts, CI/CD.

Commit after each phase.

## Status: DONE (A, B, C all green — `roq generate` = BUILD SUCCESS, 119 pages)
- Posts URL: `site.collections.posts.link=/blog/:name/` (filename-based = matches old
  Jekyll `:title` permalink exactly).
- RSS: `content/rss.xml` → `/blog/feed.atom` (full content, contentLimit=0).
- Blog pagination: `content/blog.html` → `/blog/`, `/blog/page2/` ...
- Dropped speculative plugins (sitemap/tagging/aliases) — original site had none.
  Old WP URLs were `?p=N` query strings (no static redirect possible); Jekyll
  permalink `/blog/:title/` == new `/blog/:name/`, so no aliases needed.
- CI: `.github/workflows/deploy.yml` builds via jbang `roq generate`, rsyncs
  `target/roq/` to `xam.dk@ssh.xam.dk:/www` (excludes coppermine/update, as before).
- `.htaccess` moved to `public/` (apache target still serves it).
- Draft moved to `content/posts/` with `draft: true` (excluded from output).

## Testing & content parity (how we verify it matches xam.dk)
Two layers:
1. **`@RoqAndRoll` generation test** (`src/test/java/dk/xam/SiteGenerationTest.java`)
   — validates the full site generates with no render errors and asserts the
   specific regressions stay fixed (AsciiDoc body renders, no duplicate title,
   pagination, RSS, microsite). Run: `./mvnw test` (7 tests).
2. **Content-parity diff vs live** (`/tmp/parity.py`, ad-hoc) — for every
   generated page, fetch the same URL on https://xam.dk and compare main-content
   word counts; also HEAD-checks all 98 post URLs for 200 (URL preservation).

Findings after fixes: 0 URL drift, every page r~1.00. Only residual: nanocode
`r=0.82` is a code-highlighting word-count artifact (live wraps each code token
in a span -> extra whitespace -> inflated count); prose is byte-identical.

### AsciiDoc engine: use the jruby plugin (AsciidoctorJ)
The old site was Jekyll + jekyll-asciidoc = **AsciidoctorJ**. The default Roq
plugin (`quarkus-roq-plugin-asciidoc`) uses Yupiik **asciidoc-java** (pure Java),
which has rendering gaps vs AsciidoctorJ that each needed a workaround:
- single-line `ifdef::env-github,...[:imagesdir: ..]` silently dropped the entire
  post body (reported as quarkiverse/quarkus-roq#1287);
- the `video::` macro dropped `width`/`height`;
- admonition markup differed.

To match the old site by construction and avoid per-gap hacks, we switched to
**`quarkus-roq-plugin-asciidoc-jruby`** (AsciidoctorJ). With it:
- single-line `ifdef` renders correctly (kept as-is, no block-form rewrite);
- `video::[... width=640, height=480]` emits `<iframe width height>` like old;
- content keeps its original `= Title`/`:page-*` headers.
Trade-off: bundles a JRuby runtime (heavier/slower than pure Java); fine for a
personal blog. JRuby inits lazily on first conversion, within the generator's
60s per-request timeout.

Remaining AsciiDoc config (small, matches old behaviour):
- `quarkus.asciidoc.attributes.notitle=true` — suppress the body doctitle so the
  title shows once (in the masthead), as the old site did.
- `quarkus.asciidoc.attributes.icons=font` — admonitions emit `<i class="fa icon-note">`
  (same markup as old).
- dropped `site.escaped-pages` (redundant under alt-expr-syntax).

## Known corner-cuts (ponytail debt)
- `tree/index.md` uses `layout: splash` (minimal-mistakes theme, not clean-blog) —
  legacy/broken under current Jekyll too. Move to public/ as static.
- New comments/webmentions can no longer be submitted (staticman + jekyll-webmention_io
  plugins are gone). Historical ones are shown READ-ONLY: `_data/comments/` and
  `_data/webmentions/received.yml` were consolidated into `_data/comments.json`
  and `_data/mentions.json` (keyed by post URL path) and rendered by
  `templates/partials/post-reactions.html` (213 comments / 147 mentions).
  Webmention avatars are served by unavatar.io (derived from the author twitter
  handle; original webmention.io photos are 404) with an initials-circle onerror
  fallback. Webmention receiving endpoint (`<link rel="webmention">` -> webmention.io) is
  kept in the head, but incoming mentions won't display until re-exported.
- recaptcha/contact form JS ported verbatim; untested.

## Follow-ups to investigate
- **FA `icon-note` admonition icons**: with `icons=font`, admonitions emit
  `<i class="fa icon-note">` but no stylesheet maps `icon-note` to a glyph
  (true on the old site too, so the icon cell is effectively empty on both).
  Investigate adding the Asciidoctor admonition CSS (or a Font Awesome glyph
  mapping) so NOTE/TIP/WARNING show real icons instead of a blank cell.
