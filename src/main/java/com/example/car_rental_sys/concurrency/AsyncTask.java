package com.example.car_rental_sys.concurrency;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Reusable JavaFX Task wrapper enabling responsive UI states and background execution.
 *
 * @param <T> result type of background computation
 */
public class AsyncTask<T> extends Task<T> {

    private final Callable<T> backgroundWork;
    private Consumer<T> onSuccess;
    private Consumer<Throwable> onError;

    public AsyncTask(Callable<T> backgroundWork) {
        this.backgroundWork = backgroundWork;
    }

    public static <T> AsyncTask<T> of(Callable<T> backgroundWork) {
        return new AsyncTask<>(backgroundWork);
    }

    public AsyncTask<T> onSuccess(Consumer<T> onSuccess) {
        this.onSuccess = onSuccess;
        return this;
    }

    public AsyncTask<T> onError(Consumer<Throwable> onError) {
        this.onError = onError;
        return this;
    }

    @Override
    protected T call() throws Exception {
        return backgroundWork.call();
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        if (onSuccess != null) {
            onSuccess.accept(getValue());
        }
    }

    @Override
    protected void failed() {
        super.failed();
        if (onError != null) {
            onError.accept(getException());
        }
    }

    /**
     * Dispatches this task on the managed {@link AsyncExecutor} thread pool.
     */
    public void start() {
        AsyncExecutor.getInstance().execute(this);
    }
}
