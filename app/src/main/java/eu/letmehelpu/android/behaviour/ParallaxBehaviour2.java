package eu.letmehelpu.android.behaviour;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import eu.letmehelpu.android.R;
import eu.letmehelpu.android.view.PagerIndicator;
import eu.letmehelpu.android.view.UserView;

public class ParallaxBehaviour2 extends CoordinatorLayout.Behavior<ImageView>{
    public ParallaxBehaviour2() {
        super();
        //Used when the layout has a behavior attached via the Annotation;
    }

    public ParallaxBehaviour2(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Used when the layout has a behavior attached via xml (Within the xml file e.g.
        //<app:layout_behavior=".link.to.your.behavior">
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof NestedScrollView;
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        if(dependency instanceof  NestedScrollView) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            UserView pagerIndicator = parent.findViewById(R.id.tabs);
            View toolbar = parent.findViewById(R.id.toolbar);

            lp.height = (int) (dependency.getTop() + pagerIndicator.getHeight()/2);

            child.setLayoutParams(lp);
            return true;
        }
        return false;
    }
}
