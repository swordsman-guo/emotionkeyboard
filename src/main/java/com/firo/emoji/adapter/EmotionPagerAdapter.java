package com.firo.emoji.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

/**
 * Created by zejian
 * Time  16/1/7 下午4:09
 * Email shinezejian@163.com
 * Description: EmotionCompleteFragment界面Viewpager数据适配器
 */
public class EmotionPagerAdapter extends PagerAdapter {

    private List<GridView> mGridViews;

    public EmotionPagerAdapter(List<GridView> gridViews) {
        mGridViews = gridViews;
    }

    @Override
    public int getCount() {
        return mGridViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mGridViews.get(position));
    }

    @Override
    public GridView instantiateItem(ViewGroup container, int position) {
        container.addView(mGridViews.get(position));
        return mGridViews.get(position);
    }

}
