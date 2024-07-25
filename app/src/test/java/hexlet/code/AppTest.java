//package hexlet.code;
//
//import io.javalin.Javalin;
//import io.javalin.testtools.JavalinTest;
//import org.junit.jupiter.api.Test;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.containsString;
//
//public class AppTest {
//
//    private void initializeDatabase() {
//        try (Connection conn = DatabaseConfig.getDataSource().getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            // Создаем таблицы
//            String createUrlsTable = "CREATE TABLE IF NOT EXISTS urls (" +
//                    "id SERIAL PRIMARY KEY," +
//                    "name VARCHAR(255) NOT NULL," +
//                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
//                    ");";
//            stmt.execute(createUrlsTable);
//
//            String createUrlChecksTable = "CREATE TABLE IF NOT EXISTS url_checks (" +
//                    "id SERIAL PRIMARY KEY," +
//                    "url_id BIGINT NOT NULL," +
//                    "status_code INTEGER," +
//                    "h1 VARCHAR(255)," +
//                    "title VARCHAR(255)," +
//                    "description TEXT," +
//                    "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
//                    "FOREIGN KEY (url_id) REFERENCES urls(id) ON DELETE CASCADE" +
//                    ");";
//            stmt.execute(createUrlChecksTable);
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testRoot() {
//        JavalinTest.test(Javalin.create(), (server, client) -> {
//            initializeDatabase();
//
//            server.get("/", ctx -> ctx.result("Анализатор страниц"));
//            server.start(0);
//
//            given()
//                    .baseUri(client.getOrigin())
//                    .get("/")
//                    .then()
//                    .statusCode(200)
//                    .body(containsString("Анализатор страниц"));
//        });
//    }
//
//    @Test
//    public void testAddUrl() {
//        JavalinTest.test(Javalin.create(), (server, client) -> {
//            initializeDatabase();
//
//            server.post("/urls", ctx -> {
//                ctx.status(302);
//                ctx.result("URL добавлен");
//            });
//            server.start(0);
//
//            given()
//                    .baseUri(client.getOrigin())
//                    .formParam("url", "https://example.com")
//                    .post("/urls")
//                    .then()
//                    .statusCode(302)
//                    .body(containsString("URL добавлен"));
//
//            given()
//                    .baseUri(client.getOrigin())
//                    .get("/urls")
//                    .then()
//                    .statusCode(200)
//                    .body(containsString("https://example.com"));
//        });
//    }
//}
