package springboot.rpi.alarm.drivers;

import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import springboot.rpi.alarm.drivers.management.DriverCommand;


import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KeypadDriverTest extends TestConfig {
    private KeypadDriver keypadDriver;
    private MockDigitalInput[] colsIn;

    @BeforeEach
    void initConfig() {
        keypadDriver = new KeypadDriver(pi4j,
                new int[]{18, 23, 24, 25},
                new int[]{12, 16, 20, 21});
        colsIn = Arrays.stream(keypadDriver.getColsIn()).toArray(MockDigitalInput[]::new);
    }

    @Test
    void testNoKeyPressed() throws InterruptedException {
        Thread keypadRobot = new Thread(() -> {
            keypadDriver.execute(DriverCommand.READ);
        });
        keypadRobot.start();
        TimeUnit.MILLISECONDS.sleep(500);
        assertEquals(Thread.State.WAITING, keypadRobot.getState());
        keypadRobot.interrupt();
    }

    @Test
    void testPressedKeyA() {
        colsIn[3].mockState(DigitalState.HIGH);
        assertEquals('A', keypadDriver.readFromOffset(0, 3));
    }

    @Test
    void testPressedKey9() {
        colsIn[2].mockState(DigitalState.HIGH);
        assertEquals('9', keypadDriver.readFromOffset(2, 0));
    }

    @Test
    void testPressedKey0() {
        colsIn[1].mockState(DigitalState.HIGH);
        assertEquals('0', keypadDriver.readFromOffset(3, 0));
    }
}
