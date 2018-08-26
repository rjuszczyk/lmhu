package eu.letmehelpu.android.behaviour;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import eu.letmehelpu.android.R;
import eu.letmehelpu.android.chat.UserView;

public class MarginBehaviour2 extends CoordinatorLayout.Behavior<UserView>{


    public MarginBehaviour2() {
        super();
        //Used when the layout has a behavior attached via the Annotation;
    }

    public MarginBehaviour2(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Used when the layout has a behavior attached via xml (Within the xml file e.g.
        //<app:layout_behavior=".link.to.your.behavior">
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, UserView child, View dependency) {
        return dependency instanceof NestedScrollView;
    }

    int start = Integer.MIN_VALUE;
    int end;
    public boolean onDependentViewChanged(CoordinatorLayout parent, UserView child, View dependency) {
        if(dependency instanceof  NestedScrollView) {
            int y = dependency.getTop();
            Log.d("Radek", "y = " + y);
            if(start == Integer.MIN_VALUE) {
                start = y;
                end = parent.findViewById(R.id.toolbar).getBottom();
            }
            //float start = parent.getResources().getDimension(R.dimen.offers_top_image_height)-child.getIndicatorHeight();

//            float end = ;
            float delta = end - start;
            float movedBy = y - start;
            float progress = movedBy/delta;


            child.setTranslationY(y);
            child.updateProgress(progress);
            return true;
        }
        return false;
    }
}
