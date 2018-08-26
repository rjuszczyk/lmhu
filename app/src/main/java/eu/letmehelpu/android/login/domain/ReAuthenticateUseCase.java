package eu.letmehelpu.android.login.domain;

import java.util.concurrent.Callable;

import eu.letmehelpu.android.login.entity.LoggedUser;
import eu.letmehelpu.android.login.entity.AuthenticationGateway;
import eu.letmehelpu.android.login.entity.LoginGateway;
import eu.letmehelpu.android.login.entity.SessionGateway;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.functions.Function;

public class ReAuthenticateUseCase {
    private final AuthenticationGateway authenticationGateway;
    private final LoginGateway loginGateway;
    private final SessionGateway sessionGateway;

    public ReAuthenticateUseCase(
            AuthenticationGateway authenticationGateway,
            LoginGateway loginGateway,
            SessionGateway sessionGateway) {
        this.authenticationGateway = authenticationGateway;
        this.loginGateway = loginGateway;
        this.sessionGateway = sessionGateway;
    }

    public Completable reauthenticate() {
        return Completable.defer(new Callable<CompletableSource>() {
            @Override
            public CompletableSource call() {
                if(!loginGateway.isLoggedUser()) {
                    return Completable.error(new UserNotLoggedException());
                }

                LoggedUser loggedUser = loginGateway.getLoggedUser();
                if(loggedUser.getLoggedWith() == LoggedUser.LOGGED_WITH_APP) {
                    return authenticationGateway.authenticate(loggedUser.getUserName(), loggedUser.getPassword()).flatMapCompletable(new Function<String, CompletableSource>() {
                        @Override
                        public CompletableSource apply(String token) throws Exception {
                            sessionGateway.storeSessionToken(token);
                            return Completable.complete();
                        }
                    });
                }
                if(loggedUser.getLoggedWith() == LoggedUser.LOGGED_WITH_GOOGLE) {
                    return authenticationGateway.authenticateByOAuth("google", loggedUser.getOauthId()).flatMapCompletable(new Function<String, CompletableSource>() {
                        @Override
                        public CompletableSource apply(String token) throws Exception {
                            sessionGateway.storeSessionToken(token);
                            return Completable.complete();
                        }
                    });
                }
                if(loggedUser.getLoggedWith() == LoggedUser.LOGGED_WITH_FACEBOOK) {
                    return authenticationGateway.authenticateByOAuth("facebook", loggedUser.getOauthId()).flatMapCompletable(new Function<String, CompletableSource>() {
                        @Override
                        public CompletableSource apply(String token) {
                            sessionGateway.storeSessionToken(token);
                            return Completable.complete();
                        }
                    });
                }

                throw new RuntimeException("programming error - shouldn't happen");
            }
        });
    }

    private class UserNotLoggedException extends Exception {

    }
}
