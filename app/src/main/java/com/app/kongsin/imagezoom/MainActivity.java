package com.app.kongsin.imagezoom;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.kongsin.imagezoomview.ZoomView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mPager;
    private PagerAdpter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> _urls = new ArrayList<>();
        _urls.add("http://img.7te.org/images/570x363/rusted-robo-studios-nature-hd-live-wallpaper-121659.jpg");
        _urls.add("https://www.clipartsgram.com/image/446141344-sunset-in-winter-nature-wallpaper.jpg");
        _urls.add("https://s-media-cache-ak0.pinimg.com/736x/50/51/f2/5051f25fc731ffb2bb48b09d97bbd4fb.jpg");
        _urls.add("http://wallpapercave.com/wp/zr5GMoE.jpg");
        _urls.add("http://www.zastavki.com/pictures/originals/2013/Winter_A_lonely_house_in_winter_forest_053892_.jpg");

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(2);
        mPagerAdapter = new PagerAdpter(this, _urls);
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        ZoomView zoomView = (ZoomView) mPager.getChildAt(mPager.getCurrentItem());
        zoomView.releaseZoom();
    }

    public class PagerAdpter extends PagerAdapter{

        private final List<String> mUrls;
        private Context mContext;

        public PagerAdpter(Context context, List<String> urls){
            mContext = context;
            mUrls = urls;
        }

        @Override
        public int getCount() {
            return mUrls.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ZoomView zoomView = new ZoomView(mContext);
            zoomView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(mContext).load(mUrls.get(position)).into(zoomView);
            container.addView(zoomView);
            return zoomView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (container != null){
                container.removeView((View) object);
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object.equals(view);
        }
    }

}
