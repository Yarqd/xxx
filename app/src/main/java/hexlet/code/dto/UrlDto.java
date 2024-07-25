package hexlet.code.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Генерирует геттеры, сеттеры, equals, hashCode и toString
@AllArgsConstructor // Генерирует конструктор с параметрами для всех полей
@NoArgsConstructor // Генерирует пустой конструктор
public final class UrlDto { // Сделаем класс final, чтобы он не мог быть наследован
    private long id;
    private String name;
    private String lastCheckedAt;
    private Integer statusCode;
}
