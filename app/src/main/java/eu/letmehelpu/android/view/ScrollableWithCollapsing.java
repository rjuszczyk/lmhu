package eu.letmehelpu.android.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import eu.letmehelpu.android.R;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ScrollableWithCollapsing extends FrameLayout {
    private CollapsingToolbarWithPagerIndicator collapsableView;
    private RecyclerView scrollableView;
    private int scrollableViewId;
    private int collapsableViewId;

    private ScrollEventsProvider scrollEventsProvider;

//    public static int lastDelta2 = 0;

    public ScrollableWithCollapsing(@NonNull Context context) {
        super(context);
        init(null);
    }

    public ScrollableWithCollapsing(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ScrollableWithCollapsing(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public ScrollableWithCollapsing(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void attachRecycler(RecyclerView recyclerView) {
        prepareScrollEventsProviderForRecyclerView(recyclerView);

    }

    public void setScrollEventsProvider(ScrollEventsProvider scrollEventsProvider)  {
        this.scrollEventsProvider = scrollEventsProvider;
    }

    public void setScrollableViewId(@IdRes int scrollableViewId) {
        this.scrollableViewId = scrollableViewId;
    }

    public void setCollapsableViewId(@IdRes int collapsableViewId) {
        this.collapsableViewId = collapsableViewId;
    }

    void init(@Nullable AttributeSet attrs) {
        int scrollableId = 0;
        int collapsableId = 0;
        if(attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ScrollableWithCollapsing,
                    0,0);
            try {
                scrollableId = a.getResourceId(R.styleable.ScrollableWithCollapsing_scvScrollableViewId, 0);
                collapsableId = a.getResourceId(R.styleable.ScrollableWithCollapsing_scvCollapsingViewId, 0);
            } finally {
                a.recycle();
            }
        }

        if(scrollableId != 0) {
            scrollableViewId = scrollableId;
        }
        if(collapsableId != 0) {
            collapsableViewId = collapsableId;
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(!changed) return;
        initScrollingEffect();
    }

    int lastCollapsableHeight = -1;

    private void initScrollingEffect() {



        if(scrollableViewId != 0) {
            scrollableView = findViewById(scrollableViewId);
        } else {
            throw new RuntimeException("You have to specify scvScrollableViewId in ScrollableWithCollapsing or set using setScrollableId");
        }
        if(collapsableViewId != 0) {
            collapsableView = findViewById(collapsableViewId);
        } else {
            throw new RuntimeException("You have to specify collapsableId in ScrollableWithCollapsing or set using setCollapsableId");
        }

        collapsableView.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }
        });




        collapsableView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return scrollableView.onTouchEvent(event);
            }
        });

        if(scrollEventsProvider == null) {
            prepareScrollEventsProviderForRecyclerView(scrollableView);
        }
        if(scrollEventsProvider == null) {
            throw new RuntimeException("provide custom scrollEventsProvider for view of type " + scrollableView.getClass().getName());
        }

        Toolbar toolbar = collapsableView.toolbar;

        toolbar.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                lastCollapsableHeight = collapsableView.getCollapsableHeight();
                scrollableView.setPadding(
                        scrollableView.getPaddingLeft(),
                        collapsableView.getCollapsableHeight(),
                        scrollableView.getPaddingRight(),
                        scrollableView.getPaddingBottom()
                );
                collapsableView.updateScroll(scrollEventsProvider.getScrollFromView(scrollableView));
                v.removeOnLayoutChangeListener(this);
            }
        });
        scrollEventsProvider.startProvidingScrollEventsFromView(scrollableView, collapsableView);
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
                int lastDelta = ScrollableCollapsingManager.deltaMap.containsKey(view) ? ScrollableCollapsingManager.deltaMap.get(view) : 0;
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

        return scrollY;//scrollView.getScrollY();
    }

    private static int getItemTop(int firstItemPosition, View firstView, Collapsable collapsable) {
        int _10dp = (int) (firstView.getResources().getDisplayMetrics().density * 10);
        int top = firstView.getTop() - _10dp;
        return top - collapsable.getCollapsableHeight();
    }
}
