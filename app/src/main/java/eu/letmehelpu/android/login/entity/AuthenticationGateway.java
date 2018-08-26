package eu.letmehelpu.android.login.entity;

import io.reactivex.Single;

public interface AuthenticationGateway {
    Single<String> authenticate(String username, String password);
    Single<String> authenticateByOAuth(String source, String id);
}
