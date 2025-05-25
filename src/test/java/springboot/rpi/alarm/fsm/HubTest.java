package springboot.rpi.alarm.fsm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import springboot.rpi.alarm.ApplicationProperties;
import springboot.rpi.alarm.config.AlarmConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SpringBootTest
@Import({ApplicationProperties.class, AlarmConfig.class})
public class HubTest {

    @Autowired
    private Hub hub;

    @Test
    @DirtiesContext
    void testHubSetup() {
        assertEquals(5, hub.getCountdownDuration());
        assertEquals(1234, hub.getPin());
    }

    @Test
    @DirtiesContext
    void testArm() {
        hub.arm();
        waitForHubStatus(Countdown.class);
        assertInstanceOf(Countdown.class, hub.getCurrentState());
    }

    @Test
    @DirtiesContext
    void testPanicAndDisarm() {
        assertInstanceOf(Disarmed.class, hub.getCurrentState());

        hub.panic();
        waitForHubStatus(AlarmTriggered.class);
        assertInstanceOf(AlarmTriggered.class, hub.getCurrentState());

        hub.disarm();
        waitForHubStatus(Disarmed.class);
        assertInstanceOf(Disarmed.class, hub.getCurrentState());

    }

    @Test
    @DirtiesContext
    void testChangeState() {
        assertInstanceOf(Disarmed.class, hub.getCurrentState());

        hub.changeState();
        hub.changeState();

        assertInstanceOf(Armed.class, hub.getCurrentState());

    }

    @Test
    void testChangePin() {
        assertEquals(1234, hub.getPin());
        hub.changePin("5555");
        assertEquals(5555, hub.getPin());
    }

    private void waitForHubStatus(Object status) {
        int i = 0;
        while (!status.getClass().isInstance(status) && (i < 200)) {
            i++;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
