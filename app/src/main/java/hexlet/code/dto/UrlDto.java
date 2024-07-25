package hexlet.code.dto;

public class UrlDto {
    private long id;
    private String name;
    private String lastCheckedAt;
    private Integer statusCode;

    public UrlDto(long id, String name, String lastCheckedAt, Integer statusCode) {
        this.id = id;
        this.name = name;
        this.lastCheckedAt = lastCheckedAt;
        this.statusCode = statusCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastCheckedAt() {
        return lastCheckedAt;
    }

    public void setLastCheckedAt(String lastCheckedAt) {
        this.lastCheckedAt = lastCheckedAt;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
