package com.example.user.scandemo.barcodeservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;


import com.example.user.scandemo.Base.App;
import com.example.user.scandemo.R;
import com.example.user.scandemo.db.DatabaseHelper;
import com.example.user.scandemo.utils.FileNode;
import com.example.user.scandemo.utils.SoundPlayUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;


public class SerialPortService extends Service {
	
	private final String TAG = "SerialPortService";
	
	public static String BARCODEPORT_RECEIVEDDATA_ACTION = "com.android.serial.BARCODEPORT_RECEIVEDDATA_ACTION";
	public static String BARCODEPORT_RECEIVEDDATA_EXTRA_DATA = "DATA";
	
	private SerialPortManager serialPortManager;

	private SQLiteDatabase bardb;
	private DatabaseHelper databaseHelper;
	
	private String prefix,suffix;
	private boolean suffix_table;
	/**
	 * /sys/class/EM_BT_CONTROL/input_buf
	 * */
	private String inputPath="/sys/class/EM_BT_CONTROL/input_buf";
	
	private String inputPathEnter="/sys/class/EM_BT_CONTROL/input_buf_enter";
	
	private boolean enter = true;
	 
	/**
	 * barcode串口写入数据
	 * */
	public static String BARCODEPORT_WRITEDATA_ACTION="com.android.portservice.BARCODEPORT_WRITEDATA_ACTION";
	/**
	 * barcode串口写入数据的参数DATA
	 * */
	public static String BARCODEPORT_WRITEDATA_EXTRA_DATA = "DATA";
	
	static public final String ACTION_KEYEVENT_KEYCODE_SCAN_L_DOWN = "com.android.action.keyevent.KEYCODE_KEYCODE_SCAN_L_DOWN";
	static public final String ACTION_KEYEVENT_KEYCODE_SCAN_L_UP = "com.android.action.keyevent.KEYCODE_KEYCODE_SCAN_L_UP";
	static public final String ACTION_KEYEVENT_KEYCODE_SCAN_R_DOWN = "com.android.action.keyevent.KEYCODE_KEYCODE_SCAN_R_DOWN";
	static public final String ACTION_KEYEVENT_KEYCODE_SCAN_R_UP = "com.android.action.keyevent.KEYCODE_KEYCODE_SCAN_R_UP";
	
	 /**
     * 向input文件节点写入数据
     * */
    private WriteDataAsyncTask writeDataAsyncTask;
    
    /*
     * 向串口发送指令
     * */
    private WriteDataToPortAsyncTask writeDataToPortAsyncTask;
    
    private int  mCurBaudrate = -1;
    private boolean isDataValid = true;
	

	@Override
	public void onCreate() {
		super.onCreate();
		SoundPlayUtil.init(this);
		databaseHelper = new DatabaseHelper(SerialPortService.this, getFilesDir()+"/barcode.db3", null, 1);
		
		Log.d(TAG, "path:" + getFilesDir());
		
  		SharedPreferences mSP = getSharedPreferences(App.SYMBOL_CONFIG_SIG, 0);
	    boolean isSig    =  mSP.getBoolean(App.SCAN_MODE_SIG,false);
	    boolean isFisrt  =  mSP.getBoolean(App.FIRST_INSTALL,true);
	    boolean isInited =  mSP.getBoolean(App.MODUL_INIITED,false);
	    
	    Log.e(TAG, " do  onCreate  isSig = "    + isSig);
	    Log.e(TAG, " do  onCreate  isInited = " + isInited);
	    
	    if(!isInited){
//	    	openUart(115200);
//	    	serialPortManager.write("Rev?."); 
//	    	mHandler.sendEmptyMessageDelayed(OPEN_WITH_9600, 500);
	    	
	    	openUart(9600);
	    	serialPortManager.write("Rev?."); 
	    	mHandler.sendEmptyMessageDelayed(OPEN_WITH_115200, 500);
	    	
	    }else{
		    openUart(isSig ? 9600 : 115200);
	    }

		serialPortManager.read();
		serialPortManager.setListener(mSerialPortListener);
		
		register();
		
		App mApp = (App) getApplication();
	    if(isFisrt){
    	    SharedPreferences.Editor mEditor = mSP.edit();
    	    mEditor.putBoolean(App.FIRST_INSTALL, false);
    	    mEditor.commit();
    	    mApp.LoadAllFactorySettings();
	    }
	    
		SharedPreferences mSharedPreferences = getSharedPreferences("com.android.barcodescanner_preferences", Context.MODE_PRIVATE);
		boolean blueholdEnable = mSharedPreferences.getBoolean("bluehold", false);
		if(blueholdEnable){
		   doBluetoothConnect();
		}

	}

	private static final int OPEN_WITH_115200 = 1;
	private static final int OPEN_WITH_9600   = 2;
	private static final int INIT_SUCCESS     = 3;
	
