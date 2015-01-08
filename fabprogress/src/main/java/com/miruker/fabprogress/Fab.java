package com.miruker.fabprogress;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;


public class Fab extends View {

    private static final int PROGRESS_END_DURATION = 100;
    private static final int PROGRESS_DEFAULT_COLOR = Color.parseColor("#009688");
    private static final float SHADOW_RADIUS = 10f;
    private static final int STROKE_SIZE = 12;
    private static final int PROGRESS_MAX_VALUE = 280;
    private static final float PROGRESS_ACCELERATE_MAX = 4.0f;
    private static final float PROGRESS_ACCELERATE_MIN = 1.0f;
    private static final float PROGRESS_ACCELERATE_ADDITION = 0.2f;
    private static final int mShadowColor = Color.argb(100, 0, 0, 0);
    private static final float BACKGROUND_CANVAS_SCALE = 2.3f;
    private static final float FAB_CANVAS_SCALE = 2.6f;
    private static final float FAB_PRESSED_ALPHA = 0.6f;
    private static final float FAB_DISABLED_ALPHA = 0.6f;
    private static final float DEPTH_1 = 1.0f;
    private static final float DEPTH_2 = 3.0f;
    private static final float DEPTH_3 = 10.0f;
    private static final float DEPTH_4 = 14.0f;
    private static final float DEPTH_5 = 19.0f;
    private static final int FINISH_OFF = 0;
    private static final int FINISH_ON = 1;
    private static final int FINISH_ENDED = 2;
    private static final int FINISH_DELAY = 1000;

    Context _context;
    Paint mButtonPaint;
    Paint mDrawablePaint;
    Paint mProgressBackPaint;
    Bitmap mIconBitmap;
    Bitmap mFinishIconBitmap;
    float mShadowRadius = SHADOW_RADIUS;
    int mDistance = 2;
    float pressedAlpha = FAB_PRESSED_ALPHA;
    int mFabColor = Color.WHITE;
    private float mDepth = DEPTH_2;
    Drawable mIconDrawable;
    Drawable mFinishIconDrawable;
    RectF mProgressRectF;
    Paint mProgressArcPaint;
    private boolean isProgress = false;
    private int mProgressValue = 0;
    private int mProgressStartValue = 0;
    private boolean mReverse = false;
    private int mProgressColor;
    private int mBackgroundCanvasColor = Color.WHITE;
    private float mBackgroundCanvasSize = FAB_CANVAS_SCALE;
    private float mProgressAccelerate = PROGRESS_ACCELERATE_MAX;
    private int mProgressSweepValue = 0;
    private int mFinishFlg = FINISH_OFF;
    private AnimatorSet mFinishAnimator;
    private FabListener mListener;
    private int mFinishDelay = FINISH_DELAY;


    /**
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
     *
     * @return
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState saved = new SavedState(super.onSaveInstanceState());

        saved.mBackgroundCanvasColor = mBackgroundCanvasColor;
        saved.mBackgroundCanvasSize = mBackgroundCanvasSize;
        saved.mProgressColor = mProgressColor;
        saved.mProgressValue = mProgressValue;
        saved.mReverse = mReverse;
        saved.isProgress = isProgress;
        saved.mProgressAccelerate = mProgressAccelerate;
        saved.mProgressSweepValue = mProgressSweepValue;
        saved.mFinishFlg = mFinishFlg;
        saved.mFinishDelay = mFinishDelay;
        return saved;
    }

    /**
     * restoreInstance
     *
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
        mProgressValue = saved.mProgressValue;
        mProgressAccelerate = saved.mProgressAccelerate;
        mReverse = saved.mReverse;
        isProgress = saved.isProgress;
        mFinishDelay = saved.mFinishDelay;
        mProgressSweepValue = saved.mProgressSweepValue;
        mFinishFlg = saved.mFinishFlg;
        if(mFinishFlg == FINISH_ENDED && mButtonPaint != null){
            mButtonPaint.setColor(mProgressColor);
        }
    }

    /**
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
     * @param context
     */
    public Fab(Context context) {
        super(context);
        _context = context;
        if (!isInEditMode())
            init();
    }


