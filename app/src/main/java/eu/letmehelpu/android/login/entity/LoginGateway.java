package eu.letmehelpu.android.login.entity;

import eu.letmehelpu.android.login.UserState;
import io.reactivex.Observable;

public interface LoginGateway {
    /**
     * stores credentails used to reauthenticate
     */
    void storeLoggedUser(LoggedUser loggedUser);

    /**
     * removes saved credentails used to reauthenticate
     */
    void invalidateLoggedUser();

    boolean isLoggedUser();

    LoggedUser getLoggedUser();

    Observable<UserState> loggedUser();
}
