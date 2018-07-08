package eu.letmehelpu.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import eu.letmehelpu.android.R;

public class PagerIndicator extends FrameLayout {
    private float indicatorMarkerHeight;
    private float indicatorHeight;
    private float initialHorizontalMargin;
    private float initialRadius;
    private View optionsContainer;
    private CardView card;
    private View split;
    private View option0;
    private View option1;
    private CustomTestView indicator;
    private int selectedPage;
    private ViewPager viewPager;

    public PagerIndicator(@NonNull Context context) {
        super(context);
        init(null);
    }

    public PagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public PagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    private void init(@Nullable AttributeSet attrs) {
        indicatorMarkerHeight = getDefaultIndicatorHeight();

        if(attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.PagerIndicator,
                    0, 0);
            try {
                indicatorHeight = a.getDimension(R.styleable.PagerIndicator_indicatorHeight, getDefaultIndicatorHeight());
                initialHorizontalMargin = a.getDimension(R.styleable.PagerIndicator_initialHorizontalMargin, getDefaultHorizontalMargin());
                initialRadius = a.getDimension(R.styleable.PagerIndicator_initialRadius, getDefaultRadius());
            } finally {
                a.recycle();
            }
        } else {
            indicatorHeight = getDefaultIndicatorHeight();
        }

        View view = inflate(getContext(), R.layout.pager_indicator, this);
        option0 = view.findViewById(R.id.option1);
        option1 = view.findViewById(R.id.option2);

        option0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setPageSelected(0);
            }
        });
        option1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setPageSelected(1);
            }
        });

        optionsContainer = view.findViewById(R.id.optionsContainer);
        card = view.findViewById(R.id.card);
        split = view.findViewById(R.id.split);

        indicator = view.findViewById(R.id.indicator);

        setParamsHeight(optionsContainer, indicatorHeight);
        setParamsHeight(card, indicatorHeight);

        highlightSelected(selectedPage);

    }

    public void setIndicatorHeight(float indicatorHeight) {
        setParamsHeight(optionsContainer, indicatorHeight);
        setParamsHeight(card, indicatorHeight);
    }

    public void setInitialHorizontalMargin(float initialHorizontalMargin) {
        this.initialHorizontalMargin = initialHorizontalMargin;
    }
    public void setInitialRadius(float initialRadius) {
        this.initialRadius = initialRadius;
    }

    public void updateProgress(float pr) {
        split.setAlpha(pr);

        float from1 = initialRadius;
        float to1 = 0;

        float value1 = from1*(1-pr) + to1*pr;
        card.setRadius(value1 < 0.5 ? 0 : value1);

        float from = initialHorizontalMargin;
        float to = 0;

        float value = from*(1-pr) + to*pr;

        {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) card.getLayoutParams();
            params.leftMargin = (int) value;
            params.rightMargin = (int) value;
            card.setLayoutParams(params);
        }
        {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) indicator.getLayoutParams();
            params.leftMargin = (int) value;
            params.rightMargin = (int) value;
            indicator.setLayoutParams(params);
        }
    }

    private void setParamsHeight(View optionsContainer, float indicatorHeight) {
        MarginLayoutParams params = (MarginLayoutParams) optionsContainer.getLayoutParams();
        params.height = (int) indicatorHeight;
        optionsContainer.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float getDefaultIndicatorHeight() {
        return getResources().getDisplayMetrics().density * 46;
    }

    private float getDefaultHorizontalMargin() {
        return getResources().getDisplayMetrics().density * 8;
    }

    private float getDefaultRadius() {
        return getResources().getDisplayMetrics().density * 10;
    }

    public float getIndicatorHeight() {
        return indicatorHeight;
    }

    public float getIndicatorMarkerHeight() {
        return indicatorMarkerHeight;
    }

    private void highlightSelected(int page) {
        selectedPage = page;
        option0.setSelected(page == 0);
        option1.setSelected(page == 1);
    }

    public void updateIndicator(float position) {
        indicator.updatePosition(position);
    }

    public void setPageSelected(int page) {
        highlightSelected(page);
        indicator.selectPosition(page);
        if(viewPager!=null) {
            viewPager.setCurrentItem(page);
        }
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("selectedPage", this.selectedPage); // ... save stuff
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            this.selectedPage = bundle.getInt("selectedPage"); // ... load stuff
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    public void setupWithViewPager(ViewPager mViewPager) {
        viewPager = mViewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                float pos = position + positionOffset;
                updateIndicator(pos);
            }

            @Override
            public void onPageSelected(int position) {
                setPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(viewPager!=null) {
            viewPager.setCurrentItem(selectedPage);
        }
    }
}
