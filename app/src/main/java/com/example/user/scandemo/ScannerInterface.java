package com.example.user.scandemo;

import android.content.Context;
import android.content.Intent;

/**
 * Created by user on 2018/9/12.
 */

public class ScannerInterface {
    public static final String KEY_BARCODE_ENABLESCANNER_ACTION = "android.intent.action.BARCODESCAN";

    public static final String KEY_BARCODE_STARTSCAN_ACTION = "android.intent.action.BARCODESTARTSCAN";

    public static final String KEY_BARCODE_STOPSCAN_ACTION = "android.intent.action.BARCODESTOPSCAN";


    public static final String KEY_LOCK_SCAN_ACTION = "android.intent.action.BARCODELOCKSCANKEY";

    public static final String KEY_UNLOCK_SCAN_ACTION = "android.intent.action.BARCODEUNLOCKSCANKEY";
    //android.intent.action.BEEP
    public static final String KEY_BEEP_ACTION = "android.intent.action.BEEP";

    public static final String KEY_FAILUREBEEP_ACTION = "android.intent.action.FAILUREBEEP";
    //android.intent.action.VIBRATE
    public static final String KEY_VIBRATE_ACTION = "android.intent.action.VIBRATE";
    public static final String KEY_OUTPUT_ACTION = "android.intent.action.BARCODEOUTPUT";
    public static final String KEY_CHARSET_ACTION = "android.intent.actionCHARSET";
    public static final String KEY_POWER_ACTION = "android.intent.action.POWER";
    public static final String KEY_TERMINATOR_ACTION = "android.intent.TERMINATOR";
    //android.intent.action.SHOWNOTICEICON
    public static final String KEY_SHOWNOTICEICON_ACTION  = "android.intent.action.SHOWNOTICEICON";
    //APP android.intent.action.SHOWAPPICON
    public static final String KEY_SHOWICON_ACTION  = "android.intent.action.SHOWAPPICON";

    public static final String KEY_SHOWISCANUI = "com.android.auto.iscan.show_setting_ui";

    public static final String KEY_PREFIX_ACTION = "android.intent.action.PREFIX";
    public static final String KEY_SUFFIX_ACTION = "android.intent.action.SUFFIX";
    public static final String KEY_TRIMLEFT_ACTION = "android.intent.action.TRIMLEFT";
    public static final String KEY_TRIMRIGHT_ACTION = "android.intent.action.TRIMRIGHT";
    public static final String KEY_LIGHT_ACTION = "android.intent.action.LIGHT";
    public static final String KEY_TIMEOUT_ACTION = "android.intent.action.TIMEOUT";
    public static final String KEY_FILTERCHARACTER_ACTION = "android.intent.action.FILTERCHARACTER";
    public static final String KEY_CONTINUCESCAN_ACTION = "android.intent.action.BARCODECONTINUCESCAN";
    public static final String KEY_INTERVALTIME_ACTION = "android.intent.action.INTERVALTIME";
    public static final String KEY_DELELCTED_ACTION = "android.intent.action.DELELCTED";
    public static final String KEY_RESET_ACTION = "android.intent.action.RESET";
    public static final String SCANKEY_CONFIG_ACTION = "android.intent.action.scankeyConfig";

    public static final String KEY_FAILUREBROADCAST_ACTION = "android.intent.action.FAILUREBROADCAST";

    /****************************************************************************************************/
    static final String  SET_STATUSBAR_EXPAND = "com.android.set.statusbar_expand";
    static final String  SET_USB_DEBUG = "com.android.set.usb_debug";
    static final String  SET_INSTALL_PACKAGE = "com.android.set.install.package";
    static final String  SET_SCREEN_LOCK = "com.android.set.screen_lock";
    static final String  SET_CFG_WAKEUP_ANYKEY = "com.android.set.cfg.wakeup.anykey";
    static final String  SET_UNINSTALL_PACKAGE= "com.android.set.uninstall.package";
    static final String  SET_SYSTEM_TIME="com.android.set.system.time";
    static final String  SET_KEYBOARD_CHANGE = "com.android.disable.keyboard.change";
    static final String SET_INSTALL_PACKAGE_WITH_SILENCE = "com.android.set.install.packege.with.silence";
    static final String SET_INSTALL_PACKAGE_EXTRA_APK_PATH = "com.android.set.install.packege.extra.apk.path";
    static final String SET_INSTALL_PACKAGE_EXTRA_TIPS_FORMAT = "com.android.set.install.packege.extra.tips.format";
    static final String SET_SIMULATION_KEYBOARD = "com.android.simulation.keyboard";
    static final String SET_SIMULATION_KEYBOARD_STRING = "com.android.simulation.keyboard.string";
    /****************************************************************************************************/

