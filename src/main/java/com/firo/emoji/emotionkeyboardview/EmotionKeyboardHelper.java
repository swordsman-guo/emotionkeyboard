package com.firo.emoji.emotionkeyboardview;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.firo.emoji.util.DisplayUtils;
import com.firo.emoji.util.LogUtils;
import com.firo.emoji.util.ScreenUtils;


/**
 * author :firo
 * time : 2017.8.10
 * description :源码来自开源项目https://github.com/shinezejian/emotionkeyboard
 * 源码项目思路很清晰 但是扩展性不好 而且代码写的很乱
 * 我主要是解决了键盘高度变化适应问题 并且重新封装了下代码
 * 这个类的核心是 表情面板显示逻辑 以及 切换逻辑
 */
public class EmotionKeyboardHelper {

    private static final String SHARE_PREFERENCE_NAME = "emotion_keyboard";
    private static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "soft_input_height";

    private int mDefaultKeyboardHeight;
    private Activity mActivity;
    private InputMethodManager mInputManager;//软键盘管理类
    private SharedPreferences sp;
    private View mEmotionLayout;//表情布局
    private EditText mEditText;//
    private View mContentView;//内容布局view,用于固定bar的高度，防止跳闪

    private EmotionKeyboardHelper() {

    }


    public static EmotionKeyboardHelper with(Activity activity) {
        EmotionKeyboardHelper emotionInputDetector = new EmotionKeyboardHelper();
        emotionInputDetector.mActivity = activity;
        emotionInputDetector.mInputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        emotionInputDetector.sp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        emotionInputDetector.mDefaultKeyboardHeight = (int) (ScreenUtils.getScreenHeight(activity) * 0.4 + 0.5);
        return emotionInputDetector;
    }


    public EmotionKeyboardHelper bindToContent(View contentView) {
        mContentView = contentView;
        return this;
    }

    /**
     * 设置表情内容布局
     */
    public EmotionKeyboardHelper setEmotionView(View emotionView) {
        mEmotionLayout = emotionView;
        return this;
    }

    /**
     * 绑定编辑框
     * 考虑到点击编辑框时由表情面板切换到键盘模式时的情况 固定高度时需要判断键盘高度
     */
    public EmotionKeyboardHelper bindToEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mEmotionLayout.isShown()) {
                    if (getKeyBoardHeight() > mDefaultKeyboardHeight) {

                        lockContentHeight();
                    } else {
                        lockContentHeight(mDefaultKeyboardHeight - getKeyBoardHeight()
                                + mDefaultKeyboardHeight + DisplayUtils.dp2px(mActivity, 5));
                    }
                    hideEmotionLayout(true);
                    unlockContentHeightDelayed();
                }
                return false;
            }
        });
        return this;
    }


    /**
     * 绑定表情按钮
     * 能够适应键盘高度  关键就在于你想要什么样的键盘弹出逻辑
     * lockContentHeight（）是关键代码 锁定高度
     * 因为mDefaultKeyboardHeight=screenHeight*0.4 它并不是手机实际不是键盘高度值 所以在锁定高度的时候就认为5dp和4dp是个误差
     */
    public EmotionKeyboardHelper bindToEmotionButton(View emotionButton) {

        emotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionLayout.isShown()) {
                    if (getKeyBoardHeight() >= mDefaultKeyboardHeight) {
                        lockContentHeight();
                    } else {
                        lockContentHeight(mDefaultKeyboardHeight - getKeyBoardHeight()
                                + mDefaultKeyboardHeight + DisplayUtils.dp2px(mActivity, 5));
                    }
                    hideEmotionLayout(true);
                    unlockContentHeightDelayed();//软件盘显示后，释放内容高度
                } else {
                    if (isSoftInputShown()) {//在这里切换到表情键盘 应该判断高度是否是最小高度

                        //先测量一下键盘的高度保存起来 因为键盘的高度在这个时候是可能被调节的
                        sp.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT,
                                ScreenUtils.getSupportSoftInputHeight(mActivity)).apply();

                        if (getKeyBoardHeight() >= mDefaultKeyboardHeight) {
                            lockContentHeight();
                        } else {
                            lockContentHeight(mDefaultKeyboardHeight + DisplayUtils.dp2px(mActivity, 4));
                        }
                        showEmotionLayout();
                        unlockContentHeightDelayed();
                    } else {
                        LogUtils.e("====:" + getKeyBoardHeight());
                        showEmotionLayout();//两者都没显示，直接显示表情布局 这个height应该是上一次(比如其他页面)固定高度的height
                    }
                }
            }
        });
        return this;
    }


    public EmotionKeyboardHelper build() {

        mActivity.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        hideSoftInput();
        return this;
    }

    /**
     * 获得绑定的EditText bindToEditText之后调用
     */
    public EditText getBindEditText() {

        return mEditText;
    }


    /**
     * 获取表情布局
     */
    public View getEmotionLayout() {

        return mEmotionLayout;
    }

    /**
     * 点击返回键时先隐藏表情布局
     */
    public boolean interceptBackPress() {
        if (mEmotionLayout.isShown()) {
            hideEmotionLayout(false);
            return true;
        }
        return false;
    }

    /**
     * 显示表情面板
     * 注意 不管键盘的高度是什么 我们一定要保证 表情面板的弹出高度在误差允许范围内(你没办法直接确定手机键盘的高度) 不小于最小高度
     */
    private void showEmotionLayout() {

        int softInputHeight = ScreenUtils.getSupportSoftInputHeight(mActivity);
        setEmotionLayoutHeightLimit(softInputHeight);
        hideSoftInput();
        mEmotionLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 高度下限
     *
     * @param softInputHeight 软键盘高度
     */
    private void setEmotionLayoutHeightLimit(int softInputHeight) {
        if (softInputHeight > 0) {
            sp.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
        }

        if (softInputHeight == 0) {//首次进入某个页面
            softInputHeight =
                    getKeyBoardHeight() < mDefaultKeyboardHeight ? mDefaultKeyboardHeight : getKeyBoardHeight();
        }

        mEmotionLayout.getLayoutParams().height
                = softInputHeight < mDefaultKeyboardHeight ? mDefaultKeyboardHeight : softInputHeight;
    }

    /**
     * 隐藏表情布局
     *
     * @param showSoftInput 是否显示软件盘
     */
    private void hideEmotionLayout(boolean showSoftInput) {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }

    /**
     * 锁定内容高度，防止跳闪
     */
    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    private void lockContentHeight(int height) {

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = height;
        params.weight = 0.0F;
    }

    /**
     * 释放被锁定的内容高度
     */
    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
            }
        });
    }

    /**
     * 隐藏软件盘
     */
    private void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 软键盘是否正在显示
     */
    private boolean isSoftInputShown() {
        return ScreenUtils.getSupportSoftInputHeight(mActivity) > 0;
    }

    /**
     * 获取软键盘高度，由于第一次直接弹出表情时会出现小问题，使用屏幕的0.4作为起始弹出高度
     */
    private int getKeyBoardHeight() {
        return sp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, mDefaultKeyboardHeight);
    }
}
