package hexlet.code.controllers;

import hexlet.code.DatabaseConfig;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class UrlCheckController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlCheckController.class);
    private static final UrlRepository URL_REPOSITORY = new UrlRepository(DatabaseConfig.getDataSource());
    private static final UrlCheckRepository URL_CHECK_REPOSITORY = new UrlCheckRepository(DatabaseConfig.getDataSource());

    public static void checkUrl(Context ctx) {
        long urlId = ctx.pathParamAsClass("id", Long.class).get();
        LOGGER.info("Checking URL with id: {}", urlId);

        try {
            var url = URL_REPOSITORY.findById(urlId);
            if (url == null) {
                LOGGER.error("URL not found for id: {}", urlId);
                ctx.status(404);
                return;
            }

            Document doc = Jsoup.connect(url.getName()).timeout(10_000).get();
            UrlCheck urlCheck = new UrlCheck();
            urlCheck.setUrlId(urlId);
            urlCheck.setStatusCode(200);
            urlCheck.setTitle(doc.title());
            urlCheck.setH1(doc.select("h1").text());
            urlCheck.setDescription(doc.select("meta[name=description]").attr("content"));

            URL_CHECK_REPOSITORY.save(urlCheck);
            LOGGER.info("URL check saved for id: {}", urlId);
            ctx.redirect("/urls/" + urlId);
        } catch (IOException e) {
            LOGGER.error("Error during URL check for id: {}: {}", urlId, e.getMessage());
            ctx.status(500);
        } catch (SQLException e) {
            LOGGER.error("SQL Error during URL check for id: {}: SQLState: {}, ErrorCode: {}, Message: {}", urlId, e.getSQLState(), e.getErrorCode(), e.getMessage());
            ctx.status(500);
        }
    }
}
