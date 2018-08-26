package eu.letmehelpu.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import eu.letmehelpu.android.UserFragment
import eu.letmehelpu.android.conversations.ConversationsFragment
import eu.letmehelpu.android.di.scope.FragmentScope
import eu.letmehelpu.android.offers.OffersFragment


@Suppress("unused")
@Module
abstract class FragmentBuilder {

    @FragmentScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindOffersFragment(): OffersFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindConversationsFragment(): ConversationsFragment
    @FragmentScope
    @ContributesAndroidInjector(modules = [])
    internal abstract fun bindUserFragment(): UserFragment

}
