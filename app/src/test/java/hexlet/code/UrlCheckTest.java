package hexlet.code;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RedirectConfig.redirectConfig;
import static io.restassured.config.RestAssuredConfig.config;
import static org.hamcrest.Matchers.containsString;

public class UrlCheckTest {

    /**
     * Sets up the test environment.
     */
    @BeforeEach
    public void setUp() throws Exception {
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             Statement stmt = conn.createStatement();
             var resourceStream = getClass().getResourceAsStream("/schema.sql")) {

            if (resourceStream == null) {
                throw new RuntimeException("Resource /schema.sql not found");
            }

            String sql = new String(resourceStream.readAllBytes());
            stmt.execute(sql);
        }
    }

    @Test
    public void testCheckUrl() throws Exception {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse().setBody("<html><head><title>Test Title</title>"
                + "</head><body><h1>Test H1</h1></body></html>"));
        mockWebServer.start();

        String mockUrl = mockWebServer.url("/").toString();
        System.out.println("Mock URL: " + mockUrl);

        Javalin app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            // Step 1: Add URL
            System.out.println("Step 1: Adding URL");
            given().baseUri(client.getOrigin())
                    .log().all() // Логируем запрос и ответ
                    .formParam("url", mockUrl)
                    .post("/urls")
                    .then()
                    .log().all() // Логируем запрос и ответ
                    .statusCode(302)
                    .header("Location", "/urls");

            // Step 2: Check URL
            System.out.println("Step 2: Checking URL");
            given().baseUri(client.getOrigin())
                    .config(config().redirect(redirectConfig().followRedirects(false)))
                    .log().all() // Логируем запрос и ответ
                    .post("/urls/1/checks")
                    .then()
                    .log().all() // Логируем запрос и ответ
                    .statusCode(302)
                    .header("Location", "/urls/1");

            // Step 3: Follow the redirect and check the content
            System.out.println("Step 3: Following redirect and checking content");
            given().baseUri(client.getOrigin())
                    .log().all() // Логируем запрос и ответ
                    .get("/urls/1")
                    .then()
                    .log().all() // Логируем запрос и ответ
                    .statusCode(200)
                    .body(containsString("Test Title"))
                    .body(containsString("Test H1"));
        });

        mockWebServer.shutdown();
    }
}
