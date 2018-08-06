package eu.letmehelpu.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import eu.letmehelpu.android.R;

public class BottomNavigationBar extends FrameLayout {
    private int barHeight;
    private View bottomNavIndicator;
    private int bottomItemSelected = 0;

    public BottomNavigationBar(@NonNull Context context) {
        super(context);
        init(null, 0);
    }

    public BottomNavigationBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BottomNavigationBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);

    }

    void init(@Nullable AttributeSet attrs, int defStyleAttr) {
        if(attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.BottomNavigationBar, defStyleAttr,0);
            try {
                barHeight = (int) a.getDimension(R.styleable.BottomNavigationBar_bar_height, getResources().getDisplayMetrics().density*54);
            } finally {
                a.recycle();
            }
        } else {
            barHeight = (int) getResources().getDisplayMetrics().density*54;
        }

        inflate(getContext(), R.layout.bottom_navigation_bar, this);

        bottomNavIndicator = findViewById(R.id.bottom_nav_indicator);

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
                setBottomItemSelected(3);
                //startActivity(MainActivity.getStartIntent(eu.letmehelpu.android.offers.OfferListActivity.this));
            }
        });

        addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                setBottomItemSelected(bottomItemSelected);
            }
        });
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        int id = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "id", 0);
        if(id == R.id.bottom_nav_container) {
            return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, barHeight, Gravity.BOTTOM);
        }
        if(id == R.id.shadow) {
            int _8dp = (int) (getResources().getDisplayMetrics().density*8);
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, _8dp, Gravity.BOTTOM);
            lp.bottomMargin = barHeight;
            return lp;
        }
        return super.generateLayoutParams(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float _23dp = getResources().getDisplayMetrics().density*23;
        int heightSpec = MeasureSpec.makeMeasureSpec((int) (barHeight + _23dp), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightSpec);
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
        float w = getWidth()/4f;
        float w2 = bottomItemSelected*w+w/2f;
        return w2 - bottomNavIndicator.getWidth()/2;
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("bottomItemSelected", this.bottomItemSelected);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            this.bottomItemSelected = bundle.getInt("bottomItemSelected");
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
}
