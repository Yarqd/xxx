package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@AllArgsConstructor // Генерирует конструктор с параметрами для всех полей
@NoArgsConstructor // Генерирует пустой конструктор
public final class UrlCheckDto { // Сделаем класс final, чтобы он не мог быть наследован
    private long id;
    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private long urlId;
    private String createdAt;
}
