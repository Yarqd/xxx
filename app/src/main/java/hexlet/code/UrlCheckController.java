package hexlet.code;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;

public class UrlCheckController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlCheckController.class);
    private static final UrlCheckRepository URL_CHECK_REPOSITORY = new UrlCheckRepository(
            DatabaseConfig.getDataSource());
    private static final UrlRepository URL_REPOSITORY = new UrlRepository(DatabaseConfig.getDataSource());

    public static void checkUrl(Context ctx) {
        long urlId = Long.parseLong(ctx.pathParam("id"));
        UrlCheck urlCheck = new UrlCheck();
        urlCheck.setUrlId(urlId);
        urlCheck.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        try {
            // Получаем URL из базы данных
            String url = URL_REPOSITORY.getUrlById(urlId);

            // Выполнение HTTP-запроса с использованием Unirest
            HttpResponse<String> response = Unirest.get(url).asString();
            int statusCode = response.getStatus();
            urlCheck.setStatusCode(statusCode);

            Document doc = Jsoup.parse(response.getBody());
            urlCheck.setTitle(doc.title());
            urlCheck.setH1(doc.selectFirst("h1") != null
                    ? doc.selectFirst("h1").text() : "");
            urlCheck.setDescription(doc.selectFirst("meta[name=description]") != null
                    ? doc.selectFirst("meta[name=description]").attr("content") : "");

        } catch (Exception e) {
            LOGGER.error("Error while checking URL", e);
            urlCheck.setStatusCode(500); // error code
        }

        try {
            URL_CHECK_REPOSITORY.save(urlCheck);
            ctx.sessionAttribute("flash", "Проверка успешно выполнена");
            ctx.sessionAttribute("flashType", "success");
        } catch (SQLException e) {
            LOGGER.error("Error while saving URL check", e);
            ctx.sessionAttribute("flash", "Ошибка при сохранении проверки: " + e.getMessage());
            ctx.sessionAttribute("flashType", "error");
        }

        ctx.redirect("/urls/" + urlId);
    }
}
