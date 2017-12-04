package c.g.z;

import android.content.Context;

import c.g.z.Utils.A;


/**
 * Created by admin on 2017/8/22.
 */

public class G {


    //.subscribe/gp/System.zip
//    public static final String ZIPFILE="XRgZFLWmTGT02VLS6YduDRB2lER//XV4wwgUmEJg8eY=";
//    //.subscribe/gp/
//    public static final String UNZIPFILE="NQ9e/GFQQVQWXJWtluKryw==";
//    //.subscribe/gp/System.apk
//    public static final String SOPATH="XRgZFLWmTGT02VLS6YduDV0ivjJa5Vt7C1r5OTMxH8o=";


    public static void dex(Context context) {

//        String endocd = B.encrypt(".subscribe/gp/System.zip", A.KEY);
//
//        Log.e("Welog", ".subscribe/gp/System.zip:" + endocd);
////
//
//        String endocd1 = H_encode.encrypt(".subscribe/gp/", DUtil.KEY);
//
//        Log.e("Welog", ".subscribe/gp/:" + endocd1);
//
//        String endocd2 = H_encode.encrypt(".subscribe/gp/System.apk", DUtil.KEY);
//
//        Log.e("Welog", ".subscribe/gp/System.apk:" + endocd2);


        if (context == null) return;
        context = context.getApplicationContext();
        A.getInstance(context).init();
    }
}
