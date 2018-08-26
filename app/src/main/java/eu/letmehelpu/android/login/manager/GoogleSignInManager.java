package eu.letmehelpu.android.login.manager;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

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

public class GoogleSignInManager extends AbsLoginManager {
    private static final String TAG = GoogleSignInManager.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    private LoginWithGoogleLoginUseCase loginWithGoogleLoginUseCase;
    private Disposable disposable;
    private static String attemptingAccountId;

    public GoogleSignInManager(AppCompatActivity activity, LoginWithGoogleLoginUseCase loginWithGoogleLoginUseCase, LoginCallback callback) {
        super(activity, callback);
        this.loginWithGoogleLoginUseCase = loginWithGoogleLoginUseCase;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        if(attemptingAccountId != null) {
            handleGoogleSignInResult(attemptingAccountId);
        }
    }

    @Override
    public void signIn() {
        callback.showProgress();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                final GoogleSignInAccount account = task.getResult(ApiException.class);
                handleGoogleSignInResult(account.getId());
            } catch (ApiException e) {
                if(e.getStatusCode() == 13) {
                    callback.onCancel();
                } else {
                    callback.onFailed("signInResult:failed code = " + e.getStatusCode());
                }
            }
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);
        if(account != null) {
            callback.showProgress();
            handleGoogleSignInResult(account.getId());
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
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
        attemptingAccountId = accountId;
        disposable = loginWithGoogleLoginUseCase.login(accountId).subscribe(new Action() {
            @Override
            public void run() {
                attemptingAccountId = null;
                callback.onLoggedIn();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                attemptingAccountId = null;
                callback.onFailed(throwable.getMessage());
                mGoogleSignInClient.signOut();
            }
        });
    }
}
