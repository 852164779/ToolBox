package com.newfresh.easyclean.light;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.newfresh.easyclean.R;
import com.newfresh.easyclean.util.CameraUtil;
import com.newfresh.easyclean.util.IFlashControl;

import java.io.IOException;

public class FlashActivity extends Activity implements SurfaceHolder.Callback, IFlashControl {

    private ImageView lightTurnOff;
    private ImageView lightTurnOn;
    private ImageView lightTurnBtn;
    private ImageView sosBtn;
    private boolean light = false;
    private boolean sos_light = false;
    private SosThread mSosThread;
    private Camera camera;
    private SurfaceHolder mholder;
    private SurfaceView surfaceView;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flash);
        lightTurnBtn = (ImageView) findViewById(R.id.light_turn_btn);
        lightTurnOff = (ImageView) findViewById(R.id.light_turn_off);
        lightTurnOn = (ImageView) findViewById(R.id.light_turn_on);
        sosBtn = (ImageView) findViewById(R.id.sos_btn);
        surfaceView = (SurfaceView) findViewById(R.id.main_surfaceView);
        mholder = surfaceView.getHolder();
        mholder.addCallback(this);//添加回调
        mholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview不维护
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK ) {
            CameraUtil.release();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClick (View view) {
        switch ( view.getId() ) {
            case R.id.light_turn_off:
                break;
            case R.id.light_turn_on:
                break;
            case R.id.light_turn_btn:
                if ( camera == null || !CameraUtil.check_exist_flash(this) ) {
                    startActivity(new Intent(FlashActivity.this, LightActivity.class));
                    return;
                }
                if ( !light ) {
                    lightTurnBtn.setImageResource(R.drawable.turn_on);
                    lightTurnOn.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run () {
                            openFlash();
                        }
                    }).start();
                    light = true;
                } else {
                    cancel();
                    light = false;
                }
                break;
            case R.id.sos_btn:
                if ( camera == null || !CameraUtil.check_exist_flash(this) ) {
                    startActivity(new Intent(FlashActivity.this, LightActivity.class));
                    return;
                }
                if ( !sos_light ) {
                    selectSos(true);
                    sos_light = true;
                    light = true;
                    lightTurnOn.setVisibility(View.VISIBLE);
                    lightTurnBtn.setImageResource(R.drawable.turn_on);
                    sosBtn.setImageResource(R.drawable.sos_on);

                } else {
                    sosBtn.setImageResource(R.drawable.sos_off);
                    selectSos(false);
                    sos_light = false;
                }
                break;
            case R.id.jinggao:
                startActivity(new Intent(FlashActivity.this, WarnActivity.class));
                break;
            case R.id.caideng:
                startActivity(new Intent(FlashActivity.this, LightActivity.class));
                break;
            case R.id.qiujiu:
                startActivity(new Intent(FlashActivity.this, ScreenActivity.class));
                break;
        }
    }

    private void cancel () {
        if ( mSosThread != null ) {
            mSosThread.stopThread();
        }
        new Thread(new Runnable() {
            @Override
            public void run () {
                closeFlash();
            }
        }).start();

        sos_light = false;
        light = false;
        lightTurnBtn.setImageResource(R.drawable.turn_off);
        lightTurnOn.setVisibility(View.GONE);
        sosBtn.setImageResource(R.drawable.sos_off);
    }

    @Override
    public void closeFlash () {
        CameraUtil.control(false);
    }

    @Override
    public void openFlash () {
        CameraUtil.control(true);
    }

    private void selectSos (boolean selected) {
        if ( selected ) {
            if ( mSosThread != null ) {
                mSosThread.stopThread();
            }
            closeFlash();
            mSosThread = new SosThread(this);
            mSosThread.start();
        } else {
            if ( mSosThread != null ) {
                mSosThread.stopThread();
                mSosThread = null;
            }
            openFlash();
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        CameraUtil.release();
    }

    @Override
    public void surfaceCreated (SurfaceHolder holder) {
        camera = CameraUtil.getCamera(0);
        if ( camera == null ) {
            Toast.makeText(this, getResources().getString(R.string.no_camera), Toast.LENGTH_LONG).show();
            return;
        }
        try {
            camera.setPreviewDisplay(holder);
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed (SurfaceHolder holder) {
        if ( CameraUtil.release() ) {
            cancel();
        }
    }

    class SosThread extends Thread {

        public void stopThread () {
            running = false;
        }

        private boolean running = true;

        private IFlashControl mFlashControl;

        public SosThread (IFlashControl flashControl) {
            super(SosThread.class.getSimpleName());
            this.setDaemon(true);
            mFlashControl = flashControl;
        }

        @Override
        public void run () {
            super.run();
            int time = 0;
            try {
                // 灭1300、亮200、灭200、亮200、灭200、亮200、灭500、亮400、灭200、亮400、灭200、亮400、灭500、亮200、灭200、亮200、灭200、亮200、 （MS）循环
                while ( running ) {
                    time = time % 18;
                    if ( (time % 2) == 0 ) {
                        if ( mFlashControl != null ) mFlashControl.closeFlash();
                    } else {
                        if ( mFlashControl != null ) mFlashControl.openFlash();
                    }
                    if ( time == 0 ) {
                        sleep(1300);
                    } else if ( time == 6 || time == 12 ) {
                        sleep(500);
                    } else if ( time == 7 || time == 9 || time == 11 ) {
                        sleep(400);
                    } else {
                        sleep(200);
                    }
                    time++;
                }
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
}