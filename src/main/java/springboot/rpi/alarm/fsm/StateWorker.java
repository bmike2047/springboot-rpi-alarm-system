package springboot.rpi.alarm.fsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * Threaded State representation for Finite State Machine based on the template pattern.
 * A State of type StateWorker is non-blocking as it runs in its own thread.
 * Wraps around a Thread object and gives it additional functionalities.
 */
public class StateWorker implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(StateWorker.class);

    private final Semaphore semaphore = new Semaphore(0);
    protected final Hub hub;

    private Thread thread;
    private volatile boolean stopped = false;
    private volatile boolean pause = true;

    /**
     * Main constructor.
     *
     * @param hub Hub
     */
    public StateWorker(Hub hub) {
        this.hub = hub;
    }

    /**
     * Start this state.
     */
    public void start() {
        thread = new Thread(this);
        thread.setName(this.getClass().getSimpleName());
        thread.start();
    }

    /**
     * Start this state immediately.
     */
    public void startNow() {
        this.pause = false;
        start();
    }

    /**
     * Stop this state.
     */
    public void stop() {
        if (thread != null && thread.getState().equals(Thread.State.WAITING)) {
            thread.interrupt();
        } else {
            stopped = true;
        }
    }

    /**
     * Worker logic to be implemented by subclasses.
     */
    public void doWork() {
    }

    /**
     * Thread wrapper additional logic.
     */
    @Override
    public void run() {
        try {
            while (!thread.isInterrupted() && !isStopped()) {
                if (pause) {
                    semaphore.acquire();
                }
                doWork();
            }
        } catch (InterruptedException e) {
            LOG.error("Thread interrupted");
        }
        LOG.info("Thread finished, shutting down");
    }

    /**
     * Put current thread in WAITING.
     */
    public void pause() {
        LOG.info("Pausing {}", thread.getName());
        pause = true;

    }

    /**
     * Put current thread in RUNNABLE.
     */
    public void resume() {
        LOG.info("Resuming {}", thread.getName());
        pause = false;
        semaphore.release();

    }

    /**
     * Check if the thread was instructed to stop.
     *
     * @return stopped
     */
    public boolean isStopped() {
        return stopped;
    }

    /**
     * Wait for this thread to exit.
     *
     * @throws InterruptedException InterruptedException
     */
    public void join() throws InterruptedException {
        thread.join();
    }

    /**
     * Get a worker state.
     *
     * @return worker state
     */
    public Thread.State getWorkerState() {
        return thread.getState();
    }

    /**
     * Helper method for thread sleeping.
     *
     * @param millis wait time in ms
     */
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
