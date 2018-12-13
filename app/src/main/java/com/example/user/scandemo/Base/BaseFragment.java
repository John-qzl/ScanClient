package com.example.user.scandemo.Base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Created by qiaozhili on 2018/9/17.
 */
@SuppressLint("ValidFragment")
public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = BaseFragment.class.getSimpleName();
    public static final int LOGIN_REQUEST = 1001;

    //不可连续点击
    public static final int MIN_CLICK_DELAY_TIME = 500;
    private long lastClickTime = 0;

    protected abstract int getLayoutId();

    protected abstract void init();

    protected abstract void viewClick(View view);

    private App app;
    private View rootView;
    private Activity activity;
    private DisplayMetrics metrics;

    private boolean isResume;
    protected boolean isVisible = true;

    @Override
    public void onAttach(Context context) {
        activity = (Activity) context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);

        //初始化App对象
        app = (App) activity.getApplication();
        //初始化屏幕工具对象
        metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        init();
        return rootView;
    }

    public App getApp() {
        return app;
    }

    public View getRootView() {
        return rootView;
    }

    public DisplayMetrics getMetrics() {
        return metrics;
    }

    public Context getContext() {
        return activity;
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected void onInvisible() {
    }

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            viewClick(v);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();

        isResume = true;
    }

    public boolean isResume() {
        return isResume;
    }

    public void onShowFragment() {
        if (!isResume) return;
    }



}
