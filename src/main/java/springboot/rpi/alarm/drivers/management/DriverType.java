package springboot.rpi.alarm.drivers.management;

/**
 * Enum representing available driver types.
 */
public enum DriverType {
    /**
     * Pin driver
     */
    LED_ON_PIN,
    KEYPAD_ON_PINS,
    ACTIVE_BUZZER_ON_PIN,
    PIR_ON_PIN
}
