package springboot.rpi.alarm.fsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springboot.rpi.alarm.drivers.management.DriverCommand;

/**
 * This state is responsible for the countdown buzzer sound for N seconds.
 * It runs in its own thread.
 */
public class Countdown extends StateWorker {
    private static final Logger LOG = LoggerFactory.getLogger(Countdown.class);

    public Countdown(Hub hub) {
        super(hub);
    }

    /**
     * Stop this state thread.
     */
    @Override
    public void stop() {
        if (hub.getManager().driverExist("led-yellow") && hub.getManager().driverExist("buzzer")) {
            hub.getManager().getDriver("led-yellow").execute(DriverCommand.OFF);
            hub.getManager().getDriver("buzzer").execute(DriverCommand.OFF);
        }
        super.stop();
    }

    /**
     * Start this state thread.
     */
    @Override
    public void start() {
        super.start();
    }

    /**
     * Perform main logic.
     */
    @Override
    public void doWork() {
        if (hub.getManager().driverExist("led-yellow") && hub.getManager().driverExist("buzzer")) {
            countdown();
        } else {
            LOG.error("Unable to arm as needed drivers are not found");
            sleep(hub.getCountdownDuration() * 1000L);
        }
        hub.changeState();
    }


    private void countdown() {
        for (int i = 0; i < hub.getCountdownDuration(); i++) {
            hub.getManager().getDriver("led-yellow").execute(DriverCommand.PULSE_200_MS_DUTY_2);
            hub.getManager().getDriver("buzzer").execute(DriverCommand.PULSE_100_MS_DUTY_1);
            hub.getManager().getDriver("buzzer").execute(DriverCommand.PULSE_100_MS_DUTY_1);
            sleep(600);
            LOG.info("T-minus {}", hub.getCountdownDuration() - i);
        }
        LOG.info("Arm success");
    }
}
