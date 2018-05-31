package eu.letmehelpu.android.offers;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import eu.letmehelpu.android.MainActivity;
import eu.letmehelpu.android.Network;
import eu.letmehelpu.android.R;
import eu.letmehelpu.android.TestApi;
import eu.letmehelpu.android.network.OfferItem;
import retrofit2.Call;
import retrofit2.Callback;

public class OfferListActivity extends AppCompatActivity {

    private View bottomNavIndicator;
    private View bottomNav;
    private RecyclerView offerList;
    private int bottomItemSelected = 0;
    private OffersAdapter offerListAdapter;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, OfferListActivity.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        offerList = findViewById(R.id.offer_list);
        initOfferListAdapter();

        bottomNavIndicator = findViewById(R.id.bottom_nav_indicator);
        bottomNav = findViewById(R.id.bottom_nav);
        findViewById(R.id.bottom_nav_item0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomItemSelected(0);
            }
        });
        findViewById(R.id.bottom_nav_item1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomItemSelected(1);
            }
        });
        findViewById(R.id.bottom_nav_item2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBottomItemSelected(2);
            }
        });
        findViewById(R.id.bottom_nav_item3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // setBottomItemSelected(3);
                startActivity(MainActivity.getStartIntent(OfferListActivity.this));
            }
        });

        bottomNav.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                float tX = getXOfBottomIndicator(bottomItemSelected);
                bottomNavIndicator.setTranslationX(tX);
            }
        });

        test();

        if(savedInstanceState != null) {
            bottomItemSelected = savedInstanceState.getInt("bottomItemSelected");
        }
        setBottomItemHighlighted(bottomItemSelected);
    }

    private void initOfferListAdapter() {
        offerListAdapter = new OffersAdapter();
        offerList.setAdapter(offerListAdapter);
        offerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }


    //    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            Response originalResponse = chain.proceed(chain.request());
//            if (Utils.isNetworkAvailable(this)) {
//                int maxAge = 60; // read from cache for 1 minute
//                return originalResponse.newBuilder()
//                        .header("Cache-Control", "public, max-age=" + maxAge)
//                        .build();
//            } else {
//                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
//                return originalResponse.newBuilder()
//                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                        .build();
//            }
//        }
//    }
    private void test() {
        TestApi testApi = Network.getInstance().getTestApi();
        testApi.work().enqueue(new Callback<List<OfferItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<OfferItem>> call, @NonNull retrofit2.Response<List<OfferItem>> response) {

                List<OfferItem> responseBody = response.body();
                if(responseBody != null) {
                    offerListAdapter.setItems(responseBody);
//                    for (int i = 0; i < length; i++) {
//                        OfferItem offerItem = responseBody.get(i);
//                        boolean isLast = i == length - 1;
//                        OfferItemView offerItemView = new OfferItemView(OfferListActivity.this);
//                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        int _8dp = (int) (getResources().getDisplayMetrics().density * 8);
//                        int _10dp = (int) (getResources().getDisplayMetrics().density * 10);
//
//                        if (isLast)
//                            params.setMargins(_8dp, _10dp, _8dp, _10dp);
//                        else
//                            params.setMargins(_8dp, _10dp, _8dp, 0);
//
//                        offerItemView.setLayoutParams(params);
//
//                        offerItemView.setOfferTitle(offerItem.getTitle());
//                        offerItemView.setContractor(offerItem.getContractor());
//                        offerItemView.setOfferStatus(offerItem.getStatus());
//                        offerItemView.setThumbnail(offerItem.getThumbnail());
//
//                        offerList.addView(offerItemView);
//                    }
                }
                Log.d("test", "onResponse() called with: call = [" + call + "], response = [" + response + "]");
            }

            @Override
            public void onFailure(@NonNull Call<List<OfferItem>> call, @NonNull Throwable t) {
                Log.d("test", "onFailure() called with: call = [" + call + "], t = [" + t + "]");
            }
        });
    }

    public void setBottomItemSelected(int bottomItemSelected) {
        this.bottomItemSelected = bottomItemSelected;
        float tX = getXOfBottomIndicator(bottomItemSelected);
        bottomNavIndicator.animate().translationX(tX).start();

        setBottomItemHighlighted(bottomItemSelected);
    }

    private void setBottomItemHighlighted(int bottomItemSelected) {
        findViewById(R.id.bottom_nav_item0).setSelected(bottomItemSelected == 0);
        findViewById(R.id.bottom_nav_item1).setSelected(bottomItemSelected == 1);
        findViewById(R.id.bottom_nav_item2).setSelected(bottomItemSelected == 2);
        findViewById(R.id.bottom_nav_item3).setSelected(bottomItemSelected == 3);
    }

    private float getXOfBottomIndicator(int bottomItemSelected) {
        float w = bottomNav.getWidth()/4f;
        float w2 = bottomItemSelected*w+w/2f;
        return w2 - bottomNavIndicator.getWidth()/2;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("bottomItemSelected", bottomItemSelected);
    }
}
