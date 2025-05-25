package springboot.rpi.alarm.drivers.management;

/**
 * Supported driver commands list.
 */
public enum DriverCommand {
    /**
     * Standard read operation.
     */
    READ,
    /**
     * Standard write operation.
     */
    WRITE,
    /**
     * Turn pin ON.
     */
    ON,
    /**
     * Turn pin OFF.
     */
    OFF,
    /**
     * 200ms pulse async.
     */
    PULSE_ASYNC_200_MS,
    /**
     * 200ms pulse custom duty cycle implementation.
     */
    PULSE_100_MS_DUTY_1,
    PULSE_200_MS_DUTY_2;
}
