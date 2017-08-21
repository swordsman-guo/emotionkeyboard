package com.firo.emoji.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.firo.emoji.R;
import com.firo.emoji.adapter.HorizontalRecyclerViewAdapter;
import com.firo.emoji.adapter.NoHorizontalScrollerVPAdapter;
import com.firo.emoji.emotionkeyboardview.EmotionKeyboardHelper;
import com.firo.emoji.emotionkeyboardview.NoHorizontalScrollerViewPager;
import com.firo.emoji.model.EmotionImageModel;
import com.firo.emoji.util.EmotionUtils;
import com.firo.emoji.util.GlobalOnItemClickManagerUtils;
import com.firo.emoji.util.LogUtils;
import com.firo.emoji.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by zejian
 * Time  16/1/6 下午5:26
 * Email shinezejian@163.com
 * Description:表情主界面
 */
public class EmotionMainFragment extends BaseFragment implements View.OnClickListener {

    //不使用用默认的EditText 那么你的布局中一定有EditText
    public static final String USE_DEFAULT_TAB_EDIT_TEXT = "use default tab edit text";
    //使用默认EditText的时候要不要隐藏EditText和发送按钮？
    public static final String HIDE_BAR_EDIT_TEXT_AND_SEND_BUTTON = "hide bar's editText and btn";
    public static final String HIDE_TAB_BAR_LEFT_BUTTON = "hide tab bar left button";
    //当前被选中底部tab
    private static final String CURRENT_POSITION_FLAG = "current position flag";
    private int CurrentPosition = 0;

    private HorizontalRecyclerViewAdapter horizontalRecyclerviewAdapter;
    //表情面板辅助类
    private EmotionKeyboardHelper mEmotionKeyboardHelper;

    //底部水平tab
    private RecyclerView bottomTab;
    private EditText mEditTextOfBar;
    //需要绑定的内容view
    private View contentView;
    //不可横向滚动的ViewPager
    private NoHorizontalScrollerViewPager mMainViewPager;

    //是否隐藏bar上的编辑框和发送按钮,默认不隐藏
    private boolean isHideBarEditTextAndSendBtn = false;

    private boolean isHideTabBarLeftButton = true;
    private boolean isUseDefaultTabEditText = true;

    List<Fragment> fragments = new ArrayList<>();

    //回调
    private OnEmotionLayoutClickListener mListener;


    /**
     * 创建与Fragment对象关联的View视图时调用
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_emotion, container, false);
        //获取判断绑定对象的参数
        isHideBarEditTextAndSendBtn = args.getBoolean(EmotionMainFragment.HIDE_BAR_EDIT_TEXT_AND_SEND_BUTTON);
        isHideTabBarLeftButton = args.getBoolean(EmotionMainFragment.HIDE_TAB_BAR_LEFT_BUTTON);
        isUseDefaultTabEditText = args.getBoolean(EmotionMainFragment.USE_DEFAULT_TAB_EDIT_TEXT);

        initView(rootView);
        initKeyboardHelper(rootView, isUseDefaultTabEditText);
        initData();

        //listener
        initListeners(isUseDefaultTabEditText);

        return rootView;
    }

    public void initListeners(boolean isUseDefaultTabEditText) {

        GlobalOnItemClickManagerUtils globalOnItemClickManager =
                GlobalOnItemClickManagerUtils.getInstance(getActivity());

        if (isUseDefaultTabEditText) {
            //绑定当前Bar的编辑框
            globalOnItemClickManager.attachToEditText(mEditTextOfBar);

        } else {
            // false,则表示绑定contentView,自己去绑定editText
            EditText etInput;
            if (contentView instanceof EditText) {
                etInput = (EditText) contentView;
                globalOnItemClickManager.attachToEditText(etInput);
            } else {
                etInput = (EditText) contentView.findViewById(R.id.et_input);
                globalOnItemClickManager.attachToEditText(etInput);
            }
            mEmotionKeyboardHelper.bindToEditText(etInput);
        }
    }

    public void initKeyboardHelper(View rootView, boolean isUseDefaultTabEditText) {
        mEmotionKeyboardHelper = EmotionKeyboardHelper.with(getActivity())
                .setEmotionView(rootView.findViewById(R.id.ll_emotion_layout))//绑定表情主面板
                .bindToContent(contentView)//如果不使用默认的view 则EditTextView的默认id应该是et_input
                .bindToEditText(!isUseDefaultTabEditText ?
                        ((EditText) contentView.findViewById(R.id.et_input)) : ((EditText) rootView.findViewById(R.id.et_of_tab_bar)))//判断绑定那种EditView
                .bindToEmotionButton(rootView.findViewById(R.id.emotion_button))//绑定表情按钮
                .build();
    }


    /**
     * 获得当前EmotionKeyBoard对象 onStart()方法中调用？
     */
    public EmotionKeyboardHelper getBindEmotionKeyboard() {

        return mEmotionKeyboardHelper;
    }


//    /**
//     * 获得发送按钮
//     */
//    public Button getEmotionKeyBoardSendBtn() {
//
//        if (!isUseDefaultTabEditText) {
//
//            throw new RuntimeException("you should't call this method!");
//        }
//
//        return mBtnSend;
//    }

    /**
     * 绑定内容view
     */

    public void bindToContentView(View contentView) {
        this.contentView = contentView;
    }

