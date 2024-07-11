package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**.
 * Основной класс приложения
 */
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final DataSource DATA_SOURCE = DatabaseConfig.getDataSource();

    /**.
     * Создает и возвращает экземпляр Javalin
     *
     * @return Экземпляр Javalin
     */
    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get("/", ctx -> ctx.render("index.jte"));

        return app;
    }

    /**.
     * Точка входа в приложение
     *
     * @param args Аргументы командной строки
     */
    public static void main(String[] args) {
        initializeDatabase();
        Javalin app = getApp();
        app.start(getPort());
        LOGGER.info("Application started on port " + getPort());
    }

    /**.
     * Инициализирует базу данных, выполняя SQL-скрипт из файла schema.sql
     */
    private static void initializeDatabase() {
        try (Connection conn = DATA_SOURCE.getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = new String(App.class.getResourceAsStream("/schema.sql").readAllBytes());
            stmt.execute(sql);
        } catch (SQLException | IOException e) {
            LOGGER.error("Error initializing the database", e);
        }
    }

    /**.
     * Получает номер порта из переменной окружения или возвращает значение по умолчанию (8080)
     *
     * @return Номер порта
     */
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "8080");
        return Integer.parseInt(port);
    }

    /**.
     * Создает и настраивает движок шаблонизатора JTE
     *
     * @return Экземпляр TemplateEngine
     */
    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}
