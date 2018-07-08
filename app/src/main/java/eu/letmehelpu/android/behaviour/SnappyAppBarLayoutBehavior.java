package eu.letmehelpu.android.behaviour;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

public class SnappyAppBarLayoutBehavior extends AppBarLayout.Behavior {

    private boolean skipNextStop;
    private boolean nestedScrollStopping;

    public SnappyAppBarLayoutBehavior() {
        super();
    }

    public SnappyAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes, int type) {
        if (!nestedScrollStopping) {
            onStopNestedScroll(parent, child, target, type);
            skipNextStop = true;
        }
        nestedScrollStopping = false;
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type);
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target, int type) {
        if (skipNextStop) {
            skipNextStop = false;
            return;
        }
        if (nestedScrollStopping) {
            return;
        }
        nestedScrollStopping = true;
        //The type is always set to TYPE_TOUCH to enable snapping while flinging aswell
        super.onStopNestedScroll(coordinatorLayout, abl, target, ViewCompat.TYPE_TOUCH);
    }
}
