package hexlet.code;

import io.javalin.Javalin;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class UrlCheckTest {

    private Javalin app;
    private int port;

    @BeforeEach
    public void setUp() throws Exception {
        app = App.getApp();
        port = app.start(0).port();
        RestAssured.port = port;
        initializeDatabase();
    }

    @AfterEach
    public void tearDown() {
        app.stop();
    }

    private void initializeDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:project", "SA", "")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS urls (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");
                statement.execute("CREATE TABLE IF NOT EXISTS url_checks (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "status_code INT, " +
                        "title VARCHAR(255), " +
                        "h1 VARCHAR(255), " +
                        "description TEXT, " +
                        "url_id INT, " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (url_id) REFERENCES urls(id) ON DELETE CASCADE);");
            }
        }
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

        given()
                .when()
                .get("/urls/1")
                .then()
                .statusCode(200)
                .body(containsString("https://example.com"))
                .body(containsString("200"));
    }
}
