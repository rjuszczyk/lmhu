package eu.letmehelpu.android.login.manager;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Collections;

import eu.letmehelpu.android.login.domain.LoginWithFacebookLoginUseCase;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class FacebookSignInManager  extends AbsLoginManager {
    private static final String TAG = FacebookSignInManager.class.getSimpleName();
    private final AppCompatActivity activity;

    private Disposable disposable;
    private LoginWithFacebookLoginUseCase loginWithFacebookLoginUseCase;
    private CallbackManager callbackManager;
    private static String attemptingFbToken;

    public FacebookSignInManager(AppCompatActivity activity, LoginWithFacebookLoginUseCase loginWithFacebookLoginUseCase, LoginCallback callback) {
        super(activity, callback);
        this.activity = activity;
        activity.getLifecycle().addObserver(this);
        this.loginWithFacebookLoginUseCase = loginWithFacebookLoginUseCase;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        Log.d(TAG, "onCreate() called");
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        String fbToken = loginResult.getAccessToken().getToken();
                        handleFacebookSignInResult(fbToken);
                    }

                    @Override
                    public void onCancel() {
                        callback.onCancel();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        callback.onFailed(exception.getMessage());
                    }
                });

        if(attemptingFbToken != null) {
            handleFacebookSignInResult(attemptingFbToken);
        }
    }

    @Override
    public void signIn() {
        callback.showProgress();
        LoginManager.getInstance().logInWithReadPermissions(activity, Collections.singletonList("public_profile"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if(AccessToken.getCurrentAccessToken() != null) {
            callback.showProgress();
            handleFacebookSignInResult(AccessToken.getCurrentAccessToken().getToken());
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if(disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        LoginManager.getInstance().unregisterCallback(callbackManager);
    }

    private void handleFacebookSignInResult(final String fbToken) {
        if(disposable != null) {
            disposable.dispose();
            disposable = null;
        }

        attemptingFbToken = fbToken;
        disposable = loginWithFacebookLoginUseCase.login(fbToken).subscribe(new Action() {
            @Override
            public void run() {
                attemptingFbToken = null;
                callback.onLoggedIn();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                attemptingFbToken = null;
                LoginManager.getInstance().logOut();
                callback.onFailed(throwable.getMessage());
            }
        });
    }
}
