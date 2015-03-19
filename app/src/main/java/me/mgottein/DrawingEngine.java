package me.mgottein;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;

final class DrawingEngine {

    private TextView textView;
    private ExposesPaddingOffset exposesPaddingOffset;
    private Paint mTextPaint;
    private int mLeftPadding;
    private int mRightPadding;
    private boolean mLayoutOnLeft;
    private boolean mHugLine;
    private DrawingController mController;
    //save the padding we add for the line numbers
    private int mCachedLineNumberPadding;
    private boolean hasDrawn = false;

    DrawingEngine(Context context, AttributeSet attrs) {
        mCachedLineNumberPadding = 0;
        if (attrs != null) {
            TypedArray a = context.getTheme()
                    .obtainStyledAttributes(attrs, R.styleable.LineNumberTextView, 0, 0);
            try {
                mLayoutOnLeft = a.getBoolean(R.styleable.LineNumberTextView_layoutLineNumbersOnLeft, true);
                mHugLine = a.getBoolean(R.styleable.LineNumberTextView_shouldLineNumbersHugLine, false);
            } finally {
                a.recycle();
            }
        } else {
            mLayoutOnLeft = true;
            mHugLine = false;
        }
        fixLineNumberPadding();
    }

    <T extends TextView & ExposesPaddingOffset> void onDraw(T textView, Canvas canvas) {
        if (!hasDrawn) {
            hasDrawn = true;
            this.textView = textView;
            this.exposesPaddingOffset = textView;
            configure();
        }

        canvas.save();
        textViewClip(canvas);

        Layout layout = textView.getLayout();

        int scrollY = textView.getScrollY();
        //only display all the line numbers between the first and last line
        int firstLine = layout.getLineForVertical(scrollY),
                lastLine = layout.getLineForVertical(scrollY +
                        (textView.getHeight() - textView.getExtendedPaddingTop() - textView.getExtendedPaddingBottom()));

        //the y position starts at the baseline of the first line
        int positionY =
                textView.getBaseline() + (layout.getLineBaseline(firstLine) - layout.getLineBaseline(0));
        drawLineNumber(canvas, layout, positionY, firstLine);
        for (int i = firstLine + 1; i <= lastLine; i++) {
            //get the next y position using the difference between the current and last baseline
            positionY += layout.getLineBaseline(i) - layout.getLineBaseline(i - 1);
            drawLineNumber(canvas, layout, positionY, i);
        }
        canvas.restore();
    }

    void onTextChanged(TextView textView) {
        if (mTextPaint == null) {
            //HACK: this gets called before the textview constructor finishes
            textView.post(new Runnable() {
                @Override
                public void run() {
                    fixLineNumberPadding();
                }
            });
        } else {
            fixLineNumberPadding();
        }
    }

    private void configure() {
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTypeface(textView.getTypeface());
        mTextPaint.setTextSize(textView.getTextSize());
        mTextPaint.setAntiAlias(true);
        mTextPaint.setSubpixelText(true);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        mController = getDefaultLineNumberController();
        mLeftPadding = textView.getPaddingLeft();
        mRightPadding = textView.getPaddingRight();
    }

    void setPadding(int left, int top, int right, int bottom) {
        boolean fixPadding = mLeftPadding != left || mRightPadding != right;
        mLeftPadding = left;
        mRightPadding = right;
        if (fixPadding) {
            fixLineNumberPadding();
        }
    }

    /**
     * @return the default controller used to display line numbers
     */

    protected DrawingController getDefaultLineNumberController() {
        return new DrawingController() {
            @Override
            public String getLineNumberText(boolean layoutOnLeft, int line) {
                return Integer.toString(line);
            }

            @Override
            public boolean showLineNumber(int line) {
                return true;
            }
        };
    }

    /**
     * Change how line numbers are laid out
     *
     * @param layoutLineNumbersOnLeft if line numbers should be placed in the left column
     */
    public void layoutLineNumbersOnLeft(boolean layoutLineNumbersOnLeft) {
        boolean oldLayoutDirection = mLayoutOnLeft;
        mLayoutOnLeft = layoutLineNumbersOnLeft;
        if (oldLayoutDirection != mLayoutOnLeft) {
            fixLineNumberPadding();
        }
    }

    /*
     * @return if line numbers should be placed in the left column
     */
    public boolean layoutLineNumbersOnLeft() {
        return mLayoutOnLeft;
    }

    /**
     * Change the controller used to display line numbers
     *
     * @param controller
     */
    public void setLineNumberController(DrawingController controller) {
        if (controller == null) {
            throw new IllegalStateException("controller must not be null");
        }
        mController = controller;
        textView.invalidate();
    }

    /**
     * @return get the controller used to display line numbers
     */
    public DrawingController getLineNumberController() {
        return mController;
    }

    /**
     * @return if line numbers are hugging the line or in the left or right column
     */
    public boolean doLineNumbersHugLine() {
        return mHugLine;
    }

