package eu.letmehelpu.android.offers;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import dagger.android.support.DaggerAppCompatActivity;
import eu.letmehelpu.android.R;
import eu.letmehelpu.android.UserFragment;
import eu.letmehelpu.android.conversations.ConversationsFragment;
import eu.letmehelpu.android.view.BottomNavigationBar;

public class OfferListActivity extends DaggerAppCompatActivity {

    public static Intent getStartIntent(Context context) {
        return new Intent(context, OfferListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers2);

        BottomNavigationBar bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnBottomItemSelected(new BottomNavigationBar.OnBottomItemSelected() {
            @Override
            public void onItemSelected(int index) {
                int currentIndex = -1;
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
                if(currentFragment != null) {
                    currentIndex = Integer.parseInt(currentFragment.getTag());
                }
                if(currentIndex != index) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    if(currentIndex != -1) {
                        if(currentIndex<index) {
                            transaction.setCustomAnimations(R.anim.right_in, R.anim.right_out);
                        } else {
                            transaction.setCustomAnimations(R.anim.left_in, R.anim.left_out);
                        }
                    }
//                    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

                    if(index%2==0) {
                        transaction.replace(R.id.container, new OffersFragment(), "" + index);
                    } else {
                        //transaction.replace(R.id.container, new UserFragment(), "" + index);
                        transaction.replace(R.id.container, ConversationsFragment.Companion.createFragment(10L), "" + index);
                    }
                    transaction.commit();
                }
//                if(index==1)
//                    startActivity(ChatConversationActivity.getStartIntent(OfferListActivity.this));
//                if(index==2) {
//                    startActivity(MainActivity.getStartIntent(OfferListActivity.this));
//                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}