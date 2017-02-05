package com.app.kongsin.imagezoomview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by kongsin on 12/11/2016.
 */

public class ZoomView extends AppCompatImageView implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {

    private Matrix mCurrentMatrix;
    private GestureDetector mGestureDetector;
    private static final String TAG = "ZoomView";
    private ScaleGestureDetector scaleGestureDetector;
    private PointF mRect = new PointF();
    private MatrixValueManager matrixValueManager, mImageMatrixManager;
    private TranslateAnimation mAnimation;
    private ScaleAnimation mMyScaleAnimation;
    private float lastX;
    private float lastY;
    private Handler mHandler = new Handler();
    private float mLastPositionY;
    private float mLastPositionX;

    public ZoomView(Context context) {
        super(context);
        initaial();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    public ZoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initaial();
    }

    public ZoomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initaial();
    }

    private void initaial(){
        matrixValueManager = new MatrixValueManager();
        mImageMatrixManager = new MatrixValueManager();
        setLayerType(LAYER_TYPE_HARDWARE, null);
        mCurrentMatrix = getImageMatrix();
        mGestureDetector = new GestureDetector(getContext(), this);
        scaleGestureDetector = new ScaleGestureDetector(getContext(), this);
        mGestureDetector.setOnDoubleTapListener(this);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, final MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL){
                    onActionUp();
                }
                scaleGestureDetector.onTouchEvent(motionEvent);
                mGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    @Override
    public void invalidate() {
        super.invalidate();
        matrixValueManager.setMatrix(mCurrentMatrix);
    }

    protected boolean onActionUp() {
        if (matrixValueManager.getScaleX() <= 1) {
            mCurrentMatrix.reset();
            postInvalidate();
        }
        return true;
    }

    private void adjustPosition() {

        float imgH = (getHeight() - (mImageMatrixManager.getTransitionY() *2)) * matrixValueManager.getScaleY();
        float mY = (matrixValueManager.getTransitionY() + (mImageMatrixManager.getTransitionY()) * matrixValueManager.getScaleY());
        float scrollAbleY = (getHeight() - imgH);

        float vH = ((getHeight() * matrixValueManager.getScaleY()) - getHeight()) / 2;
        float vW = ((getWidth() * matrixValueManager.getScaleX()) - getWidth()) / 2;

        float x = 0, y = 0;

        if (imgH < getHeight()){
            y = (-vH - matrixValueManager.getTransitionY());
        } else if (imgH >= getHeight()){
            if (mY > 0) {
                y = -mY;
            } else if (mY < scrollAbleY) {
                y = scrollAbleY - mY;
            }
        }

        float mX = (matrixValueManager.getTransitionX() + (mImageMatrixManager.getTransitionX()) * matrixValueManager.getScaleX());
        float imgW = (getWidth() - (mImageMatrixManager.getTransitionX() * 2)) * matrixValueManager.getScaleX();
        float scrollAbleX = (getWidth() - imgW);

        if (imgW < getWidth()){
            x = (-vW - matrixValueManager.getTransitionX());
        } else if (imgW >= getWidth()){
            if (mX > 0) {
                x = -mX;
            } else if (mX < scrollAbleX) {
                x = scrollAbleX - mX;
            }
        }

        if (x != 0 || y != 0) {
            moveAnimation(x, y);
        }
    }

    private void moveAnimation(final float x, final float y) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: " + y);
                mLastPositionY = 0;
                ValueAnimator animY = ValueAnimator.ofFloat(0, y);
                animY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        Log.i(TAG, "onAnimationUpdate: " + value);
                        mCurrentMatrix.postTranslate(0, value - mLastPositionY);
                        matrixValueManager.setMatrix(mCurrentMatrix);
                        postInvalidate();
                        mLastPositionY = value;
                    }
                });
                animY.setDuration(250);
                animY.start();

                mLastPositionX = 0;
                ValueAnimator animX = ValueAnimator.ofFloat(0, x);
                animX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        Log.i(TAG, "onAnimationUpdate: " + value);
                        mCurrentMatrix.postTranslate(value - mLastPositionX, 0);
                        matrixValueManager.setMatrix(mCurrentMatrix);
                        postInvalidate();
                        mLastPositionX = value;
                    }
                });
                animX.setDuration(250);
                animX.start();

            }
        });

    }

