package com.oom.tblib.clean;

import android.content.Context;

import com.oom.tblib.utils.NotProguard;

import java.util.List;

/**
 * Created by hwl on 2017/08/26.
 */
@NotProguard
public abstract class OnPeocessActionListenerAdapter implements CleanService.OnPeocessActionListener {

    @Override
    public void onScanStarted(Context context) {

    }

    @Override
    public void onScanProgressUpdated(Context context, int current, int max) {

    }

    @Override
    public void onScanCompleted(Context context, List<CleanComar> apps) {

    }

    @Override
    public void onCleanStarted(Context context) {

    }

    @Override
    public void onCleanCompleted(Context context, long cacheSize) {

    }
}