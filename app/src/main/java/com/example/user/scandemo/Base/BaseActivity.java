package com.example.user.scandemo.Base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

/**
 * @author qiaozhili
 * @date 2018年9月17日22:23:19
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = BaseActivity.class.getSimpleName();
    //获取用户信息请求码
    public static final int LOGIN_REQUEST = 1001;
    //不可连续点击
    public static final int MIN_CLICK_DELAY_TIME = 500;
    private long lastClickTime = 0;

    /**
     * 获取页面显示布局
     *
     * @return 显示布局
     */
    protected abstract int getLayoutId();

    /**
     * 初始化View获取数据
     */
    protected abstract void init();

    /**
     * View 点击事件回调
     *
     * @param view
     */
    protected abstract void viewClick(View view);

    //应用程序对象
    private App app;
    //屏幕工具对象 可获取屏幕高宽 或 比例
    private DisplayMetrics metrics;

    /**
     * 页面创建回调方法
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        //设置页面沉浸式状态
        //初始化App对象
        app = (App) getApplication();
        app.addActivity(this);
        //初始化屏幕工具对象
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        init();
    }

    /**
     * 获取应用程序对象
     *
     * @return
     */
    public App getApp() {
        return app;
    }

    /**
     * 获取屏幕工具对象
     *
     * @return
     */
    public DisplayMetrics getMetrics() {
        return metrics;
    }

    /**
     * 获取 页面 Context
     *
     * @return
     */
    public Context getContext() {
        return this;
    }

    /**
     * View 点击回调
     * 过滤View 点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            viewClick(v);
        }

    }

    /**
     * Toast 消息显示
     *
     * @param msg 消息内容
     */
    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }


    public  void onSuccess(boolean b){
        if(b){
            showToast("提交成功");
        }else{
            showToast("提交失败,请重试");
        }
    };


}
