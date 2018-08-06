package eu.letmehelpu.android.offers;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if(parent.getLayoutManager().findViewByPosition(0)==view) {
            outRect.top = verticalSpaceHeight;
            outRect.bottom = verticalSpaceHeight;
        } else {
            outRect.top = 0;
            outRect.bottom = verticalSpaceHeight;
        }

    }
}