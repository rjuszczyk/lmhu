package eu.letmehelpu.android.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import eu.letmehelpu.android.di.scope.AppScope
import eu.letmehelpu.android.login.entity.LoginGateway
import eu.letmehelpu.android.messaging.MessagingManager
import eu.letmehelpu.android.messaging.MessagingTokenStoreage
import javax.inject.Named


@Module
class AppModule {

    @Provides
    @AppScope
    fun provideDefaultSharedPreferences(@Named("AppContext") context: Context): SharedPreferences {
        return context.getSharedPreferences("default", Context.MODE_PRIVATE)
    }

    @Provides
    @AppScope
    fun provideMessagingManager(loginGateway: LoginGateway, messagingTokenStoreage: MessagingTokenStoreage): MessagingManager {
        return MessagingManager(loginGateway, messagingTokenStoreage)
    }

    @Provides
    @AppScope
    fun provideMessagingTokenStoreage(sharedPreferences: SharedPreferences): MessagingTokenStoreage {
        return MessagingTokenStoreage(sharedPreferences)
    }

}