package eu.letmehelpu.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import eu.letmehelpu.android.R;

public class MaskedViewPager extends ViewPager {
    float maskedTop = 0;
    public MaskedViewPager(@NonNull Context context) {
        super(context);
    }

    public MaskedViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if(attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.MaskedViewPager, 0 ,0);
            try {
                maskedTop = a.getDimension(R.styleable.MaskedViewPager_maskedTop, 0);
            } finally {
                a.recycle();
            }
        }
    }

    final Path path = new Path();


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        path.reset();
        path.addRect(new RectF(Integer.MIN_VALUE,maskedTop/2,Integer.MAX_VALUE,getHeight()), Path.Direction.CW);
    }

    @Override
    protected void dispatchDraw(Canvas canvas){
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }


}
