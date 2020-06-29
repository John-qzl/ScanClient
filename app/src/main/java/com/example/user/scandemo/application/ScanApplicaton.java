package com.example.user.scandemo.application;

import android.app.Application;

import com.example.user.scandemo.Bean.CheckDetailBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/3/24.
 */

public class ScanApplicaton extends Application {
    public List<CheckDetailBean> checkDetailBeanList = new ArrayList<>();
    public String chechID;
    public static ScanApplicaton app = null;

    public String getChechID() {
        return chechID;
    }

    public void setChechID(String chechID) {
        this.chechID = chechID;
    }
    public static ScanApplicaton getApplication()
    {
        if(app==null)
        {
            app = new ScanApplicaton();
        }
        return app;
    }
}