    private Context mContext;
    private static ScannerInterface androidjni;

    public ScannerInterface(Context context) {
        mContext = context;

    }

    //	1.
    public void ShowUI(){
        if(mContext != null){
            Intent intent = new Intent(KEY_SHOWISCANUI);
            mContext.sendBroadcast(intent);
        }
    }

    //	2.
    public void open(){
        if(mContext != null){
            Intent intent = new Intent(KEY_BARCODE_ENABLESCANNER_ACTION);
            intent.putExtra(KEY_BARCODE_ENABLESCANNER_ACTION, true);
            mContext.sendBroadcast(intent);
        }
    }

    //2
    public void  close(){
        if(mContext != null){
            Intent intent = new Intent(KEY_BARCODE_ENABLESCANNER_ACTION);
            intent.putExtra(KEY_BARCODE_ENABLESCANNER_ACTION, false);
            mContext.sendBroadcast(intent);
        }

    }

    // 3.
    public void  scan_start(){

        if(mContext != null){
            Intent intent = new Intent(KEY_BARCODE_STARTSCAN_ACTION);
            mContext.sendBroadcast(intent);
        }
    }

    //4.

    public void scan_stop(){
        if(mContext != null){
            Intent intent = new Intent(KEY_BARCODE_STOPSCAN_ACTION);
            mContext.sendBroadcast(intent);
        }
    }

    public void  lockScanKey(){
        if(mContext != null){
            Intent intent = new Intent(KEY_LOCK_SCAN_ACTION);
            mContext.sendBroadcast(intent);
        }
    }

    public void unlockScanKey(){
        if(mContext != null){
            Intent intent = new Intent(KEY_UNLOCK_SCAN_ACTION);
            mContext.sendBroadcast(intent);
        }
    }

    /**KEY_CHARSET_ACTION
     * 0  <item>ASCII</item>
     1  <item>GB2312</item>
     2  <item>GBK</item>
     3  <item>GB18030</item>
     4  <item>UTF-8</item>*/

