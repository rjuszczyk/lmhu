package eu.letmehelpu.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import eu.letmehelpu.android.login.LoginActivity;
import eu.letmehelpu.android.login.domain.LogoutUseCase;
import eu.letmehelpu.android.login.entity.LoggedUser;
import eu.letmehelpu.android.login.entity.LoginGateway;

public class UserFragment extends DaggerFragment {

    private static final String TAG = UserFragment.class.getSimpleName();
    @Inject
    LogoutUseCase logoutUseCase;
    @Inject LoginGateway loginGateway;

    private GoogleSignInClient googleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        TextView textView = view.findViewById(R.id.main_logged_user);

        LoggedUser loggedUser = loginGateway.getLoggedUser();
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

        view.findViewById(R.id.main_logout)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logout();
                    }
                });
    }

    private void logout() {
        if(loginGateway.getLoggedUser().getLoggedWith() == LoggedUser.LOGGED_WITH_GOOGLE) {
            googleSignInClient.signOut()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure() called with: e = [" + e + "]");
                        }
                    })
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            logoutUseCase.logout();
                            goToLoginActivity();
                        }
                    });
        } else if(loginGateway.getLoggedUser().getLoggedWith() == LoggedUser.LOGGED_WITH_FACEBOOK) {
            LoginManager.getInstance().logOut();
            logoutUseCase.logout();
            goToLoginActivity();
        } else if(loginGateway.getLoggedUser().getLoggedWith() == LoggedUser.LOGGED_WITH_APP) {
            logoutUseCase.logout();
            goToLoginActivity();
        }
    }

    private void goToLoginActivity() {
        Intent intent = LoginActivity.getStartIntent(getActivity());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
