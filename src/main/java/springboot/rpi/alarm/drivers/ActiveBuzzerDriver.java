package springboot.rpi.alarm.drivers;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalOutput;
import springboot.rpi.alarm.drivers.management.DriverBase;
import springboot.rpi.alarm.drivers.management.DriverCommand;
import springboot.rpi.alarm.drivers.management.OutCommandsExecutor;

/**
 * Simple configurable active buzzer driver.
 */
public class ActiveBuzzerDriver extends DriverBase {

    private final DigitalOutput buzzer;

    /**
     * Configurable pin constructor.
     *
     * @param pi4j RPi context
     * @param pin  BCM pin
     */
    public ActiveBuzzerDriver(Context pi4j, int pin) {
        super(pi4j);
        buzzer = setupOutput(pin);
    }

    @Override
    public synchronized int execute(DriverCommand driverCommand, Object... extraParams) {
        return OutCommandsExecutor.execute(buzzer, driverCommand);
    }

    /**
     * Helper.
     *
     * @return DigitalOutput
     */
    DigitalOutput getBuzzer() {
        return buzzer;
    }

}
