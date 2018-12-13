package com.example.user.scandemo.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.example.user.scandemo.R;


public class SoundPlayUtil {

    @SuppressWarnings("deprecation")
	public static SoundPool mSoundPlayer = new SoundPool(10,AudioManager.STREAM_SYSTEM, 5);
    public static SoundPlayUtil soundPlayUtil;
    public static Context mContext;
    public static int soundID;

    public static SoundPlayUtil init(Context context) {
        if (soundPlayUtil == null) {
        	soundPlayUtil = new SoundPlayUtil();
        }
        mContext = context;
        soundID = mSoundPlayer.load(mContext, R.raw.beep, 1);
        Log.d("soundID", "SoundID:"+soundID);
        return soundPlayUtil;
    }

    public static void play() {
        int ID = mSoundPlayer.play(soundID, 1, 1, 0, 0, 1);
        Log.d("soundID", "ID:"+ID);
    }
    
    public static void release(){
    	mSoundPlayer.unload(soundID);
    }
}