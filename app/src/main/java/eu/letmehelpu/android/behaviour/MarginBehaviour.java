package eu.letmehelpu.android.behaviour;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
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

    int start = Integer.MIN_VALUE;
    public boolean onDependentViewChanged(CoordinatorLayout parent, PagerIndicator child, View dependency) {
        if(dependency instanceof  ViewPager) {
            int y = dependency.getTop() - child.getHeight();
            if(start == Integer.MIN_VALUE) {
                start = y;
            }
            //float start = parent.getResources().getDimension(R.dimen.offers_top_image_height)-child.getIndicatorHeight();

            float end = 0;
            float delta = end - start;
            float movedBy = y - start;
            float progress = movedBy/delta;


            child.setTranslationY(y+child.getIndicatorHeight()+child.getIndicatorMarkerHeight()/2);
            child.updateProgress(progress);
            return true;
        }
        return false;
    }
}
