package com.firo.emoji.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.firo.emoji.R;
import com.firo.emoji.util.EmotionUtils;

import java.util.List;

/**
 * Created by zejian
 * Time  16/1/7 下午4:46
 * Email shinezejian@163.com
 * Description:一个GridView中有20个表情和1个删除按钮
 */
public class EmotionGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mEmotionNames;
    private int mEmotionWidth;
    private int mEmotionType;

    public EmotionGridViewAdapter(Context context, List<String> emotionNames, int itemWidth, int emotionType) {
        mContext = context;
        mEmotionNames = emotionNames;
        mEmotionWidth = itemWidth;
        mEmotionType = emotionType;
    }

    @Override
    public int getCount() {
        //删除按钮
        return mEmotionNames.size() + 1;
    }

    @Override
    public String getItem(int position) {
        return mEmotionNames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        // 设置内边距
        imageView.setPadding(mEmotionWidth / 8, mEmotionWidth / 8, mEmotionWidth / 8, mEmotionWidth / 8);
        LayoutParams params = new LayoutParams(mEmotionWidth, mEmotionWidth);
        imageView.setLayoutParams(params);

        //判断是否为最后一个item
        if (position == getCount() - 1) {
            imageView.setImageResource(R.drawable.compose_emotion_delete);
        } else {
            String emotionName = mEmotionNames.get(position);
            imageView.setImageResource(EmotionUtils.getImgByName(mEmotionType, emotionName));
        }

        return imageView;
    }

}
