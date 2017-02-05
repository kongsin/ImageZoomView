package com.app.kongsin.imagezoom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.kongsin.imagezoomview.ZoomView;

public class MainActivity extends AppCompatActivity {

    private ZoomView mZoomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mZoomView = (ZoomView) findViewById(R.id.my_zoom_view);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_cute);
        mZoomView.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        mZoomView.releaseZoom();
        //super.onBackPressed();
    }
}
