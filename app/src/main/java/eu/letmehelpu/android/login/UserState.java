package eu.letmehelpu.android.login;

import android.support.annotation.Nullable;

import eu.letmehelpu.android.login.entity.LoggedUser;

public class UserState {
    public final boolean loggedIn;
    @Nullable
    public final LoggedUser loggedUser;

    public UserState(boolean loggedIn, @Nullable LoggedUser loggedUser) {
        this.loggedIn = loggedIn;
        this.loggedUser = loggedUser;
    }
}
