package hexlet.code;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.sql.Connection;
import java.sql.Statement;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class AppTest {

    @BeforeEach
    public void setUp() {
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS urls (" +
                    "id SERIAL PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "created_at TIMESTAMP NOT NULL)";
            stmt.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() {
        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS urls");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRoot() {
        Javalin app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            given().baseUri(client.getOrigin())
                    .get("/")
                    .then()
                    .statusCode(200)
                    .body(containsString("Добавление URL"));
        });
    }

    @Test
    public void testAddUrl() {
        Javalin app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            given().baseUri(client.getOrigin())
                    .formParam("url", "https://example.com")
                    .post("/urls")
                    .then()
                    .statusCode(302);

            given().baseUri(client.getOrigin())
                    .get("/urls")
                    .then()
                    .statusCode(200)
                    .body(containsString("https://example.com"));
        });
    }

    @Test
    public void testListUrls() {
        Javalin app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            given().baseUri(client.getOrigin())
                    .get("/urls")
                    .then()
                    .statusCode(200)
                    .body(containsString("Список URL"));
        });
    }

    @Test
    public void testShowUrl() {
        Javalin app = App.getApp();

        JavalinTest.test(app, (server, client) -> {
            given().baseUri(client.getOrigin())
                    .formParam("url", "https://example.com")
                    .post("/urls")
                    .then()
                    .statusCode(302);

            given().baseUri(client.getOrigin())
                    .get("/urls/1")
                    .then()
                    .statusCode(200)
                    .body(containsString("https://example.com"));
        });
    }
}
