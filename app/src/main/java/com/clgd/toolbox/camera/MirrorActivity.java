package com.clgd.toolbox.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;

import com.clgd.toolbox.R;
import com.clgd.toolbox.util.CameraUtil;

import java.io.IOException;

/**
 * Created by xlc on 2016/12/1.
 */
public class MirrorActivity extends Activity implements SurfaceHolder.Callback {

    private SurfaceHolder mholder;
    private SurfaceView surfaceView;
   // private SeekBar mirror_seek;
    private Camera camera;
    private ImageView mirror_background;

    private int current_index = 0;

    private static final int[] mis = {R.drawable.mirror_1, R.drawable.mirror_2, R.drawable.mirror_3, R.drawable.mirror_4};

    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mirror_activity);

        surfaceView = (SurfaceView) findViewById(R.id.mirror_view);

        mirror_background = (ImageView) findViewById(R.id.mirror_background);

        mirror_background.setImageResource(mis[0]);

        mholder = surfaceView.getHolder();

        mholder.addCallback(this);//添加回调

        mholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview不维护
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera == null) {

                camera = CameraUtil.getCamera(1);

                if (camera == null) {

                    Toast.makeText(this, getResources().getString(R.string.face_camera_error), Toast.LENGTH_LONG).show();

                    return;
                }
            }
            camera.setPreviewDisplay(mholder);//通过surfaceview显示取景画面

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (camera == null) return;
               camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    //initCamera();//实现相机的参数初始化
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

    public boolean onTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            x2 = event.getX();
            y2 = event.getY();
            if (x1 - x2 > 50) {

                right_Mirror_background();

            } else if (x2 - x1 > 50) {

                left_Mirror_background();
            }
        }
        return super.onTouchEvent(event);
    }


    private void right_Mirror_background() {
        current_index = current_index + 1;
        if (current_index > mis.length-1) {
            current_index = 0;
        }
        mirror_background.setImageResource(mis[current_index]);
    }

    private void left_Mirror_background() {

        current_index = current_index - 1;
        if (current_index < 0) {
            current_index = mis.length-1;
        }
        mirror_background.setImageResource(mis[current_index]);
    }

}
