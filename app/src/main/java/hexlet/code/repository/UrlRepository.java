package hexlet.code.repository;

import hexlet.code.model.Url;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для работы с URL.
 */
public final class UrlRepository extends BaseRepository {

    public UrlRepository(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Сохраняет URL в базе данных.
     *
     * @param url объект Url для сохранения.
     * @throws SQLException если возникает ошибка при доступе к базе данных.
     */
    public void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getName());
            stmt.setTimestamp(2, url.getCreatedAt());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    url.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    /**
     * Возвращает список всех URL из базы данных.
     *
     * @return список объектов Url.
     * @throws SQLException если возникает ошибка при доступе к базе данных.
     */
    public List<Url> findAll() throws SQLException {
        List<Url> urls = new ArrayList<>();
        String sql = "SELECT id, name FROM urls";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Url url = new Url();
                url.setId(rs.getLong("id"));
                url.setName(rs.getString("name"));
                urls.add(url);
            }
        }
        return urls;
    }

    /**
     * Ищет URL по его идентификатору.
     *
     * @param id идентификатор URL.
     * @return объект Url или null, если URL не найден.
     * @throws SQLException если возникает ошибка при доступе к базе данных.
     */
    public Url findById(long id) throws SQLException {
        String sql = "SELECT id, name FROM urls WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Url url = new Url();
                    url.setId(rs.getLong("id"));
                    url.setName(rs.getString("name"));
                    return url;
                }
            }
        }
        return null;
    }

    /**
     * Проверяет, существует ли URL с заданным именем в базе данных.
     *
     * @param name имя URL.
     * @return true, если URL существует, иначе false.
     * @throws SQLException если возникает ошибка при доступе к базе данных.
     */
    public boolean existsByName(String name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM urls WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public String getUrlById(long id) throws SQLException {
        String sql = "SELECT name FROM urls WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return null;
    }
}
