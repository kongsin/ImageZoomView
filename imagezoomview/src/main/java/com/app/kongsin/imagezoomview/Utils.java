package com.app.kongsin.imagezoomview;

import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by kongsin on 2/4/2017.
 */

public class Utils {

    private static Gson gson;

    private static final String TAG = "Utils";

    public static void printObject(Object object){
        if (gson == null) gson = new Gson();
        Log.i(TAG, "printObject: " + gson.toJson(object));
    }

}
