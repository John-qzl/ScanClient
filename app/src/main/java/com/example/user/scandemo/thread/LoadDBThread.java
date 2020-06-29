package com.example.user.scandemo.thread;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.user.scandemo.Base.App;
import com.example.user.scandemo.Bean.CheckDetailBean;
import com.example.user.scandemo.application.ScanApplicaton;
import com.example.user.scandemo.db.SQLdb;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/3/24.
 */

public class LoadDBThread  extends Thread {
    private Context context;
    private Handler handler;
    public static SQLdb sqLdb;
    private static List<CheckDetailBean> checkDetailBeanList = new ArrayList<>();
    private String flag = "";

    public LoadDBThread(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        super.run();
        // 1,更新用户信息
        readDb();

    }

    /**
     * @Description:  读取DB文件
     * @author qiaozhili
     * @date 2020/3/24 11:04
     * @param
     * @return
     */
    public boolean readDb() {
        checkDetailBeanList.clear();
        DataSupport.deleteAll(CheckDetailBean.class);
        Boolean result = false;
        sqLdb = new SQLdb();
        SQLiteDatabase db = sqLdb.openDatabase();

        String sqlqurey = "select * from W_PDSBB where REFID=?";
        if (db != null) {
            Cursor cursor = db.rawQuery(sqlqurey, new String[]{ScanApplicaton.getApplication().getChechID()});
            int id = cursor.getColumnIndex("ID");
            for (cursor.moveToFirst();!(cursor.isAfterLast());cursor.moveToNext()) {
                CheckDetailBean checkDetailBean = new CheckDetailBean();
                checkDetailBean.setDeviceName(cursor.getString(cursor.getColumnIndex("F_SBMC")));
                checkDetailBean.setStatus(cursor.getString(cursor.getColumnIndex("F_PDZT")));
                checkDetailBean.setNumber(cursor.getString(cursor.getColumnIndex("F_TYBH")));
                checkDetailBean.setPerson(cursor.getString(cursor.getColumnIndex("F_SYRBGR")));
                checkDetailBean.setCfdd(cursor.getString(cursor.getColumnIndex("F_CFDD")));
                checkDetailBean.setDeviceID(cursor.getString(cursor.getColumnIndex("F_XTLSH")));
                checkDetailBean.setCheckID(cursor.getString(cursor.getColumnIndex("REFID")));
                checkDetailBean.save();
                checkDetailBeanList.add(checkDetailBean);
            }
            ScanApplicaton.getApplication().checkDetailBeanList = checkDetailBeanList;

            flag = "true";
            cursor.close();
            result = true;
        } else {
            Toast.makeText(App.getApp().getApplicationContext(), "数据库不存在", Toast.LENGTH_LONG).show();
            result = false;
            flag = "false";
        }
        notifyCompletion();
        return result;
    }

    public void notifyCompletion() {
//        OrientApplication.getApplication().updataInfoList = updataList;
        Message msg = handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("localread", "okupdata");
        bundle.putString("flag", flag);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }
}