//    private void moveAnimation(final float x, final float y) {
//
//        mAnimation = new TranslateAnimation(0, x, 0, y);
//        mAnimation.setDuration(250);
//        mAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                move(x, y);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//
//        startAnimation(mAnimation);
//    }

    private void move(float x, float y) {
        Matrix matrix = new Matrix(mCurrentMatrix);
        matrix.postTranslate(x, y);
        mCurrentMatrix.set(matrix);
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.concat(mCurrentMatrix);
        matrixValueManager.setMatrix(mCurrentMatrix);
        mImageMatrixManager.setMatrix(getImageMatrix());
        super.onDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        mRect.set(motionEvent.getX(), motionEvent.getY());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(final MotionEvent motionEvent, final MotionEvent motionEvent1, float v, float v1) {
        if (motionEvent1.getPointerCount() == 1) {
            if (!mRect.equals(motionEvent1.getX(), motionEvent1.getY())) {
                calculatePosition(motionEvent1.getX(), motionEvent1.getY());
            }
        }
        return true;
    }

    private void calculatePosition(float rawX, float rawY){
        float x = (rawX - mRect.x);
        float y  = (rawY - mRect.y);

        float mY = (matrixValueManager.getTransitionY() + (mImageMatrixManager.getTransitionY() * matrixValueManager.getScaleY()));
        float imgH = (getHeight() - (mImageMatrixManager.getTransitionY() * 2)) * matrixValueManager.getScaleY();
        float scrollAbleY = (getHeight() - imgH);
        if (imgH > getHeight()){
            float r = (mY + y);
            float s = (r - scrollAbleY);

            if (s < 0) {
                y = 0;
            }

            if (r > 0) {
                y = 0;
            }

        } else {
            y = 0;
        }

        float mX = (matrixValueManager.getTransitionX() + (mImageMatrixManager.getTransitionX() * matrixValueManager.getScaleX()));
        float imgW = (getWidth() - (mImageMatrixManager.getTransitionX() * 2)) * matrixValueManager.getScaleX();
        float scrollAbleX = (getWidth() - imgW);
        if ((imgW) > getWidth()){
            float l = (mX + x);
            float s = (l - scrollAbleX);

            if (s < 0) {
                x = 0;
            }

            if (l > 0) {
                x = 0;
            }

        } else {
            x = 0;
        }

        mRect.set(rawX, rawY);
        move(x, y);
    }

    private Paint getPaint(){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(16);
        return paint;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(final MotionEvent motionEvent, final MotionEvent motionEvent1, float v, float v1) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    public void releaseZoom(){
        if (matrixValueManager.getScaleX() > 1 || matrixValueManager.getScaleY()> 1) {
            final float scale = matrixValueManager.getScaleX();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(Math.abs(1 - scale));
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    Matrix matrix = new Matrix(mCurrentMatrix);
                    matrix.setScale(scale - value, scale - value, mRect.x, getHeight() / 2);
                    mCurrentMatrix.set(matrix);
                    postInvalidate();
                }
            });
            valueAnimator.setDuration(250);
            valueAnimator.start();
        }
    }

    @Override
    public boolean onDoubleTap(final MotionEvent motionEvent) {
        if (matrixValueManager.getScaleX() > 1 || matrixValueManager.getScaleX() > 1) {
            releaseZoom();
        } else {
            zoomAnimation(motionEvent, 2.0F);
        }
        return true;
    }

    private void zoomAnimation(final MotionEvent motionEvent, final float scale) {

        mMyScaleAnimation = new ScaleAnimation(1.0F, scale, 1.0F, scale, motionEvent.getX(), getHeight() / 2);
        mMyScaleAnimation.setDuration(250);
        mMyScaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Matrix matrix = new Matrix(mCurrentMatrix);
                matrix.postScale(scale, scale, motionEvent.getX(), getHeight() / 2);
                mCurrentMatrix.set(matrix);
                postInvalidate();

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        adjustPosition();
                    }
                });

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        startAnimation(mMyScaleAnimation);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        float scale = scaleGestureDetector.getCurrentSpan() / scaleGestureDetector.getPreviousSpan();
        float focusX = scaleGestureDetector.getFocusX();
        float focusY = scaleGestureDetector.getFocusY();
        mCurrentMatrix.postScale(scale, scale, focusX, focusY);
        postInvalidate();
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        Log.i(TAG, "onScaleBegin: ");
        mRect.set(scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        Log.i(TAG, "onScaleEnd: ");
        mRect.set(scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        postDelayed(new Runnable() {
            @Override
            public void run() {
                adjustPosition();
            }
        }, 250);
    }
}
