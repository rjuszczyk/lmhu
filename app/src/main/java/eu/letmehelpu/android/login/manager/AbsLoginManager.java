package eu.letmehelpu.android.login.manager;

import android.arch.lifecycle.LifecycleObserver;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public abstract class AbsLoginManager implements LifecycleObserver {
    protected LoginCallback callback;
    protected AppCompatActivity activity;
    public AbsLoginManager(AppCompatActivity activity, LoginCallback callback) {
        this.callback = callback;
        activity.getLifecycle().addObserver(this);
        this.activity = activity;
    }

    public abstract void signIn();

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);
}
