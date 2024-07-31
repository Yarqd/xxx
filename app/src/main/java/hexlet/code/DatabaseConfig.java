package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {

    private static final String H2_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    private static final String H2_USERNAME = "sa";
    private static final String H2_PASSWORD = "";

    private static final String POSTGRES_URL = System.getenv("JDBC_DATABASE_URL");
    private static final String POSTGRES_USERNAME = System.getenv("DB_USER");
    private static final String POSTGRES_PASSWORD = System.getenv("DB_PASSWORD");

    public static DataSource getDataSource() {
        return configureDataSource(POSTGRES_URL, POSTGRES_USERNAME, POSTGRES_PASSWORD);
    }

    public static DataSource getTestDataSource() {
        return configureDataSource(H2_URL, H2_USERNAME, H2_PASSWORD);
    }

    private static DataSource configureDataSource(String url, String username, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        return new HikariDataSource(config);
    }
}
