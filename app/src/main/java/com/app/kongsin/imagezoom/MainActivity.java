package com.app.kongsin.imagezoom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.app.kongsin.imagezoomview.ZoomView;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private ZoomView mZoomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mZoomView = (ZoomView) findViewById(R.id.my_zoom_view);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_cute);
        Glide.with(this).load("https://s-media-cache-ak0.pinimg.com/originals/54/7e/94/547e9497876a3a534cc5e749f060325b.png").into(mZoomView);
    }

    @Override
    public void onBackPressed() {
        mZoomView.releaseZoom();
        //super.onBackPressed();
    }
}
