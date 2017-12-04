package com.xxm.sublibrary.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by xlc on 2017/5/24.
 */

public class Ua {

    private static Ua instance=null;

    private AudioManager audioManager=null;

    public static Ua getInstance(Context c)
    {
        if(instance==null)
            instance=new Ua(c);
        return instance;
    }
    private Ua(Context context)
    {
        audioManager= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }
    public void setSlience()
    {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }
    public void setNomal()
    {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 6, 0);
    }
}
