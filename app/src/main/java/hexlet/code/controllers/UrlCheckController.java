package hexlet.code.controllers;

import hexlet.code.DatabaseConfig;
import hexlet.code.dto.UrlCheckDto;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public final class UrlCheckController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlCheckController.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final UrlCheckRepository urlCheckRepository = new UrlCheckRepository(DatabaseConfig.getDataSource());
    private static final UrlRepository URL_REPOSITORY = new UrlRepository(DatabaseConfig.getDataSource());

    public static void checkUrl(Context ctx) {
        long urlId = ctx.pathParamAsClass("id", Long.class).get();

        try {
            // Получаем URL из базы данных
            String url = URL_REPOSITORY.getUrlById(urlId);

            // Отправляем HTTP-запрос к сайту и получаем HTML-документ
            Document doc = Jsoup.connect(url).get();

            // Извлекаем необходимые данные из HTML-документа
            String title = doc.title();
            String h1 = doc.selectFirst("h1") != null ? doc.selectFirst("h1").text() : "";
            String description = doc.selectFirst("meta[name=description]") != null ? doc.selectFirst("meta[name=description]").attr("content") : "";

            // Создаем объект UrlCheck с извлеченными данными
            UrlCheck urlCheck = new UrlCheck(urlId, 200, title, h1, description, new Timestamp(System.currentTimeMillis()));
            urlCheckRepository.save(urlCheck);

            UrlCheckDto urlCheckDto = new UrlCheckDto(
                    urlCheck.getId(),
                    urlCheck.getStatusCode(),
                    urlCheck.getTitle(),
                    urlCheck.getH1(),
                    urlCheck.getDescription(),
                    urlCheck.getUrlId(),
                    DATE_FORMAT.format(urlCheck.getCreatedAt())
            );

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");

        } catch (Exception e) {
            LOGGER.error("Error during URL check", e);
            ctx.sessionAttribute("flash", "Ошибка при проверке URL");
            ctx.sessionAttribute("flashType", "danger");
        }

        ctx.redirect("/urls/" + urlId);
    }
}
