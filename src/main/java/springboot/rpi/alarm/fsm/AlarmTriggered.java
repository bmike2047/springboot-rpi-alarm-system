package springboot.rpi.alarm.fsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springboot.rpi.alarm.drivers.management.DriverCommand;
import springboot.rpi.alarm.drivers.management.DriverManager;
import springboot.rpi.alarm.drivers.management.DriverType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This state is responsible for the alarm siren triggering.
 * It runs in its own thread.
 */
public class AlarmTriggered extends StateWorker {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmTriggered.class);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AlarmTriggered(Hub hub) {
        super(hub);
    }

    /**
     * Stop this state thread.
     */
    @Override
    public void stop() {
        if (hub.getManager().driverExist("led-red") && hub.getManager().driverExist("buzzer")) {
            hub.getManager().getDriver("led-red").execute(DriverCommand.OFF);
            hub.getManager().getDriver("buzzer").execute(DriverCommand.OFF);
        }
        super.stop();
    }

    /**
     * Start this state thread.
     */
    @Override
    public void start() {
        hub.getManager().registerDriver("buzzer", DriverType.ACTIVE_BUZZER_ON_PIN, 26);
        super.start();
    }

    /**
     * Perform main logic.
     */
    public void doWork() {
        soundAlarm(hub.getManager());
    }


    private void soundAlarm(DriverManager manager) {
        if (hub.getManager().driverExist("led-red") && hub.getManager().driverExist("buzzer")) {
            manager.getDriver("led-red").execute(DriverCommand.PULSE_200_MS_DUTY_2);
            manager.getDriver("buzzer").execute(DriverCommand.PULSE_200_MS_DUTY_2);
            LOG.info("ALARM BEEP {}", LocalDateTime.now().format(formatter));
        } else {
            LOG.error("Unable sound alarm as needed drivers are not found. Waiting");
            sleep(1000);
        }
    }
}
