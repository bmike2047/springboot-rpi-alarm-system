package springboot.rpi.alarm.fsm;

/**
 * This state is final finale state. NOOP will happen here.
 */
public class Disarmed extends StateWorker {

    public Disarmed(Hub hub) {
        super(hub);
    }

    @Override
    public void pause() {
        //NOOP
    }

    @Override
    public void resume() {
        //NOOP
    }

    @Override
    public void start() {
        //NOOP
    }
}
