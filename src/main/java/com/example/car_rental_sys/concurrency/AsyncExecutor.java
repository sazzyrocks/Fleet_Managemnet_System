package com.example.car_rental_sys.concurrency;

import javafx.application.Platform;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Enterprise Asynchronous Thread Pool Manager.
 *
 * <p>Prevents JavaFX Application Thread blocking by offloading heavy database
 * queries, external network calls, and calculations to managed daemon worker threads.</p>
 */
public final class AsyncExecutor {

    private static final Logger LOGGER = Logger.getLogger(AsyncExecutor.class.getName());
    private static volatile AsyncExecutor instance;

    private final ExecutorService executorService;

    private AsyncExecutor() {
        int corePoolSize = Math.max(4, Runtime.getRuntime().availableProcessors());
        ThreadFactory daemonThreadFactory = runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true); // Daemon threads exit automatically when JavaFX application closes
            thread.setName("fleet-async-worker-" + thread.getId());
            return thread;
        };

        this.executorService = new ThreadPoolExecutor(
                corePoolSize,
                corePoolSize * 2,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                daemonThreadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public static AsyncExecutor getInstance() {
        if (instance == null) {
            synchronized (AsyncExecutor.class) {
                if (instance == null) {
                    instance = new AsyncExecutor();
                }
            }
        }
        return instance;
    }

    /**
     * Executes a background task asynchronously.
     *
     * @param task task to run
     */
    public void execute(Runnable task) {
        executorService.execute(task);
    }

    /**
     * Submits a value-returning background task with JavaFX UI-thread callbacks.
     *
     * @param backgroundWork callable producing the result off the UI thread
     * @param onSuccess      consumer executed on the JavaFX Application Thread with the result
     * @param onError        consumer executed on the JavaFX Application Thread if an error occurs
     * @param <T>            result type
     */
    public <T> void executeAsync(Callable<T> backgroundWork,
                                Consumer<T> onSuccess,
                                Consumer<Throwable> onError) {
        executorService.submit(() -> {
            try {
                T result = backgroundWork.call();
                if (onSuccess != null) {
                    Platform.runLater(() -> onSuccess.accept(result));
                }
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, "Asynchronous task failed: " + t.getMessage(), t);
                if (onError != null) {
                    Platform.runLater(() -> onError.accept(t));
                }
            }
        });
    }

    /**
     * Gracefully shuts down the executor pool.
     */
    public void shutdown() {
        LOGGER.info("Shutting down AsyncExecutor thread pool...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
