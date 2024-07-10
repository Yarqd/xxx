package hexlet.code.repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**.
 * Базовый репозиторий для работы с базой данных
 */
public abstract class BaseRepository {

    private final DataSource dataSource;

    public BaseRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**.
     * Получает соединение с базой данных
     * @return соединение с базой данных
     * @throws SQLException если не удается получить соединение
     */
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