    /**
     * @param attrs
     * @param defStyle
     */
    private void parseAttrs(AttributeSet attrs, int defStyle) {
        TypedArray a = _context.obtainStyledAttributes(attrs, com.miruker.fabprogress.R.styleable.Fab, defStyle, 0);
        mIconDrawable = a.getDrawable(R.styleable.Fab_fabDrawable);
        mFinishIconDrawable = a.getDrawable(R.styleable.Fab_fabFinishDrawable);
        mIconBitmap = ((BitmapDrawable) mIconDrawable).getBitmap();
        if(mFinishIconDrawable != null)
            mFinishIconBitmap = ((BitmapDrawable) mFinishIconDrawable).getBitmap();
        mFabColor = a.getColor(R.styleable.Fab_fabColor, Color.WHITE);
        mProgressColor = a.getColor(R.styleable.Fab_progressColor, PROGRESS_DEFAULT_COLOR);
        mBackgroundCanvasColor = a.getColor(R.styleable.Fab_progressBackgroundColor, Color.WHITE);
        mDistance = a.getInt(R.styleable.Fab_fabDepth, mDistance);
        mShadowRadius = a.getFloat(R.styleable.Fab_fabShadowRadius, SHADOW_RADIUS);
        mFinishDelay = a.getInt(R.styleable.Fab_fabFinishDelay, FINISH_DELAY);
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
     */
    public void showProgress() {
        isProgress = true;
        mFinishFlg = FINISH_OFF;
        mFinishAnimator = null;
        mButtonPaint.setColor(mFabColor);
        setAlpha(1.0f);
        mBackgroundCanvasSize = FAB_CANVAS_SCALE;
        mProgressStartValue = 0;
        mReverse = false;
        mProgressValue = 0;
        mProgressAccelerate = PROGRESS_ACCELERATE_MAX;
        invalidate();
    }

    /**
     * hide progress ring
     */
    public void hideProgress() {
        if(isProgress == true && mFinishFlg == FINISH_OFF) {
            isProgress = false;
            invalidate();
        }
    }

    public void finishProgress(){
    //    isProgress = false;
        mFinishFlg = FINISH_ON;
        invalidate();
    }

    public void setFinishAnimationDelay(int delay){
        mFinishDelay = delay;
        invalidate();
    }

    public void clearProgress(){
        isProgress = false;
        mFinishFlg = FINISH_OFF;
        mButtonPaint.setColor(mFabColor);
        invalidate();
    }

    /**
     * @return
     */
    public boolean isProgress() {
        return isProgress;
    }

    public void addListener(FabListener listner){
        mListener = listner;
    }

    public void removeListener(){
        mListener = null;
    }

    /**
     * fab color setter
     *
     * @param color
     */
    public void setFabColor(int color) {
        this.mFabColor = color;
        invalidate();
    }

    public void setFinishAnimationColor(int color){
        this.mButtonPaint.setColor(color);
        invalidate();
    }

    /**
     * fab drawable getter
     *
     * @param fabDrawable
     */
    public void setFabDrawable(Drawable fabDrawable) {
        mIconDrawable = fabDrawable;
        mIconBitmap = ((BitmapDrawable) mIconDrawable).getBitmap();
        invalidate();
    }

    /**
     * fab shadow radius setter
     *
     * @param radius
     */
    public void setFabShadowRadius(float radius) {
        this.mShadowRadius = radius;
        invalidate();
    }

    /**
     * depth setter
     *
     * @param depth
     */
    public void setFabDepth(int depth) {
        this.mDistance = depth;
        invalidate();
    }

    /**
     * pressed alpha setter
     *
     * @param alpha
     */
    public void setPressedAlpha(float alpha) {
        this.pressedAlpha = alpha;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mProgressRectF = new RectF(0 + dpToPx(10), 0 + dpToPx(10), w - dpToPx(10), h - dpToPx(10));
    }

    public void init() {
        setWillNotDraw(false);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //Fab draw Paint
        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(mFabColor);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setShadowLayer(dpToPx(mShadowRadius), 0.0f, mDepth, mShadowColor);

        //Icon draw Paint
        mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mProgressBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressBackPaint.setColor(mBackgroundCanvasColor);
        mProgressBackPaint.setStyle(Paint.Style.FILL);
        mProgressBackPaint.setShadowLayer(dpToPx(mShadowRadius), 0.0f, mDepth, mShadowColor);


        mProgressArcPaint = new Paint();
        mProgressArcPaint.setStrokeWidth(dpToPx(STROKE_SIZE));
        mProgressArcPaint.setStrokeCap(Paint.Cap.SQUARE);
        mProgressArcPaint.setAntiAlias(true);
        mProgressArcPaint.setStyle(Paint.Style.STROKE);
        mProgressArcPaint.setColor(mProgressColor);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        boolean isInvalidate = false;
        setClickable(true);
        if (isProgress) {
            //invisible shadow layer
            mButtonPaint.clearShadowLayer();
            if (mBackgroundCanvasSize == BACKGROUND_CANVAS_SCALE) {
                if (mFinishFlg == FINISH_OFF && mReverse && mProgressSweepValue >= 0) {
                    mProgressStartValue += 1;
                }else {
                    mProgressStartValue += 3;
                }

                if (mFinishFlg == FINISH_OFF && mProgressStartValue >= 360) {
                    mProgressStartValue -= 360;
                }
                if (mFinishFlg == FINISH_OFF && mProgressSweepValue >= 360) {
                    mProgressSweepValue -= 360;
                }
                if (mProgressAccelerate <= PROGRESS_ACCELERATE_MIN) {
                    mProgressAccelerate = PROGRESS_ACCELERATE_MIN;
                }
                if (mProgressAccelerate >= PROGRESS_ACCELERATE_MAX) {
                    mProgressAccelerate = PROGRESS_ACCELERATE_MAX;
                }

                mProgressAccelerate -= PROGRESS_ACCELERATE_ADDITION;

                if(mFinishFlg == FINISH_ON && !mReverse){
                    mProgressSweepValue += 3 * mProgressAccelerate;
                }else {
                    mProgressSweepValue += 5 * mProgressAccelerate;
                }

                if (mFinishFlg == FINISH_OFF && mReverse && mProgressSweepValue >= 0) {
                    mReverse = !mReverse;
                    mProgressSweepValue = 0;
                    mProgressAccelerate = PROGRESS_ACCELERATE_MAX;
                } else if (mFinishFlg == FINISH_OFF && mProgressSweepValue > PROGRESS_MAX_VALUE) {
                    mReverse = !mReverse;
                    mProgressStartValue += mProgressSweepValue;
                    mProgressSweepValue = -PROGRESS_MAX_VALUE;
                    mProgressAccelerate = PROGRESS_ACCELERATE_MAX;
                } else if(mProgressSweepValue > 400){
                    isProgress = false;
                }
            }


            mBackgroundCanvasSize -= 0.05f;
            if (mBackgroundCanvasSize < BACKGROUND_CANVAS_SCALE)
                mBackgroundCanvasSize = BACKGROUND_CANVAS_SCALE;

            //progress background
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / mBackgroundCanvasSize), mProgressBackPaint);
            //ProgressArc
            if (mBackgroundCanvasSize == BACKGROUND_CANVAS_SCALE) {
                canvas.drawArc(mProgressRectF, mProgressStartValue, mProgressSweepValue, false, mProgressArcPaint);
            }
            isInvalidate = true;
        } else {
            if (mBackgroundCanvasSize > FAB_CANVAS_SCALE) {
                mBackgroundCanvasSize = FAB_CANVAS_SCALE;
                mButtonPaint.setShadowLayer(dpToPx(mShadowRadius), 0.0f, mDepth, mShadowColor);
                if(mFinishFlg == FINISH_ON && mFinishAnimator == null){
                    ValueAnimator finishAnimator = ObjectAnimator.ofInt(this, "finishAnimationColor", mFabColor, mProgressColor);
                    finishAnimator.setDuration(PROGRESS_END_DURATION);
                    finishAnimator.setEvaluator(new ArgbEvaluator());
                    finishAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mFinishFlg = FINISH_ENDED;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    ValueAnimator delayAnimator = ObjectAnimator.ofInt(this, "scaleX", 1, 1);
                    delayAnimator.setDuration(mFinishDelay);

                    delayAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mFinishAnimator = null;
                            if(mListener != null && mFinishFlg == FINISH_ENDED)
                                mListener.finishAnimation();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    mFinishAnimator = new AnimatorSet();
                    mFinishAnimator.playSequentially(finishAnimator,delayAnimator);
                    mFinishAnimator.start();
                }
            } else {
                mBackgroundCanvasSize += 0.05f;
                isInvalidate = true;
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / mBackgroundCanvasSize), mProgressBackPaint);
            }
        }

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / FAB_CANVAS_SCALE), mButtonPaint);

        if(mFinishFlg == FINISH_ENDED){
            canvas.drawBitmap(mFinishIconBitmap, (getWidth() - mIconBitmap.getWidth()) / 2, (getHeight() - mIconBitmap.getHeight()) / 2, mDrawablePaint);
        }else{
            canvas.drawBitmap(mIconBitmap, (getWidth() - mIconBitmap.getWidth()) / 2, (getHeight() - mIconBitmap.getHeight()) / 2, mDrawablePaint);
        }
        if (isInvalidate)
            invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getAction() == MotionEvent.ACTION_CANCEL) {
            setAlpha(1.0f);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isEnabled()) {
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
        if (isProgress || mFinishFlg == FINISH_ON || mFinishFlg == FINISH_ENDED) {
            setAlpha(1.0f);
        } else {
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


    public static class SavedState extends BaseSavedState {
        private boolean isProgress;
        private int mProgressValue;
        private int mProgressStartValue;
        private boolean mReverse;
        private int mProgressColor;
        private int mBackgroundCanvasColor;
        private float mBackgroundCanvasSize;
        private float mProgressAccelerate;
        private int mProgressSweepValue;
        private int mFinishFlg;
        private int mFinishDelay;

        public SavedState(Parcel in) {
            /*
             * 必ず書いた順序で読み込むこと！
             */
            super(in);

            isProgress = in.readInt() == 0 ? false : true;
            mProgressValue = in.readInt();
            mProgressStartValue = in.readInt();
            mReverse = in.readInt() == 0 ? false : true;
            mProgressColor = in.readInt();
            mBackgroundCanvasColor = in.readInt();
            mBackgroundCanvasSize = in.readFloat();
            mProgressAccelerate = in.readFloat();
            mProgressSweepValue = in.readInt();
            mFinishFlg = in.readInt();
            mFinishDelay = in.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeInt(isProgress ? 1 : 0);
            out.writeInt(mProgressValue);
            out.writeInt(mProgressStartValue);
            out.writeInt(mReverse ? 1 : 0);
            out.writeInt(mProgressColor);
            out.writeInt(mBackgroundCanvasColor);
            out.writeFloat(mBackgroundCanvasSize);
            out.writeFloat(mProgressAccelerate);
            out.writeInt(mProgressSweepValue);
            out.writeFloat(mFinishFlg);
            out.writeInt(mFinishDelay);
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