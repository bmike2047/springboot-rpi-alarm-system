package springboot.rpi.alarm.fsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springboot.rpi.alarm.drivers.management.DriverCommand;
import springboot.rpi.alarm.drivers.management.DriverType;

/**
 * This class is responsible for the keypad interface and logic.
 * It runs in its own thread independently as a StateWorker but is not managed by the Finite State Machine.
 */
public class Keypad extends StateWorker {
    private static final Logger LOG = LoggerFactory.getLogger(Keypad.class);

    private String keypadCommand = "";

    public Keypad(Hub hub) {
        super(hub);
    }

    /**
     * Stop this thread.
     */
    @Override
    public void stop() {
        super.stop();
    }

    /**
     * Start this thread.
     */
    @Override
    public void start() {
        hub.getManager().registerDriver("main-keypad", DriverType.KEYPAD_ON_PINS,
                new int[]{18, 23, 24, 25},
                new int[]{12, 16, 20, 21});
        super.start();
    }

    /**
     * Perform main logic.
     */
    @Override
    public void doWork() {
        if (hub.getManager().driverExist("main-keypad") && hub.getManager().driverExist("led-yellow")) {
            char key = (char) hub.getManager().getDriver("main-keypad").execute(DriverCommand.READ);
            LOG.info("Pressed key: {}", key);
            hub.getManager().getDriver("led-yellow").execute(DriverCommand.PULSE_ASYNC_200_MS);
            keypadCommand = keypadCommand + key;
            keypadCommand = rollover(keypadCommand);
            if (keypadCommand.contains("D" + hub.getPin())) {
                hub.disarm();
                keypadCommand = "";
                LOG.info("Disarm success");
            } else if (keypadCommand.contains("A")) {
                hub.arm();
                keypadCommand = "";
                LOG.info("Arming and starting countdown");
            }
        } else {
            LOG.error("Unable to read keyboard as needed drivers are not found. Waiting");
            pause();
        }
    }


    private static String rollover(String str) {
        if (str.length() > 5) {
            return str.substring(str.length() - 5);
        }
        return str;
    }
}
