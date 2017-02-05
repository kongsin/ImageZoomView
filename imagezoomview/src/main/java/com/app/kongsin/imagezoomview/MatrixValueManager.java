package com.app.kongsin.imagezoomview;

import android.graphics.Matrix;

/**
 * Created by kongsin on 2/4/2017.
 */

public class MatrixValueManager {

    float[] floats;

    public void setMatrix(Matrix matrix){
        floats = new float[9];
        matrix.getValues(floats);
    }

    public float getTransitionX(){
        return floats[Matrix.MTRANS_X];
    }

    public float getTransitionY(){
        return floats[Matrix.MTRANS_Y];
    }

    public float getScaleX(){
        return floats[Matrix.MSCALE_X];
    }

    public float getScaleY(){
        return floats[Matrix.MSCALE_Y];
    }

}
