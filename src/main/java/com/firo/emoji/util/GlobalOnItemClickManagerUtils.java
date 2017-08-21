package com.firo.emoji.util;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.firo.emoji.adapter.EmotionGridViewAdapter;

import java.lang.ref.WeakReference;

/**
 * Description:点击表情的全局监听管理类
 */
public class GlobalOnItemClickManagerUtils {

    private static GlobalOnItemClickManagerUtils instance;
    private WeakReference<EditText> mWeakEditText;//输入框
    private WeakReference<Context> mWeakContext;

    private GlobalOnItemClickManagerUtils(Context context) {

        mWeakContext = new WeakReference<>(context.getApplicationContext());
    }

    public static GlobalOnItemClickManagerUtils getInstance(Context context) {

        if (instance == null) {
            synchronized (GlobalOnItemClickManagerUtils.class) {
                if (instance == null) {
                    instance = new GlobalOnItemClickManagerUtils(context);
                }
            }
        }
        return instance;
    }

    public void attachToEditText(EditText editText) {
        mWeakEditText = new WeakReference<>(editText);
    }

    public AdapterView.OnItemClickListener getOnItemClickListener(final int emotion_map_type) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAdapter = parent.getAdapter();

                if (itemAdapter instanceof EmotionGridViewAdapter) {
                    // 点击的是表情
                    EmotionGridViewAdapter emotionGvAdapter = (EmotionGridViewAdapter) itemAdapter;

                    if (position == emotionGvAdapter.getCount() - 1) {
                        // 如果点击了最后一个回退按钮,则调用删除键事件
                        mWeakEditText.get().dispatchKeyEvent(new KeyEvent(
                                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                    } else {
                        // 如果点击了表情,则添加到输入框中
                        String emotionName = emotionGvAdapter.getItem(position);

                        // 获取当前光标位置,在指定位置上添加表情图片文本
                        int curPosition = mWeakEditText.get().getSelectionStart();
                        StringBuilder sb = new StringBuilder(mWeakEditText.get().getText().toString());
                        sb.insert(curPosition, emotionName);

                        // 特殊文字处理,将表情等转换一下
                        mWeakEditText.get().setText(SpanStringUtils.getEmotionContent(emotion_map_type,
                                mWeakContext.get(), mWeakEditText.get().getTextSize(), sb.toString()));
                        // 将光标设置到新增完表情的右侧
                        mWeakEditText.get().setSelection(curPosition + emotionName.length());
                    }

                }
            }
        };
    }

}
