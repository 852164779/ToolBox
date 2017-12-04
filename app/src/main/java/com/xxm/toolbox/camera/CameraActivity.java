package com.xxm.toolbox.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.xxm.toolbox.R;
import com.xxm.toolbox.util.CameraUtil;

import java.io.IOException;

public class CameraActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener {

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private SeekBar camera_seek;
    private ImageView zoom_light_btn;

    private boolean zoom_light_open = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera_activity);

        surfaceView = (SurfaceView) findViewById(R.id.camera_view);

        zoom_light_btn = (ImageView) findViewById(R.id.zoom_light_btn);

        zoom_light_btn.setOnClickListener(this);

        /***判断是否有闪光灯****/
        if (!CameraUtil.check_exist_flash(getApplicationContext())) {
            zoom_light_btn.setImageResource(R.drawable.zoom_no_light);
            zoom_light_btn.setEnabled(false);
        } else {
            zoom_light_btn.setBackgroundResource(R.drawable.zoom_light_close);
        }

        holder = surfaceView.getHolder();

        holder.addCallback(this);//添加回调

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview不维护

        camera_seek = (SeekBar) findViewById(R.id.camera_seek);

        //文本、进度条显示

        camera_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                CameraUtil.setZoom(progress);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        try {
            camera = CameraUtil.getCamera(0);

            if (camera == null) {

                Toast.makeText(this, getResources().getString(R.string.no_camera), Toast.LENGTH_LONG).show();

                return;
            }
            camera_seek.setMax(CameraUtil.getMaxZoom());

            camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera == null) {
            return;
        }
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    //camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
                }
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera = null;
        holder = null;
        surfaceView = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraUtil.release();
    }

    @Override
    public void onClick(View v) {
        if (zoom_light_open) {
            zoom_light_btn.setBackgroundResource(R.drawable.zoom_light_close);
            zoom_light_open = false;
            CameraUtil.closeFlash();
        } else {
            zoom_light_btn.setBackgroundResource(R.drawable.zoom_light_open);
            zoom_light_open = true;
            CameraUtil.openFlash();
        }

    }
}