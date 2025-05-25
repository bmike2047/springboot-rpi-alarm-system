package springboot.rpi.alarm.drivers;

import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import springboot.rpi.alarm.drivers.management.DriverCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class LedDriverTest extends TestConfig {
    private LedDriver ledDriver;
    private MockDigitalOutput led;

    @BeforeEach
    void initConfig() {
        ledDriver = new LedDriver(pi4j,27);
        led = (MockDigitalOutput) ledDriver.getLed();
    }

    @Test
    void testLedCommandOn() {
        ledDriver.execute(DriverCommand.ON);
        assertEquals(DigitalState.HIGH, led.state());
    }

    @Test
    void testLedCommandOff() {
        ledDriver.execute(DriverCommand.OFF);
        assertEquals(DigitalState.LOW, led.state());
    }

}
