package eu.letmehelpu.android.login.domain;

import eu.letmehelpu.android.login.entity.LoginGateway;
import eu.letmehelpu.android.login.entity.SessionGateway;

public class LogoutUseCase {
    private final LoginGateway loginGateway;
    private final SessionGateway sessionGateway;

    public LogoutUseCase(LoginGateway loginGateway, SessionGateway sessionGateway) {
        this.loginGateway = loginGateway;
        this.sessionGateway = sessionGateway;
    }

    public void logout() {
        loginGateway.invalidateLoggedUser();
        sessionGateway.invalidate();
    }
}
