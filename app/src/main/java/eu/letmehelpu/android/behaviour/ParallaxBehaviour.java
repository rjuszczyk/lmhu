package eu.letmehelpu.android.behaviour;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import eu.letmehelpu.android.R;
import eu.letmehelpu.android.view.PagerIndicator;

public class ParallaxBehaviour  extends CoordinatorLayout.Behavior<ImageView>{
    public ParallaxBehaviour() {
        super();
        //Used when the layout has a behavior attached via the Annotation;
    }

    public ParallaxBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Used when the layout has a behavior attached via xml (Within the xml file e.g.
        //<app:layout_behavior=".link.to.your.behavior">
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof ViewPager;
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        if(dependency instanceof  ViewPager) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            PagerIndicator pagerIndicator = parent.findViewById(R.id.tabs);
            View toolbar = parent.findViewById(R.id.toolbar);

            lp.height = (int) (dependency.getTop() + pagerIndicator.getIndicatorHeight()/2);

            child.setLayoutParams(lp);
            return true;
        }
        return false;
    }
}
