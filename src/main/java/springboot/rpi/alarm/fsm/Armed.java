package springboot.rpi.alarm.fsm;

import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springboot.rpi.alarm.drivers.management.DriverManager;
import springboot.rpi.alarm.drivers.management.DriverType;

/**
 * This state is responsible for the PIR detection.
 */
public class Armed extends StateWorker {
    private static final Logger LOG = LoggerFactory.getLogger(Armed.class);

    public Armed(Hub hub) {
        super(hub);
    }

    /**
     * Setup DigitalStateChangeListener (interrupt handle).
     */
    @Override
    public void start() {
        try {
            hub.getManager().registerDriver("main-pir", DriverType.PIR_ON_PIN,
                    getDigitalStateChangeListener(hub.getManager()), 13);

            //Since this registers an interrupt handle there is no need to start it as a thread.
        } catch (Exception e) {
            LOG.error("Error registering driver: ", e);
        }
    }

    private DigitalStateChangeListener getDigitalStateChangeListener(DriverManager manager) {
        return (event) -> {
            if (event.state() == DigitalState.HIGH && hub.getCurrentState() instanceof Armed) {
                hub.changeState();
            }
        };
    }

    @Override
    public void resume() {
        //NOOP
    }

    @Override
    public void pause() {
        //NOOP
    }

}
