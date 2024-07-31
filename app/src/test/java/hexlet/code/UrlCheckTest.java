package hexlet.code;

import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static io.restassured.RestAssured.given;

public final class UrlCheckTest {

    private static Javalin app;

    @BeforeAll
    public static void setUpBeforeClass() {
        app = App.getApp();
        app.start(0);
        RestAssured.port = app.port();
    }

    @BeforeEach
    public void setUp() throws Exception {
        initializeDatabase();
    }

    @AfterEach
    public void tearDown() throws Exception {
        clearDatabase();
    }

    @AfterAll
    public static void tearDownAfterClass() {
        app.stop();
    }

    @Test
    public void testUrlCheckCreation() {
        given()
                .formParam("url", "https://example.com")
                .when()
                .post("/urls")
                .then()
                .statusCode(302);

        given()
                .when()
                .post("/urls/1/checks")
                .then()
                .statusCode(302);
    }

    private void initializeDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
             Statement stmt = conn.createStatement();
             var resourceStream = getClass().getResourceAsStream("/schema.sql")) {

            if (resourceStream == null) {
                throw new IOException("Resource /schema.sql not found");
            }
            String sql = new String(resourceStream.readAllBytes());
            stmt.execute(sql);
        }
    }

    private void clearDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM url_checks");
            stmt.execute("DELETE FROM urls");
        }
    }
}
