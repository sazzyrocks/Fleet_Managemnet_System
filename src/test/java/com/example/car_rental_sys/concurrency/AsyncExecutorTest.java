package com.example.car_rental_sys.concurrency;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests verifying the Asynchronous Thread Pool concurrency manager.
 */
class AsyncExecutorTest {

    @Test
    @DisplayName("AsyncExecutor should run tasks asynchronously on a daemon worker thread")
    void shouldExecuteTaskOnWorkerThread() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean executedOnWorker = new AtomicBoolean(false);
        AtomicReference<String> threadName = new AtomicReference<>();

        AsyncExecutor.getInstance().execute(() -> {
            threadName.set(Thread.currentThread().getName());
            executedOnWorker.set(Thread.currentThread().isDaemon());
            latch.countDown();
        });

        boolean completed = latch.await(2, TimeUnit.SECONDS);

        assertTrue(completed, "Task must execute within timeout");
        assertTrue(executedOnWorker.get(), "Task must run on a daemon thread to prevent hanging on app exit");
        assertTrue(threadName.get().startsWith("fleet-async-worker-"), "Thread should use custom pool naming convention");
    }

    @Test
    @DisplayName("AsyncExecutor should return singleton instance")
    void shouldReturnSingletonInstance() {
        AsyncExecutor instance1 = AsyncExecutor.getInstance();
        AsyncExecutor instance2 = AsyncExecutor.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2, "AsyncExecutor must be a strict singleton");
    }
}
