package com.xxm.toolbox.clean;

import android.content.Context;

import java.util.List;

/**
 * Created by hwl on 2017/08/26.
 */
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