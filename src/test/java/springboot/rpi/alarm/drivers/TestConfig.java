package springboot.rpi.alarm.drivers;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.plugin.mock.platform.MockPlatform;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalInputProvider;
import com.pi4j.plugin.mock.provider.gpio.digital.MockDigitalOutputProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class TestConfig {
    protected Context pi4j;

    @BeforeEach
    public void initContext() {
        pi4j = Pi4J.newContextBuilder()
                .add(new MockPlatform())
                .add(MockDigitalInputProvider.newInstance(), MockDigitalOutputProvider.newInstance())
                .build();
    }

    @AfterEach
    public void shutdown() {
        pi4j.shutdown();
    }
}
