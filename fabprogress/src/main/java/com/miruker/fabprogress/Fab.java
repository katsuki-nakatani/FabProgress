package com.miruker.fabprogress;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;


public class Fab extends View {

    private static final int PROGRESS_DEFAULT_COLOR = Color.parseColor("#009688");
    private static final float SHADOW_RADIUS = 10f;
    private static final int STROKE_SIZE = 12;
    private static final int PROGRESS_START_VALUE = 320;
    private static final int mShadowColor = Color.argb(100, 0, 0, 0);
    private static final float BACKGROUND_CANVAS_SCALE = 2.3f;
    private static final float FAB_CANVAS_SCALE = 2.6f;
    private static final float FAB_PRESSED_ALPHA = 0.6f;
    private static final float FAB_DISABLED_ALPHA = 0.6f;
    public static final int MODE_FROM_BOTTOM = 0;
    public static final int MODE_FROM_TOP = 1;
    public static final int MODE_FROM_LEFT = 2;
    public static final int MODE_FROM_RIGHT = 3;
    private static final float DEPTH_1 = 1.0f;
    private static final float DEPTH_2 = 3.0f;
    private static final float DEPTH_3 = 10.0f;
    private static final float DEPTH_4 = 14.0f;
    private static final float DEPTH_5 = 19.0f;

    Context _context;
    Paint mButtonPaint;
    Paint mDrawablePaint;
    Paint mProgressBackPaint;
    Bitmap mBitmap;
    int mScreenHeight;
    int mScreenWidth;
    float mCurrentX;
    float mCurrentY;
    float mShadowRadius = SHADOW_RADIUS;
    int mDistance = 2;
    float pressedAlpha = FAB_PRESSED_ALPHA;
    boolean mHidden = false;
    int mFabColor = Color.WHITE;
    private float mDepth = DEPTH_2;
    Drawable myDrawable;
    private int mMode = 0;
    RectF mProgressRectF;
    Paint mProgressArcPaint;
    private boolean isProgress = false;
    private int mProgressMaxValue = PROGRESS_START_VALUE;
    private int mProgressValue = PROGRESS_START_VALUE;
    private int mProgressStartValue = 0;
    private boolean mReverse = false;
    private int mProgressColor;
    private int mBackgroundCanvasColor = Color.WHITE;
    private float mBackgroundCanvasSize = FAB_CANVAS_SCALE;


    /**
     *
     * @param context
     * @param attributeSet
     */
    public Fab(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        _context = context;
        if (!isInEditMode()) {
            parseAttrs(attributeSet, 0);
            init();
        }
    }

    /**
     * savedInstance
     * @return
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState saved = new SavedState(super.onSaveInstanceState());

        saved.mBackgroundCanvasColor = mBackgroundCanvasColor;
        saved.mBackgroundCanvasSize = mBackgroundCanvasSize;
        saved.mProgressColor = mProgressColor;
        saved.mProgressMaxValue = mProgressMaxValue;
        saved.mProgressValue = mProgressValue;
        saved.mReverse = mReverse;
        saved.isProgress = isProgress;
        return saved;
    }

    /**
     * restoreInstance
     * @param state
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState))
            return;

        SavedState saved = (SavedState) state;

        super.onRestoreInstanceState(saved.getSuperState());

        mBackgroundCanvasColor = saved.mBackgroundCanvasColor;
        mBackgroundCanvasSize = saved.mBackgroundCanvasSize;
        mProgressColor = saved.mProgressColor;
        mProgressMaxValue = saved.mProgressMaxValue;
        mProgressValue = saved.mProgressValue;
        mReverse = saved.mReverse;
        isProgress = saved.isProgress;
    }

    /**
     *
     * @param context
     * @param attributeSet
     * @param defStyle
     */
    public Fab(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        _context = context;
        if (!isInEditMode()) {
            parseAttrs(attributeSet, defStyle);
            init();
        }
    }

    /**
     *
     * @param context
     */
    public Fab(Context context) {
        super(context);
        _context = context;
        if (!isInEditMode())
            init();
    }


