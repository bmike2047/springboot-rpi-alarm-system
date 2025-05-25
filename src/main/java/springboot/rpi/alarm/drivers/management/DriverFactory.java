package springboot.rpi.alarm.drivers.management;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import springboot.rpi.alarm.drivers.ActiveBuzzerDriver;
import springboot.rpi.alarm.drivers.KeypadDriver;
import springboot.rpi.alarm.drivers.LedDriver;
import springboot.rpi.alarm.drivers.PirDriver;

/**
 * Factory for creating drivers.
 */
public class DriverFactory {
    private final Context pi4j;

    public DriverFactory(Context pi4j) {
        this.pi4j = pi4j;
    }

    /**
     * Factory method with variable constructor arguments.
     *
     * @param type driver types
     * @param args variable arguments
     * @return driver
     */
    public DriverBase getDriverFor(DriverType type, Object... args) {
        return switch (type) {
            case LED_ON_PIN -> new LedDriver(pi4j, (int) args[0]);
            case KEYPAD_ON_PINS -> new KeypadDriver(pi4j, (int[]) args[0], (int[]) args[1]);
            case ACTIVE_BUZZER_ON_PIN -> new ActiveBuzzerDriver(pi4j, (int) args[0]);
            case PIR_ON_PIN -> new PirDriver(pi4j, (DigitalStateChangeListener) args[0], (int) args[1]);
        };
    }
}
