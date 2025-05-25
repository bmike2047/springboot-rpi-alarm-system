package springboot.rpi.alarm.drivers.management;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import com.pi4j.io.gpio.digital.PullResistance;

/**
 * Generic RPi driver with support for threading.
 */
public class DriverBase {
    protected final Context pi4j;

    /**
     * RPi context to be used later.
     *
     * @param pi4j Context
     */
    protected DriverBase(Context pi4j) {
        this.pi4j = pi4j;
    }

    /**
     * Start this driver.
     */
    public void start() {
    }

    /**
     * Stop this driver.
     */
    public void stop() {
    }

    /**
     * Send a command to the lower I/O provider.
     *
     * @param driverCommand command to send
     * @param extraParams   extra params for custom commands
     * @return command result
     */
    public int execute(DriverCommand driverCommand, Object... extraParams) {
        return 0;
    }

    /**
     * Setup specific pin as output.
     *
     * @param pin pin BCM pin
     * @return DigitalOutput
     */
    protected DigitalOutput setupOutput(int pin) {
        var cfg = DigitalOutput.newConfigBuilder(pi4j)
                .id("BCM" + pin)
                .name("BCM out:" + pin)
                .address(pin)
                .build();
        return pi4j.create(cfg);
    }

    /**
     * Setup specific pin as input.
     *
     * @param pin        BCM pin
     * @param resistance pull type
     * @param listener   listener callback method (AKA interrupt handle)
     * @return DigitalInput
     */
    protected DigitalInput setupInput(int pin, PullResistance resistance, DigitalStateChangeListener listener) {
        var cfg = DigitalInput.newConfigBuilder(pi4j)
                .id("BCM" + pin)
                .name("BCM in:" + pin)
                .address(pin)
                .pull(resistance)
                .build();
        DigitalInput digitalInput = pi4j.create(cfg);
        if (listener != null) {
            digitalInput.addListener(listener);
        }
        return digitalInput;
    }

}
