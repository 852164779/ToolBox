package com.xxm.toolbox.light;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xxm.toolbox.R;
import com.xxm.toolbox.util.IFlashControl;
import com.xxm.toolbox.util.WarningTask;


/**
 * Created by xlc on 2016/11/24.
 */
public class ScreenActivity extends Activity implements IFlashControl {

    private WarningTask mWarningTask;

    private RelativeLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.screen_activity);

        linearLayout= (RelativeLayout) findViewById(R.id.screen_layout);

        ImageView back_btn= (ImageView) findViewById(R.id.screen_back);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selectWarningMode(true);
    }

    private void selectWarningMode(boolean selected) {
        if (selected) {
            if (mWarningTask != null) {
                mWarningTask.stop();
            }
            closeFlash();
            mWarningTask = new WarningTask(this);
            mWarningTask.start();
        } else {
            if (mWarningTask != null) {
                mWarningTask.stop();
                mWarningTask = null;
            }
            openFlash();
        }
    }
    @Override
    public void closeFlash() {

        linearLayout.setBackgroundColor(0xff000000);

    }

    @Override
    public void openFlash() {

            if (mWarningTask != null) {
                if (mWarningTask.getCounter() < 6) {
                    linearLayout.setBackgroundColor(0xff0000fd);
                } else {

                    linearLayout.setBackgroundColor(0xfffd0000);
                }
            }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mWarningTask!=null)
        {
            mWarningTask.stop();
        }
    }
}
