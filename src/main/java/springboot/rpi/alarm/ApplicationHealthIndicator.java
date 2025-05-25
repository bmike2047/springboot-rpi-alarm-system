package springboot.rpi.alarm;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Application Health indicator.
 */
@Component
public class ApplicationHealthIndicator implements HealthIndicator {

    private boolean success = true;
    private String details;

    /**
     * Mark as healthy.
     */
    public void success() {
        this.success = true;
    }

    /**
     * Mark as unhealthy.
     *
     * @param details String
     */
    public void error(final String details) {
        this.details = details;
        this.success = false;
    }

    /**
     * Check if healthy.
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Health check logic.
     *
     * @return Health
     */
    @Override
    public Health health() {
        if (!success) {
            return Health.down()
                    .withDetail("message", details).build();
        }
        return Health.up().build();
    }

}
