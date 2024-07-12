package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class UrlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);
    private static final UrlRepository URL_REPOSITORY = new UrlRepository(DatabaseConfig.getDataSource());

    public static void addUrl(Context ctx) {
        String inputUrl = ctx.formParam("url");

        try {
            URL url = new URL(inputUrl);
            String domainUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":"
                    + url.getPort());

            try {
                if (URL_REPOSITORY.existsByName(domainUrl)) {
                    ctx.sessionAttribute("flash", "Страница уже существует");
                    ctx.sessionAttribute("flashType", "error");
                    ctx.redirect("/");
                    return;
                }

                Url newUrl = new Url();
                newUrl.setName(domainUrl);
                newUrl.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                URL_REPOSITORY.save(newUrl);

                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flashType", "success");
            } catch (SQLException e) {
                LOGGER.error("Ошибка при добавлении URL", e);
                ctx.sessionAttribute("flash", "Ошибка при добавлении URL: " + e.getMessage());
                ctx.sessionAttribute("flashType", "error");
            }
        } catch (MalformedURLException e) {
            LOGGER.error("Некорректный URL", e);
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "error");
        }

        ctx.redirect("/");
    }

    public static void listUrls(Context ctx) {
        try {
            List<Url> urls = URL_REPOSITORY.findAll();
            ctx.attribute("urls", urls);
            ctx.render("urls.jte", Map.of("urls", urls));
        } catch (SQLException e) {
            LOGGER.error("Ошибка при получении URL", e);
            ctx.sessionAttribute("flash", "Ошибка при получении URL: " + e.getMessage());
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect("/");
        }
    }

    public static void showUrl(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        try {
            Url url = URL_REPOSITORY.findById(id);
            if (url != null) {
                ctx.attribute("url", url);
                ctx.render("url.jte", Map.of("url", url));
            } else {
                ctx.sessionAttribute("flash", "URL не найден");
                ctx.sessionAttribute("flashType", "error");
                ctx.redirect("/urls");
            }
        } catch (SQLException e) {
            LOGGER.error("Ошибка при получении URL", e);
            ctx.sessionAttribute("flash", "Ошибка при получении URL: " + e.getMessage());
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect("/urls");
        }
    }
}
