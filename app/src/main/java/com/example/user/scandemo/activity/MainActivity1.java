package com.example.user.scandemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.user.scandemo.R;
import com.example.user.scandemo.ScannerInterface;

/**
 * Created by qiaozhili on 2018/9/26 22:57.
 */

public class MainActivity1 extends AppCompatActivity {
    int i;

    TextView tvScanResult;
    ScannerInterface scanner;
    IntentFilter intentFilter;
    BroadcastReceiver scanReceiver;

    private boolean isContinue = false;

    private static final String RES_ACTION = "android.intent.action.SCANRESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main1);
        tvScanResult = (TextView) this.findViewById(R.id.tv_scan_result);
        initScanner();
    }
    private void initScanner(){
        i=0;
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
        finishScanner();
    }

    private void finishScanner(){
        scanner.scan_stop();
        scanner.close();
        unregisterReceiver(scanReceiver);
        scanner.continceScan(false);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 139&&event.getRepeatCount()==0){
            scanner.scan_start();
        }
        return super.onKeyDown(keyCode, event);
    }
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 139){
            scanner.scan_stop();
        }else if (keyCode == 140){
            scanner.scan_stop();

            isContinue=!isContinue;
            if(isContinue){
                scanner.continceScan(true);
            }else{
                scanner.continceScan(false);
            }
        }
        return super.onKeyUp(keyCode, event);
    }


    public void singleScan(View v){
        scanner.scan_start();
    }

    public void continueScan(View v){
        isContinue=!isContinue;
        if(isContinue){
            scanner.continceScan(true);
        }else{
            scanner.continceScan(false);
        }
    }

    public void scanSet(View v){
        scanner.scanKeySet(139,0);
        scanner.scanKeySet(140,1);
        scanner.scanKeySet(141,1);
    }

    private class ScannerResultReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RES_ACTION)){
                final String scanResult = intent.getStringExtra("value") + "\n";
                tvScanResult.append("第"+i+"次的扫描结果为："+scanResult);
                i++;
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
