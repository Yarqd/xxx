package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlCheckControllerTest {

    private static Javalin app;
    private static MockWebServer mockServer;
    private static String baseUrl;

    private UrlRepository urlRepository;
    private UrlCheckRepository urlCheckRepository;

    @BeforeAll
    public static void beforeAll() throws IOException {
        app = App.getApp();
        mockServer = new MockWebServer();
        baseUrl = mockServer.url("/").toString();
        MockResponse mockResponse = new MockResponse().setBody("<html><head><title>Test Title</title></head>"
                + "<body><h1>Test Page Analyzer</h1></body></html>");
        mockServer.enqueue(mockResponse);
    }

    @BeforeEach
    public final void setUp() throws SQLException {
        App.initializeDatabase();
        DataSource dataSource = DatabaseConfig.getDataSource();
        urlRepository = new UrlRepository(dataSource);
        urlCheckRepository = new UrlCheckRepository(dataSource);
    }

    @AfterAll
    public static void afterAll() throws IOException {
        mockServer.shutdown();
        app.stop();
    }

    @Test
    public void testUrlCheck() throws SQLException {
        Url url = new Url();
        url.setName(baseUrl);
        url.setCreatedAt(new Timestamp(new Date().getTime()));
        urlRepository.save(url);

        // Проверка, что URL был успешно сохранен
        Url savedUrl = urlRepository.findById(url.getId());
        assertThat(savedUrl).isNotNull();
        assertThat(savedUrl.getName()).isEqualTo(baseUrl);

        JavalinTest.test(app, (server, client) -> {
            var postResponse = client.post("/urls/" + savedUrl.getId() + "/checks");
            assertThat(postResponse.code()).isEqualTo(200);

            // Проверка сохраненного URL Check
            UrlCheck check = urlCheckRepository.findLatestByUrlId(savedUrl.getId());
            assertThat(check).isNotNull();
            assertThat(check.getTitle()).isEqualTo("Test Title");
            assertThat(check.getH1()).isEqualTo("Test Page Analyzer");
            assertThat(check.getDescription()).isEqualTo("");

            var getResponse = client.get("/urls/" + savedUrl.getId());
            assertThat(getResponse.code()).isEqualTo(200);
            assertThat(getResponse.body().string()).contains("Test Title");
        });
    }
}
