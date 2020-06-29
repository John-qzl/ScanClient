package com.example.user.scandemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.scandemo.Base.App;
import com.example.user.scandemo.Bean.CheckBean;
import com.example.user.scandemo.Bean.CheckDetailBean;
import com.example.user.scandemo.R;
import com.example.user.scandemo.ScannerInterface;
import com.example.user.scandemo.application.ScanApplicaton;
import com.example.user.scandemo.barcodeservice.SerialPortService;
import com.example.user.scandemo.db.SQLdb;
import com.example.user.scandemo.fragment.HomeFragment;
import com.example.user.scandemo.thread.LoadDBThread;
import com.example.user.scandemo.utils.ActivityCollector;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by qiaozhili on 2018/9/28 22:22.
 */

public class CheckDetailActivity extends FragmentActivity implements AdapterView.OnItemClickListener {
    private static int localPosition = 0;
    private LinearLayout ll_back;
    private TextView tv_scan;
    private static String chechID;
    private static List<CheckDetailBean> checkDetailBeanList = new ArrayList<>();
    public static SQLdb sqLdb;
    private ProgressDialog prodlg;
    private Context context;

    ScannerInterface scanner;
    IntentFilter intentFilter;
    BroadcastReceiver scanReceiver;


    private boolean isContinue = false;

    private static final String RES_ACTION = "android.intent.action.SCANRESULT";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkdetail);
//        getActionBar().hide();
        ActivityCollector.addActivity(this);
        context = this;
        initview();
        sqLdb = new SQLdb();
        chechID = getIntent().getStringExtra("checkID");
//        localPosition = 0;
        selectItem(localPosition);

        this.prodlg = ProgressDialog.show(this, "提示", "正在加载数据");
        prodlg.setIcon(this.getResources().getDrawable(R.mipmap.scan1));
        LoadDBThread uptataThread = new LoadDBThread(context, handler);
        uptataThread.start();
