package springboot.rpi.alarm.drivers;

import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;

public class PirDriverTest extends TestConfig {
    private PirDriver pirDriver;
    private MockDigitalInput pir;
    private String triggerMessage;

    @BeforeEach
    void initConfig() {
        pirDriver = new PirDriver(pi4j, (event -> {
            if (event.state().equals(DigitalState.HIGH)) {
                triggerMessage = "I detected something";
            }
        }),13);
        pir = (MockDigitalInput) pirDriver.getPir();
    }

    @Test
    void testPirDriverTrigger() {
        triggerMessage = "No one here";
        pir.mockState(DigitalState.LOW);
        assertEquals("No one here", triggerMessage);

        pir.mockState(DigitalState.HIGH);
        assertEquals("I detected something", triggerMessage);
    }

}
