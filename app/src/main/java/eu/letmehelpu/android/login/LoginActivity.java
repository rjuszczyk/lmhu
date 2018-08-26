package eu.letmehelpu.android.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import eu.letmehelpu.android.R;
import eu.letmehelpu.android.login.domain.LoginWithFacebookLoginUseCase;
import eu.letmehelpu.android.login.domain.LoginWithGoogleLoginUseCase;
import eu.letmehelpu.android.login.domain.LoginWithUsernameAndPasswordUseCase;
import eu.letmehelpu.android.login.manager.ApplicationLoginManager;
import eu.letmehelpu.android.login.manager.FacebookSignInManager;
import eu.letmehelpu.android.login.manager.GoogleSignInManager;
import eu.letmehelpu.android.login.manager.LoginCallback;
import eu.letmehelpu.android.offers.OfferListActivity;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends DaggerAppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Inject
    LoginWithUsernameAndPasswordUseCase loginWithUsernameAndPasswordUseCase;
    @Inject
    LoginWithGoogleLoginUseCase loginWithGoogleLoginUseCase;
    @Inject
    LoginWithFacebookLoginUseCase loginWithFacebookLoginUseCase;

    private GoogleSignInManager googleSignInManager;
    private FacebookSignInManager facebookSignInManager;
    private ApplicationLoginManager applicationLoginManager;

    private View loginWithGoogle;
    private View loginWithFacebook;
    private TextView loginUserName;
    private TextView loginPassword;
    private View progress;
    private View loginWithApplication;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);

        progress = findViewById(R.id.progress);

        loginUserName = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);

        loginWithGoogle = findViewById(R.id.login_login_with_google);
        loginWithFacebook = findViewById(R.id.login_login_with_facebook);
        loginWithApplication = findViewById(R.id.login_login);

        LoginCallback loginCallback = new LoginCallback() {
            @Override
            public void showProgress() {
                if(getSupportFragmentManager().findFragmentByTag(ProgressDialog.TAG)==null) {
                    ProgressDialog.newFragment(R.string.loading).show(getSupportFragmentManager(), ProgressDialog.TAG);
                }
            }

            private void hideProgress() {
                DialogFragment dialog = (DialogFragment) getSupportFragmentManager().findFragmentByTag(ProgressDialog.TAG);
                if(dialog!=null) {
                    dialog.dismiss();
                }
            }
            
            @Override
            public void onLoggedIn() {
                hideProgress();
                navigateToMainActivity();
            }

            @Override
            public void onFailed(String message) {
                ProgressDialog dialog = (ProgressDialog) getSupportFragmentManager().findFragmentByTag(ProgressDialog.TAG);
                dialog.displayError(message);
            }

            @Override
            public void onCancel() {
                hideProgress();
            }
        };

        applicationLoginManager = new ApplicationLoginManager(this, loginWithUsernameAndPasswordUseCase, loginCallback);
        googleSignInManager = new GoogleSignInManager(this, loginWithGoogleLoginUseCase, loginCallback);
        facebookSignInManager = new FacebookSignInManager(this, loginWithFacebookLoginUseCase, loginCallback);

        setupLoginWithFacebook();
        setupLoginWithGoogle();
        setupLoginWithApplication();
    }


    private void setupLoginWithGoogle() {
        loginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignInManager.signIn();
            }
        });
    }

    private void setupLoginWithFacebook() {
        loginWithFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookSignInManager.signIn();
            }
        });
    }

    private void setupLoginWithApplication() {
        loginWithApplication.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                final String username = loginUserName.getText().toString();
                final String password = loginPassword.getText().toString();

                applicationLoginManager.signIn(username, password);
            }
        });
    }

    private void navigateToMainActivity() {
        finish();
        startActivity(OfferListActivity.getStartIntent(this));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        googleSignInManager.onActivityResult(requestCode, resultCode, data);
        facebookSignInManager.onActivityResult(requestCode, resultCode, data);
    }
}
