package hexlet.code.controllers;

import hexlet.code.DatabaseConfig;
import hexlet.code.model.Url;
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

public class UrlCheckController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlCheckController.class);
    private static final UrlRepository URL_REPOSITORY =
            new UrlRepository(DatabaseConfig.getDataSource());
    private static final UrlCheckRepository URL_CHECK_REPOSITORY =
            new UrlCheckRepository(DatabaseConfig.getDataSource());

    public static void checkUrl(Context ctx) {
        long urlId = Long.parseLong(ctx.pathParam("id"));
        try {
            Url url = URL_REPOSITORY.findById(urlId);
            if (url == null) {
                ctx.sessionAttribute("flash", "URL не найден");
                ctx.sessionAttribute("flashType", "error");
                ctx.redirect("/urls");
                return;
            }

            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            Document doc = Jsoup.parse(response.getBody());

            UrlCheck urlCheck = new UrlCheck();
            urlCheck.setUrlId(urlId);
            urlCheck.setStatusCode(response.getStatus());
            urlCheck.setTitle(doc.title());
            urlCheck.setH1(doc.selectFirst("h1") != null ? doc.selectFirst("h1").text() : null);
            urlCheck.setDescription(doc.selectFirst("meta[name=description]") != null
                    ? doc.selectFirst("meta[name=description]").attr("content") : null);
            URL_CHECK_REPOSITORY.save(urlCheck);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect("/urls/" + urlId);
        } catch (SQLException e) {
            LOGGER.error("SQL Error during URL check for id: {}: SQLState: {}, ErrorCode: {}, Message: {}",
                    urlId, e.getSQLState(), e.getErrorCode(), e.getMessage());
            ctx.sessionAttribute("flash", "Ошибка при проверке URL: " + e.getMessage());
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect("/urls");
        } catch (Exception e) {
            LOGGER.error("Error during URL check for id: {}", urlId, e);
            ctx.sessionAttribute("flash", "Ошибка при проверке URL: " + e.getMessage());
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect("/urls");
        }
    }
}
