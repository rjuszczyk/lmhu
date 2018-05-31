package eu.letmehelpu.android.view;

import android.view.View;

public abstract class CollapsableAdapter implements Collapsable {
    private View view;

    public void init(View view) {
        this.view = view;
    }

    public abstract void updateScroll(View view, int scrollY);
    public abstract int getCollapsableHeight(View view);

    @Override
    public void updateScroll(int scrollY) {
        updateScroll(view, scrollY);
    }
    @Override
    public int getCollapsableHeight() {
        return getCollapsableHeight(view);
    }
}
