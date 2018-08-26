package eu.letmehelpu.android;

import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import eu.letmehelpu.android.login.LoginActivity;
import eu.letmehelpu.android.login.entity.LoginGateway;
import eu.letmehelpu.android.offers.OfferListActivity;

public class EntryActivity extends DaggerAppCompatActivity {

    @Inject LoginGateway loginGateway;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(loginGateway.isLoggedUser()) {
            finish();
            startActivity(OfferListActivity.getStartIntent(this));
        } else {
            finish();
            startActivity(LoginActivity.getStartIntent(this));
        }
    }
}
