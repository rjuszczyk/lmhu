package eu.letmehelpu.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.TransformationMethod;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.view.View;

import eu.letmehelpu.android.R;

import static android.text.style.DynamicDrawableSpan.ALIGN_BASELINE;

public class ThinBorderButton extends android.support.v7.widget.AppCompatButton {

    private Drawable drawableStart;
    private Drawable drawableEnd;
    private Drawable left;
    private Drawable right;

    public ThinBorderButton(Context context) {
        super(context);
    }

    public ThinBorderButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.buttonStyleThinBorder);
    }

    public ThinBorderButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        handleAttrs(attrs);
    }

    private void handleAttrs(AttributeSet attrs) {
        if(attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ThinBorderButton);
            try {
                drawableStart = a.getDrawable(R.styleable.ThinBorderButton_tbbDrawableStart);
                drawableEnd = a.getDrawable(R.styleable.ThinBorderButton_tbbDrawableEnd);
            } finally {
                a.recycle();
            }
        }
        if(ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            left = drawableStart;
            right = drawableEnd;
        } else {
            left = drawableEnd;
            right = drawableStart;
        }

        final TransformationMethod tm = getTransformationMethod();
        setTransformationMethod(new TransformationMethod() {
            @Override
            public CharSequence getTransformation(CharSequence source, View view) {

                return updateDrawables(tm.getTransformation(source, view));
            }

            @Override
            public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect) {

            }
        });
//        super.setText(getText());
    }

//    @Override
//    public void setText(CharSequence text, BufferType type) {
//        CharSequence sequence = updateDrawables(text);
//
//        super.setText(sequence, type);
//    }

    private CharSequence updateDrawables(CharSequence text) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if(left != null) {
            builder.append("@#$@#$");
        }
        if(text != null) {
            builder.append(text);
        }
        if(right != null) {
            builder.append("@#$@#$");
        }

        if(left != null) {
            ImageSpan span = getImageSpan(left);
            ReplacementSpan a = getPaddingReplacementSpan();

            builder.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            builder.setSpan(a, 3, 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            
        }
        if(right != null) {
            ReplacementSpan a = getPaddingReplacementSpan();
            ImageSpan span = getImageSpan(right);

            builder.setSpan(span, builder.length()-3, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            builder.setSpan(a, builder.length()-6, builder.length()-3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        setTransformationMethod(null);
        return builder;
    }

    @NonNull
    private ImageSpan getImageSpan(Drawable image) {
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        return new ImageSpan(image) {
            @Override
            public void draw(Canvas canvas, CharSequence text,
                             int start, int end, float x,
                             int top, int y, int bottom, Paint paint) {
                Drawable b = getDrawable();
                canvas.save();

                int transY = bottom - b.getBounds().bottom;

                transY -= paint.getFontMetricsInt().descent;

                float tY = (b.getIntrinsicHeight() - paint.getTextSize()) / 2;
                transY += tY;

                canvas.translate(x, transY);
                b.draw(canvas);
                canvas.restore();
            }
        };
    }

    @NonNull
    private ReplacementSpan getPaddingReplacementSpan() {
        return new ReplacementSpan() {

            @Override
            public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
                return getCompoundDrawablePadding();
            }

            @Override
            public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {

            }
        };
    }
}
