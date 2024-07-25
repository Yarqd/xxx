package hexlet.code;

import io.javalin.Javalin;
import hexlet.code.controllers.UrlCheckController;
import hexlet.code.controllers.UrlController;
import hexlet.code.dto.BasePage;

import static io.javalin.rendering.template.TemplateUtil.model;

public final class Routes {

    public static void configure(Javalin app) {
        app.get("/", ctx -> {
            String flashMessage = ctx.sessionAttribute("flash");
            String flashType = ctx.sessionAttribute("flashType");
            BasePage page = new BasePage(flashMessage, flashType);
            ctx.render("index.jte", model("page", page));
        });

        app.post("/", ctx -> {
            ctx.sessionAttribute("flash", null);
            ctx.sessionAttribute("flashType", null);
            ctx.status(204); // No Content
        });

        app.post("/urls", UrlController::addUrl);
        app.get("/urls", UrlController::listUrls);
        app.get("/urls/{id}", UrlController::showUrl);
        app.post("/urls/{id}/checks", UrlCheckController::checkUrl);
    }

    public static String rootPath() {
        return "/";
    }

    public static String urlsPath() {
        return "/urls";
    }

    public static String urlPath(long id) {
        return "/urls/" + id;
    }

    public static String urlChecksPath(long urlId) {
        return "/urls/" + urlId + "/checks";
    }
}
