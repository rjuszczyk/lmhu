package eu.letmehelpu.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;

import eu.letmehelpu.android.R;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ScrollableCollapsingManager {
    private final CollapsingToolbarWithPagerIndicator collapsableView;
    private RecyclerView scrollableView;

    private ScrollEventsProvider scrollEventsProvider;

//    public static int lastDelta2 = 0;

    public ScrollableCollapsingManager(
            CollapsingToolbarWithPagerIndicator collapsableView,
        RecyclerView scrollableView) {
        this.collapsableView = collapsableView;
        this.scrollableView = null;//scrollableView;
    }

    public void attachRecycler(RecyclerView recyclerView) {
        if(scrollEventsProvider!= null)scrollEventsProvider.stopProvidingScrollEventsFromView(scrollableView, collapsableView);

        int lastDelta = 0;
        int currentDelta = 0;
        if(scrollableView != null) {
            lastDelta = ScrollableCollapsingManager.deltaMap.containsKey(scrollableView.getId()) ? ScrollableCollapsingManager.deltaMap.get(scrollableView.getId()) : 0;
            currentDelta = ScrollableCollapsingManager.deltaMap.containsKey(recyclerView.getId()) ? ScrollableCollapsingManager.deltaMap.get(recyclerView.getId()) : 0;
        }
        int lastScroll = 0;
        if(scrollableView != null)
            lastScroll = ScrollableWithCollapsing.getScrollFromRecyclerView(scrollableView, collapsableView);
        scrollableView = recyclerView;
        int currentScroll;
        int desiredScroll;

        if(scrollableView.getPaddingTop() == 0) {
            scrollableView.setPadding(
                    scrollableView.getPaddingLeft(),
                    collapsableView.getCollapsableHeight(),
                    scrollableView.getPaddingRight(),
                    scrollableView.getPaddingBottom()
            );
            currentScroll = 0;
            desiredScroll = -collapsableView.getCollapsableHeight();
            scrollableView.scrollBy(0, desiredScroll - currentScroll);
//            currentScroll = 0;//ScrollableWithCollapsing.getScrollFromRecyclerView(scrollableView, collapsableView);;
//            desiredScroll = currentDelta + collapsableView.getTTT()-collapsableView.getCollapsableHeight();
        }
//        else
            {
            currentScroll = ScrollableWithCollapsing.getScrollFromRecyclerView(scrollableView, collapsableView);
            if(lastDelta>0 && currentDelta>0) desiredScroll = currentScroll;
            else if(lastDelta > 0 && currentDelta <= 0) {
                desiredScroll = collapsableView.getTTT();
            } else
                if(lastScroll>=0 && lastScroll<=collapsableView.getTTT() &&
                        currentScroll>=0 && currentScroll<=collapsableView.getTTT()     ) {
                    desiredScroll = lastScroll;
                } else {
                    Log.d("Radek", "lastDelta = " + lastDelta + " currentDelta = " + currentDelta);
                    Log.d("Radek", "lastScroll = " + lastScroll + " currentScroll = " + currentScroll);
                    desiredScroll = currentScroll;
                }
        //    desiredScroll = lastScroll;
        }



        prepareScrollEventsProviderForRecyclerView(scrollableView);
        initScrollingEffect();

        scrollableView.scrollBy(0, desiredScroll - currentScroll);// - currentScroll);
        scrollEventsProvider.startProvidingScrollEventsFromView(scrollableView, collapsableView);

        collapsableView.updateScrollSmoothly(desiredScroll);
    }

    public static HashMap<Integer, Integer> deltaMap = new HashMap<>();

    public void setScrollEventsProvider(ScrollEventsProvider scrollEventsProvider)  {
        this.scrollEventsProvider = scrollEventsProvider;
    }




    private void initScrollingEffect() {




        collapsableView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.d("onScrollChange", "onScrollChange() called with: v = [" + v + "], scrollX = [" + scrollX + "], scrollY = [" + scrollY + "], oldScrollX = [" + oldScrollX + "], oldScrollY = [" + oldScrollY + "]");
            }
        });




        collapsableView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return scrollableView.onTouchEvent(event);
            }
        });




    }

    public int getDetlatScroll(RecyclerView recyclerView) {

        int scroll = getScrollFromRecyclerView(recyclerView, collapsableView);
        CollapsingToolbarWithPagerIndicator collapsingToolbarWithPagerIndicator = (CollapsingToolbarWithPagerIndicator) collapsableView;
        int ttt = collapsingToolbarWithPagerIndicator.getTTT();
        return scroll - ttt;

    }

    public int getDetlatScroll2(RecyclerView recyclerView) {

        int scroll = getScrollFromRecyclerView(recyclerView, collapsableView);
        CollapsingToolbarWithPagerIndicator collapsingToolbarWithPagerIndicator = (CollapsingToolbarWithPagerIndicator) collapsableView;
        int ttt = collapsingToolbarWithPagerIndicator.getCollapsableHeight();
        return scroll - ttt;

    }


    private void prepareScrollEventsProviderForRecyclerView(final RecyclerView scrollView) {
        scrollEventsProvider = new ScrollEventsProvider() {
            RecyclerView.OnScrollListener listener;

            @Override
            public void startProvidingScrollEventsFromView(View view, final ScrollConsumer scrollConsumer) {

                listener = new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        int ttt = collapsableView.getTTT();

                        int lastDelta = getDetlatScroll(recyclerView);
                        ScrollableCollapsingManager.deltaMap.put(recyclerView.getId(), lastDelta);
//                            lastDelta2 = getDetlatScroll2(recyclerView);
                        if(newState == SCROLL_STATE_IDLE) {
                            int scrollY = getScrollFromRecyclerView(recyclerView, collapsableView);

                            if(scrollY < ttt) {
                                if (scrollY <  ttt/2) {

                                    recyclerView.smoothScrollBy(0, -scrollY);
                                } else {



                                    recyclerView.smoothScrollBy(0, ttt - scrollY);
                                }
                            }


                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        scrollConsumer.updateScroll(getScrollFromRecyclerView(recyclerView, collapsableView));

                        int ds = getDetlatScroll(recyclerView);
                        Log.d("delta", ""+ds);
                    }
                };
                scrollView.addOnScrollListener(listener);
            }

            @Override
            public void stopProvidingScrollEventsFromView(View view, ScrollConsumer scrollConsumer) {
                scrollView.removeOnScrollListener(listener);
            }

            @Override
            public int getScrollFromView(View view) {
                int lastDelta = ScrollableCollapsingManager.deltaMap.containsKey(view.getId()) ? ScrollableCollapsingManager.deltaMap.get(view.getId()) : 0;
                if(lastDelta < 0) return 0;

                if(lastDelta != 0) {
                    int ttt = collapsableView.getTTT();
                    return lastDelta + ttt;
                } else{
                    return 0;
                }
            }
        };
    }

    public static int getScrollFromRecyclerView(RecyclerView recyclerView, Collapsable ttt) {
        LinearLayoutManager linearLayoutManager1= ((LinearLayoutManager)recyclerView.getLayoutManager());
        if(linearLayoutManager1 == null) return 0;
        int firstItemPosition = linearLayoutManager1.findFirstVisibleItemPosition();
        int _95dp = (int) (recyclerView.getResources().getDisplayMetrics().density * 95);
        int itemHeight = _95dp;
        int scrollY = 0;
        if(firstItemPosition != -1) {
            View firstView = linearLayoutManager1.findViewByPosition(firstItemPosition);
            int top = getItemTop(firstItemPosition, firstView, ttt);
            scrollY = firstItemPosition*itemHeight  - top;
        }
        Log.d("scrollY", "" + scrollY );
        return scrollY;//scrollView.getScrollY();
    }

    private static int getItemTop(int firstItemPosition, View firstView, Collapsable collapsable) {
        int _10dp = (int) (firstView.getResources().getDisplayMetrics().density * 10);
        int top = firstView.getTop() - _10dp;
        return top - collapsable.getCollapsableHeight();
    }
}
