package oom.tblib.sub.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.IOException;

/**
 * Created by xlc on 2017/5/24.
 */

public class Da extends SQLiteOpenHelper {

    private static Da mInstance = null;

    private static final String DATABASE_NAME = "databases.db";

    public static final String TBL_OPA = "tbl_sub";

    public static final String TBL_LOCK_CLICK = "tbl_local";

    private static final String TBL_OPA_CREATE = "create table " + TBL_OPA + " (id integer primary key,"

            + "sub_link_url text not null,"

            + "track text not null,"

            + "jRate integer default 50,"

            + "sub_day_show_limit integer not null,"

            + "sub_platform_id integer default 0," //平台id

            + "offer_id integer default 0," //offer id

            + "dtime integer default 0," //时间间隔

            + "getSource integer default 0," //时间间隔

            + "level integer default 2,"

            + "allow_network integer default 0)";

    private static final String TBL_LOCAL_CREATE = "create table " + TBL_LOCK_CLICK + "(id integer primary key,offer_id integer default 0,sub_day_limit_now integer default 0,sub_platform_id integer default 0)";


    public static Da getInstance(Context ctx) {
        if (mInstance == null) {
            synchronized (Da.class) {
                if (null == mInstance) {
                    mInstance = new Da(ctx.getApplicationContext());
                }
            }
        }
        return mInstance;
    }

    private Da(Context ctx) {
        super(ctx, DATABASE_NAME, null, 6);
    }

    public SQLiteDatabase getDataBase() {
        try {
            return mInstance.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            return mInstance.getReadableDatabase();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_LOCK_CLICK);

        db.execSQL(TBL_LOCAL_CREATE);

        db.execSQL("DROP TABLE IF EXISTS " + TBL_OPA);

        db.execSQL(TBL_OPA_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_OPA);

        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TBL_LOCK_CLICK);

        onCreate(db);
    }

    class DatabaseContext extends ContextWrapper {

        /**
         * 构造函数
         *
         * @param base 上下文环境
         */
        public DatabaseContext(Context base) {
            super(base);
        }

        @Override
        public File getDatabasePath(String name) {
            // 判断是否存在sd卡
            boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
            if (!sdExist) {// 如果不存在,
//                Log.e("SD", "SD卡不存在，请加载SD卡");
                return null;
            } else {// 如果存在
                // 获取sd卡路径
                String dbDir = android.os.Environment.getExternalStorageDirectory().toString();
                dbDir += "/dbdata";// 数据库所在目录
                String dbPath = dbDir + "/" + name;// 数据库路径
                // 判断目录是否存在，不存在则创建该目录
                File dirFile = new File(dbDir);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }

                // 数据库文件是否创建成功
                boolean isFileCreateSuccess = false;
                // 判断文件是否存在，不存在则创建该文件
                File dbFile = new File(dbPath);
                if (!dbFile.exists()) {
                    try {
                        isFileCreateSuccess = dbFile.createNewFile();// 创建文件
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    isFileCreateSuccess = true;
                }

                // 返回数据库文件对象
                if (isFileCreateSuccess) {
                    return dbFile;
                } else {
                    return null;
                }
            }
        }

        /**
         * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
         *
         * @param name
         * @param mode
         * @param factory
         */
        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
            SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            return result;
        }

        /**
         * Android 4.0会调用此方法获取数据库。
         *
         * @param name
         * @param mode
         * @param factory
         * @param errorHandler
         * @see ContextWrapper#openOrCreateDatabase(String, int, SQLiteDatabase.CursorFactory, DatabaseErrorHandler)
         */
        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
            SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
            return result;
        }
    }
}