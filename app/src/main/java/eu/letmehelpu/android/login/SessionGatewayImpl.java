package eu.letmehelpu.android.login;

import eu.letmehelpu.android.login.entity.SessionGateway;

public class SessionGatewayImpl implements SessionGateway {
    private String token;
    @Override
    public void storeSessionToken(String token) {
        this.token = token;
    }

    @Override
    public String getSessionToken() {
        return token;
    }

    @Override
    public void invalidate() {
        token = null;
    }
}
