package com.app.kongsin.imagezoomview;

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
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

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
                    if (matrixValueManager.getScaleX() < 1) {
                        mCurrentMatrix.reset();
                        postInvalidate();
                        return true;
                    } else {
                        float imgH = (getHeight() - (mImageMatrixManager.getTransitionY() *2)) * matrixValueManager.getScaleY();
                        if (imgH < getHeight()){
                            float v = ((getHeight() * matrixValueManager.getScaleY()) - getHeight()) / 2;
                            mCurrentMatrix.postTranslate(0, (-v - matrixValueManager.getTransitionY()));
                            postInvalidate();
                        }
                    }
                }
                scaleGestureDetector.onTouchEvent(motionEvent);
                mGestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
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
        float imgH = (getHeight() - (mImageMatrixManager.getTransitionY() * 2));
        float scrollAbleY = (getHeight() - (imgH * matrixValueManager.getScaleY()));
        if ((imgH * matrixValueManager.getScaleY()) > getHeight()){
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
        float imgW = (getWidth() - (mImageMatrixManager.getTransitionX() * 2));
        float scrollAbleX = (getWidth() - (imgW * matrixValueManager.getScaleX()));
        if ((imgW * matrixValueManager.getScaleX()) > getWidth()){
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

        mCurrentMatrix.postTranslate(x, y);
        mRect.set(rawX, rawY);
        invalidate();
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
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mCurrentMatrix.reset();
                ViewCompat.postInvalidateOnAnimation(ZoomView.this);
            }
        });
    }

    @Override
    public boolean onDoubleTap(final MotionEvent motionEvent) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                float[] matrixValue = new float[9];
                mCurrentMatrix.getValues(matrixValue);
                if (matrixValue[Matrix.MSCALE_X] > 1 || matrixValue[Matrix.MSCALE_Y] > 1) {
                    mCurrentMatrix.reset();
                } else {
                    mCurrentMatrix.postScale(2.0F, 2.0F, motionEvent.getX(), motionEvent.getY());
                }
                ViewCompat.postInvalidateOnAnimation(ZoomView.this);
            }
        });
        return true;
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
    }
}
