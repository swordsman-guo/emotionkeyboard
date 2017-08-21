package com.firo.emoji.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.firo.emoji.R;
import com.firo.emoji.adapter.EmotionGridViewAdapter;
import com.firo.emoji.adapter.EmotionPagerAdapter;
import com.firo.emoji.emotionkeyboardview.EmotionIndicatorView;
import com.firo.emoji.util.DisplayUtils;
import com.firo.emoji.util.EmotionUtils;
import com.firo.emoji.util.GlobalOnItemClickManagerUtils;
import com.firo.emoji.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:可替换的模板表情，gridview实现
 */
public class EmotionCompleteFragment extends BaseFragment {
    private ViewPager mEmotionViewPager;
    private EmotionIndicatorView mEmotionIndicator;//表情面板对应的点列表
    private int mEmotionType;

    /**
     * 创建与Fragment对象关联的View视图时调用
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_complate_emotion, container, false);
        initView(rootView);
        initListener();
        return rootView;
    }

    /**
     * 初始化view控件
     */
    protected void initView(View rootView) {
        mEmotionViewPager = (ViewPager) rootView.findViewById(R.id.vp_complete_emotion_layout);
        mEmotionIndicator = (EmotionIndicatorView) rootView.findViewById(R.id.ll_point_group);
        mEmotionType = args.getInt(FragmentFactory.EMOTION_MAP_TYPE);//emotion_type
        initEmotion();
    }

    /**
     * 监听ViewPager的滑动 设置Indicator
     */
    protected void initListener() {

        mEmotionViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int oldPagerPos = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mEmotionIndicator.playByStartPointToNext(oldPagerPos, position);
                oldPagerPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化表情面板
     * 思路：获取表情的总数，按每行存放7个表情，动态计算出每个表情所占的宽度大小（包含间距），
     * 而每个表情的高与宽应该是相等的，这里我们约定只存放3行
     * 每个面板最多存放7*3=21个表情，再减去一个删除键，即每个面板包含20个表情
     * 根据表情总数，循环创建多个容量为20的List，存放表情，对于大小不满20进行特殊
     * 处理即可。
     */
    private void initEmotion() {
        // 获取屏幕宽度
        int screenWidth = ScreenUtils.getScreenWidth(getActivity());
        // 自定义一个间距 有了这个间距 包括emotion的大小 面板的高度就很容易了
        int spacing = DisplayUtils.dp2px(getActivity(), 12);
        // 动态计算item的宽度和高度
        int itemWidth = (screenWidth - spacing * 8) / 7;
        //动态计算gridView的总高度 *6意味着竖直间距是水平间距的2倍
        int gvHeight = itemWidth * 3 + spacing * 6;

        List<GridView> emotionViews = new ArrayList<>();
        List<String> emotionNames = new ArrayList<>();
        // 遍历所有的表情的key
        for (String emotionName : EmotionUtils.getEmojiMap(mEmotionType).keySet()) {
            emotionNames.add(emotionName);
            // 每20个表情作为一组,同时添加到ViewPager对应的view集合中
            if (emotionNames.size() == 20) {
                GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
                emotionViews.add(gv);
                // 添加完一组表情,重新创建一个表情名字集合
                emotionNames = new ArrayList<>();
            }
        }

        // 判断最后是否有不足20个表情的剩余情况
        if (emotionNames.size() > 0) {
            GridView gv = createEmotionGridView(emotionNames, screenWidth, spacing, itemWidth, gvHeight);
            emotionViews.add(gv);
        }

        //初始化指示器
        mEmotionIndicator.initIndicator(emotionViews.size());
        //初始化ViewPager
        EmotionPagerAdapter emotionPagerGvAdapter = new EmotionPagerAdapter(emotionViews);
        mEmotionViewPager.setAdapter(emotionPagerGvAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, gvHeight);
        mEmotionViewPager.setLayoutParams(params);
    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(List<String> emotionNames, int gridViewWidth, int padding, int emotionWidth, int gridViewHeight) {
        // 创建GridView
        GridView gv = new GridView(getActivity());
        //设置点击背景透明
        gv.setSelector(android.R.color.transparent);
        //设置7列
        gv.setNumColumns(7);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding * 2);
        //设置GridView的宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gridViewWidth, gridViewHeight);
        gv.setLayoutParams(params);
        // 给GridView设置表情图片
        EmotionGridViewAdapter adapter = new EmotionGridViewAdapter(getActivity(), emotionNames, emotionWidth, mEmotionType);
        gv.setAdapter(adapter);
        //设置全局点击事件
        gv.setOnItemClickListener(GlobalOnItemClickManagerUtils.getInstance(getActivity()).getOnItemClickListener(mEmotionType));
        return gv;
    }
}
