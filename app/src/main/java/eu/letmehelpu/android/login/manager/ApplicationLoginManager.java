package eu.letmehelpu.android.login.manager;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import eu.letmehelpu.android.login.domain.LoginWithUsernameAndPasswordUseCase;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class ApplicationLoginManager implements LifecycleObserver {
    private final LoginCallback callback;
    private final LoginWithUsernameAndPasswordUseCase loginWithUsernameAndPasswordUseCase;
    private static String attemptingUsername = null;
    private static String attemptingPassword = null;
    public Disposable disposable;

    public ApplicationLoginManager(AppCompatActivity appCompatActivity, LoginWithUsernameAndPasswordUseCase loginWithUsernameAndPasswordUseCase, LoginCallback callback) {
        appCompatActivity.getLifecycle().addObserver(this);
        this.callback = callback;
        this.loginWithUsernameAndPasswordUseCase = loginWithUsernameAndPasswordUseCase;
    }

    public void signIn(String username, String password) {
        callback.showProgress();

        if(disposable != null) {
            disposable.dispose();
            disposable = null;
        }

        attemptingUsername = username;
        attemptingPassword = password;
        disposable = loginWithUsernameAndPasswordUseCase.login(username, password).subscribe(new Action() {
            @Override
            public void run() {
                attemptingUsername = null;
                attemptingPassword = null;
                callback.onLoggedIn();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                attemptingUsername = null;
                attemptingPassword = null;
                callback.onFailed(throwable.getMessage());
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        if(attemptingUsername != null && attemptingPassword != null) {
            signIn(attemptingUsername, attemptingPassword);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if(disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }
}
