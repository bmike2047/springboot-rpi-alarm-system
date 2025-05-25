package springboot.rpi.alarm.drivers.management;

import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;

import java.util.concurrent.TimeUnit;

/**
 * Execute a driver command.
 */
public class OutCommandsExecutor {
    /**
     * Execute a command on a digital output.
     *
     * @param digitalOutput Digital Output.
     * @param driverCommand driver command.
     * @return
     */
    public static int execute(DigitalOutput digitalOutput, DriverCommand driverCommand) {
        switch (driverCommand) {
            case ON -> digitalOutput.on();
            case OFF -> digitalOutput.off();
            case PULSE_ASYNC_200_MS -> digitalOutput.pulseAsync(200, TimeUnit.MILLISECONDS);
            case PULSE_100_MS_DUTY_1 -> {
                digitalOutput.state(DigitalState.LOW);
                digitalOutput.pulseHigh(50, TimeUnit.MILLISECONDS);
                digitalOutput.pulseLow(50, TimeUnit.MILLISECONDS);
                digitalOutput.state(DigitalState.LOW);
            }
            case PULSE_200_MS_DUTY_2 -> {
                digitalOutput.state(DigitalState.LOW);
                digitalOutput.pulseHigh(150, TimeUnit.MILLISECONDS);
                digitalOutput.pulseLow(50, TimeUnit.MILLISECONDS);
                digitalOutput.state(DigitalState.LOW);
            }
            default -> throw new UnsupportedOperationException("Command: " + driverCommand + " not found");
        }
        return 0; //default return value at this point
    }


}
