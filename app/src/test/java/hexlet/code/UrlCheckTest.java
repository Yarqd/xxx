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

public final class UrlCheckTest {

    private Javalin app;
    private int port;

    /**
     * Настройка приложения и базы данных перед каждым тестом.
     * @throws Exception если возникает ошибка при настройке
     */
    @BeforeEach
    public void setUp() throws Exception {
        app = App.getApp();
        port = app.start(0).port();
        RestAssured.port = port;
        initializeDatabase();
    }

    /**
     * Очистка базы данных после каждого теста.
     * @throws Exception если возникает ошибка при очистке базы данных
     */
    @AfterEach
    public void tearDown() throws Exception {
        clearDatabase();
        app.stop();
    }

    private void initializeDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:project", "SA", "")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE urls ("
                        + "id INT AUTO_INCREMENT, "
                        + "name VARCHAR(255), "
                        + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                        + ");");
            }
        }
    }

    private void clearDatabase() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:project", "SA", "")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("DELETE FROM urls");
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
                .statusCode(200);

        given()
                .when()
                .get("/urls/1")
                .then()
                .statusCode(200)
                .body(containsString("https://example.com"))
                .body(containsString("200"));
    }
}
