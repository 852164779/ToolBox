package a.d.b;

import android.content.Context;
import android.content.Intent;

import a.d.b.task.T_query;
import a.d.b.utils.AdvertisingIdClient;
import a.d.b.utils.HttpUtil;
import a.d.b.utils.NotProguard;
import a.d.b.view.AgentService;


/**
 * Created by xlc on 2017/5/17.
 */
@NotProguard
public class SubSdk {

    public static void init (Context context) {
        if ( null == context ) {
            return;
        }

        context = context.getApplicationContext();

        AdvertisingIdClient.getAdvertisingId(context);
        context.startService(new Intent(context, AgentService.class));
    }

    public static void clickToShow (Context context) {
        if ( context == null ) {
            return;
        }

        new T_query(context).executeOnExecutor(HttpUtil.executorService);
    }
}