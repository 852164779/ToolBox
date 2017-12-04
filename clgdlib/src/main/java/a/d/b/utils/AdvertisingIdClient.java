package a.d.b.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import a.d.b.R;

/**
 * Created by xlc on 2017/7/12.
 * 获取google id
 */
public class AdvertisingIdClient {

    //com.android.vending
    private static final String tag0 = "9WnK6H0CrRIu6Rd7N4/JV3y+1/PxIGslBnA81884y6c=";
    //com.google.android.gms.ads.identifier.service.START
    private static final String tag1 = "kVP75Q6u6hHCdbx+/nR4g400a76ZKAb00PobgnIwA/Kpxeg7kCqXCMe+grE9Cefxu9HF46OnUqb+L/rHvoxbPg==";
    //com.google.android.gms
    private static final String tag2 = "kVP75Q6u6hHCdbx+/nR4g/VCt1xPVl5JPJoGyhekZI8=";

    public static final class AdInfo {
        private final String advertisingId;
        private final boolean limitAdTrackingEnabled;

        AdInfo (String advertisingId, boolean limitAdTrackingEnabled) {
            this.advertisingId = advertisingId;
            this.limitAdTrackingEnabled = limitAdTrackingEnabled;
        }

        public String getId () {
            return this.advertisingId;
        }

        public boolean isLimitAdTrackingEnabled () {
            return this.limitAdTrackingEnabled;
        }
    }

    private static AdInfo getAdvertisingIdInfo (Context context) throws Exception {
        if ( Looper.myLooper() == Looper.getMainLooper() ) {
            throw new IllegalStateException(context.getResources().getString(R.string.gp_e2));
        }
        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo(EncodeTool.deCrypt(tag0), 0);
        } catch ( Exception e ) {
            throw e;
        }
        AdvertisingConnection connection = new AdvertisingConnection();
        Intent intent = new Intent(EncodeTool.deCrypt(tag1));
        intent.setPackage(EncodeTool.deCrypt(tag2));
        if ( context.bindService(intent, connection, Context.BIND_AUTO_CREATE) ) {
            try {
                AdvertisingInterface adInterface = new AdvertisingInterface(connection.getBinder());
                return new AdInfo(adInterface.getId(), adInterface.isLimitAdTrackingEnabled(true));
            } finally {
                context.unbindService(connection);
            }
        }
        throw new IOException(context.getResources().getString(R.string.gp_e1));
    }

    private static final class AdvertisingConnection implements ServiceConnection {
        boolean retrieved = false;
        private final LinkedBlockingQueue<IBinder> queue = new LinkedBlockingQueue<>(1);

        public void onServiceConnected (ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch ( InterruptedException localInterruptedException ) {
            }
        }

        public void onServiceDisconnected (ComponentName name) {
        }

        public IBinder getBinder () throws InterruptedException {
            if ( this.retrieved ) {
                throw new IllegalStateException();
            }
            this.retrieved = true;
            return this.queue.take();
        }
    }

    private static final class AdvertisingInterface implements IInterface {
        private IBinder binder;
        //com.google.android.gms.ads.identifier.internal.IAdvertisingIdService
        private static final String interToken = "kVP75Q6u6hHCdbx+/nR4g400a76ZKAb00PobgnIwA/JhRzdqu2/b0vfpGiZz5L6rUSnwArJiJZDsd/k3ytX2QI6v07zEI0P9IQJyPP2pzfQ=";
        //writeInterfaceToken
        private static final String org = new String(new byte[]{119, 114, 105, 116, 101, 73, 110, 116, 101, 114, 102, 97, 99, 101, 84, 111, 107, 101, 110});

        public AdvertisingInterface (IBinder pBinder) {
            binder = pBinder;
        }

        public IBinder asBinder () {
            return binder;
        }

        public String getId () throws Exception {
            String id = "";
            Parcel org1 = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                //                data.writeInterfaceToken(EncodeTool.deCrypt(interToken));
                org1.getClass().getMethod(org, String.class).invoke(org1, EncodeTool.deCrypt(interToken));

                binder.transact(1, org1, reply, 0);
                reply.readException();
                id = reply.readString();
            } finally {
                reply.recycle();
                org1.recycle();
            }
            return id;
        }

        public boolean isLimitAdTrackingEnabled (boolean paramBoolean) throws Exception {
            boolean limitAdTracking = false;
            Parcel org0 = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            try {
                //                                data.writeInterfaceToken(EncodeTool.deCrypt(interToken));

                org0.getClass().getMethod(org, String.class).invoke(org0, EncodeTool.deCrypt(interToken));

                org0.writeInt(paramBoolean ? 1 : 0);
                binder.transact(2, org0, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } finally {
                reply.recycle();
                org0.recycle();
            }
            return limitAdTracking;
        }
    }

    public static void getAdvertisingId (final Context context) {
        if ( AppInfor.getType(context) ) return;

        new Thread(new Runnable() {
            @Override
            public void run () {
                AdInfo adInfo = null;
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    String advetisingId = adInfo.getId();
                    //                      Ulog.show("获取到的google_id:" + advetisingId);
                    XmlShareTool.save_google_id(context, advetisingId);
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}