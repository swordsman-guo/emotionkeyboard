package com.firo.emoji.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Description:封装代码的基类
 * 基类BaseFragment中的传递参数args可以供子类选择性使用
 */
public class BaseFragment extends Fragment {

    protected Bundle args;

    /**
     * 创建fragment的静态方法，方便传递参数
     *
     * @param args 传递的参数
     */
    public static <T extends Fragment> T newInstance(Class<T> clazz, Bundle args) {
        T mFragment = null;
        try {
            mFragment = clazz.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        assert mFragment != null;
        mFragment.setArguments(args);
        return mFragment;
    }

    /**
     * 初始创建Fragment对象时调用
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments();
    }


}
