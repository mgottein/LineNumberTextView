package me.mgottein;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;

/**
 * Created by mgottein on 3/17/15.
 * <p/>
 * Lifted from {@see android.widget.EditText}. Configures a {@see me.mgottein.LineNumberTextView}
 * to be editable
 */
public class LineNumberEditText extends EditText implements ExposesPaddingOffset {
    private DrawingEngine drawingEngine;

    public LineNumberEditText(Context context) {
        super(context);
        init(null);
    }

    public LineNumberEditText(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.editTextStyle);
        init(attrs);
    }

    public LineNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, android.R.attr.editTextStyle);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineNumberEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, android.R.attr.editTextStyle, defStyleRes);
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
