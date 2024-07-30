package hexlet.code.model;

import lombok.Data;

import java.sql.Timestamp;

/**
 * Модель URL.
 */
@Data
public final class Url {
    private long id;
    private String name;
    private Timestamp createdAt;
}
