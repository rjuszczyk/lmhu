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
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import eu.letmehelpu.android.R;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ScrollableWithCollapsing extends FrameLayout {
    private Collapsable collapsable;
    private int scrollableViewId;
    private int collapsableViewId;
    private View scrollableView;
    private View collapsableView;
    @Nullable
    private CollapsableAdapter collapsableAdapter;
    private ScrollEventsProvider scrollEventsProvider;

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

    public void setCollapsableAdapter(CollapsableAdapter collapsableAdapter)  {
        this.collapsableAdapter = collapsableAdapter;
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
        inflate(getContext(), R.layout.coltest, this);

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
                Log.d("onScrollChange", "onScrollChange() called with: v = [" + v + "], scrollX = [" + scrollX + "], scrollY = [" + scrollY + "], oldScrollX = [" + oldScrollX + "], oldScrollY = [" + oldScrollY + "]");
            }
        });

        if(collapsableView instanceof Collapsable) {
            collapsable = (Collapsable) collapsableView;
        } else {
            if(collapsableAdapter != null) {
                collapsableAdapter.init(collapsableView);
                collapsable = collapsableAdapter;
            } else {
                throw new RuntimeException("provide custom collapsableAdapter for view of type " + collapsableView.getClass().getName());
            }
        }

        collapsableView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return scrollableView.onTouchEvent(event);
            }
        });

        if(scrollEventsProvider == null) {
            tryCreateDefaultForView(scrollableView);
        }
        if(scrollEventsProvider == null) {
            throw new RuntimeException("provide custom scrollEventsProvider for view of type " + scrollableView.getClass().getName());
        }

        scrollableView.setPadding(
                scrollableView.getPaddingLeft(),
                collapsable.getCollapsableHeight(),
                scrollableView.getPaddingRight(),
                scrollableView.getPaddingBottom()
        );

        collapsable.updateScroll(scrollEventsProvider.getScrollFromView(scrollableView));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                collapsable.updateScroll(scrollEventsProvider.getScrollFromView(scrollableView));
            }
        }, 1);
        scrollEventsProvider.startProvidingScrollEventsFromView(scrollableView, collapsable);
    }

    private void tryCreateDefaultForView(View scroll) {
        if(scroll instanceof ScrollView) {
            final ScrollView scrollView = (ScrollView) scroll;
            scrollEventsProvider = new ScrollEventsProvider() {
                @Override
                public void startProvidingScrollEventsFromView(View view, final ScrollConsumer scrollConsumer) {
                    scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {


                        @Override
                        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                            scrollConsumer.updateScroll(scrollY);
                        }
                    });
                }

                @Override
                public void stopProvidingScrollEventsFromView(View view, ScrollConsumer scrollConsumer) {
                    scrollView.setOnScrollChangeListener(null);
                }

                @Override
                public int getScrollFromView(View view) {
                    return scrollView.getScrollY();
                }
            };
        }
        if(scroll instanceof RecyclerView) {
            final RecyclerView scrollView = (RecyclerView) scroll;
            LinearLayoutManager linearLayoutManager = ((LinearLayoutManager)scrollView.getLayoutManager());

            scrollEventsProvider = new ScrollEventsProvider() {
                RecyclerView.OnScrollListener listener;


                @Override
                public void startProvidingScrollEventsFromView(View view, final ScrollConsumer scrollConsumer) {




                    listener = new RecyclerView.OnScrollListener() {
                        int totalScrolled = 0;

                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if(newState == SCROLL_STATE_IDLE) {
                                CollapsingToolbarWithPagerIndicator collapsingToolbarWithPagerIndicator = (CollapsingToolbarWithPagerIndicator) collapsable;
                                int ttt = collapsingToolbarWithPagerIndicator.getTTT();
                                if(totalScrolled < ttt) {
                                    if (totalScrolled <  ttt/2) {

                                        recyclerView.smoothScrollBy(0, -totalScrolled);
                                    } else {
                                        if(totalScrolled>ttt)return;

                                        recyclerView.smoothScrollBy(0, ttt - totalScrolled);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalScrolled+=dy;


                            int scrollY  = totalScrolled;
                                scrollConsumer.updateScroll(scrollY);
//                            }

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
                    return scrollView.getScrollY();
                }


            };
        }
    }
}
