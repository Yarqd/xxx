//package hexlet.code;
//
//import hexlet.code.model.Url;
//import hexlet.code.model.UrlCheck;
//import hexlet.code.repository.UrlCheckRepository;
//import hexlet.code.repository.UrlRepository;
//import io.javalin.Javalin;
//import io.javalin.testtools.JavalinTest;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.util.Date;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.slf4j.LoggerFactory.getLogger;
//import org.slf4j.Logger;
//
//public class UrlCheckControllerTest {
//
//    private static final Logger LOGGER = getLogger(UrlCheckControllerTest.class);
//    private static Javalin app;
//    private static MockWebServer mockServer;
//    private static String baseUrl;
//
//    private UrlRepository urlRepository;
//    private UrlCheckRepository urlCheckRepository;
//
//    @BeforeAll
//    public static void beforeAll() throws IOException {
//        app = App.getApp();
//        mockServer = new MockWebServer();
//        baseUrl = mockServer.url("/").toString();
//        MockResponse mockResponse = new MockResponse().setBody("<html><head><title>Test Title</title></head>"
//                + "<body><h1>Test Page Analyzer</h1></body></html>");
//        mockServer.enqueue(mockResponse);
//        LOGGER.info("Mock server started with base URL: {}", baseUrl);
//    }
//
//    @BeforeEach
//    public final void setUp() throws SQLException {
//        App.initializeDatabase();
//        DataSource dataSource = DatabaseConfig.getDataSource();
//        urlRepository = new UrlRepository(dataSource);
//        urlCheckRepository = new UrlCheckRepository(dataSource);
//        LOGGER.info("Database initialized for test");
//    }
//
//    @AfterAll
//    public static void afterAll() throws IOException {
//        mockServer.shutdown();
//        app.stop();
//        LOGGER.info("Mock server stopped");
//    }
//
//    @Test
//    public void testUrlCheck() throws SQLException {
//        Url url = new Url();
//        url.setName(baseUrl);
//        url.setCreatedAt(new Timestamp(new Date().getTime()));
//        urlRepository.save(url);
//
//        // Проверка, что URL был успешно сохранен
//        Url savedUrl = urlRepository.findById(url.getId());
//        LOGGER.info("Saved URL: {}", savedUrl);
//        assertThat(savedUrl).isNotNull();
//        assertThat(savedUrl.getName()).isEqualTo(baseUrl);
//
//        JavalinTest.test(app, (server, client) -> {
//            LOGGER.info("Sending POST request to /urls/{}/checks", savedUrl.getId());
//            var postResponse = client.post("/urls/" + savedUrl.getId() + "/checks");
//            LOGGER.info("POST response status: {}", postResponse.code());
//            assertThat(postResponse.code()).isEqualTo(200);
//
//            // Проверка сохраненного URL Check
//            UrlCheck check = urlCheckRepository.findLatestByUrlId(savedUrl.getId());
//            LOGGER.info("Latest URL check: {}", check);
//            assertThat(check).isNotNull();
//            assertThat(check.getTitle()).isEqualTo("Test Title");
//            assertThat(check.getH1()).isEqualTo("Test Page Analyzer");
//            assertThat(check.getDescription()).isEqualTo("");
//
//            var getResponse = client.get("/urls/" + savedUrl.getId());
//            LOGGER.info("GET response status: {}", getResponse.code());
//            assertThat(getResponse.code()).isEqualTo(200);
//            assertThat(getResponse.body().string()).contains("Test Title");
//        });
//    }
//}
