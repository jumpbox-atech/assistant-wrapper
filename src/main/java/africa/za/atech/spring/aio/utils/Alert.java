package africa.za.atech.spring.aio.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class Alert {

    private String message;
    private AlertType alertType;

    public Alert build(AlertType type, String message) {
        this.alertType = type;
        this.message = message;
        return this;
    }

    @Getter
    @AllArgsConstructor
    public enum AlertType {

        INFO("alert-info"),
        DANGER("alert-danger"),
        SUCCESS("alert-success"),
        WARNING("alert-warning");

        private final String value;
    }
}