    public void setCharSetMode(int mode){
        if(mContext != null){
            Intent intent = new Intent(KEY_CHARSET_ACTION);
            intent.putExtra(KEY_CHARSET_ACTION, mode);
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * mode 0:
     * mode 1: actionΪ android.intent.action.SCANRESULT onReceive(Context context, Intent arg1)
     String  barocode=arg1.getStringExtra("value");
     int barocodelen=arg1.getIntExtra("length",0);

     */
    public void setOutputMode(int mode){
        if(mContext != null){
            Intent intent = new Intent(KEY_OUTPUT_ACTION);
            intent.putExtra(KEY_OUTPUT_ACTION, mode);
            mContext.sendBroadcast(intent);
        }
    }

    /**8 */
    public void enablePlayBeep(boolean enable){
        if(mContext != null){
            Intent intent = new Intent(KEY_BEEP_ACTION);
            intent.putExtra(KEY_BEEP_ACTION, enable);
            mContext.sendBroadcast(intent);
        }
    }

    public void enableFailurePlayBeep(boolean enable){
        if(mContext != null){
            Intent intent = new Intent(KEY_FAILUREBEEP_ACTION);
            intent.putExtra(KEY_FAILUREBEEP_ACTION, enable);
            mContext.sendBroadcast(intent);
        }
    }


    /**9 */
    public void enablePlayVibrate(boolean enable){
        if(mContext != null){
            Intent intent = new Intent(KEY_VIBRATE_ACTION);
            intent.putExtra(KEY_VIBRATE_ACTION, enable);
            mContext.sendBroadcast(intent);
        }
    }

    /**KEY_POWER_ACTION
     * true
     * false */
    public void enablePower(boolean enable){
        if(mContext != null){
            Intent intent = new Intent(KEY_POWER_ACTION);
            intent.putExtra(KEY_POWER_ACTION, enable);
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * 0 <item>��</item>
     1 <item>���ӻس���</item>
     2 <item>����TAB��</item>
     3 <item>���ӻ��з�</item>*/
    public void  enableAddKeyValue(int value){
        if(mContext != null){
            Intent intent = new Intent(KEY_TERMINATOR_ACTION);
            intent.putExtra(KEY_TERMINATOR_ACTION, value);
            mContext.sendBroadcast(intent);
        }
    }

    /************************************************************/

    //KEY_PREFIX_ACTION ׺
    public void  addPrefix(String  text){
        if(mContext != null){
            Intent intent = new Intent(KEY_PREFIX_ACTION);
            intent.putExtra(KEY_PREFIX_ACTION, text);
            mContext.sendBroadcast(intent);
        }
    }

    //KEY_SUFFIX_ACTION
    public void  addSuffix(String  text){
        if(mContext != null){
            Intent intent = new Intent(KEY_SUFFIX_ACTION);
            intent.putExtra(KEY_SUFFIX_ACTION, text);
            mContext.sendBroadcast(intent);
        }
    }

    //KEY_TRIMLEFT_ACTION
    public void   interceptTrimleft  (int  num){
        if(mContext != null){
            Intent intent = new Intent(KEY_TRIMLEFT_ACTION);
            intent.putExtra(KEY_TRIMLEFT_ACTION, num);
            mContext.sendBroadcast(intent);
        }
    }

    //KEY_TRIMRIGHT_ACTION
    public void   interceptTrimright  (int  num){
        if(mContext != null){
            Intent intent = new Intent(KEY_TRIMRIGHT_ACTION);
            intent.putExtra(KEY_TRIMRIGHT_ACTION, num);
            mContext.sendBroadcast(intent);
        }
    }

    //KEY_LIGHT_ACTION
    public void   lightSet (boolean enable ){
        if(mContext != null){
            Intent intent = new Intent(KEY_LIGHT_ACTION);
            intent.putExtra(KEY_LIGHT_ACTION, enable);
            mContext.sendBroadcast(intent);
        }
    }

    //KEY_TIMEOUT_ACTION
    public void   timeOutSet(int  value){
        if(mContext != null){
            Intent intent = new Intent(KEY_TIMEOUT_ACTION);
            intent.putExtra(KEY_TIMEOUT_ACTION, value);
            mContext.sendBroadcast(intent);
        }
    }

    //KEY_FILTERCHARACTER_ACTION
    public void   filterCharacter (String text ){
        if(mContext != null){
            Intent intent = new Intent(KEY_FILTERCHARACTER_ACTION);
            intent.putExtra(KEY_FILTERCHARACTER_ACTION, text);
            mContext.sendBroadcast(intent);
        }
    }

    //KEY_CONTINUCESCAN_ACTION
    public void   continceScan (boolean enable ){
        if(mContext != null){
            Intent intent = new Intent(KEY_CONTINUCESCAN_ACTION);
            intent.putExtra(KEY_CONTINUCESCAN_ACTION, enable);
            mContext.sendBroadcast(intent);
        }
    }

    //KEY_INTERVALTIME_ACTION
    public void  intervalSet(int  value){
        if(mContext != null){
            Intent intent = new Intent(KEY_INTERVALTIME_ACTION);
            intent.putExtra(KEY_INTERVALTIME_ACTION, value);
            mContext.sendBroadcast(intent);
        }
    }
    //KEY_FAILUREBROADCAST_ACTION
    public void   SetErrorBroadCast (boolean enable ){
        if(mContext != null){
            Intent intent = new Intent(KEY_FAILUREBROADCAST_ACTION);
            intent.putExtra(KEY_FAILUREBROADCAST_ACTION, enable);
            mContext.sendBroadcast(intent);
        }
    }

    //KEY_RESET_ACTION
    public void resultScan(){
        if(mContext != null){
            Intent intent = new Intent(KEY_RESET_ACTION);
            mContext.sendBroadcast(intent);
        }
    }

    //SCANKEY_CONFIG_ACTION
    //KEYCODE  value
    public void scanKeySet(int  keycode,int value){
        if(mContext != null){
            Intent intent = new Intent(SCANKEY_CONFIG_ACTION);
            intent.putExtra("KEYCODE", keycode);
            intent.putExtra("value", value);
            mContext.sendBroadcast(intent);
        }
    }
}
