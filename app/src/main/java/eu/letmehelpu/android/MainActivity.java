package eu.letmehelpu.android;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    UserRepository userRepository;
    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleSignInClient googleSignInClient;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.toolbar).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ViewCompat.requestApplyInsets(v);
            }
        });
        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCompat.requestApplyInsets(v);
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        userRepository = new UserRepository(getSharedPreferences("userRepository", MODE_PRIVATE));

        TextView textView = findViewById(R.id.main_logged_user);

        LoggedUser loggedUser = userRepository.getLoggedUser();
        String loggedWith;
        switch (loggedUser.getLoggedWith()) {
            case LoggedUser.LOGGED_WITH_APP:
                loggedWith = "Logged with APP";
                break;
            case LoggedUser.LOGGED_WITH_GOOGLE:
                loggedWith = "Logged with GOOGLE";
                break;
            case LoggedUser.LOGGED_WITH_FACEBOOK:
                loggedWith = "Logged with FACEBOOK";
                break;
            default:
                throw new RuntimeException("unsupported");
        }

        textView.setText(loggedWith);

        findViewById(R.id.main_logout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logout();
                    }
                });
    }

    private void logout() {
        if(userRepository.getLoggedUser().getLoggedWith() == LoggedUser.LOGGED_WITH_GOOGLE) {
            googleSignInClient.signOut()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure() called with: e = [" + e + "]");
                        }
                    })
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            userRepository.logout();
                            goToLoginActivity();
                        }
                    });
        }
        if(userRepository.getLoggedUser().getLoggedWith() == LoggedUser.LOGGED_WITH_FACEBOOK) {
            LoginManager.getInstance().logOut();
            userRepository.logout();
            goToLoginActivity();
        }
        if(userRepository.getLoggedUser().getLoggedWith() == LoggedUser.LOGGED_WITH_APP) {
            userRepository.logout();
            goToLoginActivity();
        }
    }

    private void goToLoginActivity() {
        Intent intent = LoginActivity.getStartIntent(MainActivity.this);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
