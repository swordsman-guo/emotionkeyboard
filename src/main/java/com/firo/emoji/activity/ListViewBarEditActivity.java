package com.firo.emoji.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.firo.emoji.R;
import com.firo.emoji.fragment.EmotionMainFragment;

public class ListViewBarEditActivity extends AppCompatActivity {

    private ListView listView;
    private EmotionMainFragment emotionMainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_listview_bar_edit);
        initView();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        listView = (ListView) findViewById(R.id.listview);
    }


    /**
     * 初始化布局数据
     */
    private void initData() {
        initEmotionMainFragment();
    }

    /**
     * 初始化表情面板
     */
    public void initEmotionMainFragment() {

        Bundle bundle = new Bundle();
        bundle.putBoolean(EmotionMainFragment.USE_DEFAULT_TAB_EDIT_TEXT, true);
        bundle.putBoolean(EmotionMainFragment.HIDE_BAR_EDIT_TEXT_AND_SEND_BUTTON, false);
        bundle.putBoolean(EmotionMainFragment.HIDE_TAB_BAR_LEFT_BUTTON, true);
        emotionMainFragment = EmotionMainFragment.newInstance(EmotionMainFragment.class, bundle);
        emotionMainFragment.bindToContentView(listView);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_emotionview_main, emotionMainFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (!emotionMainFragment.isInterceptBackPress()) {
            super.onBackPressed();
        }
    }

}
