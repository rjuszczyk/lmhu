package eu.letmehelpu.android.behaviour;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import eu.letmehelpu.android.R;
import eu.letmehelpu.android.view.PagerIndicator;

public class MarginBehaviour extends CoordinatorLayout.Behavior<PagerIndicator>{
    public MarginBehaviour() {
        super();
        //Used when the layout has a behavior attached via the Annotation;
    }

    public MarginBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Used when the layout has a behavior attached via xml (Within the xml file e.g.
        //<app:layout_behavior=".link.to.your.behavior">
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, PagerIndicator child, View dependency) {
        return dependency instanceof ViewPager;
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, PagerIndicator child, View dependency) {
        if(dependency instanceof  ViewPager) {

            int startMargin = (int) (parent.getResources().getDisplayMetrics().density*26);
            int endMargin = 0;

            View appBar = parent.findViewById(R.id.appBar);
            View toolbar = parent.findViewById(R.id.toolbar);
            int toolbarEndY  = toolbar.getTop();
            int y = dependency.getTop() - parent.findViewById(R.id.tabs).getHeight();
            int c = (int) (toolbar.getResources().getDimension(R.dimen.offers_top_image_height)-toolbar.getHeight());
            int dd = (appBar.getHeight()-toolbar.getHeight()) - c;
            int p = y - dd;
            int total = (appBar.getHeight()-toolbar.getHeight()-dd);

            float progress = (float) p / (float) total;
            progress = 1-progress;
            int currentMargin = (int)(endMargin*progress + (1-progress)*startMargin);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) child.getLayoutParams();


            marginLayoutParams.leftMargin = currentMargin;
            marginLayoutParams.rightMargin = currentMargin;

            child.setTranslationY(y+child.getIndicatorHeight()+child.getIndicatorMarkerHeight()/2);
            child.updateProgress(progress);
            return true;
        }
        return false;
    }
}
