package eu.letmehelpu.android.chat;

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

public class UserView extends android.support.v7.widget.CardView {
    private float initialHorizontalMargin;
    private float initialRadius;
    private CardView card;
    private float elevationFrom;
    private float elevationTo;

    public UserView(@NonNull Context context) {
        super(context);
        init(null);
    }

    public UserView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public UserView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if(attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.PagerIndicator,
                    0, 0);
            try {
                initialHorizontalMargin = a.getDimension(R.styleable.PagerIndicator_initialHorizontalMargin, getDefaultHorizontalMargin());
                initialRadius = a.getDimension(R.styleable.PagerIndicator_initialRadius, getDefaultRadius());
            } finally {
                a.recycle();
            }
        }

        elevationFrom = getResources().getDisplayMetrics().density*2;
        elevationTo = getResources().getDisplayMetrics().density*8;

        View view = inflate(getContext(), R.layout.user_view, this);

        card = this;
    }

    public void setInitialHorizontalMargin(float initialHorizontalMargin) {
        this.initialHorizontalMargin = initialHorizontalMargin;
    }
    public void setInitialRadius(float initialRadius) {
        this.initialRadius = initialRadius;
    }

    public void updateProgress(float pr) {
        float from1 = initialRadius;
        float to1 = 0;

        float value1 = from1*(1-pr) + to1*pr;
        card.setRadius(value1 < 0.5 ? 0 : value1);


        float currentElevation = elevationFrom*(1-pr) + pr*elevationTo;
        card.setElevation(currentElevation);

        float from = initialHorizontalMargin;
        float to = 0;

        float value = from*(1-pr) + to*pr;

        {
            MarginLayoutParams params = (MarginLayoutParams) card.getLayoutParams();
            params.leftMargin = (int) value;
            params.rightMargin = (int) value;
            card.setLayoutParams(params);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float getDefaultHorizontalMargin() {
        return getResources().getDisplayMetrics().density * 8;
    }

    private float getDefaultRadius() {
        return getResources().getDisplayMetrics().density * 10;
    }

    public float getInitialHorizontalMargin() {
        return initialHorizontalMargin;
    }
}
