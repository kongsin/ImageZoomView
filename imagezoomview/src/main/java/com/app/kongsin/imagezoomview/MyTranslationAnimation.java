package com.app.kongsin.imagezoomview;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

/**
 * Created by kongsin on 2/5/2017.
 */

public class MyTranslationAnimation extends TranslateAnimation {

    private Matrix mMatrix;

    public MyTranslationAnimation(Matrix matrix, Context context, AttributeSet attrs) {
        super(context, attrs);
        mMatrix = matrix;
    }

    public MyTranslationAnimation(Matrix matrix, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta) {
        super(fromXDelta, toXDelta, fromYDelta, toYDelta);
        mMatrix = matrix;
    }

    public MyTranslationAnimation(Matrix matrix, int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue) {
        super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue, toYType, toYValue);
        mMatrix = matrix;
    }

}