    /**
     *
     * @param attrs
     * @param defStyle
     */
    private void parseAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = _context.obtainStyledAttributes(attrs, com.miruker.fabprogress.R.styleable.Fab, defStyle, 0);
        myDrawable = a.getDrawable(R.styleable.Fab_fabDrawable);
        mBitmap = ((BitmapDrawable) myDrawable).getBitmap();
        mFabColor = a.getColor(R.styleable.Fab_fabColor, Color.WHITE);
        mProgressColor = a.getColor(R.styleable.Fab_progressColor, PROGRESS_DEFAULT_COLOR);
        mBackgroundCanvasColor = a.getColor(R.styleable.Fab_progressBackgroundColor, Color.WHITE);
        mDistance = a.getInt(R.styleable.Fab_fabDepth, mDistance);
        mShadowRadius = a.getFloat(R.styleable.Fab_fabShadowRadius, SHADOW_RADIUS);
        switch (mDistance) {
            case 0:
                mDepth = DEPTH_1;
                break;
            case 1:
                mDepth = DEPTH_2;
                break;
            case 2:
                mDepth = DEPTH_3;
                break;
            case 3:
                mDepth = DEPTH_4;
                break;
            case 4:
                mDepth = DEPTH_5;
                break;
            default:
                mDepth = DEPTH_2;
        }
        a.recycle();
    }

    /**
     * show progress ring
     * @param flg
     */
    public void showProgress(boolean flg) {
        isProgress = flg;
        if (isProgress) {
            setAlpha(1.0f);
            mBackgroundCanvasSize = FAB_CANVAS_SCALE;
            mProgressStartValue = 0;
            mReverse = false;
            mProgressValue = PROGRESS_START_VALUE;
            mProgressMaxValue = PROGRESS_START_VALUE;
        }
        invalidate();
    }

    /**
     *
     * @return
     */
    public boolean isProgress() {
        return isProgress;
    }

    /**
     * fab color setter
     * @param color
     */
    public void setFabColor(int color) {
        this.mFabColor = color;
        invalidate();
    }

    /**
     * fab drawable getter
     * @param fabDrawable
     */
    public void setFabDrawable(Drawable fabDrawable) {
        myDrawable = fabDrawable;
        mBitmap = ((BitmapDrawable) myDrawable).getBitmap();
        invalidate();
    }

    /**
     * fab shadow radius setter
     * @param radius
     */
    public void setFabShadowRadius(float radius) {
        this.mShadowRadius = radius;
        invalidate();
    }

    /**
     * depth setter
     * @param depth
     */
    public void setFabDepth(int depth) {
        this.mDistance = depth;
        invalidate();
    }

    /**
     * pressed alpha setter
     * @param alpha
     */
    public void setPressedAlpha(float alpha) {
        this.pressedAlpha = alpha;
        invalidate();
    }

    /**
     * animatino mode setter
     * @param mode
     */
    public void setAnimationMode(int mode) {
        this.mMode = mode;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mProgressRectF = new RectF(0 + dpToPx(10), 0 + dpToPx(10), w - dpToPx(10), h - dpToPx(10));
    }

    public void init() {
        setWillNotDraw(false);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(mFabColor);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setShadowLayer(dpToPx(mShadowRadius), 0.0f, mDepth, mShadowColor);
        mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mProgressBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressBackPaint.setColor(mBackgroundCanvasColor);
        mProgressBackPaint.setStyle(Paint.Style.FILL);
        mProgressBackPaint.setShadowLayer(dpToPx(mShadowRadius), 0.0f, mDepth, mShadowColor);

        invalidate();

        WindowManager mWindowManager = (WindowManager) _context.getSystemService(Context.WINDOW_SERVICE);
        Display display = mWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenHeight = size.y;
        mScreenWidth = size.x;


        mProgressArcPaint = new Paint();
        mProgressArcPaint.setStrokeWidth(dpToPx(STROKE_SIZE));
        mProgressArcPaint.setStrokeCap(Paint.Cap.SQUARE);
        mProgressArcPaint.setAntiAlias(true);
        mProgressArcPaint.setStyle(Paint.Style.STROKE);
        mProgressArcPaint.setColor(mProgressColor);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        boolean isInvalidate = false;
        setClickable(true);
        if (isProgress) {
            //invisible shadow layer
            mButtonPaint.clearShadowLayer();
            if (mBackgroundCanvasSize == BACKGROUND_CANVAS_SCALE) {
                if (mReverse)
                    mProgressStartValue += 10;
                else
                    mProgressStartValue += 5;

                if (mProgressStartValue >= 360) {
                    mProgressStartValue = 1;
                }
                if (!mReverse)
                    mProgressValue -= 5;
                else
                    mProgressValue += 5;
                if (mProgressValue == 0 || mProgressValue == mProgressMaxValue) {
                    mReverse = !mReverse;
                }
            }

            mBackgroundCanvasSize -= 0.05f;
            if (mBackgroundCanvasSize < BACKGROUND_CANVAS_SCALE)
                mBackgroundCanvasSize = BACKGROUND_CANVAS_SCALE;

            //progress background
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / mBackgroundCanvasSize), mProgressBackPaint);
            //ProgressArc
            if (mBackgroundCanvasSize == BACKGROUND_CANVAS_SCALE) {
                canvas.drawArc(mProgressRectF, mProgressStartValue, mProgressMaxValue - mProgressValue, false, mProgressArcPaint);
            }
            isInvalidate = true;
        } else {
            if (mBackgroundCanvasSize > FAB_CANVAS_SCALE) {
                mBackgroundCanvasSize = FAB_CANVAS_SCALE;
                mButtonPaint.setShadowLayer(dpToPx(mShadowRadius), 0.0f, mDepth, mShadowColor);
            } else {
                mBackgroundCanvasSize += 0.05f;
                isInvalidate = true;
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / mBackgroundCanvasSize), mProgressBackPaint);
            }
        }
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / FAB_CANVAS_SCALE), mButtonPaint);
        canvas.drawBitmap(mBitmap, (getWidth() - mBitmap.getWidth()) / 2, (getHeight() - mBitmap.getHeight()) / 2, mDrawablePaint);
        if (isInvalidate)
            invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL){
            setAlpha(1.0f);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(isEnabled()) {
                setAlpha(pressedAlpha);
            }
        }

        if (!isEnabled() && !isProgress)
            setAlpha(FAB_DISABLED_ALPHA);
        return super.onTouchEvent(event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(isProgress){
            setAlpha(1.0f);
        }else {
            setAlpha(enabled ? 1.0f : FAB_DISABLED_ALPHA);
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public float dpToPx(float dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public boolean isHidden() {
        return this.mHidden;
    }

    public void hideFab() {
        if (mHidden == false) {
            mCurrentX = getY();
            mCurrentY = getX();

            ObjectAnimator mHideAnimation = null;
            switch (mMode) {
                case MODE_FROM_BOTTOM:
                    mHideAnimation = ObjectAnimator.ofFloat(this, "Y", mScreenHeight);
                    break;
                case MODE_FROM_TOP:
                    mHideAnimation = ObjectAnimator.ofFloat(this, "Y", -mScreenHeight);
                    break;
                case MODE_FROM_LEFT:
                    mHideAnimation = ObjectAnimator.ofFloat(this, "X", -mScreenWidth);
                    break;
                case MODE_FROM_RIGHT:
                    mHideAnimation = ObjectAnimator.ofFloat(this, "X", mScreenWidth);
                    break;
                default:
                    mHideAnimation = ObjectAnimator.ofFloat(this, "Y", mScreenHeight);
                    break;
            }
            mHideAnimation.setInterpolator(new AccelerateInterpolator());
            mHideAnimation.start();
            mHidden = true;
        }
    }

    public void showFab() {
        if (mHidden == true) {
            ObjectAnimator mShowAnimation = null;

            switch (mMode) {
                case MODE_FROM_BOTTOM:
                    mShowAnimation = ObjectAnimator.ofFloat(this, "Y", mCurrentX);
                    break;
                case MODE_FROM_TOP:
                    mShowAnimation = ObjectAnimator.ofFloat(this, "Y", -mCurrentX);
                    break;
                case MODE_FROM_LEFT:
                    mShowAnimation = ObjectAnimator.ofFloat(this, "X", mCurrentY);
                    break;
                case MODE_FROM_RIGHT:
                    mShowAnimation = ObjectAnimator.ofFloat(this, "X", mCurrentY);
                    break;
                default:
                    mShowAnimation = ObjectAnimator.ofFloat(this, "Y", mCurrentX);
                    break;
            }

            mShowAnimation.setInterpolator(new DecelerateInterpolator());
            mShowAnimation.start();
            mHidden = false;
        }
    }

    public static class SavedState extends BaseSavedState {
        private boolean isProgress;
        private int mProgressMaxValue;
        private int mProgressValue;
        private int mProgressStartValue;
        private boolean mReverse;
        private int mProgressColor;
        private int mBackgroundCanvasColor;
        private float mBackgroundCanvasSize;

        public SavedState(Parcel in) {
            /*
             * 必ず書いた順序で読み込むこと！
             */
            super(in);

            isProgress = in.readInt() == 0 ? false : true;
            mProgressMaxValue = in.readInt();
            mProgressValue = in.readInt();
            mProgressStartValue = in.readInt();
            mReverse = in.readInt() == 0 ? false : true;
            mProgressColor = in.readInt();
            mBackgroundCanvasColor = in.readInt();
            mBackgroundCanvasSize = in.readFloat();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeInt(isProgress ? 1 : 0);
            out.writeInt(mProgressMaxValue);
            out.writeInt(mProgressValue);
            out.writeInt(mProgressStartValue);
            out.writeInt(mReverse ? 1 : 0);
            out.writeInt(mProgressColor);
            out.writeInt(mBackgroundCanvasColor);
            out.writeFloat(mBackgroundCanvasSize);
        }

        public static final Creator CREATOR =
                new Creator() {
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

}