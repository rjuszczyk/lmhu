package eu.letmehelpu.android.di

import android.content.Context
import dagger.Module
import dagger.Provides
import eu.letmehelpu.android.Network
import eu.letmehelpu.android.TestApi
import eu.letmehelpu.android.di.scope.AppScope
import javax.inject.Named

@Module
class NetworkModule {

    @Provides
    @AppScope
    fun provideNetwork(
            @Named("AppContext") context: Context
    ): Network {
        return Network(
                context
        )
    }

    @Provides
    @AppScope
    fun provideTestApi(network:Network) :TestApi {
        return network.createOpenApi("https://letmehelpu-v2.preview.cloudart.pl/api/", TestApi::class.java)
    }
}