package com.app.kongsin.imagezoomview;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

/**
 * Created by kongsin on 2/5/2017.
 */

public class MyScaleAnimation extends ScaleAnimation {

    private Matrix mMatrix;

    public MyScaleAnimation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScaleAnimation(Matrix matrix, float fromX, float toX, float fromY, float toY) {
        super(fromX, toX, fromY, toY);
        mMatrix = matrix;
    }

    public MyScaleAnimation(Matrix matrix, float fromX, float toX, float fromY, float toY, float pivotX, float pivotY) {
        super(fromX, toX, fromY, toY, pivotX, pivotY);
        mMatrix = matrix;
    }

    public MyScaleAnimation(Matrix matrix, float fromX, float toX, float fromY, float toY, int pivotXType, float pivotXValue, int pivotYType, float pivotYValue) {
        super(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType, pivotYValue);
        mMatrix = matrix;
    }

}
