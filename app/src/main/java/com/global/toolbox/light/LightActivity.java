package com.global.toolbox.light;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.global.toolbox.R;

import java.util.HashMap;

/**
 * Created by xlc on 2016/11/24.
 */
public class LightActivity extends Activity implements View.OnClickListener {

    private final int[] mImgIds = {
            R.id.te_img_right,
            R.id.te_img_right_top, //te_img_left_top
            R.id.te_img_top,
            R.id.te_img_left_top,
            R.id.te_img_left,
            R.id.te_img_left_bottom,
            R.id.te_img_center};

    private RelativeLayout layout;

    private int curent_index = 0;

    private boolean flash=false;

    public static HashMap<Integer, Integer> hashMap = new HashMap<>();

    static {
        hashMap.put(R.id.te_img_right, R.color.te_img_right);
        hashMap.put(R.id.te_img_right_top, R.color.te_img_right_top);
        hashMap.put(R.id.te_img_top, R.color.te_img_top);
        hashMap.put(R.id.te_img_left_top, R.color.te_img_left_top);
        hashMap.put(R.id.te_img_left, R.color.te_img_left);
        hashMap.put(R.id.te_img_left_bottom, R.color.te_img_left_bottom);
        hashMap.put(R.id.te_img_center, R.color.te_img_center);
    }

    private ImageView back_light;

    private ImageView light_flashing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.light_activity);

        layout = (RelativeLayout) findViewById(R.id.te_interface);

        back_light = (ImageView) findViewById(R.id.back_light);

        back_light.setOnClickListener(this);

        light_flashing = (ImageView) findViewById(R.id.light_flashing);

        light_flashing.setOnClickListener(this);

        for (int i = 0; i < mImgIds.length; i++) {

            final ImageView imageView = (ImageView) findViewById(mImgIds[i]);

            imageView.setOnClickListener(this);
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            layout.setBackgroundResource(hashMap.get(mImgIds[curent_index]));

            curent_index++;

            if (curent_index >= mImgIds.length) {
                curent_index = 0;
            }
            handler.sendEmptyMessageDelayed(0, 500);
        }
    };


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_light) {
            finish();
        } else if (v.getId() == R.id.light_flashing) {

            if(!flash)
            {
                light_flashing.setImageResource(R.drawable.x_flash_turn_on);
                handler.sendEmptyMessageDelayed(0,0);
                flash=true;

            }else
            {
                light_flashing.setImageResource(R.drawable.x_flash_turn_off);
                handler.removeCallbacksAndMessages(null);
                flash=false;
            }

        } else {

            layout.setBackgroundResource(hashMap.get(v.getId()));
        }
    }
}
