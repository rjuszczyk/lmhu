package eu.letmehelpu.android;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import eu.letmehelpu.android.di.DaggerAppComponent;

public class LetMeHelpUApplication extends DaggerApplication {

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder()
                .create(this);
    }
}
