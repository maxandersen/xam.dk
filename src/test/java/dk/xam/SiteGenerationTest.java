package dk.xam;

import io.quarkiverse.roq.testing.RoqAndRoll;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

/**
 * Validates full Roq generation and guards against the regressions found during the
 * Jekyll->Roq migration (see .specs/roq-migration.md):
 *  - AsciiDoc posts rendering their body (the `ifdef::env-github` bug swallowed it)
 *  - no duplicate `<h1>` doctitle inside the post body
 *  - URL preservation (/blog/<name>/), pagination, RSS feed, microsites.
 */
@QuarkusTest
@RoqAndRoll
public class SiteGenerationTest {

    @Test
    public void homeListsPosts() {
        when().get("/").then().statusCode(200)
                .body(containsString("post-preview"));
    }

    @Test
    public void markdownPostRenders() {
        when().get("/blog/deployment-time-blues/").then().statusCode(200)
                .body(containsString("how long time do you spend waiting"));
    }

    @Test
    public void asciidocPostBodyRenders() {
        // Regression: ifdef::env-github line used to swallow the whole body.
        when().get("/blog/announcing-tamboui/").then().statusCode(200)
                .body(containsString("Terminal UI framework"))
                .body(containsString("How it started"));
    }

    @Test
    public void asciidocPostHasNoDuplicateTitleInBody() {
        when().get("/blog/nanocode-coding-agent-in-260-lines-of-java/").then().statusCode(200)
                .body(containsString("e-content"))
                // the title must appear only in the masthead, not as a body <h1>
                .body(not(containsString("<div class=\"e-content\">\n <h1>")));
    }

    @Test
    public void blogPaginationExists() {
        when().get("/blog/").then().statusCode(200).body(containsString("post-preview"));
        when().get("/blog/page2/").then().statusCode(200);
    }

    @Test
    public void rssFeedAtOriginalPath() {
        when().get("/blog/feed.atom").then().statusCode(200)
                .body(containsString("<rss"));
    }

    @Test
    public void postHasDefaultMastheadBackground() {
        // Jekyll set a site-wide default background via _config.yml defaults;
        // reproduced as a frontmatter default on the root layout.
        // relativized to the current page (/blog/<slug>/ is 2 levels deep)
        when().get("/blog/nanocode-coding-agent-in-260-lines-of-java/").then().statusCode(200)
                .body(containsString("background-image: url('../../img/post-bg.jpg')"));
    }

    @Test
    public void historicalCommentsRender() {
        when().get("/blog/new-job-at-jboss/").then().statusCode(200)
                .body(containsString("Comment"))
                .body(containsString("class=\"comment h-cite\""));
    }

    @Test
    public void historicalWebmentionsRender() {
        when().get("/blog/the-black-swan-of-java/").then().statusCode(200)
                .body(containsString("Webmention"))
                .body(containsString("class=\"webmention h-cite\""));
    }

    @Test
    public void webmentionDiscoveryLinkPresent() {
        when().get("/").then().statusCode(200)
                .body(containsString("rel=\"webmention\""));
    }

    @Test
    public void micrositeRenders() {
        when().get("/hibern8ide/").then().statusCode(200)
                .body(containsString("Hibernate Query Language"));
    }
}
