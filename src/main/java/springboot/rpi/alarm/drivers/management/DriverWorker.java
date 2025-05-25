package springboot.rpi.alarm.drivers.management;

import com.pi4j.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reusable thread wrapper based on the template pattern.
 * Wraps around a Thread object and gives it additional functionalities.
 */
public class DriverWorker extends DriverBase implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(DriverWorker.class);

    private Thread thread;
    private volatile boolean stopped = false;

    /**
     * Get thread running status.
     *
     * @return status
     */
    public boolean isStopped() {
        return stopped;
    }

    public DriverWorker(Context pi4j) {
        super(pi4j);
    }

    /**
     * Wait for this thread to exit.
     *
     * @throws InterruptedException
     */
    public void join() throws InterruptedException {
        thread.join();
    }

    /**
     * Start this driver.
     */
    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Stop this driver.
     */
    public void stop() {
        stopped = true;
    }

    /**
     * Get thread live status.
     * @return live status
     */
    public boolean isAlive() {
        return thread.isAlive();
    }

    /**
     * Get thread interrupt status.
     *
     * @return interrupt status
     */
    public boolean isInterrupted() {
        return thread.isInterrupted();
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
     * Worker logic must be implemented by subclasses.
     */
    public void doWork() throws InterruptedException {
    }

    /**
     * Run as a separate thread.
     */
    @Override
    public void run() {
        try {
            while (!isInterrupted() && !isStopped()) {
                doWork();
            }
        } catch (InterruptedException e) {
            LOG.error("Driver thread interrupted, shutting down", e);
        }
        LOG.info("Driver thread finished, shutting down");
    }

}
