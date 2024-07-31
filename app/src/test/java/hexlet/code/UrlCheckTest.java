package hexlet.code;

import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static io.restassured.RestAssured.given;

public final class UrlCheckTest {

    private static Javalin app;

    @BeforeAll
    public static void setUpBeforeClass() {
        System.setProperty("TEST_ENV", "true");
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
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS urls");
            stmt.execute("DROP TABLE IF EXISTS url_checks");
            stmt.execute("CREATE TABLE urls ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(255) NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");
            stmt.execute("CREATE TABLE url_checks ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "status_code INT, "
                    + "title VARCHAR(255), "
                    + "h1 VARCHAR(255), "
                    + "description TEXT, "
                    + "url_id INT, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY (url_id) REFERENCES urls(id)"
                    + ")");
        }
    }

    private void clearDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM url_checks");
            stmt.execute("DELETE FROM urls");
        }
    }
}
