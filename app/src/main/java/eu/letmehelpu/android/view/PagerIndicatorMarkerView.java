package eu.letmehelpu.android.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class PagerIndicatorMarkerView extends FrameLayout {
    View child;
    int selectedPage = 0;

    public PagerIndicatorMarkerView(@NonNull Context context) {
        super(context);
    }

    public PagerIndicatorMarkerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PagerIndicatorMarkerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PagerIndicatorMarkerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        child = getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = right - left;

        int availableSize = width - getPaddingLeft() - getPaddingRight();

        final int height = child.getMeasuredHeight();

        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)child.getLayoutParams();
        int childWidth = availableSize/2 - marginLayoutParams.leftMargin - marginLayoutParams.rightMargin;

        child.layout(
                getPaddingLeft() + marginLayoutParams.leftMargin,
                getPaddingTop()+marginLayoutParams.topMargin,
                getPaddingLeft() + marginLayoutParams.leftMargin + childWidth,
                getPaddingTop() + marginLayoutParams.topMargin +height);

        child.setTranslationX(getTranslationXForPosition(selectedPage));
    }

    public void selectPosition(int position) {
        selectedPage = position;
       // float translationX = getTranslationXForPosition(position);
       // child.animate().translationX(translationX).start();
    }

    public void updatePosition(float position) {
//        selectedPage = position;
        float translationX = getTranslationXForPosition(position);
        child.setTranslationX(translationX);
    }

    private float getTranslationXForPosition(float position) {
        int availableSize = getWidth() - getPaddingLeft() - getPaddingRight();
        return position*availableSize/2;
    }

    @Override
    public Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putInt("selectedPage", this.selectedPage);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle) // implicit null check
        {
            Bundle bundle = (Bundle) state;
            this.selectedPage = bundle.getInt("selectedPage");
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
}
