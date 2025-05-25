package springboot.rpi.alarm.drivers.management;

import com.pi4j.context.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import springboot.rpi.alarm.drivers.management.DriverWorker;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


public class DriverWorkerTest {
    static class Driver extends DriverWorker {

        public Driver(Context pi4j) {
            super(pi4j);
        }

        @Override
        public void doWork() {
            stop();
        }
    }

    private Driver driver;

    @BeforeEach
    void setupWorker() {
        driver = spy(new Driver(null));
    }

    @Test
    void testWorkerStartsAndDoesSomeWorkAndStops() throws InterruptedException {
        driver.start();
        driver.join();
        verify(driver).doWork();
        verify(driver).stop();
    }

}
