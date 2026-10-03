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

## Known corner-cuts (ponytail debt)
- `tree/index.md` uses `layout: splash` (minimal-mistakes theme, not clean-blog) —
  legacy/broken under current Jekyll too. Move to public/ as static.
- webmentions + disqus comments were already disabled in layouts — not ported.
- recaptcha/contact form JS ported verbatim; untested.