    /**
     * 初始化view控件
     */
    protected void initView(View rootView) {

        mMainViewPager = (NoHorizontalScrollerViewPager) rootView.findViewById(R.id.nsvp_emotion_main);
        bottomTab = (RecyclerView) rootView.findViewById(R.id.rv_bottom_tab);
        mEditTextOfBar = (EditText) rootView.findViewById(R.id.et_of_tab_bar);
        ImageView leftAddButton = (ImageView) rootView.findViewById(R.id.iv_add_btn);
        Button sendButton = (Button) rootView.findViewById(R.id.btn_send);
        LinearLayout llEditTabBar = (LinearLayout) rootView.findViewById(R.id.ll_edit_bar_bg);

        LogUtils.e("======isHideTabBarLeftButton:" + isHideTabBarLeftButton);
        LogUtils.e("======isUseDefaultTabEditText:" + isUseDefaultTabEditText);
        LogUtils.e("======isUseDefaultTabEditText:" + isUseDefaultTabEditText);
        //左边的添加按钮 默认隐藏
        if (isHideTabBarLeftButton) {
            leftAddButton.setVisibility(GONE);
        } else {
            leftAddButton.setVisibility(VISIBLE);
        }

        //编辑框和发送按钮是否隐藏 默认不隐藏
        if (isHideBarEditTextAndSendBtn) {
            mEditTextOfBar.setVisibility(GONE);
            sendButton.setVisibility(GONE);
            llEditTabBar.setBackgroundResource(android.R.color.transparent);
        } else {
            mEditTextOfBar.setVisibility(VISIBLE);
            sendButton.setVisibility(VISIBLE);
            llEditTabBar.setBackgroundResource(R.drawable.shape_bg_reply_edittext);
        }

        sendButton.setOnClickListener(this);
    }

    /**
     * 数据操作,这里是测试数据，请自行更换数据
     * 在这里添加下边表情分类按钮
     */
    protected void initData() {
        replaceFragment();
        List<EmotionImageModel> list = new ArrayList<>();
        for (int i = 0; i < fragments.size(); i++) {
            if (i == 0) {
                EmotionImageModel model1 = new EmotionImageModel();
                model1.icon = getResources().getDrawable(R.drawable.ic_emotion);
                model1.flag = "经典笑脸";
                model1.isSelected = true;
                list.add(model1);
            } else {
                EmotionImageModel model = new EmotionImageModel();
                model.icon = getResources().getDrawable(R.drawable.ic_plus);
                model.flag = "其他笑脸" + i;
                model.isSelected = false;
                list.add(model);
            }
        }

        //记录底部默认选中第一个
        CurrentPosition = 0;
        SharedPreferenceUtil.setInteger(getActivity(), CURRENT_POSITION_FLAG, CurrentPosition);

        //底部tab
        horizontalRecyclerviewAdapter = new HorizontalRecyclerViewAdapter(getActivity(), list);
        bottomTab.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能
        bottomTab.setAdapter(horizontalRecyclerviewAdapter);
        bottomTab.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        //初始化recycler_view_horizontal监听器
        horizontalRecyclerviewAdapter.setOnClickItemListener(new HorizontalRecyclerViewAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(View view, int position, List<EmotionImageModel> data) {
                //获取先前被点击tab
                int oldPosition = SharedPreferenceUtil.getInteger(getActivity(), CURRENT_POSITION_FLAG, 0);
                //修改背景颜色的标记
                data.get(oldPosition).isSelected = false;
                //记录当前被选中tab下标
                CurrentPosition = position;
                data.get(CurrentPosition).isSelected = true;
                SharedPreferenceUtil.setInteger(getActivity(), CURRENT_POSITION_FLAG, CurrentPosition);
                //通知更新，这里我们选择性更新就行了
                horizontalRecyclerviewAdapter.notifyItemChanged(oldPosition);
                horizontalRecyclerviewAdapter.notifyItemChanged(CurrentPosition);
                //viewpager界面切换
                mMainViewPager.setCurrentItem(position, false);
            }

            @Override
            public void onItemLongClick(View view, int position, List<EmotionImageModel> data) {
            }
        });


    }

    private void replaceFragment() {
        //创建fragment的工厂类
        FragmentFactory factory = FragmentFactory.getSingleFactoryInstance();
        //创建修改实例
        EmotionCompleteFragment f1 = (EmotionCompleteFragment) factory.getFragment(EmotionUtils.EMOTION_CLASSIC_TYPE);
        fragments.add(f1);
        //增加表情在这里
        Bundle b = null;
        for (int i = 0; i < 1; i++) {
            b = new Bundle();
            b.putString("Interge", "Fragment-" + i);
            Fragment1 fg = newInstance(Fragment1.class, b);
            fragments.add(fg);
        }

        NoHorizontalScrollerVPAdapter adapter = new NoHorizontalScrollerVPAdapter(getActivity().getSupportFragmentManager(), fragments);
        mMainViewPager.setAdapter(adapter);
    }


    /**
     * 是否拦截返回键操作，如果此时表情布局未隐藏，先隐藏表情布局
     *
     * @return true则隐藏表情布局，拦截返回键操作
     * false 则不拦截返回键操作
     */
    public boolean isInterceptBackPress() {
        return mEmotionKeyboardHelper.interceptBackPress();
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btn_send) {
            mListener.onSendBtnClick(v);
        }
    }

    interface OnEmotionLayoutClickListener {

        void onSendBtnClick(View view);
    }

    public void setOnEmotionLayoutClickListener(OnEmotionLayoutClickListener listener) {
        mListener = listener;
    }
}


