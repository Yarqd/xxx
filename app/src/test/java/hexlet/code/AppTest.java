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

public final class AppTest {

    private static Javalin app;

    /**
     * Настройка приложения перед запуском всех тестов.
     */
    @BeforeAll
    public static void setUpBeforeClass() {
        app = App.getApp();
        app.start(0);
        RestAssured.port = app.port();
    }

    /**
     * Инициализация базы данных перед каждым тестом.
     * @throws Exception если возникает ошибка при инициализации базы данных
     */
    @BeforeEach
    public void setUp() throws Exception {
        initializeDatabase();
    }

    /**
     * Очистка базы данных после каждого теста.
     * @throws Exception если возникает ошибка при очистке базы данных
     */
    @AfterEach
    public void tearDown() throws Exception {
        clearDatabase();
    }

    /**
     * Остановка приложения после выполнения всех тестов.
     */
    @AfterAll
    public static void tearDownAfterClass() {
        app.stop();
    }

    @Test
    public void testRootPath() {
        given()
                .when()
                .get("/")
                .then()
                .statusCode(200);
    }

    @Test
    public void testUrlCreation() {
        given()
                .formParam("url", "https://example.com")
                .when()
                .post("/urls")
                .then()
                .statusCode(302);
    }

    private void initializeDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS urls");
            stmt.execute("CREATE TABLE urls ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(255) NOT NULL, "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")");
        }
    }

    private void clearDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM urls");
        }
    }
}
