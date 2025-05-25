package springboot.rpi.alarm.drivers;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import springboot.rpi.alarm.drivers.management.DriverBase;
import springboot.rpi.alarm.drivers.management.DriverCommand;
import springboot.rpi.alarm.drivers.management.OutCommandsExecutor;

/**
 * Simple configurable LED driver.
 */
public class LedDriver extends DriverBase {

    private final DigitalOutput led;

    /**
     * Configurable pin constructor.
     *
     * @param pi4j RPi context
     * @param pin  BCM pin
     */
    public LedDriver(Context pi4j, int pin) {
        super(pi4j);
        led = setupOutput(pin);
    }

    @Override
    public synchronized int execute(DriverCommand driverCommand, Object... extraParams) {
        return OutCommandsExecutor.execute(led, driverCommand);
    }

    /**
     * Helper.
     *
     * @return DigitalOutput
     */
    DigitalOutput getLed() {
        return led;
    }

}
