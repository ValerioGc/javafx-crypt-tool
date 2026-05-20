package app.testutil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;

public final class FxTestSupport {

    private static final AtomicBoolean STARTED = new AtomicBoolean(false);

    private FxTestSupport() {
    }

    public static void startFx() {
        if (STARTED.get()) 
            return;
        

        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Timeout starting JavaFX toolkit");
            }
        } catch (IllegalStateException e) {
            // JavaFX was already started by another test runner.
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while starting JavaFX toolkit", e);
        }

        STARTED.set(true);
    }
}
