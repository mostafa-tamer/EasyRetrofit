package com.mostafatamer.apiservice;

import android.os.CountDownTimer;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public abstract class ApiCall<T> implements Call<T>  {
    private final Callback<T> callback = new Callback<T>() {
        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
            onSuccess(response);
            onEnd();
        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            onFail(t);
            onEnd();
        }
    };
    private final Callback<T> safeCallback = new Callback<T>() {
        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
            onSuccess(response);
            countDownTimer.start();
        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            onFail(t);
            countDownTimer.start();
        }
    };
    CountDownTimer countDownTimer = new CountDownTimer(500, 500) {
        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            onEnd();
        }
    };
    //    private final Call<T> call;
    private static final int DELAY = 0;
    private boolean isLoading;
    private static boolean isSafeLoading;
    private static int numberOfRunningServices;
    private OnServerResponseSucceed<T> onServiceInteractionSuccess;
    private OnServerResponseSucceedWithStatusCode<T> onServiceInteractionSuccessWithStatusCode;
    private OnServerResponseFail onServiceInteractionFail;
    private OnStartInteraction onStartInteraction;
    private OnEndInteraction onEndServiceInteraction;
    private LoadingObserver loadingObserver;
    private static LoadingObserver staticLoadingObserver;
    private static OnBusyLoading onBusy;

//    public CallDecorator(Call<T> call) {
//        this.call = call;
//    }

    public static void setOnSafeExecution(OnBusyLoading onBusy) {
        ApiCall.onBusy = onBusy;
    }

    public ApiCall<T> setLoadingObserver(LoadingObserver loadingObserver) {
        this.loadingObserver = loadingObserver;
        return this;
    }

    public static void setStaticLoadingObserver(LoadingObserver loadingObserver) {
        ApiCall.staticLoadingObserver = loadingObserver;
    }

    public ApiCall<T> setOnStartInterAction(OnStartInteraction onStartInteraction) {
        this.onStartInteraction = onStartInteraction;
        return this;
    }

    public ApiCall<T> setOnSuccess(OnServerResponseSucceed<T> onServiceInteractionSuccess) {
        this.onServiceInteractionSuccess = onServiceInteractionSuccess;
        return this;
    }

    public ApiCall<T> setOnResponse(OnServerResponseSucceedWithStatusCode<T> onServiceInteractionSuccessWithStatusCode) {
        this.onServiceInteractionSuccessWithStatusCode = onServiceInteractionSuccessWithStatusCode;
        return this;
    }

    public ApiCall<T> setOnFail(OnServerResponseFail onServiceInteractionFail) {
        this.onServiceInteractionFail = onServiceInteractionFail;
        return this;
    }

    public ApiCall<T> setOnEndServiceInteraction(OnEndInteraction onEndServiceInteraction) {
        this.onEndServiceInteraction = onEndServiceInteraction;
        return this;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public static int getNumberOfRunningServices() {
        return numberOfRunningServices;
    }

    public void startExecution(
            OnServerResponseSucceed<T> onServerResponseSucceed
    ) {
        this.setOnSuccess(onServerResponseSucceed);
        startExecution();
    }

    public void startExecution() {
        this.run();
    }

    public void startExecution(
            OnServerResponseSucceed<T> onServerResponseSucceed,
            OnServerResponseFail onServiceInteractionFail
    ) {
        this.setOnFail(onServiceInteractionFail);
        startExecution(onServerResponseSucceed);
    }

    public void startExecution(
            OnStartInteraction onStartInteraction,
            OnServerResponseSucceed<T> onServerResponseSucceed,
            OnServerResponseFail onServiceInteractionFail,
            OnEndInteraction onEndInteraction
    ) {
        this.setOnStartInterAction(onStartInteraction);
        startExecution(onServerResponseSucceed, onServiceInteractionFail, onEndInteraction);
    }

    public void startExecution(
            OnServerResponseSucceed<T> onServerResponseSucceed,
            OnServerResponseFail onServiceInteractionFail,
            OnEndInteraction onEndInteraction
    ) {
        this.setOnEndServiceInteraction(onEndInteraction);
        startExecution(onServerResponseSucceed, onServiceInteractionFail);
    }

    public void safeExecute(
            OnServerResponseSucceed<T> onServerResponseSucceed,
            OnServerResponseFail onServiceInteractionFail,
            OnEndInteraction onEndInteraction
    ) {
        this.setOnEndServiceInteraction(onEndInteraction);
        safeExecute(onServerResponseSucceed, onServiceInteractionFail);
    }

    public void safeExecute(
            OnServerResponseSucceed<T> onServerResponseSucceed
    ) {
        this.setOnSuccess(onServerResponseSucceed);
        this.runSafe();
    }

    public void safeExecute(
            OnServerResponseSucceed<T> onServerResponseSucceed,
            OnServerResponseFail onServiceInteractionFail
    ) {
        this.setOnFail(onServiceInteractionFail);
        this.safeExecute(onServerResponseSucceed);
    }

    public void unlockSafeLoading() {
        isSafeLoading = false;
    }

    public void run() {
        onStart();
        enqueue(callback);
    }

    private void runSafe() {
        if (!isSafeLoading) {
            onStart();
            enqueue(safeCallback);
        } else {
            onBusyHandler();
        }
    }

    private void onFail(@NonNull Throwable t) {
        if (onServiceInteractionFail != null) {
            onServiceInteractionFail.onResponse(t);
        }
    }

    private void onStart() {
        setLoadingState(true);
        incrementNumberOfRunningServices();
        onStartEventHandler();
    }

    private void onBusyHandler() {
        if (onBusy != null) {
            onBusy.handleOnBusy();
        }
    }

    private void onStartEventHandler() {
        if (onStartInteraction != null) {
            onStartInteraction.handleEvent();
        }
    }

    private static void incrementNumberOfRunningServices() {
        numberOfRunningServices++;
    }

    private void setLoadingState(boolean isLoading) {
        this.isLoading = isLoading;
        isSafeLoading = isLoading;
        if (loadingObserver != null) {
            loadingObserver.observeLoading(isLoading);
        }
        if (staticLoadingObserver != null) {
            staticLoadingObserver.observeLoading(isLoading);
        }
    }

    private void onEnd() {
        decrementNumberOfRunningServices();
        onEndEventHandler();
        setLoadingState(false);
    }

    private void decrementNumberOfRunningServices() {
        numberOfRunningServices--;
    }

    private void onEndEventHandler() {
        if (onEndServiceInteraction != null) {
            onEndServiceInteraction.handleEvent();
        }
    }

    private void onSuccess(@NonNull Response<T> response) {
        if (onServiceInteractionSuccess != null) {
            onServiceInteractionSuccess.onResponse(response.body());
        }

        if (onServiceInteractionSuccessWithStatusCode != null) {
            onServiceInteractionSuccessWithStatusCode.onResponse(response.body(), response.code());
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public ApiCall<T> clone() {
        try {
            return (ApiCall<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface LoadingObserver {
        void observeLoading(boolean isLoading);
    }

    @FunctionalInterface
    public interface OnStartInteraction {
        void handleEvent();
    }

    @FunctionalInterface
    public interface OnEndInteraction {
        void handleEvent();
    }

    public interface OnServerResponseSucceed<T> {
        void onResponse(T response);
    }

    public interface OnServerResponseSucceedWithStatusCode<T> {
        void onResponse(T response, int statusCode);
    }

    @FunctionalInterface
    public interface OnServerResponseFail {
        void onResponse(Throwable throwable);
    }

    @FunctionalInterface
    public interface OnBusyLoading {
        void handleOnBusy();
    }
}