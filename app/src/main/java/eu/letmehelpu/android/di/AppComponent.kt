package eu.letmehelpu.android.di


import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import eu.letmehelpu.android.LetMeHelpUApplication
import eu.letmehelpu.android.di.scope.AppScope

@Component(modules = [
    AppModule::class,
    LoginModule::class,
    NetworkModule::class,
    ActivityBuilder::class,
    MessagingModule::class,
    AndroidSupportInjectionModule::class
])

@AppScope
interface AppComponent : AndroidInjector<LetMeHelpUApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<LetMeHelpUApplication>()
}
