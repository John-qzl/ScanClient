package com.example.user.scandemo.Base;

import android.content.Context;

/**
 * Created by qiaozhili on 2018/9/17.
 */
public interface BaseView {

    App getApp();

    Context getContext();

    void showError(String msg);
}