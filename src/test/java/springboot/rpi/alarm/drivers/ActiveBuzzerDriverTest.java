package springboot.rpi.alarm.drivers;

import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import springboot.rpi.alarm.drivers.management.DriverCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ActiveBuzzerDriverTest extends TestConfig {
    private ActiveBuzzerDriver buzzerDriver;
    private MockDigitalOutput buzzer;

    @BeforeEach
    void initConfig() {
        buzzerDriver = new ActiveBuzzerDriver(pi4j,26);
        buzzer = (MockDigitalOutput) buzzerDriver.getBuzzer();
    }

    @Test
    void testLedCommandOn() {
        buzzerDriver.execute(DriverCommand.ON);
        assertEquals(DigitalState.HIGH, buzzer.state());
    }

    @Test
    void testLedCommandOff() {
        buzzerDriver.execute(DriverCommand.OFF);
        assertEquals(DigitalState.LOW, buzzer.state());
    }

}
