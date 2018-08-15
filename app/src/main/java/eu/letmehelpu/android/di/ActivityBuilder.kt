package eu.letmehelpu.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import eu.letmehelpu.android.MyFirebaseMessagingService
import eu.letmehelpu.android.conversation.ConversationActivity
import eu.letmehelpu.android.di.scope.ActivityScope
import eu.letmehelpu.android.messaging.SendMessageService

@Suppress("unused")
@Module
abstract class ActivityBuilder {

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindConversationActivity(): ConversationActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindMyFirebaseMessagingService(): MyFirebaseMessagingService

    @ActivityScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindSendMessageService(): SendMessageService


//    @ActivityScope
//    @ContributesAndroidInjector(modules = [MovieDetailsModule::class])
//    internal abstract fun bindMovieDetailsActivity(): MovieDetailsActivity
}
