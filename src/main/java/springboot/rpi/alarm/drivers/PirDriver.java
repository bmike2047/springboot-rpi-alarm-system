package springboot.rpi.alarm.drivers;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import com.pi4j.io.gpio.digital.PullResistance;
import springboot.rpi.alarm.drivers.management.DriverBase;

/**
 * Simple configurable PIR driver.
 */
public class PirDriver extends DriverBase {

    private final DigitalInput pir;

    /**
     * Configurable pin constructor.
     *
     * @param pi4j     RPi context
     * @param listener listener callback method (interrupt handle)
     * @param pin      BCM pin
     */
    public PirDriver(Context pi4j, DigitalStateChangeListener listener, int pin) {
        super(pi4j);
        pir = setupInput(pin, PullResistance.PULL_DOWN, listener);
    }

    /**
     * Helper.
     *
     * @return DigitalOutput
     */
    DigitalInput getPir() {
        return pir;
    }

}
