package springboot.rpi.alarm.fsm;

import com.pi4j.Pi4J;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.context.Context;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springboot.rpi.alarm.ApplicationProperties;
import springboot.rpi.alarm.drivers.management.DriverCommand;
import springboot.rpi.alarm.drivers.management.DriverManager;
import springboot.rpi.alarm.drivers.management.DriverType;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is a hub, a Finite State Machine that controls the behaviour of the alarm system.
 * This class is safely shared between multiple threads.
 */
public class Hub {
    private static final Logger LOG = LoggerFactory.getLogger(Hub.class);

    private final AtomicInteger pin;
    private final AtomicInteger countdownDuration;
    private Context pi4j;
    private DriverManager manager;

    private StateWorker currentState;
    private Iterator<StateWorker> statesIterator;
    private List<StateWorker> states;

    /**
     * Main constructor.
     *
     * @param applicationProperties ApplicationProperties
     */
    public Hub(ApplicationProperties applicationProperties) {
        this.pin = new AtomicInteger(applicationProperties.getDefaultPin());
        this.countdownDuration = new AtomicInteger(applicationProperties.getDefaultCountdownDuration());
    }

    @PostConstruct
    private void setupComponents() {
        setupPi4j();

        states = Arrays.asList(
                new Countdown(this),
                new Armed(this),
                new AlarmTriggered(this),
                new Disarmed(this));

        Keypad keypad = new Keypad(this);
        keypad.startNow();

        currentState = states.get(3);
        resetStatesIterator();
        startAll();
    }

    private void resetStatesIterator() {
        statesIterator = states.listIterator();
    }

    /**
     * Transition the state machine to next state.
     */
    synchronized void changeState() {
        if (statesIterator.hasNext()) {
            changeStateTo(statesIterator.next());
        }
    }

    private void changeStateTo(StateWorker newState) {
        currentState.pause();
        currentState = newState;
        currentState.resume();
    }

    private void setupPi4j() {
        pi4j = Pi4J.newAutoContext();
        manager = new DriverManager(pi4j);

        if (manager.isKnownBoard()) {
            LOG.info("Operating system: {}", pi4j.boardInfo().getOperatingSystem());
            LOG.info("Java versions: {}", pi4j.boardInfo().getJavaInfo());
            LOG.info("Board model: {}", BoardInfoHelper.current().getBoardModel().getLabel());
            LOG.info("OS is 64-bit: {}", BoardInfoHelper.is64bit());
            LOG.info("JVM memory used (MB): {}", BoardInfoHelper.getJvmMemory().getUsedInMb());
            LOG.info("Board temperature (°C): {}", BoardInfoHelper.getBoardReading().getTemperatureInCelsius());

            manager.registerDriver("led-green", DriverType.LED_ON_PIN, 27);
            manager.registerDriver("led-yellow", DriverType.LED_ON_PIN, 17);
            manager.registerDriver("led-red", DriverType.LED_ON_PIN, 5);

            manager.getDriver("led-green").execute(DriverCommand.ON);
        }
    }

    /**
     * Arm and transition to Countdown state.
     */
    public synchronized void arm() {
        if (!getCurrentState().equals(states.get(1)) && !getCurrentState().equals(states.get(2))) {
            resetStatesIterator();
            changeState();
            LOG.info("Hub arming...");
        }
    }

    /**
     * Disarm and transition to Disarm state.
     * All state threads will be in WAITING.
     */
    public synchronized void disarm() {
        changeStateTo(states.get(3));
        LOG.info("Hub disarm");

    }

    /**
     * Panic and transition to AlarmTriggered state.
     */
    public synchronized void panic() {
        if (!getCurrentState().equals(states.get(2))) {
            changeStateTo(states.get(2));
            LOG.info("Hub panic!");
        }
    }

    /**
     * Helper method to get current hub state.
     *
     * @return StateWorker
     */
    public StateWorker getCurrentState() {
        return currentState;
    }

    /**
     * Change pin.
     *
     * @param newPin new pin
     */
    public void changePin(String newPin) {
        this.pin.set(Integer.parseInt(newPin));
    }

    public int getPin() {
        return pin.get();
    }

    public int getCountdownDuration() {
        return countdownDuration.get();
    }

    public DriverManager getManager() {
        return manager;
    }

    private void stopAll() {
        states.forEach(StateWorker::stop);
    }

    private void startAll() {
        states.forEach(StateWorker::start);
    }

    /**
     * Called by Spring on shutdown, all threads will terminate.
     */
    @PreDestroy
    public void destroy() {
        LOG.info("Shutting down hub and all state workers");
        stopAll();
        manager.unregisterAll();
        pi4j.shutdown();
    }

}
