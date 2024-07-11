package hexlet.code;

import io.javalin.http.Context;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UrlController {

    public static void addUrl(Context ctx) {
        String inputUrl = ctx.formParam("url");

        try {
            URL url = new URL(inputUrl);
            String domainUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() == -1 ? "" : ":"
                    + url.getPort());

            try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
                String checkSql = "SELECT COUNT(*) FROM urls WHERE name = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, domainUrl);

                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    ctx.sessionAttribute("flash", "Страница уже существует");
                    ctx.redirect("/urls");
                    return;
                }

                String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, domainUrl);
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                stmt.executeUpdate();

                ctx.sessionAttribute("flash", "Страница успешно добавлена");
            } catch (SQLException e) {
                ctx.sessionAttribute("flash", "Ошибка при добавлении URL: " + e.getMessage());
            }
        } catch (MalformedURLException e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
        }

        ctx.redirect("/urls");
    }

    public static void listUrls(Context ctx) {
        List<String> urls = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            String sql = "SELECT id, name FROM urls";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                urls.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            ctx.sessionAttribute("flash", "Ошибка при получении URL: " + e.getMessage());
        }

        ctx.attribute("urls", urls);
        ctx.render("urls.jte");
    }

    public static void showUrl(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        String urlName = null;
        try (Connection conn = DatabaseConfig.getDataSource().getConnection()) {
            String sql = "SELECT name FROM urls WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                urlName = rs.getString("name");
            }
        } catch (SQLException e) {
            ctx.sessionAttribute("flash", "Ошибка при получении URL: " + e.getMessage());
        }

        if (urlName != null) {
            ctx.attribute("url", urlName);
            ctx.render("url.jte");
        } else {
            ctx.sessionAttribute("flash", "URL не найден");
            ctx.redirect("/urls");
        }
    }
}
