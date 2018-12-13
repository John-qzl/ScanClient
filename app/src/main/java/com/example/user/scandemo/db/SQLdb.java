package com.example.user.scandemo.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.ColorSpace;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.litepal.tablemanager.Connector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.example.user.scandemo.Base.App.getApp;
import static java.security.AccessController.getContext;

/**
 * Created by qiaozhili on 2018/10/9 9:11.
 */

public class SQLdb {
    private SQLiteDatabase db;
    private static SQLdb sqLdb;
    public SQLdb() {
        db = Connector.getDatabase();
    }
    /**
     * 获得数据库实例对象
     * @return
     */
    public synchronized static SQLdb getInstance(){
        if(sqLdb == null){
            sqLdb = new SQLdb();
        }
        return sqLdb;
    }
    //数据库存储路径
    public static String filePath1 = Environment.getExternalStorageDirectory() + "/ScanClient/pandian.db";
    public static String filePath = Environment.getExternalStorageDirectory() + "/ScanClient";
    public  SQLiteDatabase openDatabase(Context context){
        System.out.println("filePath:" + filePath);
        File jhPath = new File(filePath);
        File jhPath1 = new File(filePath1);
        if (!jhPath.exists()) {
            jhPath.mkdirs();
        }
        //查看数据库文件是否存在
        if(jhPath1.exists()){
            //存在则直接返回打开的数据库
            return SQLiteDatabase.openOrCreateDatabase(jhPath1, null);
        }else{
            Toast.makeText(getApp().getApplicationContext(), "数据库不存在", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}
