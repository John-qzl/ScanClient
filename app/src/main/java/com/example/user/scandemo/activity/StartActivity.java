package com.example.user.scandemo.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.user.scandemo.Base.BaseActivity;
import com.example.user.scandemo.R;
import com.example.user.scandemo.barcodeservice.SerialPortService;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.user.scandemo.Base.App.getApp;
import static com.example.user.scandemo.db.SQLdb.filePath;
import static com.example.user.scandemo.db.SQLdb.filePath1;

/**
 * Created by user on 2018/10/20.
 */

public class StartActivity extends BaseActivity {
//    private String filePath = Environment.getExternalStorageDirectory() + File.separator + "ScanClient" + File.separator;
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        getContext().unregisterReceiver(receiver);
    }

    private Button mSatrt;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    protected void init() {
        mSatrt = (Button) findViewById(R.id.start);
        mSatrt.setOnClickListener(this);
        checkPermission();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(SerialPortService.BARCODEPORT_RECEIVEDDATA_ACTION);
//        registerReceiver(receiver, intentFilter);
//        Intent  mIntent = new Intent(this,SerialPortService.class);
//        StartActivity.this.startService(mIntent);
//        Intent intentOne = new Intent(this, SerialPortService.class);
//        startService(intentOne);


//
//        startService(mIntent);
    }

    @Override
    protected void viewClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                File jhPath1 = new File(filePath1);
                File jhPath = new File(filePath);
                //查看数据库文件是否存在
                if(jhPath1.exists()){
                    Intent intent = new Intent();
                    intent.setClass(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else if (!jhPath.exists()) {
                    jhPath.mkdirs();
                    showToast("数据库不存在，请检查数据库！");
                    scanFileAsync(getContext(), jhPath.getPath());
                } else {
                    showToast("数据库不存在，请检查数据库1！");
                }
                break;
            default:
        }
    }

    //刷新系统文件
    public static void scanFileAsync(Context ctx, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        ctx.sendBroadcast(scanIntent);
    }


    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    /**
     * @Description:检查获取设备相关权限
     * @author qiaozhili
     * @date 2018/10/18 8:30
     * @param
     * @return
     */
    public void checkPermission() {

        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionStrs = new ArrayList<>();
            int hasWriteSdcardPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteSdcardPermission != PackageManager.PERMISSION_GRANTED) {
                permissionStrs.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            int hasCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissionStrs.add(Manifest.permission.CAMERA);
            }
            int hasBluetoothPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
            if (hasBluetoothPermission != PackageManager.PERMISSION_GRANTED) {
                permissionStrs.add(Manifest.permission.BLUETOOTH);
            }
            String[] stringArray = permissionStrs.toArray(new String[0]);
            if (permissionStrs.size() > 0) {
                requestPermissions(stringArray, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
//        if (Build.VERSION.SDK_INT >= 23) {
//            int REQUEST_CODE_CONTACT = 101;
//            String[] permissions = {Manifest.permission.CAMERA,
//                    Manifest.permission.RECEIVE_BOOT_COMPLETED,
//                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.BLUETOOTH,
//                    Manifest.permission.VIBRATE,
//                    Manifest.permission.BLUETOOTH_ADMIN,
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.CHANGE_NETWORK_STATE};
//            for (String str : permissions) {
//                if (ActivityCompat.checkSelfPermission(getApplicationContext(), str) != PackageManager.PERMISSION_GRANTED) {
//                    //申请权限
//                    ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_CONTACT);
//                    return;
//                }
//            }
//        }
    }
    /**
     * 广播接收器
     * */
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // TODO Auto-generated method stub
//            String action = intent.getAction();
//            if(action!=null){
//                if(action.equals(SerialPortService.BARCODEPORT_RECEIVEDDATA_ACTION)){
//                    String data=intent.getStringExtra(SerialPortService.BARCODEPORT_RECEIVEDDATA_EXTRA_DATA);
//                    if(data!=null){
//                        showToast(data);
//                        Log.v(TAG, "receiver data:"+data);
//                    }
//                }
//            }
//        }
//    };
}
