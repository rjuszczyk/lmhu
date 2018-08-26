package eu.letmehelpu.android.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import eu.letmehelpu.android.EntryActivity
import eu.letmehelpu.android.LetMeHelpUApplication
import eu.letmehelpu.android.messaging.MyFirebaseInstanceIdService
import eu.letmehelpu.android.di.scope.ActivityScope
import eu.letmehelpu.android.login.LoginActivity
import eu.letmehelpu.android.messaging.MyFirebaseMessagingService
import eu.letmehelpu.android.messaging.SendMessageService
import eu.letmehelpu.android.offers.OfferListActivity
import javax.inject.Named

@Suppress("unused")
@Module
abstract class ActivityBuilder {

    @Binds
    @Named("AppContext")
    internal abstract fun provideAppContext(app: LetMeHelpUApplication): Context

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindLoginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindEntryActivity(): EntryActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [FragmentBuilder::class])
    internal abstract fun bindOfferListActivity(): OfferListActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindMyFirebaseInstanceIdService(): MyFirebaseInstanceIdService

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindSendMessageService(): SendMessageService


    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindMyFirebaseMessagingService(): MyFirebaseMessagingService



//    @ActivityScope
//    @ContributesAndroidInjector(modules = [MovieDetailsModule::class])
//    internal abstract fun bindMovieDetailsActivity(): MovieDetailsActivity
}
