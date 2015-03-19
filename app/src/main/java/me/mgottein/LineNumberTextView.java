package me.mgottein;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by mgottein on 3/16/15.
 * <p/>
 * Adds line numbers to a {@see android.widget.TextView} inside the padding
 */
public class LineNumberTextView extends TextView implements ExposesPaddingOffset {

    private DrawingEngine drawingEngine;

    public LineNumberTextView(Context context) {
        super(context);
        init(null);
    }

    public LineNumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LineNumberTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineNumberTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        drawingEngine = new DrawingEngine(getContext(), attrs);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        drawingEngine.onTextChanged(this);
    }

    @Override
    public boolean isPaddingOffsetRequired() {
        return true;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        drawingEngine.setPadding(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawingEngine.onDraw(this, canvas);
    }

    @Override
    public int getLeftPaddingOffset() {
        return super.getLeftPaddingOffset();
    }

    @Override
    public int getRightPaddingOffset() {
        return super.getRightPaddingOffset();
    }
}
