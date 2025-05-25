package springboot.rpi.alarm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springboot.rpi.alarm.ApplicationProperties;
import springboot.rpi.alarm.fsm.Hub;

/**
 * Main application configuration.
 */
@Configuration
public class AlarmConfig {
    @Bean
    public Hub alarmHub(ApplicationProperties applicationProperties) {
        return new Hub(applicationProperties);
    }
}
