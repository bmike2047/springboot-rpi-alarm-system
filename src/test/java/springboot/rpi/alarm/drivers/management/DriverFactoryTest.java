package springboot.rpi.alarm.drivers.management;

import com.pi4j.io.gpio.digital.DigitalStateChangeListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import springboot.rpi.alarm.drivers.TestConfig;
import springboot.rpi.alarm.drivers.ActiveBuzzerDriver;
import springboot.rpi.alarm.drivers.KeypadDriver;
import springboot.rpi.alarm.drivers.LedDriver;
import springboot.rpi.alarm.drivers.PirDriver;


import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class DriverFactoryTest extends TestConfig {
    private DriverFactory driverFactory;

    @BeforeEach
    void initConfig() {
        driverFactory = new DriverFactory(pi4j);
    }

    @Test
    void testInstances() {
        DigitalStateChangeListener listener = (e)->{};
        assertInstanceOf(LedDriver.class, driverFactory.getDriverFor(DriverType.LED_ON_PIN,1));
        assertInstanceOf(ActiveBuzzerDriver.class, driverFactory.getDriverFor(DriverType.ACTIVE_BUZZER_ON_PIN,2));
        assertInstanceOf(KeypadDriver.class, driverFactory.getDriverFor(DriverType.KEYPAD_ON_PINS,new int[]{3},new int[]{4}));
        assertInstanceOf(PirDriver.class, driverFactory.getDriverFor(DriverType.PIR_ON_PIN,listener,5));
    }
}
