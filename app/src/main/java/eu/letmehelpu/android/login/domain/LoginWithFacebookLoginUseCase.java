package eu.letmehelpu.android.login.domain;

import eu.letmehelpu.android.login.entity.AuthenticationGateway;
import eu.letmehelpu.android.login.entity.LoggedUser;
import eu.letmehelpu.android.login.entity.LoginGateway;
import eu.letmehelpu.android.login.entity.SessionGateway;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.functions.Function;

public class LoginWithFacebookLoginUseCase {
    private final AuthenticationGateway authenticationGateway;
    private final LoginGateway loginGateway;
    private final SessionGateway sessionGateway;

    public LoginWithFacebookLoginUseCase(
            AuthenticationGateway authenticationGateway,
            LoginGateway loginGateway,
            SessionGateway sessionGateway) {
        this.authenticationGateway = authenticationGateway;
        this.loginGateway = loginGateway;
        this.sessionGateway = sessionGateway;
    }

    public Completable login(final String accountId) {
        return authenticationGateway.authenticateByOAuth("facebook", accountId).flatMapCompletable(new Function<String, CompletableSource>() {
            @Override
            public CompletableSource apply(String token) {
                LoggedUser loggedUser = new LoggedUser(LoggedUser.LOGGED_WITH_FACEBOOK, accountId);
                loginGateway.storeLoggedUser(loggedUser);
                sessionGateway.storeSessionToken(token);
                return Completable.complete();
            }
        });
    }
}
