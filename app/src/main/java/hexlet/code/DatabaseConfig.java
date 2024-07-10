package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig {

    private static final String H2_URL = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1";
    private static final String H2_USERNAME = "sa";
    private static final String H2_PASSWORD = "";

    public static DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        String dbUrl = System.getenv().getOrDefault("JDBC_DATABASE_URL", H2_URL);

        config.setJdbcUrl(dbUrl);

        if (dbUrl.equals(H2_URL)) {
            config.setUsername(H2_USERNAME);
            config.setPassword(H2_PASSWORD);
        }

        return new HikariDataSource(config);
    }
}
