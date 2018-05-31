package eu.letmehelpu.android;

import android.app.Application;

public class LetMeHelpUApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Network.init(this);
    }
}
