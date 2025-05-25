package springboot.rpi.alarm.drivers;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import springboot.rpi.alarm.drivers.management.DriverCommand;
import springboot.rpi.alarm.drivers.management.DriverWorker;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simple configurable 4x4 keypad or switch matrix driver.
 * Includes a state debounce algorithm.
 * Runs in its own separate thread.
 */
public class KeypadDriver extends DriverWorker {

    private static final int DEFAULT_SCAN_PERIOD_MS = 80;

    private static final char[] SYMBOLS = "123A456B789C*0#D".toCharArray();

    private final DigitalOutput[] rowsOut;

    private final DigitalInput[] colsIn;

    private final AtomicBoolean[] debounceStates = new AtomicBoolean[SYMBOLS.length];

    private final BlockingQueue<Character> pressedKey = new ArrayBlockingQueue<>(1);

    /**
     * Configurable pins constructor.
     *
     * @param pi4j     RPi context
     * @param rowsPins rows custom pins
     * @param colsPins cols custom pins
     */
    public KeypadDriver(Context pi4j, int[] rowsPins, int[] colsPins) {
        super(pi4j);
        rowsOut = setupKeypadOutputs(rowsPins);
        colsIn = setupKeypadInputs(colsPins);
        setupDebounceStates();
    }

    private void setupDebounceStates() {
        for (int i = 0; i < debounceStates.length; i++) {
            debounceStates[i] = new AtomicBoolean(false);
        }
    }


    private DigitalInput[] setupKeypadInputs(int[] pins) {
        DigitalInput[] colsIn = new DigitalInput[pins.length];
        for (int i = 0; i < pins.length; i++) {
            colsIn[i] = setupInput(pins[i], PullResistance.PULL_DOWN, null);
        }
        return colsIn;
    }


    private DigitalOutput[] setupKeypadOutputs(int[] pins) {
        DigitalOutput[] rowsOut = new DigitalOutput[pins.length];
        for (int i = 0; i < pins.length; i++) {
            rowsOut[i] = setupOutput(pins[i]);
        }
        return rowsOut;
    }

    /**
     * Worker entry point.
     * This will loop scanning the keypad until interrupted.
     */
    @Override
    public void doWork() throws InterruptedException {
        var key = readFromOffset(0, 0);
        if (key != 0) {
            pressedKey.put(key);
        }
        TimeUnit.MILLISECONDS.sleep(DEFAULT_SCAN_PERIOD_MS);
    }

    @Override
    public synchronized int execute(DriverCommand driverCommand, Object... extraParams) {
        /*
         * Read a pressed key.
         * This is a blocking operation.
         */
        if (DriverCommand.READ.equals(driverCommand)) {
            try {
                return pressedKey.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return 0;
    }

    /**
     * Main keyboard scan algorithm.
     *
     * @param iOffset helper, skip rows if necessary
     * @param jOffset helper, skip cols if necessary
     * @return passed char
     */
    Character readFromOffset(int iOffset, int jOffset) {
        char val = 0;
        for (int i = iOffset; i < rowsOut.length; i++) {
            rowsOut[i].high();
            for (int j = jOffset; j < colsIn.length; j++) {
                var symbolsIndex = i * colsIn.length + j;
                //start debounce logic
                var currentState = colsIn[j].state() == DigitalState.HIGH;
                var previousState = debounceStates[symbolsIndex].getAndSet(currentState);
                if (previousState == currentState) {
                    continue;
                }
                //end debounce logic
                if (currentState) {
                    val = SYMBOLS[symbolsIndex];
                    rowsOut[i].low();
                    return val;
                }
            }
            rowsOut[i].low();
        }
        return val;
    }

    /**
     * Helper.
     *
     * @return DigitalInput array
     */
    DigitalInput[] getColsIn() {
        return colsIn;
    }

}