//        initScanner();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(SerialPortService.BARCODEPORT_RECEIVEDDATA_ACTION);
//        registerReceiver(receiver, intentFilter);

    }

    /**
     * @Description: 读取盘点设备表中数据信息
     * @author qiaozhili
     * @date 2018/10/18 8:28
     * @param
     * @return
     */
    public static boolean readDb() {
        checkDetailBeanList.clear();
        DataSupport.deleteAll(CheckDetailBean.class);
        Boolean result = false;
        SQLiteDatabase db = sqLdb.openDatabase();

        String sqlqurey = "select * from W_PDSBB where REFID=?";
        if (db != null) {
            Cursor cursor = db.rawQuery(sqlqurey, new String[]{chechID});
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
            cursor.close();
            result = true;
        } else {
            Toast.makeText(App.getApp().getApplicationContext(), "数据库不存在", Toast.LENGTH_LONG).show();
            result = false;
        }
        return result;
    }

    /**
     * @Description: 更新外部W_PDSBB（子表）数据库及内部数据
     * @author qiaozhili
     * @date 2018/10/18 8:27
     * @param
     * @return
     */
    public static void updateDb(String data, String chechId) {
        SQLiteDatabase db = sqLdb.openDatabase();
        String status = "";
        //查询此盘点计划有无此信息
        List<CheckDetailBean> checkDetailBean1 = DataSupport.where("checkID=? and deviceID=?", chechId,data).find(CheckDetailBean.class);
        if (checkDetailBean1 != null && !checkDetailBean1.equals("") && checkDetailBean1.size() != 0) {
            Cursor cursor = db.rawQuery("select * from W_PDSBB where F_XTLSH=?", new String[]{data});
            if (cursor.moveToNext()) {
                status = cursor.getString(cursor.getColumnIndex("F_PDZT"));
            }
            if (status.equals("是")) {
                Toast.makeText(App.getApp().getApplicationContext(), "此设备已盘点！", Toast.LENGTH_LONG).show();
            } else if (status.equals("否")) {
                //实例化ContentValues
                ContentValues cv = new ContentValues();
                cv.put("F_PDZT", "是");
                //更新条件
                String whereClause = "F_XTLSH = ?";
                //更新条件数组
                String[] whereArgs = new String[]{data};
                if (db.update("W_PDSBB", cv, whereClause, whereArgs) == 1) {
                } else {
                    Toast.makeText(App.getApp().getApplicationContext(), "盘点计划无此二维码2！", Toast.LENGTH_LONG).show();
                }
                db.close();
                CheckDetailBean checkDetailBean = new CheckDetailBean();
                checkDetailBean.setStatus("是");
                checkDetailBean.updateAll("deviceID=?", data);
                updateMainDb(data);
            } else {
                Toast.makeText(App.getApp().getApplicationContext(), "盘点计划无此二维码3！", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(App.getApp().getApplicationContext(), "盘点计划无此二维码1！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * @Description: 更新外部W_ZCPDJHB数据库及内部数据
     * @author qiaozhili
     * @date 2018/10/29 15:39
     * @param
     * @return
     */
    public static void updateMainDb(String data) {
        SQLiteDatabase db = sqLdb.openDatabase();
        String parentID = "";
        String WCQK = "";
        int YPD = 0;
        int PDSBZS = 0;
        float pressent = 0;
        Cursor cursor = db.rawQuery("select * from W_PDSBB where F_XTLSH=?", new String[]{data});
        if (cursor.moveToNext()) {
            parentID = cursor.getString(cursor.getColumnIndex("REFID"));
        }
        Cursor cursor1 = db.rawQuery("select * from W_ZCPDJHB where ID=?", new String[]{parentID});
        if (cursor1.moveToNext()) {
            YPD = Integer.parseInt(cursor1.getString(cursor1.getColumnIndex("F_YPD")));
            PDSBZS = Integer.parseInt(cursor1.getString(cursor1.getColumnIndex("F_PDSBZS")));
        }
        if (PDSBZS != 0) {
            pressent = (float) (YPD + 1) / PDSBZS * 100;
            DecimalFormat decimalFormat =new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
            WCQK = decimalFormat.format(pressent);
        }
        //实例化ContentValues
        ContentValues cv= new ContentValues();
        cv.put("F_YPD",String.valueOf(YPD+1));
        cv.put("F_WCQK",WCQK);
        if (WCQK.equals("100.00")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss
            //获取当前时间
            Date date = new Date(System.currentTimeMillis());
            cv.put("F_JSSJ",simpleDateFormat.format(date));
        }
        //更新条件
        String whereClause = "ID = ?";
        //更新条件数组
        String[] whereArgs = new String[]{parentID};
        if (db.update("W_ZCPDJHB", cv, whereClause, whereArgs) == 1) {
            Toast.makeText(App.getApp().getApplicationContext(), "盘点成功", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(App.getApp().getApplicationContext(), "此二维码已盘点！", Toast.LENGTH_LONG).show();
        }


        db.close();
        List<CheckDetailBean> checkDetailBeans = DataSupport.where("deviceID=?","10000010930462").find(CheckDetailBean.class);
        CheckBean checkBean = new CheckBean();
        checkBean.setYpd(checkBean.getPdzs() + 1);
        checkBean.updateAll("checkID=?", parentID);
//        Toast.makeText(App.getApp().getApplicationContext(), "数据更新成功", Toast.LENGTH_LONG).show();
//        readDb();
    }

    private void initview() {
        ll_back = (LinearLayout) findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_scan = (TextView) findViewById(R.id.tv_scan);
        tv_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent mIntent = new Intent();
//                  ComponentName componentName = new ComponentName("com.android.barcodescanner","com.android.barcodeservice.SerialPortService");
//                 mIntent.setComponent(componentName);

//                Intent  mIntent = new Intent(CheckDetailActivity.this,SerialPortService.class);
                mIntent.setClass(CheckDetailActivity.this, SerialPortService.class);
                CheckDetailActivity.this.startService(mIntent);

                startService(mIntent);
                tv_scan.setEnabled(false);
            }
        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void selectItem(final int position) {
//        readDb();

        Fragment fragment = null;
        if(checkDetailBeanList.size() > 0){
            localPosition = position;
            fragment = new HomeFragment(checkDetailBeanList, chechID);
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    private void initScanner(){
//        i=0;
        scanner = new ScannerInterface(this);
        scanner.open();	//scanner.close()
        scanner.enablePlayBeep(true);
        scanner.enableFailurePlayBeep(false);
        scanner.enablePlayVibrate(true);
        scanner.enableAddKeyValue(1);/**ޡTeble*/
        scanner.timeOutSet(2);
        scanner.intervalSet(1000);
        scanner.lightSet(false);
        scanner.enablePower(true);
        //		scanner.addPrefix("AAA");׺
        //		scanner.addSuffix("BBB");׺
        //		scanner.interceptTrimleft(2);
        //		scanner.interceptTrimright(3);
        //		scanner.filterCharacter("R");
        scanner.SetErrorBroadCast(true);
        //scanner.resultScan();

        //		scanner.lockScanKey();
        scanner.unlockScanKey();
        scanner.setOutputMode(1);

        //android.intent.action.SCANRESULT"
        intentFilter = new IntentFilter(RES_ACTION);
        scanReceiver = new ScannerResultReceiver();
        registerReceiver(scanReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        finishScanner();
//        unregisterReceiver(receiver);
    }

//    private void finishScanner(){
//        scanner.scan_stop();
//        scanner.close();
//        unregisterReceiver(scanReceiver);
//        scanner.continceScan(false);
//    }
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == 280 && event.getRepeatCount() == 1) {
//            Intent mIntent = new Intent();
//            mIntent.setClass(CheckDetailActivity.this, SerialPortService.class);
//            CheckDetailActivity.this.startService(mIntent);
//
//            startService(mIntent);
//            tv_scan.setEnabled(false);
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    private class ScannerResultReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RES_ACTION)){
//                final String scanResult = intent.getStringExtra("value") + "\n";
//                tvScanResult.append("第"+i+"次的扫描结果为："+scanResult);
//                i++;
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * @Description: 读取DB文件线程
     * @author qiaozhili
     * @date 2020/3/24 10:53
     * @param
     * @return
     */
    private void starReadDB() {
        this.prodlg = ProgressDialog.show(this, "提示", "正在读取数据");
        prodlg.setIcon(this.getResources().getDrawable(R.mipmap.scan1));
        LoadDBThread uptataThread = new LoadDBThread(this, handler);
        uptataThread.start();
    }

    // 根据消息更新界面
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String processTitle = (String) bundle.get("UPDATE PORGRESS");
            if (processTitle != null && !processTitle.isEmpty()) {
                String infor = (String) bundle.get("INFORMATION");
                prodlg.setTitle(processTitle); // title
                prodlg.setMessage(infor); // information
                return;
            }

            // 响应读取本地文件或者同步结束
            String readResult = (String) bundle.get("localread");
            if (readResult != null && readResult.equalsIgnoreCase("okupdata"))// 读本地出错
            {
                prodlg.dismiss();
                String flag = (String) bundle.get("flag");
                readDBWarn(flag);
            }
            return;
        }
    };

    private void readDBWarn(final String flag) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setIcon(R.mipmap.scan1).setTitle("数据加载完毕，点击确认刷新页面！");
//        dialog.setMessage("version:" + apkVersion);
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (flag != "") {
                    checkDetailBeanList = ScanApplicaton.getApplication().checkDetailBeanList;
                    selectItem(localPosition);
                } else {
                    Toast.makeText(CheckDetailActivity.this, "数据加载失败，请检查服务端数据！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

//    /**
//     * 广播接收器
//     * */
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // TODO Auto-generated method stub
//            String action = intent.getAction();
//            if(action!=null){
//                if(action.equals(SerialPortService.BARCODEPORT_RECEIVEDDATA_ACTION)){
//                    String data=intent.getStringExtra(SerialPortService.BARCODEPORT_RECEIVEDDATA_EXTRA_DATA);
//                    if(data!=null){
////                        show.append(data);
////                        Log.v(TAG, "receiver data:"+data);
//                    }
//                }
//            }
//        }
//    };


}
