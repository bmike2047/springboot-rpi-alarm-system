package springboot.rpi.alarm.drivers.management;

import com.pi4j.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * This class provides services for managing RPi drivers.
 * It supports registering multiple drivers of the same type as they can be mapped on different BCM pins.
 */
public class DriverManager {
    private static final Logger LOG = LoggerFactory.getLogger(DriverManager.class);

    private final HashMap<String, DriverBase> registeredDrivers = new HashMap<>();
    private final DriverFactory driverFactory;
    private final boolean knownBoard;

    /**
     * Default constructor.
     *
     * @param pi4j RPi context
     */
    public DriverManager(Context pi4j) {
        this.driverFactory = new DriverFactory(pi4j);
        this.knownBoard = !"Unknown".equals(pi4j.boardInfo().getBoardModel().getLabel());
    }

    /**
     * Add and initialize driver to memory.
     *
     * @param id   driver unique id
     * @param type driver type
     * @param pin  BCM pin
     */
    public void registerDriver(String id, DriverType type, Object... pin) {
        if (knownBoard) {
            DriverBase driverBase = driverFactory.getDriverFor(type, pin);
            registeredDrivers.putIfAbsent(id, driverBase);
            driverBase.start();
        } else {
            LOG.error("No known board found, cannot register driver {}", id);
        }
    }

    /**
     * Unregister and shutdown all drivers.
     */
    public void unregisterAll() {
        registeredDrivers.values().forEach(DriverBase::stop);
    }

    /**
     * Get driver from memory.
     *
     * @param id driver id
     * @return driver
     */
    public DriverBase getDriver(String id) {
        return registeredDrivers.get(id);
    }

    /**
     * Check if the driver exists.
     *
     * @param id driver id
     * @return boolean
     */
    public boolean driverExist(String id) {
        return registeredDrivers.get(id) != null;
    }

    /**
     * Check if RPi board is present.
     *
     * @return boolean
     */
    public boolean isKnownBoard() {
        return knownBoard;
    }
}
