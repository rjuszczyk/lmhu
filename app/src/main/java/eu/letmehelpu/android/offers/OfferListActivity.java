package eu.letmehelpu.android.offers;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CollapsingToolbarLayout;
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
import eu.letmehelpu.android.view.CollapsingToolbarWithPagerIndicator;
import eu.letmehelpu.android.view.PagerIndicator;
import eu.letmehelpu.android.view.ScrollableCollapsingManager;
import eu.letmehelpu.android.view.ScrollableWithCollapsing;
import retrofit2.Call;
import retrofit2.Callback;

@RequiresApi(api = Build.VERSION_CODES.M)
public class OfferListActivity extends AppCompatActivity implements PagerIndicator.IndicatorChangeListener {

    private View bottomNavIndicator;
    private View bottomNav;
    private RecyclerView offerList;
    private RecyclerView offerList2;
    ScrollableCollapsingManager scrollableCollapsingManager;
    private CollapsingToolbarWithPagerIndicator collapsingToolbar;
    private int bottomItemSelected = 0;





    private int delta = -1;


    public static Intent getStartIntent(Context context) {
        return new Intent(context, OfferListActivity.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);




        if(savedInstanceState != null) {
            delta = savedInstanceState.getInt("delta");
           // linearLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable("linearLayoutManager"));

        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        offerList = findViewById(R.id.offer_list);
        offerList2 = findViewById(R.id.offer_list2);
        collapsingToolbar = findViewById(R.id.collapsingToolbar);

        scrollableCollapsingManager = new ScrollableCollapsingManager(collapsingToolbar, offerList);


        collapsingToolbar.setIndicatorListener(this);
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
        RecyclerView.LayoutManager linearLayoutManager  = new LinearLayoutManager(OfferListActivity.this, LinearLayoutManager.VERTICAL, false);
        offerList.setLayoutManager(linearLayoutManager);

        offerList2.setLayoutManager(new LinearLayoutManager(OfferListActivity.this, LinearLayoutManager.VERTICAL, false));


    }

    static boolean s = true;
    private void test() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                TestApi testApi = Network.getInstance().getTestApi();
                testApi.work().enqueue(new Callback<List<OfferItem>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<OfferItem>> call, @NonNull retrofit2.Response<List<OfferItem>> response) {

                        List<OfferItem> responseBody = response.body();
                        if(responseBody != null) {
                            List<OfferItem>  responseBody2 = responseBody;//.subList(0,3);
                            OffersAdapter offerListAdapter = new OffersAdapter();
                            offerListAdapter.setItems(responseBody2);
                            int h = offerList.getHeight() - collapsingToolbar.getTTT2();
                            int _95dp = (int) (offerList.getResources().getDisplayMetrics().density * 95);
                            int hh = responseBody2.size()*_95dp;
                            int dh = h-hh;
                            if(dh>0) {
                                offerListAdapter.setTransparentItem(dh);
                            }

                            offerList.setAdapter(offerListAdapter);

                            OffersAdapter offerListAdapter2 = new OffersAdapter();
                            offerListAdapter2.setItems(responseBody);
                            offerList2.setAdapter(offerListAdapter2);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    scrollableCollapsingManager.attachRecycler(offerList);
                                }
                            }, 1);


                        }
                        Log.d("test", "onResponse() called with: call = [" + call + "], response = [" + response + "]");
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<OfferItem>> call, @NonNull Throwable t) {
                        Log.d("test", "onFailure() called with: call = [" + call + "], t = [" + t + "]");
                    }
                });
            }
        }, 1000);


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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("bottomItemSelected", bottomItemSelected);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) offerList.getLayoutManager();
//        if(linearLayoutManager != null) {
//            outState.putParcelable("linearLayoutManager", linearLayoutManager.onSaveInstanceState());
//        }


//        outState.putInt("delta", scrollableWithCollapsing.getDetlatScroll());



    }

    @Override
    public void onPageChanged(int page) {
        if(page == 0) {
            scrollableCollapsingManager.attachRecycler(offerList);
            offerList.setVisibility(View.VISIBLE);
            offerList2.setVisibility(View.VISIBLE);
            offerList.setTranslationX(offerList.getWidth());
            offerList.animate().translationX(0).start();
            offerList2.animate().translationX(-offerList.getWidth()).start();
        } else {
            scrollableCollapsingManager.attachRecycler(offerList2);
            offerList.setVisibility(View.VISIBLE);
            offerList2.setVisibility(View.VISIBLE);
            offerList2.setTranslationX(offerList2.getWidth());
            offerList2.animate().translationX(0).start();
            offerList.animate().translationX(-offerList2.getWidth()).start();
        }
    }
}
