package com.mostafatamer.api;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Abstract class that provides a template for API calls, handling success, failure,
 * and loading states with optional callbacks.
 *
 * @param <T> the type of response expected from the API call
 */
public abstract class Api<T> {
    private final Callback<T> callback = new Callback<T>() {
        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
            invokeResponseCallback(response);
            end();
        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            invokeFailureCallback(t);
            end();
        }
    };

    private boolean loading;

    private static boolean blocking;
    private static boolean loadingGlobally;
    private static int numberOfRunningServices;

    private OnResponse<T> onResponse;
    private OnFailure onFailure;
    private OnStart onStart;
    private OnEnd onEnd;
    private LoadingStateObserver loadingStateObserver;
    private static OnBusy onBusy;
    private static LoadingStateObserver globalLoadingStateObserver;

    /**
     * Sets the loading state observer for this API call.
     * The observer will be notified whenever the loading state changes,
     * allowing the client to respond to these state changes appropriately.
     *
     * @param loadingStateObserver the observer to be notified of loading state changes
     * @return the current Api instance, allowing for method chaining
     */
    public Api<T> setLoadingStateObserver(LoadingStateObserver loadingStateObserver) {
        this.loadingStateObserver = loadingStateObserver;
        return this;
    }

    /**
     * Sets a global loading state observer for all API calls.
     * This observer will be notified whenever the loading state changes for any instance of the API class,
     * allowing centralized handling of loading states.
     *
     * @param globalLoadingStateObserver the global observer to be notified of loading state changes
     */
    public static void setGlobalLoadingStateObserver(LoadingStateObserver globalLoadingStateObserver) {
        Api.globalLoadingStateObserver = globalLoadingStateObserver;
    }

    /**
     * Sets the callback to be invoked when the API call starts.
     * This callback is useful for performing actions such as showing a loading indicator.
     *
     * @param onStart the callback to be invoked at the start of the API call
     * @return the current Api instance, allowing for method chaining
     */
    public Api<T> setOnStart(OnStart onStart) {
        this.onStart = onStart;
        return this;
    }

    /**
     * Sets the callback to be invoked when the API call receives a response.
     *
     * @param onResponse the callback to be invoked upon receiving a response
     * @return the current Api instance, allowing for method chaining
     */
    public Api<T> setOnResponse(OnResponse<T> onResponse) {
        this.onResponse = onResponse;
        return this;
    }

    /**
     * Sets the callback to be invoked when the API call fails.
     *
     * @param onFailure the callback to be invoked upon failure of the API call
     * @return the current Api instance, allowing for method chaining
     */
    public Api<T> setOnFailure(OnFailure onFailure) {
        this.onFailure = onFailure;
        return this;
    }

    /**
     * Sets the callback to be invoked when the API call ends.
     * This callback is useful for performing actions such as hiding a loading indicator
     * or finalizing the request handling.
     *
     * @param onEnd the callback to be invoked at the end of the API call
     * @return the current Api instance, allowing for method chaining
     */
    public Api<T> setOnEnd(OnEnd onEnd) {
        this.onEnd = onEnd;
        return this;
    }

    /**
     * Enables blocking mode for the API call and sets a callback to be invoked when the API call is busy.
     * When blocking mode is enabled, it will prevent any other API calls from being executed,
     * even if they are initiated from another instance of the API class.
     * <p>
     * This is useful in scenarios where only one API call should be processed at a time to prevent
     * conflicts or excessive load. The provided callback will be invoked whenever an API call
     * attempts to execute while another is already in progress.
     * <p>
     * Note: Blocking mode affects all instances of this API class, not just the current instance.
     *
     * @param onBusy the callback to be invoked when an API call is attempted while another is in progress.
     * @return the current Api instance, allowing for method chaining.
     */
    public Api<T> allowBlocking(OnBusy onBusy) {
        Api.onBusy = onBusy;
        blocking = true;
        return this;
    }

    /**
     * @return true if the API call is loading, false otherwise
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * @return the number of running services
     */
    public static int getNumberOfRunningServices() {
        return numberOfRunningServices;
    }

    /**
     * Unlocks the blocking mode, allowing subsequent API calls to be executed.
     * <p>
     * When blocking mode is active, it prevents any new API calls from being executed until
     * the blocking interval has passed. This method disables the blocking mode immediately,
     * permitting new API calls to proceed.
     * <p>
     * Use this method with caution, ensuring that unlocking the blocking state is appropriate
     * for your application's workflow and does not lead to unintended concurrent API requests.
     */
    public void unLockBlocking() {
        blocking = false;
    }

    protected void beginRequest(Call<T> call) {
        if (blocking && loadingGlobally) {
            invokeBusyCallback();
        } else {
            start();
            call.enqueue(callback);
        }
    }

    public abstract void beginRequest();

    private void invokeFailureCallback(@NonNull Throwable t) {
        if (onFailure != null) {
            onFailure.invoke(t);
        }
    }

    private void start() {
        numberOfRunningServices++;
        changeLoadingState(true);
        invokeStartCallback();
    }

    private void invokeBusyCallback() {
        if (onBusy != null) {
            onBusy.invoke();
        }
    }

    private void invokeStartCallback() {
        if (onStart != null) {
            onStart.invoke();
        }
    }

    private void changeLoadingState(boolean isLoading) {
        this.loading = isLoading;
        Api.loadingGlobally = isLoading;

        if (loadingStateObserver != null) {
            loadingStateObserver.invoke(isLoading);
        }
        if (globalLoadingStateObserver != null) {
            globalLoadingStateObserver.invoke(isLoading);
        }
    }

    private void end() {
        numberOfRunningServices--;
        invokeEndCallback();
        changeLoadingState(false);

        if (blocking) unLockBlocking();
    }

    private void invokeEndCallback() {
        if (onEnd != null) {
            onEnd.invoke();
        }
    }

    private void invokeResponseCallback(@NonNull Response<T> response) {
        if (onResponse != null) {
            onResponse.invoke(response.body(), response.code());
        }
    }

    /**
     * Interface for observing the loading state of the API call.
     */
    @FunctionalInterface
    public interface LoadingStateObserver {
        /**
         * Invoked when the loading state changes.
         *
         * @param isLoading true if loading, false otherwise
         */
        void invoke(boolean isLoading);
    }

    /**
     * Interface for handling the start of the API call.
     */
    @FunctionalInterface
    public interface OnStart {
        /**
         * Invoked when the API call starts.
         */
        void invoke();
    }

    /**
     * Interface for handling the end of the API call.
     */
    @FunctionalInterface
    public interface OnEnd {
        /**
         * Invoked when the API call ends.
         */
        void invoke();
    }

    /**
     * Interface for handling the response of the API call.
     *
     * @param <T> the type of response
     */
    @FunctionalInterface
    public interface OnResponse<T> {
        /**
         * Invoked when a response is received.
         *
         * @param response   the response body
         * @param statusCode the status code of the response
         */
        void invoke(T response, int statusCode);
    }

    /**
     * Interface for handling the failure of the API call.
     */
    @FunctionalInterface
    public interface OnFailure {
        /**
         * Invoked when the API call fails.
         *
         * @param throwable the throwable that caused the failure
         */
        void invoke(Throwable throwable);
    }

    /**
     * Interface for handling the busy state of the API call.
     */
    @FunctionalInterface
    public interface OnBusy {
        /**
         * Invoked when the API call is busy.
         */
        void invoke();
    }
}