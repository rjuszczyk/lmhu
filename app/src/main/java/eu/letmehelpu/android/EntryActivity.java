package eu.letmehelpu.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import eu.letmehelpu.android.offers.OfferListActivity;

public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserRepository userRepository = new UserRepository(getSharedPreferences("userRepository", MODE_PRIVATE));

        if(userRepository.isLogged()) {
            finish();
            startActivity(OfferListActivity.getStartIntent(this));
        } else {
            finish();
            startActivity(LoginActivity.getStartIntent(this));
        }
    }
}
