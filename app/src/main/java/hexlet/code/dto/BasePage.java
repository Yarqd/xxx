package hexlet.code.dto;

public class BasePage {
    private String flash;
    private String flashType;

    public BasePage(String flash, String flashType) {
        this.flash = flash;
        this.flashType = flashType;
    }

    public String getFlash() {
        return flash;
    }

    public void setFlash(String flash) {
        this.flash = flash;
    }

    public String getFlashType() {
        return flashType;
    }

    public void setFlashType(String flashType) {
        this.flashType = flashType;
    }
}
