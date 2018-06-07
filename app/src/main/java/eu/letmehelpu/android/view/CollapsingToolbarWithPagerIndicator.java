package eu.letmehelpu.android.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import eu.letmehelpu.android.R;
import eu.letmehelpu.android.Spline;

public class CollapsingToolbarWithPagerIndicator extends FrameLayout implements Collapsable {

    private float indicatorHeight;
    private float initialHorizontalMargin;
    private float initialRadius;
    private float toolbarShadowToBeShownAfter = 150;
    private PagerIndicator pagerIndicator;
    private float imageHeight;
    private View top;
    private View parallaxImage;
    private View logo;
    private View toolbarShadow;
    private View image;
    public Toolbar toolbar;

    public CollapsingToolbarWithPagerIndicator(@NonNull Context context) {
        super(context);
        init(null);
    }

    public CollapsingToolbarWithPagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CollapsingToolbarWithPagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @SuppressWarnings("unused")
    public CollapsingToolbarWithPagerIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    ValueAnimator animator;
    final float[] a = new float[]{0};
    int animateTo = 0;
    ValueAnimator.AnimatorUpdateListener listenerAnim = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {

            a[0] = (float) animation.getAnimatedValue();

            updateIndicatorProgress();
        }
    };


    ValueAnimator animatorSmoothly;
    float currentScroll;
    float targetScroll;
    ValueAnimator.AnimatorUpdateListener listenerAnimSmoothly = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {

            updateScroll((Integer) animation.getAnimatedValue(), true);
        }
    };


    private void init(@Nullable AttributeSet attrs) {
        if(attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CollapsingToolbarWithPagerIndicator,
                    R.attr.collapsingToolbarStyle, R.style.CollapsingToolbarBase);
            try {
                indicatorHeight = a.getDimension(R.styleable.CollapsingToolbarWithPagerIndicator_ctindicatorHeight, getDefaultIndicatorHeight());
                initialHorizontalMargin = a.getDimension(R.styleable.CollapsingToolbarWithPagerIndicator_ctinitialHorizontalMargin, getDefaultHorizontalMargin());
                toolbarShadowToBeShownAfter = a.getDimension(R.styleable.CollapsingToolbarWithPagerIndicator_ctToolbarShadowAfter, getDefaultToolbarShadowAfter());
                initialRadius = a.getDimension(R.styleable.CollapsingToolbarWithPagerIndicator_ctinitialRadius, getDefaultRadius());
                imageHeight = a.getDimension(R.styleable.CollapsingToolbarWithPagerIndicator_ctimageHeight, getDefaultImageHeight());
            } finally {
                a.recycle();
            }
        } else {
            indicatorHeight = getDefaultIndicatorHeight();
        }

        View view = inflate(getContext(), R.layout.collapsing_toolbar_with_pager_indicator, this);

        logo = view.findViewById(R.id.logo);
        toolbarShadow = view.findViewById(R.id.toolbar_shadow);
        toolbar = view.findViewById(R.id.toolbar);
        image = view.findViewById(R.id.image);
        pagerIndicator = view.findViewById(R.id.pagerIndicator);
        top = view.findViewById(R.id.top);
        parallaxImage = view.findViewById(R.id.parallaxImage);
        View toolbarContainer = view.findViewById(R.id.toolbarContainer);

        pagerIndicator.setIndicatorHeight(indicatorHeight);
        pagerIndicator.setInitialHorizontalMargin(initialHorizontalMargin);
        pagerIndicator.setInitialRadius(initialRadius);

        float topHeight = imageHeight + indicatorHeight /2f + pagerIndicator.getIndicatorMarkerHeight()/2f;
        setParamsHeight(top, topHeight);
        setParamsHeight(parallaxImage, imageHeight);
        setParamsHeight(toolbarContainer, imageHeight);
    }

    private float getDefaultIndicatorHeight() {
        return getResources().getDisplayMetrics().density * 46;
    }

    private float getDefaultHorizontalMargin() {
        return getResources().getDisplayMetrics().density * 8;
    }

    private float getDefaultToolbarShadowAfter() {
        return getResources().getDisplayMetrics().density * 16;
    }

    private float getDefaultRadius() {
        return getResources().getDisplayMetrics().density * 10;
    }

    private float getDefaultImageHeight() {
        return getResources().getDisplayMetrics().density * 200;
    }

    private void setParamsHeight(View optionsContainer, float indicatorHeight) {
        MarginLayoutParams params = (MarginLayoutParams) optionsContainer.getLayoutParams();
        params.height = (int) indicatorHeight;
        optionsContainer.setLayoutParams(params);
    }

    boolean first = true;
    int heightTitle;
    int leftTitle;
    int topTitle;
    int leftLogoStart;
    int topLogoStart;
    int leftLogoEnd;
    int topLogoEnd;
    int heightLogo;

    public void updateScrollSmoothly(int targetScroll) {
        if(first) {
            updateScroll(targetScroll);
            return;
        }
        if(this.targetScroll == targetScroll) return;
        this.targetScroll = targetScroll;
        if(animatorSmoothly != null) {
            animatorSmoothly.cancel();
        }


        animatorSmoothly = ValueAnimator.ofInt((int) currentScroll, targetScroll);
        animatorSmoothly .addUpdateListener(listenerAnimSmoothly);
        animatorSmoothly .start();
    }

    public void updateScroll(int scrollY) {
        updateScroll(scrollY, false);
    }

    public void updateScroll(int scrollY, boolean byAnim) {
        if(!byAnim) {
            if(animatorSmoothly != null) {
                animatorSmoothly.cancel();
                animatorSmoothly = null;
                targetScroll = Integer.MIN_VALUE;
            }
        }
        currentScroll = scrollY;
        if(first) {
            TextView title = (TextView) toolbar.getChildAt(0);

            View toolbarParent = (View) toolbar.getParent();
            heightTitle = title.getHeight();
            leftTitle = title.getLeft();
            topTitle = title.getTop();

            int leftLogoStartX = getRelativeLeft(logo, toolbarParent);
            int topLogoStartX = getRelativeTop(logo, toolbarParent);
            int leftLogoEndX = getRelativeLeft(title, toolbarParent);// - title.getPaddingLeft();
            int topLogoEndX = getRelativeTop(title, toolbarParent);// - title.getPaddingTop();

            toolbarShadow.setTranslationY(toolbar.getHeight());

            leftLogoStart = 0;
            topLogoStart = 0;

            leftLogoEnd = leftLogoStartX - leftLogoEndX;
            topLogoEnd = topLogoStartX - topLogoEndX;

            heightLogo = logo.getHeight();
            first = false;
        }

        if(scrollY < 0) scrollY = 0;



        float parallaxTo = top.getHeight() - toolbar.getHeight() - (pagerIndicator.getHeight());// getActionBarHeight();// getResources().getDisplayMetrics().density * 30;


        if (scrollY < parallaxTo) {
            top.setTranslationY(-scrollY);
            image.setTranslationY(scrollY / 2);
        } else {
            top.setTranslationY(-parallaxTo);
            image.setTranslationY(parallaxTo / 2);
        }


        float startY = getResources().getDisplayMetrics().density * 50;
        float endY = parallaxImage.getHeight();

        double delta = endY - startY;
        double delta2 = parallaxTo;//endY - toolbar.getHeight();
        double relativeY = scrollY - startY;
        double relativeYprogressShadow = scrollY - parallaxTo - toolbarShadowToBeShownAfter;
        double relativeY2 = scrollY;
        if (relativeY < 0) relativeY = 0;
        if (relativeY2 < 0) relativeY2 = 0;
        if (relativeYprogressShadow < 0) relativeYprogressShadow = 0;
        if (relativeY > delta) relativeY = delta;
        if (relativeY2 > delta2) relativeY2 = delta2;
        if (relativeYprogressShadow > toolbarShadowToBeShownAfter)
            relativeYprogressShadow = toolbarShadowToBeShownAfter;

        double shadowProgress = relativeYprogressShadow / toolbarShadowToBeShownAfter;
        double progress = relativeY / delta;
        double progress2 = relativeY2 / delta2;

        toolbarShadow.setAlpha((float)shadowProgress);

        float scaleTo;

        if (heightLogo == 0) {
            scaleTo = 1;
        } else {
            scaleTo = (float) heightTitle / (float) heightLogo;
        }

        logo.setTranslationX((float) (-leftLogoEnd * progress2));
        logo.setTranslationY((float) (-topLogoEnd * easeInOut(progress2)));
        logo.setScaleX((float) ((1 - progress2) + scaleTo * progress2));
        logo.setScaleY((float) ((1 - progress2) + scaleTo * progress2));


        if(scrollY >= parallaxTo-1) {
            if(animateTo != 1) {
                if(animator != null) animator.cancel();
                animateTo = 1;
                animator = ValueAnimator.ofFloat(a[0], 1);
                animator.addUpdateListener(listenerAnim);
                animator.start();
            }
        } else {
            if(animateTo != 0) {
                if(animator != null) animator.cancel();
                animateTo = 0;
                animator = ValueAnimator.ofFloat(a[0], 0);
                animator.addUpdateListener(listenerAnim);
                animator.start();
            }
        }

        indicatorProgressUpdate((float) progress);
    }

    public void setIndicatorListener(PagerIndicator.IndicatorChangeListener pageChangeListener) {
        pagerIndicator.setIndicatorListener(pageChangeListener);
    }

    float indicatorOrogress;
    private void indicatorProgressUpdate(float progress) {
        indicatorOrogress = progress;
        updateIndicatorProgress();
    }

    private void updateIndicatorProgress() {
        pagerIndicator.updateProgress(a[0] + (1-a[0])*indicatorOrogress);
    }

    private static final Spline spline = new Spline(.42,1.01,.38,.96) ;

    private double easeInOut(double progress2) {
        return spline.get(progress2);
    }

    private int getRelativeLeft(View myView, View relativeTo) {
        if (myView.getParent() == relativeTo)
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeLeft(View myView) {
        return getRelativeLeft(myView, myView.getRootView());
    }

    private int getRelativeTop(View myView, View relativeTo) {
        if (myView.getParent() == relativeTo)
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
    private int getRelativeTop(View myView) {
        return getRelativeTop(myView, myView.getRootView());
    }

    @Override
    public int getCollapsableHeight() {
        return (int) (imageHeight + indicatorHeight /2f + pagerIndicator.getIndicatorMarkerHeight()/2f - toolbar.getPaddingTop());
        //return getHeight() - toolbar.getPaddingTop();
    }

    public int getTTT() {
        return (int)( imageHeight - ( toolbar.getHeight() + indicatorHeight/2) );
    }

    public int getTTT2() {
        return (int)(imageHeight - toolbar.getHeight());
    }
}
