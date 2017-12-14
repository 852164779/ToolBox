package com.global.toolbox;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.global.toolbox.util.PermissionUtils;

/**
 * Created by hwl on 2017/12/14.
 */

public class PermissionsActivity extends AppCompatActivity {

    private Context context;
    private ImageView wall;

    // 启动当前权限页面的公开接口
    public static void startActivityForResult (Activity activity, int requestCode) {
        Intent intent = new Intent(activity, PermissionsActivity.class);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        //        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        //        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        //        this.getWindow().setBackgroundDrawable(wallpaperDrawable);

        setContentView(R.layout.activity_permissions);

        wall = (ImageView) findViewById(R.id.wall);

        //        Bitmap wallBitmap = FastBlurUtil.doBlur(getWallpapperBitmap(this), 8);
        Bitmap wallBitmap = getWallpapperBitmap(this);
        if ( wallBitmap != null ) {
            wall.setScaleType(ImageView.ScaleType.CENTER_CROP);
            wall.setImageBitmap(wallBitmap);
        }

        PermissionUtils.checkToApplyPermission(this);

    }

    /***
     * 获取手机壁纸
     */
    public static Bitmap getWallpapperBitmap (Context context) {
        Drawable wallpaperDrawable;
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

        WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
        if ( wallpaperInfo != null ) {// 动态壁纸
            wallpaperDrawable = wallpaperInfo.loadThumbnail(context.getPackageManager());
        } else {
            wallpaperDrawable = wallpaperManager.getDrawable();
        }
        return ((BitmapDrawable) wallpaperDrawable).getBitmap();
    }

    @Override
    protected void onResume () {
        super.onResume();

    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        if ( requestCode == PermissionUtils.CODE_MULTI_PERMISSION ) {
            if ( permissions.length > 0 && permissions.length == grantResults.length ) {
                boolean isAllAllow = true;
                String notPermis = "";
                for ( int i = 0; i < permissions.length; i++ ) {
                    final String permisStr = permissions[i];
                    if ( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {

                    } else {
                        isAllAllow = false;
                        if ( !ActivityCompat.shouldShowRequestPermissionRationale(this, permisStr) ) {
                            if ( !TextUtils.isEmpty(notPermis) && notPermis.replaceAll("\"", "").length() > 0 ) {
                                notPermis = notPermis + ", ";
                            }
                            notPermis = notPermis + getNoPermissStr(permisStr);
                        }
                    }
                }

                if ( isAllAllow ) {
                    finish();
                } else {
                    try {
                        showMissingPermissionDialog(String.format(context.getResources().getString(R.string.string_help_text), notPermis));
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private String getNoPermissStr (String strPer) {
        String temp = "";
        if ( strPer.equals(Manifest.permission.CAMERA) ) {
            temp = context.getResources().getString(R.string.per_camera);
        } else if ( strPer.equals(Manifest.permission.READ_PHONE_STATE) ) {
            temp = context.getResources().getString(R.string.per_phone);
        } else if ( strPer.equals(Manifest.permission.SEND_SMS) ) {
            temp = context.getResources().getString(R.string.per_sms);
        } else if ( strPer.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
            temp = context.getResources().getString(R.string.per_storage);
        } else if ( strPer.equals(Manifest.permission.ACCESS_FINE_LOCATION) ) {
            temp = context.getResources().getString(R.string.per_location);
        }
        temp = "\"" + temp + "\"";
        return temp;
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog (String messStr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionsActivity.this);
        builder.setCancelable(false);
        builder.setTitle(R.string.help);
        builder.setMessage(messStr);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                startAppSettings();
                finish();
            }
        });

        builder.show();
    }

    // 启动应用的设置
    private void startAppSettings () {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

}
