package eu.letmehelpu.android;

import android.content.SharedPreferences;

public class UserRepository {
    private final SharedPreferences sharedPreferences;

    public UserRepository(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public boolean isLogged() {
        return sharedPreferences.getBoolean("isLogged", false);
    }

    public void setLogged(LoggedUser logged) {
        SharedPreferences.Editor editor = sharedPreferences.edit()
                .putBoolean("isLogged", true)
                .putInt("loggedWith", logged.getLoggedWith());
        if(logged.getLoggedWith() == LoggedUser.LOGGED_WITH_APP) {
            editor.putString("userName", logged.getUserName());
            editor.putString("password", logged.getPassword());
        }
        if(logged.getLoggedWith() == LoggedUser.LOGGED_WITH_GOOGLE) {
            editor.putString("oauthId", logged.getOauthId());
        }

        editor.commit();
    }

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

    public void logout() {
        Network.getInstance().claerToken();
        sharedPreferences.edit()
                .remove("loggedWith")
                .putBoolean("isLogged", false)
                .commit();
    }
}
