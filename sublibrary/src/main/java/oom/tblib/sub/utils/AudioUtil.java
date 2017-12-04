package oom.tblib.sub.utils;

import android.content.Context;
import android.media.AudioManager;

import java.lang.reflect.Method;

/**
 * Created by xlc on 2017/5/24.
 */

public class AudioUtil {

    private static AudioUtil instance = null;

    private AudioManager audioManager = null;

    public static AudioUtil getInstance(Context c) {
        if (instance == null) {
            instance = new AudioUtil(c);
        }
        return instance;
    }

    private AudioUtil(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setSlience() {
        //                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
        try {

            Class cls = audioManager.getClass();
            Method method = cls.getMethod("setStreamVolume", new Class[]{int.class, int.class, int.class});
            method.invoke(audioManager, new Object[]{AudioManager.STREAM_MUSIC, 0, 0});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNomal() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 6, 0);
    }
}
