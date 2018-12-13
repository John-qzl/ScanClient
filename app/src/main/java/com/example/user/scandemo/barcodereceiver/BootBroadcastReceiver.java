package com.example.user.scandemo.barcodereceiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.user.scandemo.barcodeservice.SerialPortService;


public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v("BootBroadcastReceiver", intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent screenMonitorServiceIntent=new Intent(context, SerialPortService.class);
			context.startService(screenMonitorServiceIntent);
		}
	}

}
