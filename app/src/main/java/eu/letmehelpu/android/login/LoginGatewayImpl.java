package eu.letmehelpu.android.login;

import android.content.SharedPreferences;

import java.util.concurrent.Callable;

import eu.letmehelpu.android.login.entity.LoggedUser;
import eu.letmehelpu.android.login.entity.LoginGateway;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.internal.operators.observable.ObservableFromCallable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

public class LoginGatewayImpl implements LoginGateway {
    private final SharedPreferences sharedPreferences;
    private final BehaviorSubject<UserState> subject = BehaviorSubject.create();
    public LoginGatewayImpl(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void storeLoggedUser(LoggedUser loggedUser) {
        SharedPreferences.Editor editor = sharedPreferences.edit()
                .putBoolean("isLogged", true)
                //.putString("loggedUser", loggedUser.getUserDetails())
                .putInt("loggedWith", loggedUser.getLoggedWith());
        if(loggedUser.getLoggedWith() == LoggedUser.LOGGED_WITH_APP) {
            editor.putString("userName", loggedUser.getUserName());
            editor.putString("password", loggedUser.getPassword());
        }
        if(loggedUser.getLoggedWith() == LoggedUser.LOGGED_WITH_GOOGLE) {
            editor.putString("oauthId", loggedUser.getOauthId());
        }

        editor.commit();
        subject.onNext(new UserState(true, loggedUser));
    }

    @Override
    public void invalidateLoggedUser() {
        sharedPreferences.edit()
                .remove("loggedWith")
                .putBoolean("isLogged", false)
                .commit();
        subject.onNext(new UserState(false, null));
    }

    @Override
    public boolean isLoggedUser() {
        return sharedPreferences.getBoolean("isLogged", false);
    }

    @Override
    public LoggedUser getLoggedUser() {
        int loggedWith = sharedPreferences.getInt("loggedWith", 0);

        if(loggedWith == LoggedUser.LOGGED_WITH_APP) {
            String userName = sharedPreferences.getString("userName", null);
            String password = sharedPreferences.getString("password", null);
            return new LoggedUser(loggedWith, userName, password);
        }
        if(loggedWith == LoggedUser.LOGGED_WITH_GOOGLE) {
            String oauthId = sharedPreferences.getString("oauthId", null);
            return new LoggedUser(loggedWith, oauthId);
        }
        if(loggedWith == LoggedUser.LOGGED_WITH_FACEBOOK) {
            String oauthId = sharedPreferences.getString("oauthId", null);
            return new LoggedUser(loggedWith, oauthId);
        }

        throw new RuntimeException("not logged, check first if loggged!");
    }

    @Override
    public Observable<UserState> loggedUser() {
        return Observable.defer(new Callable<ObservableSource<? extends UserState>>() {
            @Override
            public ObservableSource<? extends UserState> call() throws Exception {
                if(isLoggedUser()) {
                    return subject.startWith(new UserState(true, getLoggedUser()));
                }
                return subject.startWith(new UserState(false, null));
            }
        });
    }
}
