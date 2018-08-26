package eu.letmehelpu.android.login.entity;

public interface SessionGateway {
    void storeSessionToken(String token);
    String getSessionToken();
    void invalidate();
}
