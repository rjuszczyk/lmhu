package eu.letmehelpu.android;

import com.crashlytics.android.Crashlytics;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import eu.letmehelpu.android.di.DaggerAppComponent;
import io.fabric.sdk.android.Fabric;

public class LetMeHelpUApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder()
                .create(this);
    }
}
