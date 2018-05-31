package eu.letmehelpu.android;

public class LoggedUser {
    public final static int LOGGED_WITH_APP = 1;
    public final static int LOGGED_WITH_GOOGLE = 2;
    public final static int LOGGED_WITH_FACEBOOK = 3;

    private final int loggedWith;
    private final String userName;
    private final String password;
    private final String oauthId;

    public LoggedUser(int loggedWith, String userName, String password) {
        this.loggedWith = loggedWith;
        this.userName = userName;
        this.password = password;
        this.oauthId = null;
    }

    public LoggedUser(int loggedWith, String  oauthId) {
        this.loggedWith = loggedWith;
        this.userName = null;
        this.password = null;
        this.oauthId = oauthId;
    }


    public int getLoggedWith() {
        return loggedWith;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getOauthId() {
        return oauthId;
    }
}
