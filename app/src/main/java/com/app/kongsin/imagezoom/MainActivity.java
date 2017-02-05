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
        Glide.with(this).load("https://muffinbros.com/wp-content/uploads/fancy_products_uploads/2016/06/14/aasdsadasd.png").into(mZoomView);
    }

    @Override
    public void onBackPressed() {
        mZoomView.releaseZoom();
        //super.onBackPressed();
    }
}
