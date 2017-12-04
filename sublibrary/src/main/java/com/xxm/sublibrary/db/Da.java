package com.xxm.sublibrary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

            + "allow_network integer default 0)";

    private static final String TBL_LOCAL_CREATE = "create table " + TBL_LOCK_CLICK + "(id integer primary key,offer_id integer default 0,sub_day_limit_now integer default 0)";


    public Da(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);
    }

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
        super(ctx, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        db.execSQL("DROP TABLE IF EXISTS " + TBL_LOCK_CLICK);

        db.execSQL(TBL_LOCAL_CREATE);

        db.execSQL("DROP TABLE IF EXISTS " + TBL_OPA);

        db.execSQL(TBL_OPA_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TBL_OPA);

        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + TBL_LOCK_CLICK);

        onCreate(db);
    }


}
