package eu.letmehelpu.android.login.manager;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import eu.letmehelpu.android.login.domain.LoginWithGoogleLoginUseCase;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class GoogleSignInManager {
    private static final String TAG = GoogleSignInManager.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private Callback callback;
    private LoginWithGoogleLoginUseCase loginWithGoogleLoginUseCase;
    private Disposable disposable;

    public void onCreate(Activity activity, LoginWithGoogleLoginUseCase loginWithGoogleLoginUseCase, Callback callback) {
        this.callback = callback;
        this.loginWithGoogleLoginUseCase = loginWithGoogleLoginUseCase;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void signIn(Activity activity) {
        callback.showProgress();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleSignInResult(account.getId());
            } catch (ApiException e) {
                callback.onFailed("signInResult:failed code = " + e.getStatusCode());
            }
        }
    }

    public void onStart(Activity activity) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if(account != null) {
            callback.showProgress();
            handleGoogleSignInResult(account.getId());
        }
    }

    public void onStop() {
        if(disposable != null) {
            disposable.dispose();
            disposable = null;
        }
    }

    private void handleGoogleSignInResult(final String accountId) {
        if(disposable != null) {
            disposable.dispose();
            disposable = null;
        }
        disposable = loginWithGoogleLoginUseCase.login(accountId).subscribe(new Action() {
            @Override
            public void run() {
                callback.onLoggedIn();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                callback.onFailed(throwable.getMessage());
                mGoogleSignInClient.signOut();
            }
        });
    }

    public interface Callback {
        void showProgress();
        void onLoggedIn();
        void onFailed(String message);
    }
}