    /**
     * Set if line numbers hug the line or are in the left or right column
     *
     * @param hugLine
     */
    public void doLineNumbersHugLine(boolean hugLine) {
        boolean doInvalidate = hugLine != mHugLine;
        mHugLine = hugLine;
        if (doInvalidate) {
            textView.invalidate();
        }
    }

    /**
     * Change line number color
     *
     * @param color
     */
    public void setLineNumberColor(int color) {
        boolean doInvalidate = color != mTextPaint.getColor();
        mTextPaint.setColor(color);
        if (doInvalidate) {
            textView.invalidate();
        }
    }

    /**
     * Change line number typeface
     *
     * @param typeface
     */
    public void setLineNumberTypeface(Typeface typeface) {
        boolean doInvalidate = typeface != mTextPaint.getTypeface();
        mTextPaint.setTypeface(typeface);
        if (doInvalidate) {
            textView.invalidate();
        }
    }

    /**
     * Change line number size (in px, uses {@see android.graphics.Paint#setTextSize(int)})
     *
     * @param size
     */
    public void setLineNumberSize(int size) {
        boolean doInvalidate = size != mTextPaint.getTextSize();
        mTextPaint.setTextSize(size);
        if (doInvalidate) {
            textView.invalidate();
        }
    }

    /**
     * Setup a slightly modified version of the clipping bounds {@see android.widget.TextView} uses
     *
     * @param canvas
     */
    private void textViewClip(Canvas canvas) {
        int scrollX = textView.getScrollX();
        int scrollY = textView.getScrollY();
        int left = textView.getLeft();
        int right = textView.getRight();
        int top = textView.getTop(), bottom = textView.getBottom();
        int maxScrollY =
                textView.getLayout().getHeight() - bottom - top - textView.getCompoundPaddingBottom() -
                        textView.getCompoundPaddingTop();

        //ignore left and right padding
        float clipLeft = scrollX;
        float clipTop = (scrollY == 0) ? 0 : textView.getExtendedPaddingTop() + scrollY;
        float clipRight = right - left + scrollX;
        float clipBottom = bottom - top + scrollY -
                ((scrollY == maxScrollY) ? 0 : textView.getExtendedPaddingBottom());

        //account for shadow if it exists
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            float shadowRadius = textView.getShadowRadius();
            if (shadowRadius != 0) {
                float shadowDx = textView.getShadowDx();
                float shadowDy = textView.getShadowDy();
                clipLeft += Math.min(0, shadowDx - shadowRadius);
                clipRight += Math.max(0, shadowDx + shadowRadius);

                clipTop += Math.min(0, shadowDy - shadowRadius);
                clipBottom += Math.max(0, shadowDy + shadowRadius);
            }
        }

        canvas.clipRect(clipLeft, clipTop, clipRight, clipBottom);
    }

    private void drawLineNumber(Canvas canvas, Layout layout, int positionY, int line) {
        if (mController.showLineNumber(line + 1)) {
            int positionX = getLineNumberX(layout, line);
            canvas.drawText(mController.getLineNumberText(mLayoutOnLeft, line + 1), positionX, positionY,
                    mTextPaint);
        }
    }

    //Whenever the padding changes (or is set up for the first time) we need to add enough padding to the correct side to show our line numbers
    private void fixLineNumberPadding() {
        mCachedLineNumberPadding = getLineNumberPadding();
        if (mLayoutOnLeft) {
            textView.setPadding(mLeftPadding + mCachedLineNumberPadding, textView.getPaddingTop(),
                    mRightPadding, textView.getPaddingBottom());
        } else {
            textView.setPadding(mLeftPadding, textView.getPaddingTop(),
                    mRightPadding + mCachedLineNumberPadding, textView.getPaddingBottom());
        }
        textView.invalidate();
    }

    //the line number padding is calculated as the width of the largest line number text
    private int getLineNumberPadding() {
        Layout layout = textView.getLayout();
        int lineCount = layout != null ? layout.getLineCount() : 1;
        return (int) mTextPaint.measureText(mController.getLineNumberText(mLayoutOnLeft, lineCount));
    }

    //get the x coordinate of a line number
    private int getLineNumberX(Layout layout, int line) {
        //hugging a line means we display as close to the line as possible
        if (mLayoutOnLeft) {
            //padding offset ignores the padding we set - we are displaying content inside that padding
            int leftColumn = exposesPaddingOffset.getLeftPaddingOffset();
            if (mHugLine) {
                int lineLeft = (int) layout.getLineLeft(line);
                return Math.max(leftColumn, lineLeft - mLeftPadding);
            } else {
                return leftColumn;
            }
        } else {
            //getRightPaddingOffset() returns a - number
            int rightColumn =
                    textView.getWidth() + exposesPaddingOffset.getRightPaddingOffset() - mCachedLineNumberPadding;
            if (mHugLine) {
                int lineRight = (int) layout.getLineRight(line);
                return Math.min(rightColumn, lineRight + textView.getCompoundPaddingLeft() + mRightPadding);
            } else {
                return rightColumn;
            }
        }
    }

}
