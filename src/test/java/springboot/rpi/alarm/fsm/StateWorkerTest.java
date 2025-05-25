package springboot.rpi.alarm.fsm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import springboot.rpi.alarm.ApplicationProperties;

import static java.lang.Thread.State.RUNNABLE;
import static java.lang.Thread.State.WAITING;
import static org.mockito.Mockito.*;

public class StateWorkerTest {
    static class State extends StateWorker {
        private boolean returnValue;

        public void setReturnValue(boolean returnValue) {
            this.returnValue = returnValue;
        }

        public State(Hub hub) {
            super(hub);
        }

        @Override
        public void doWork() {
            if (returnValue) {
                stop();
            }
        }
    }

    private State state;
    private Hub hub;
    private final ApplicationProperties properties = Mockito.mock(ApplicationProperties.class);

    @BeforeEach
    void setupComponent() {
        when(properties.getDefaultPin()).thenReturn(1234);
        when(properties.getDefaultCountdownDuration()).thenReturn(5);
        hub = new Hub(properties);
        state = spy(new State(hub));
    }

    @AfterEach
    void stopComponent() {
        state.stop();
    }

    @Test
    void testWorkerStartsAndDoesSomeWorkAndChangesStatusAndStops() {
        state.setReturnValue(true);
        state.startNow();
        join(state);
        verify(state).doWork();
    }

    @Test
    void testWorkerStartsAndIsWaiting() {
        state.setReturnValue(false);
        state.start();
        checkThreadState(WAITING);
        verify(state, times(0)).doWork();
    }

    @Test
    void testWorkerStartsAndIsResumedAfterWaiting() {
        state.setReturnValue(false);
        state.start();
        checkThreadState(WAITING);
        state.setReturnValue(true);
        state.resume();
        checkThreadState(RUNNABLE);
        join(state);
        verify(state).doWork();
    }


    private void checkThreadState(Thread.State state) {
        int i = 0;
        while (this.state.getWorkerState() != state && (i < 200)) {
            i++;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void join(StateWorker state) {
        try {
            state.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
