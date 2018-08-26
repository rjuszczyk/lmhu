package eu.letmehelpu.android.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import eu.letmehelpu.android.TestApi
import eu.letmehelpu.android.di.scope.AppScope
import eu.letmehelpu.android.login.AuthenticationGatewayImpl
import eu.letmehelpu.android.login.LoginGatewayImpl
import eu.letmehelpu.android.login.SessionGatewayImpl
import eu.letmehelpu.android.login.domain.*
import eu.letmehelpu.android.login.entity.AuthenticationGateway
import eu.letmehelpu.android.login.entity.LoginGateway
import eu.letmehelpu.android.login.entity.SessionGateway

@Module
class LoginModule {
    @Provides
    @AppScope
    fun provideLoginWithFacebookLoginUseCase(
            authenticationGateway: AuthenticationGateway,
            loginGateway: LoginGateway,
            sessionGateway: SessionGateway
    ): LoginWithFacebookLoginUseCase {
        return LoginWithFacebookLoginUseCase(authenticationGateway, loginGateway, sessionGateway)
    }
    @Provides
    @AppScope
    fun provideLoginWithGoogleLoginUseCase(
            authenticationGateway: AuthenticationGateway,
            loginGateway: LoginGateway,
            sessionGateway: SessionGateway
    ): LoginWithGoogleLoginUseCase {
        return LoginWithGoogleLoginUseCase(authenticationGateway, loginGateway, sessionGateway)
    }
    @Provides
    @AppScope
    fun provideLoginWithUsernameAndPasswordUseCase(
            authenticationGateway: AuthenticationGateway,
            loginGateway: LoginGateway,
            sessionGateway: SessionGateway
    ): LoginWithUsernameAndPasswordUseCase {
        return LoginWithUsernameAndPasswordUseCase(authenticationGateway, loginGateway, sessionGateway)
    }

    @Provides
    @AppScope
    fun provideLogoutUseCase(
            loginGateway: LoginGateway,
            sessionGateway: SessionGateway
    ) : LogoutUseCase {
        return LogoutUseCase(loginGateway, sessionGateway)
    }

    @Provides
    @AppScope
    fun provideReAuthenticateUseCase(
            authenticationGateway: AuthenticationGateway,
            loginGateway: LoginGateway,
            sessionGateway: SessionGateway
    ): ReAuthenticateUseCase {
        return ReAuthenticateUseCase(authenticationGateway, loginGateway, sessionGateway)
    }

    @Provides
    @AppScope
    fun provideLoginGateway(sharedPreferences: SharedPreferences) : LoginGateway {
        return LoginGatewayImpl(sharedPreferences)
    }

    @Provides
    @AppScope
    fun provideSessionGateway() : SessionGateway {
        return SessionGatewayImpl()
    }

    @Provides
    @AppScope
    fun provideAuthenticationGateway(testApi: TestApi) : AuthenticationGateway {
        return AuthenticationGatewayImpl(testApi)
    }

}