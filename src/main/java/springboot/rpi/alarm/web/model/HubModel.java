package springboot.rpi.alarm.web.model;


import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Hub model with validation rules.
 */
public class HubModel {
    @NotEmpty(message = "Field cannot be empty")
    @Digits(integer = 4, fraction = 0, message = "Field must contain 4 digits only")
    private String pin;

    public @NotNull String getPin() {
        return pin;
    }

    public void setPin(@NotNull String pin) {
        this.pin = pin;
    }

}
