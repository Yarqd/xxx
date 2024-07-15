package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UrlController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);
    private static final UrlRepository URL_REPOSITORY = new UrlRepository(DatabaseConfig.getDataSource());
    private static final UrlCheckRepository URL_CHECK_REPOSITORY = new UrlCheckRepository(
            DatabaseConfig.getDataSource());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void addUrl(Context ctx) {
        String inputUrl = ctx.formParam("url");

        try {
            java.net.URL url = new java.net.URL(inputUrl);
            String domainUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":"
                    + url.getPort());

            try {
                if (URL_REPOSITORY.existsByName(domainUrl)) {
                    ctx.sessionAttribute("flash", "Страница уже существует");
                    ctx.sessionAttribute("flashType", "error");
                    ctx.redirect("/urls");
                    return;
                }

                Url newUrl = new Url();
                newUrl.setName(domainUrl);
                newUrl.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
                URL_REPOSITORY.save(newUrl);

                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flashType", "success");
                ctx.redirect("/urls");
            } catch (SQLException e) {
                LOGGER.error("Ошибка при добавлении URL", e);
                ctx.sessionAttribute("flash", "Ошибка при добавлении URL: " + e.getMessage());
                ctx.sessionAttribute("flashType", "error");
                ctx.redirect("/urls");
            }
        } catch (java.net.MalformedURLException e) {
            LOGGER.error("Некорректный URL", e);
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect("/urls");
        }
    }

    public static void listUrls(Context ctx) {
        try {
            List<Url> urls = URL_REPOSITORY.findAll();
            List<Map<String, String>> urlsWithChecks = urls.stream().map(url -> {
                try {
                    UrlCheck latestCheck = URL_CHECK_REPOSITORY.findLatestByUrlId(url.getId());
                    return Map.of(
                            "id", String.valueOf(url.getId()),
                            "name", url.getName(),
                            "lastCheckedAt", latestCheck != null
                                    ? DATE_FORMAT.format(latestCheck.getCreatedAt()) : "Не проверялось",
                            "statusCode", latestCheck != null ? String.valueOf(latestCheck.getStatusCode()) : "N/A"
                    );
                } catch (SQLException e) {
                    LOGGER.error("Ошибка при получении проверки для URL", e);
                    return Map.of(
                            "id", String.valueOf(url.getId()),
                            "name", url.getName(),
                            "lastCheckedAt", "Ошибка",
                            "statusCode", "Ошибка"
                    );
                }
            }).collect(Collectors.toList());

            ctx.attribute("urls", urlsWithChecks);
            ctx.render("urls.jte", Map.of("urls", urlsWithChecks));
        } catch (SQLException e) {
            LOGGER.error("Ошибка при получении URL", e);
            ctx.sessionAttribute("flash", "Ошибка при получении URL: " + e.getMessage());
            ctx.sessionAttribute("flashType", "error");
            ctx.redirect("/urls");
        }
    }

    public static void showUrl(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        try {
            Url url = URL_REPOSITORY.findById(id);
            List<UrlCheck> checks = URL_CHECK_REPOSITORY.findByUrlId(id);
            List<Map<String, String>> formattedChecks = checks.stream().map(check -> {
                return Map.of(
                        "id", String.valueOf(check.getId()),
                        "statusCode", String.valueOf(check.getStatusCode()),
                        "h1", check.getH1(),
                        "title", check.getTitle(),
                        "description", check.getDescription(),
                        "createdAt", DATE_FORMAT.format(check.getCreatedAt())
                );
            }).collect(Collectors.toList());

            if (url != null) {
                ctx.attribute("url", url);
                ctx.attribute("checks", formattedChecks);
                ctx.render("url.jte", Map.of("url", url, "checks", formattedChecks));
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
