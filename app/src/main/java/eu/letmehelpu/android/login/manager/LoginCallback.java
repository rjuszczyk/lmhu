package eu.letmehelpu.android.login.manager;

public interface LoginCallback {
    void showProgress();
    void onLoggedIn();
    void onFailed(String message);
    void onCancel();
}