	Handler  mHandler  = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			
			case OPEN_WITH_115200:
		    	openUart(115200);
		    	serialPortManager.write("Rev?."); 
				break;
			
			case OPEN_WITH_9600:
		    	openUart(9600);
		    	serialPortManager.write("Rev?."); 
				break;
				
			case INIT_SUCCESS:
				Log.e(TAG, "INIT_SUCCESS: mCurBaudrate = " + mCurBaudrate);
				
		  		SharedPreferences mSP = getSharedPreferences(App.SYMBOL_CONFIG_SIG, 0);
		  		SharedPreferences.Editor  mEditor = mSP.edit();
		  		mEditor.putBoolean(App.MODUL_INIITED, true);
		  		
				if(mCurBaudrate == 115200){
					mEditor.putBoolean(App.SCAN_MODE_SIG, false);
				}else if(mCurBaudrate == 9600){
					mEditor.putBoolean(App.SCAN_MODE_SIG, true);
				}
				
				mEditor.commit();
				break;	
			}
		};
	};
	
	
	public void openUart(int  mbaudrate){
		    mCurBaudrate  = mbaudrate;
			serialPortManager = SerialPortManager.getInstance();
			serialPortManager.open(mbaudrate);
	}

	private byte[] InValidAnswer1 = new byte[]{ (byte)0xef,(byte) 0xbf, (byte) 0xbd };
	private byte[] InValidAnswer2 = new byte[]{ (byte)0xef,(byte) 0xbf, (byte) 0xbd, 0x00};
	private byte[] InValidAnswer3 = new byte[]{ (byte) 0x40, (byte) 0xef,(byte) 0xbf,(byte)0xbd, (byte)0x1f};

	private byte[] InValidAnswer4 = new byte[]{ (byte) 0xef,(byte) 0xbf,(byte) 0xbd};

	boolean isInvliadChars(byte[] mReceiveData, byte[] mAnswer){
		
		if(mReceiveData.length == mAnswer.length){
			for(int i = 0;  i <mAnswer.length;  i ++ ){
				if(mReceiveData[i] != mAnswer[i]){
					Log.e("isInvliadChars", "false: i = " + i);
					return false;
				}
			}
			Log.e("isInvliadChars", "true ");
			return true;
		}else{
			
			return false;
		}

	}
	
	
	private boolean isSameEnd(byte[] mReceiveData, byte[] mAnswer){
		
		for(int i = 0 ; i < mAnswer.length;  i ++){
			if(mAnswer[mAnswer.length - 1 - i] != mReceiveData[mReceiveData.length -1 - i]){
				return false;
			}
		}
		
		return true;
	}
	
	
	
	SerialPortManager.SerialPortListener mSerialPortListener = new SerialPortManager.SerialPortListener() {

				@SuppressLint("NewApi")
				@Override
				public void onResult(String data) {
					
					String temp = byte2hex(data.getBytes());
					
					// ef bf bd
					Log.d(TAG, "temp1:"+temp);
					
					Log.d(TAG, "data:"+data);
					
					if(App.onSettings){
						return;
					}
					if(data == null||data.isEmpty() || temp.equals(" 00")){
						Log.d(TAG, "temp2:"+temp);
						return ;
					}

					if(isInvliadChars(data.getBytes(), InValidAnswer1)){
						Log.e(TAG, "isInvliadChars1");
						return ;
					}
					if(isInvliadChars(data.getBytes(), InValidAnswer2)){
						Log.e(TAG, "isInvliadChars2");
						return ;
					}
					
					if(isSameEnd(data.getBytes(), InValidAnswer3)){
						Log.e(TAG, "isInvliadChars3");
						return;
					}
					
					if(isSameEnd(data.getBytes(), InValidAnswer4)){
						Log.e(TAG, "isInvliadChars4");
						return;
					}
					
					if(data.startsWith("SUFCA2") && data.length() == 8){
						return;
					}
					
					if(data.contains("ProjectRevision")){
						return;
					}
					
					if(!isDataValid || data.length() <= 3 ){
						return;
					}
					
					if(data.startsWith("REV")){
						Log.e(TAG, "removeMessages");
						mHandler.removeMessages(OPEN_WITH_9600);
						mHandler.removeMessages(OPEN_WITH_115200);
						mHandler.sendEmptyMessage(INIT_SUCCESS);
					  return;	
					}
					
					SharedPreferences mSharedPreferences = getSharedPreferences("com.android.barcodescanner_preferences", Context.MODE_PRIVATE);
					boolean hid = mSharedPreferences.getBoolean("hid", true);
					boolean sound = mSharedPreferences.getBoolean("sound", true);
					boolean vibrate = mSharedPreferences.getBoolean("vibrate", false);
					prefix = mSharedPreferences.getString("prefix", null);
					suffix = mSharedPreferences.getString("suffix", null);
					suffix_table = mSharedPreferences.getBoolean("suffix_table", false);
					enter = mSharedPreferences.getBoolean("enter", true);
					//Log.d("myHID", "hid:"+hid);
					Log.d("myHID", "sound:"+sound);
					
					if(sound){
						SoundPlayUtil.play();
					}
					
					if(vibrate){
						Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
						long []  pattern  = {100,400};   // 停止 开启 停止 开启   
						vibrator.vibrate(pattern,-1);
					}
					
					if( data.startsWith("DEFALT") && data.length() == 8 ){
						App mApp = (App)getApplication();
						mApp.LoadAllFactorySettings();
						if( mCurBaudrate == 9600){
							serialPortManager.write("sufca2."); //sufca2.
						}
						return;
					}
					
                    if(data.startsWith("aosmpt") || data.startsWith("AOSMPT")  || data.startsWith("hstcdt") || data.startsWith("HSTCDT")){
                    	 return;
                    }
					
				/*	if(data!=null && hid){
						synchronized (this) {
		                writeDataAsyncTask=new WriteDataAsyncTask();
		                writeDataAsyncTask.execute(data);
						}
		                Log.v("HID", "write data:"+data);
		            }
		        */ 
					if(bardb == null || !bardb.isOpen()){
						bardb = databaseHelper.getWritableDatabase();
					}
					
					databaseHelper.insert(bardb,data);	
					
					String edit_data = data;
					
					//设置前缀
					Log.v(TAG, "suffix_table:"+suffix_table);
					if(prefix!=null){
						edit_data = prefix+edit_data;
					}
					
					//设置后缀
					if(suffix_table){
						if(suffix!=null){
							suffix = suffix+"\t";
						}else{
							suffix = "\t";
						}
					}
					
					if(suffix!=null){
						edit_data = edit_data+suffix;
					}
					
					if(edit_data!=null && hid){
						synchronized (this) {
		                writeDataAsyncTask=new WriteDataAsyncTask();
		            	writeDataAsyncTask.execute(edit_data);
		
						}
		                Log.v("HID", "write edit_data:"+edit_data);
		            }
					
					if(edit_data!=null){
						Intent intent=new Intent(BARCODEPORT_RECEIVEDDATA_ACTION);
						if(enter){
							intent.putExtra(BARCODEPORT_RECEIVEDDATA_EXTRA_DATA, edit_data+"\r\n");
						}else{
							intent.putExtra(BARCODEPORT_RECEIVEDDATA_EXTRA_DATA, edit_data);
						}
						getApplicationContext().sendBroadcast(intent);
					}
				}
	};
	
	/*public static Cursor query(){
		Cursor cursor = bardb.rawQuery("select * from barcode;", null);
		return cursor;
	}*/
	
	@Override
	public IBinder onBind(Intent intent) {
		return new MyBinder();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SoundPlayUtil.release();
		unregisterReceiver(receiver);
		serialPortManager.close();
		if(bardb!=null&&bardb.isOpen()){
			bardb.close();
		}
	}

	/**
     * 向dev/uinput文件节点写入数据
     * */
    private class WriteDataAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            int res=0;
            // TODO Auto-generated method stub
            String data=params!=null&&params.length>0?params[0]:null;
            Log.v("HID","hid write data:"+ data);

            if(data!=null){
            	if(enter){
            		FileNode.write(inputPathEnter,data);
            	}else{
            		FileNode.write(inputPath,data);
            	}
            	
            	//SoundPlayUtil.play(1);
            }
            return res;
        }
    }
    
    
    /**
	 * Broadcast receiver.
	 * */
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
	  		SharedPreferences mSP = getSharedPreferences(App.SYMBOL_CONFIG_SIG, 0);
	  		boolean isLefeEnable  = mSP.getBoolean(App.SCAN_MODE_LEFT, true);
	  		boolean isRightEnable  = mSP.getBoolean(App.SCAN_MODE_RIGHT, true);
	  		
			String action = intent.getAction();
			Log.d(TAG, "action : "+action);
			if(action!=null){
				if(action.equals(BARCODEPORT_WRITEDATA_ACTION)){
					
					String dataHex=intent.getStringExtra(BARCODEPORT_WRITEDATA_EXTRA_DATA);
					if(dataHex!=null){
						writeDataToPortAsyncTask = new WriteDataToPortAsyncTask();
						writeDataToPortAsyncTask.execute(dataHex);
					}
					
				}if(action.equals(ACTION_KEYEVENT_KEYCODE_SCAN_L_DOWN) && isLefeEnable){
					   Log.d(TAG, "receiver : KEYCODE  LEFT ");
					    keySwitchToTigger(TRIGGER_VALUE);					    
				}else if(action.endsWith(ACTION_KEYEVENT_KEYCODE_SCAN_L_UP) && isLefeEnable){
					 Log.d(TAG, "receiver : KEYCODE  LEFT UP ");
					    keySwitchToTigger(TRIGGER_CANCEL_VALUE);
				}else if(action.endsWith(ACTION_KEYEVENT_KEYCODE_SCAN_R_DOWN) && isRightEnable){
					 Log.d(TAG, "receiver : KEYCODE   RIGHT ");
					    keySwitchToTigger(TRIGGER_VALUE);
				}else if(action.endsWith(ACTION_KEYEVENT_KEYCODE_SCAN_R_UP) && isRightEnable){
					 Log.d(TAG, "receiver : KEYCODE   RIGHT UP");
					    keySwitchToTigger(TRIGGER_CANCEL_VALUE);
				}else if(Intent.ACTION_SCREEN_ON.equals(action)){
					  isDataValid  = true;
					 Log.d(TAG, "receiver : ACTION_SCREEN_ON");
					 
				}else if(Intent.ACTION_SCREEN_OFF.equals(action)){
					 isDataValid = false;
					 Log.d(TAG, "receiver : ACTION_SCREEN_OFF ");
				}
			}
		}
	};
	
	/**
	 * Write data task
	 * */
	private class WriteDataToPortAsyncTask extends AsyncTask<String, Integer, Integer>{

		@Override
		protected Integer doInBackground(String... params) {
			int res=0;
			String data=params!=null&&params.length>0?params[0]:null;
			isDataValid = true;
			res = serialPortManager.write(data);
			return res;
		}
	}
	final int TRIGGER_VALUE = 1;
	final int TRIGGER_CANCEL_VALUE = 0;
	private void keySwitchToTigger(int value){
		byte[] trigger;
			if(value == TRIGGER_VALUE){
				trigger = new byte[] { 0x31 };		//触发
			}else{	
				trigger = new byte[] { 0x30 };		//取消
			}
		    File file = new File("/sys/devices/soc/10003000.keypad/scan_leveltrigger_enable");
		    try {
		      FileOutputStream fos = new FileOutputStream(file);
		      fos.write(trigger);
		      fos.flush();
		      fos.close();
		    } catch (FileNotFoundException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
	}
	
	private void register(){
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(BARCODEPORT_WRITEDATA_ACTION);
			intentFilter.addAction(ACTION_KEYEVENT_KEYCODE_SCAN_L_DOWN);
			intentFilter.addAction(ACTION_KEYEVENT_KEYCODE_SCAN_L_UP);
			intentFilter.addAction(ACTION_KEYEVENT_KEYCODE_SCAN_R_DOWN);
			intentFilter.addAction(ACTION_KEYEVENT_KEYCODE_SCAN_R_UP);
			intentFilter.addAction(Intent.ACTION_SCREEN_ON);
			intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
			registerReceiver(receiver, intentFilter);
	}
	
	private String byte2hex(byte [] buffer){  
        String h = "";  
          
        for(int i = 0; i < buffer.length; i++){  
            String temp = Integer.toHexString(buffer[i] & 0xFF);  
            if(temp.length() == 1){  
                temp = "0" + temp;  
            }  
            h = h + " "+ temp; 
        }  
          
        return h;  
    }
	
   public class MyBinder extends Binder{
		
		   public void openUartQR() {
				openUart(115200);
			}
			
		   public void openUartSig() {
				openUart(9600);
			}
   }
	

	private void doBluetoothConnect() {
		  Log.e("doBluetoothConnect","  1234 ");
		  
		  BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();  
		
          if(adapter != null){  
           
              if(!adapter.isEnabled()){   //如果蓝牙设备未开启  
                  Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
                  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  startActivity(intent);  
              }  
              
              Set<BluetoothDevice> devices = adapter.getBondedDevices();  //获得已配对的远程蓝牙设备的集合  
              if(devices.size()>0){  
                  for(Iterator<BluetoothDevice> it = devices.iterator();it.hasNext();){  
                	  
                      BluetoothDevice device = (BluetoothDevice)it.next();  
                      if(device.getName().equals("BLE_Handle")){
                    	    ParcelUuid  uuid[]  =  device.getUuids();

	                	    UUID mUuid = UUID.fromString(uuid[0].toString());    
	                  	    BluetoothSocket socket;
	                  	     
							try {
								socket = device.createRfcommSocketToServiceRecord(mUuid);
		                  	    socket.connect();   
		                  	    
							} catch (IOException e) {
								e.printStackTrace();
							}   
							
							break;
                      }
                  }  
              }else{
                  Toast.makeText(getApplicationContext(), R.string.str_bluetooth_prompt, Toast.LENGTH_LONG).show();
              }
          }
	}
	

}
