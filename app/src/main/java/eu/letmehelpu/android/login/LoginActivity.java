package eu.letmehelpu.android;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.letmehelpu.android.network.InvalidField;
import eu.letmehelpu.android.offers.OfferListActivity;
import io.fabric.sdk.android.Fabric;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = LoginActivity.class.getSimpleName();

    private View loginWithGoogle;
    private View loginWithFacebook;
    private GoogleSignInClient mGoogleSignInClient;
    private UserRepository userRepository;
    private CallbackManager callbackManager;
    private TextView loginUserName;
    private TextView loginPassword;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);

        userRepository = new UserRepository(getSharedPreferences("userRepository", MODE_PRIVATE));

        loginUserName = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);

        loginWithGoogle = findViewById(R.id.login_login_with_google);
        loginWithFacebook = findViewById(R.id.login_login_with_facebook);

        findViewById(R.id.login_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String userName = loginUserName.getText().toString();
                final String password = loginPassword.getText().toString();
                Network.getInstance().getTestApi().login(
                        userName,
                        password
                ).enqueue(new Callback<Map>() {
                    @Override
                    public void onResponse(Call<Map> call, Response<Map> response) {
                        ResponseBody errorBody = response.errorBody();
                        if(errorBody != null) {
                            List<InvalidField> invalidFields = new Gson().fromJson(errorBody.charStream(), TypeToken.getParameterized(List.class, InvalidField.class).getType());
                            String message = invalidFields.get(0).getMessage();
                            showAlert(message);
                            return;
                        }

                        userRepository.setLogged(new LoggedUser(LoggedUser.LOGGED_WITH_APP, userName, password));
                        navigateToMainActivity();
                    }

                    @Override
                    public void onFailure(Call<Map> call, Throwable t) {
                        showAlert(t.getLocalizedMessage());
                    }

                    private void showAlert(String message) {
                        AlertDialog.Builder builder  = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(message);
                        builder.show();
                    }
                });

            }
        });

        setupLoginWithFacebook();
        setupLoginWithGoogle();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupLoginWithGoogle() {
        loginWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void setupLoginWithFacebook() {
        callbackManager = CallbackManager.Factory.create();

        loginWithFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Collections.singletonList("public_profile"));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if(loginResult.getAccessToken() != null) {
                            userRepository.setLogged(new LoggedUser(LoggedUser.LOGGED_WITH_FACEBOOK, loginResult.getAccessToken().getToken()));
                            navigateToMainActivity();
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginManager.getInstance().unregisterCallback(callbackManager);
    }

    private void navigateToMainActivity() {
        finish();
        startActivity(OfferListActivity.getStartIntent(this));
    }

    @Override
    public void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            userRepository.setLogged(new LoggedUser(LoggedUser.LOGGED_WITH_GOOGLE, account.getId()));
            navigateToMainActivity();
        } else
        if(AccessToken.getCurrentAccessToken() != null) {
            userRepository.setLogged(new LoggedUser(LoggedUser.LOGGED_WITH_FACEBOOK, AccessToken.getCurrentAccessToken().getToken()));
            navigateToMainActivity();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            userRepository.setLogged(new LoggedUser(LoggedUser.LOGGED_WITH_GOOGLE, account.getId()));
            Log.d(TAG, "Logged with account name = [" + account.getDisplayName() + "]");
            navigateToMainActivity();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            Log.w(TAG, "signInResult:failed code = " + e.getStatusCode());
        }
    }
}
