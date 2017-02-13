package com.app.kongsin.imagezoomview;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
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
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

import java.math.BigDecimal;

/**
 * Created by kongsin on 12/11/2016.
 */

public class ZoomView extends AppCompatImageView implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener, ScaleGestureDetector.OnScaleGestureListener {

    private Matrix mCurrentMatrix;
    private GestureDetector mGestureDetector;
    private static final String TAG = "ZoomView";
    private ScaleGestureDetector scaleGestureDetector;
    private PointF mRect = new PointF();
    private PointF mCurrentZoomPoint = new PointF();
    private MatrixValueManager matrixValueManager, mImageMatrixManager;
    private Handler mHandler = new Handler();
    private float mLastPositionY;
    private float mLastPositionX;
    private boolean isZooming = false;

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
        } else {
            findCurrentZoomPoint();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    adjustPosition();
                }
            });
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
        } else {
            findCurrentZoomPoint();
        }
    }

    private void moveAnimation(final float x, final float y) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mLastPositionY = 0;
                mLastPositionX = 0;
                PropertyValuesHolder valueY = PropertyValuesHolder.ofFloat("y", 0, y);
                PropertyValuesHolder valueX = PropertyValuesHolder.ofFloat("x", 0, x);
                ValueAnimator anim = new ValueAnimator();
                anim.setValues(valueX, valueY);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float valueX = (float) animation.getAnimatedValue("x");
                        float valueY = (float) animation.getAnimatedValue("y");
                        mCurrentMatrix.postTranslate(valueX - mLastPositionX, valueY - mLastPositionY);
                        matrixValueManager.setMatrix(mCurrentMatrix);
                        postInvalidate();
                        mLastPositionY = valueY;
                        mLastPositionX = valueX;
                        if (valueX >= x && valueY >= y){
                            findCurrentZoomPoint();
                        }
                    }
                });
                anim.setDuration(250);
                anim.start();
            }
        });

    }

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
        mRect.set(motionEvent.getX(motionEvent.getPointerCount() -1), motionEvent.getY(motionEvent.getPointerCount() -1));
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
        if (!isZooming) {
            if (!mRect.equals(motionEvent1.getX(), motionEvent1.getY())) {
                calculatePosition(motionEvent1.getX(), motionEvent1.getY());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return matrixValueManager.getScaleX() > 1;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return matrixValueManager.getScaleY() > 1;
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

    private void findCurrentZoomPoint() {
        float _x, _y;
        //X
        float imgW = (getWidth() - (mImageMatrixManager.getTransitionX() * 2)) * matrixValueManager.getScaleX();
        float scrollAbleX = (getWidth() - imgW);
        if (scrollAbleX < 0) {
            float mX = ((matrixValueManager.getTransitionX() / matrixValueManager.getScaleX()));
            float visibleScreenX = (getWidth() / matrixValueManager.getScaleX());
            float percentX = ((Math.abs(mX)) * 100) / (getWidth() - visibleScreenX);
            _x = Math.abs(mX) + ((percentX * visibleScreenX) / 100);
        } else {
            _x = getWidth() / 2;
        }
        //Y
        float imgH = (getHeight() - (mImageMatrixManager.getTransitionY() * 2)) * matrixValueManager.getScaleY();
        float scrollAbleY = (getHeight() - imgH);
        if (scrollAbleY < 0) {
            float mY = ((matrixValueManager.getTransitionY() / matrixValueManager.getScaleY()));
            float visibleScreenY = (getHeight() / matrixValueManager.getScaleY());
            float percentY = ((Math.abs(mY)) * 100) / (getHeight() - visibleScreenY);
            _y = Math.abs(mY) + ((percentY * visibleScreenY) / 100);
        } else {
            _y = getHeight() / 2;
        }
        mCurrentZoomPoint.set(_x, _y);
        postInvalidate();
    }

    private Paint getPaint(){
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setTextSize(50);
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

    float mLastScale = 0;
    public void releaseZoom(){
        if (matrixValueManager.getScaleX() > 1 || matrixValueManager.getScaleY()> 1) {
            isZooming = true;
            mLastScale = 0;
            final float scale = matrixValueManager.getScaleX();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(scale, 1.0f);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    if (value != scale) {
                        Matrix matrix = new Matrix(mCurrentMatrix);
                        matrix.setScale(value, value, mCurrentZoomPoint.x, mCurrentZoomPoint.y);
                        mCurrentMatrix.set(matrix);
                        postInvalidate();
                        if (value == 1) {
                            isZooming = false;
                        }
                    }
                }
            });
            valueAnimator.setDuration(350);
            valueAnimator.start();
        }
    }

    @Override
    public boolean onDoubleTap(final MotionEvent motionEvent) {
        return false;
    }

    private void zoomAnimation(final float scale) {
        isZooming = true;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(matrixValueManager.getScaleX(), scale);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Matrix matrix = new Matrix(mCurrentMatrix);
                matrix.setScale(value, value, mCurrentZoomPoint.x, mCurrentZoomPoint.y);
                mCurrentMatrix.set(matrix);
                matrixValueManager.setMatrix(mCurrentMatrix);
                postInvalidate();
                if (value == scale) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adjustPosition();
                        }
                    }, 100);
                    isZooming = false;
                }
            }
        });
        valueAnimator.setDuration(250);
        valueAnimator.start();
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            if (matrixValueManager.getScaleX() > 1 || matrixValueManager.getScaleX() > 1) {
                releaseZoom();
            } else {
                mCurrentZoomPoint.set(motionEvent.getX(), motionEvent.getY());
                zoomAnimation(2.0F);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        isZooming = true;
        float scale = scaleGestureDetector.getCurrentSpan() / scaleGestureDetector.getPreviousSpan();
        if (matrixValueManager.getScaleX() >= 1) {
            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();
            mCurrentMatrix.postScale(scale, scale, focusX, focusY);
            postInvalidate();
            mCurrentZoomPoint.set(focusX, focusY);
            return true;
        }
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        mRect.set(scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        mRect.set(scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        isZooming = false;
    }
}
