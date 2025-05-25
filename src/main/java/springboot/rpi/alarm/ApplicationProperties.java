package springboot.rpi.alarm;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;


/**
 * Application Properties.
 */
@Configuration
@ConfigurationProperties
@Validated
public class ApplicationProperties {

    @NotNull
    private Integer defaultPin;

    @NotNull
    private Integer defaultCountdownDuration;

    @NotNull
    private String defaultPassword;

    @NotNull
    private String defaultUser;

    public @NotNull Integer getDefaultPin() {
        return defaultPin;
    }

    public void setDefaultPin(@NotNull Integer defaultPin) {
        this.defaultPin = defaultPin;
    }

    public @NotNull Integer getDefaultCountdownDuration() {
        return defaultCountdownDuration;
    }

    public void setDefaultCountdownDuration(@NotNull Integer defaultCountdownDuration) {
        this.defaultCountdownDuration = defaultCountdownDuration;
    }

    public @NotNull String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(@NotNull String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public @NotNull String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(@NotNull String defaultUser) {
        this.defaultUser = defaultUser;
    }
}
