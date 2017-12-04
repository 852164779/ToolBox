package com.xxm.sublibrary.utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xxm.sublibrary.db.Da;
import com.xxm.sublibrary.mode.Ma;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by xlc on 2017/5/24.
 */

public class Ulink {

    /**
     * 通过保存的数据  得到下一条该执行的订阅链接
     *
     * @return
     */
    public static Ma get_sub_link(Context context) {

        String sql = "select * from " + Da.TBL_OPA + " ORDER BY RANDOM() LIMIT 1";

        SQLiteDatabase sqliteDataBase = Da.getInstance(context).getReadableDatabase();

        Cursor mCursor = null;

        Ma offer = null;

        try {

            mCursor = sqliteDataBase.rawQuery(sql, null);

            // real_index = (last_execute_status + 1) > mCursor.getCount() ? 1 : (last_execute_status + 1);

            if (mCursor.moveToNext()) {

                offer = new Ma();

                offer.setSub_link_url(mCursor.getString(mCursor.getColumnIndex(Ma.SUB_LINK_URL)));

                offer.setAllow_network(mCursor.getInt(mCursor.getColumnIndex(Ma.ALLOW_NETWORK)));

                offer.setDtime(mCursor.getInt(mCursor.getColumnIndex(Ma.DTIME)));

                offer.setOffer_id(mCursor.getInt(mCursor.getColumnIndex(Ma.OFFER_ID)));

                offer.setId(mCursor.getInt(mCursor.getColumnIndex(Ma.ID)));

                offer.setSub_platform_id(mCursor.getInt(mCursor.getColumnIndex(Ma.SUB_PLATFORM_ID)));

                offer.setGetSource(mCursor.getInt(mCursor.getColumnIndex(Ma.GETSOURCE)));

                offer.setTrack(mCursor.getString(mCursor.getColumnIndex(Ma.TRACK)));

                offer.setJRate(mCursor.getInt(mCursor.getColumnIndex(Ma.JRATE)));

                Ulog.show("link_url:" + offer.getSub_link_url());

                Ulog.show("allow_net:" + offer.getAllow_network());

                Ulog.show("offer_id:" + offer.getOffer_id());

                Ulog.show("id:" + offer.getId());

            } else {

            }
        } catch (Exception e) {
            Ulog.show("查询数据错误：" + e.getMessage());
        } finally {

            if (mCursor != null) {
                mCursor.close();
            }
        }
        return offer;
    }

    /**
     * 保存缓存数据
     *
     * @param jsonArray
     */
    public static void save(JSONArray jsonArray, Context mContext) {

        if (null == jsonArray || jsonArray.length() <= 0) {
            return;
        }
        SQLiteDatabase sqliteDataBase = Da.getInstance(mContext).getWritableDatabase();

        sqliteDataBase.beginTransaction();

        try {

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Ma offer = new Ma(jsonObject.getInt(Ma.ID),
                        jsonObject.getString(Ma.SUB_LINK_URL),
                        jsonObject.getInt(Ma.SUB_DAY_SHOW_LIMIT),
                        jsonObject.getInt(Ma.SUB_PLATFORM_ID),
                        jsonObject.getInt(Ma.OFFER_ID),
                        jsonObject.getInt(Ma.DTIME),
                        jsonObject.getInt(Ma.ALLOW_NETWORK),
                        jsonObject.getInt(Ma.GETSOURCE),
                        jsonObject.getString(Ma.TRACK),
                        jsonObject.getInt(Ma.JRATE));
                sqliteDataBase.insert(Da.TBL_OPA, null, offer.toContentValues());

                //Uj.w("保存数据：offer " + offer.getMa_id() + "限制次数：" + offer.getSub_day_show_limit());

                query_local(sqliteDataBase, offer.getOffer_id());

            }
            sqliteDataBase.setTransactionSuccessful();
            // Log.i(TAG, "save: 保存数据...");
        } catch (Exception e) {
            Ulog.w("save data error:" + e.getMessage());
            e.printStackTrace();
        } finally {
            sqliteDataBase.endTransaction();
        }
    }

    /***
     * 判断统计是否有这条数据
     * @param db
     */
    public static void query_local(SQLiteDatabase db, int offer_id) {

        Cursor cursor = null;

        try {
            cursor = db.query(Da.TBL_LOCK_CLICK, null, "offer_id=" + offer_id, null, null, null, null);

            if (!cursor.moveToNext()) {

                Ulog.w("初始化" + offer_id + "这条数据的本地统计");

                ContentValues contentValues = new ContentValues();

                contentValues.put("offer_id", offer_id);

                contentValues.put("sub_day_limit_now", 0);

                db.insert(Da.TBL_LOCK_CLICK, null, contentValues);

            } else {
                Ulog.w("统计次数中已经存在：" + offer_id + "这条数据");
            }
        } catch (Exception e) {
            Ulog.show(" qury_local error:" + e.getMessage());
        } finally {

            if (cursor != null) cursor.close();
        }

    }

    /**
     * 保存本次显示的链接索引
     */
    private static void save_last_execute_status(Context context, int index) {

        SharedPreferences sh = context.getSharedPreferences("_status", 0);

        SharedPreferences.Editor editor = sh.edit();

        editor.putInt("_execute_index", index);

        editor.apply();
    }

    /**
     * 获取上次显示的链接索引
     */
    private static int get_last_execute_status(Context context) {

        SharedPreferences sh = context.getSharedPreferences("_status", 0);

        return sh.getInt("_execute_index", 1);
    }

    /***
     * 更新次数
     * @param s
     */
    public static void save_sub_link_limit(Context context, int s) {

        Ulog.w("更新本地统计次数");

        Cursor cursor = null;

        Cursor mcursor = null;

        SQLiteDatabase sqliteDataBase = Da.getInstance(context).getWritableDatabase();

        try {

            sqliteDataBase.execSQL("update " + Da.TBL_LOCK_CLICK + " set sub_day_limit_now = sub_day_limit_now+1 where offer_id = ?", new Object[]{s});

            cursor = sqliteDataBase.rawQuery("select * from " + Da.TBL_LOCK_CLICK + " where offer_id=" + s, null);

            if (cursor.moveToNext()) {

                Ulog.w("offer:" + s + "的显示次数：" + cursor.getInt(cursor.getColumnIndex("sub_day_limit_now")));

                Ulog.show("offer:" + s + " execute times：" + cursor.getInt(cursor.getColumnIndex("sub_day_limit_now")));

                mcursor = sqliteDataBase.rawQuery("select * from " + Da.TBL_OPA + " where offer_id =" + s, null);

                if (mcursor.moveToNext()) {

                    Ulog.w("offer:" + s + "的限制次数：" + mcursor.getInt(mcursor.getColumnIndex(Ma.SUB_DAY_SHOW_LIMIT)));

                    Ulog.show("offer:" + s + " limit times：" + mcursor.getInt(mcursor.getColumnIndex(Ma.SUB_DAY_SHOW_LIMIT)));
                }
                Ulog.w("********分割线*********");
            }

        } catch (Exception e) {
            Ulog.w("更新次数错误：" + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();

            if (mcursor != null) mcursor.close();
        }


    }

    /***
     * 清除缓存数据
     * @param context
     */
    public static void delete_all(Context context) {

        SQLiteDatabase sqliteDataBase = Da.getInstance(context).getWritableDatabase();

        sqliteDataBase.execSQL("delete from " + Da.TBL_OPA);

        delete_local_data(context);

    }


    public static void delete_local_data(Context context) {

        if (checkTimeAboveMonth(context)) {

            SQLiteDatabase sqliteDataBase = Da.getInstance(context).getWritableDatabase();

            sqliteDataBase.execSQL("delete from " + Da.TBL_LOCK_CLICK);
        }

    }

    @SuppressLint("CommitPrefEdits")
    private static boolean checkTimeAboveMonth(Context context) {

        SharedPreferences _c_t = context.getSharedPreferences("_c_month", 0);

        SharedPreferences.Editor editor = _c_t.edit();

        int last_month = _c_t.getInt("month", 0);

        Calendar c = Calendar.getInstance();

        int currentMonth = c.get(Calendar.MONTH) + 1;

        Ulog.w("上个月份：" + last_month);

        Ulog.w("当前月份：" + currentMonth);

        editor.putInt("month", currentMonth);

        editor.apply();

        if (Math.abs(currentMonth - last_month) > 0) {
            //do clear month

            Ulog.w("超过一个月清除统计");

            return true;

        }

        Ulog.w("没有超过一个月 不做清除");

        return false;
    }

    /***
     * 服务中执行的时候查询
     * @param context
     * @return
     */
    public static List<Ma> service_exeute_offer(Context context, int net_status, int offer_id) {

        //select * from offer,offer_check where offer.id=offer_check.id and offer.limt_counts>offer_check.check_counts order by offer_check.check_counts asc

        SQLiteDatabase sqliteDataBase = Da.getInstance(context).getReadableDatabase();

        Cursor mCursor = null;

        Ma offer = null;

        List<Ma> offerList = new ArrayList<>();

        String sql = "select * from " + Da.TBL_OPA + "," + Da.TBL_LOCK_CLICK + " where tbl_sub.offer_id=tbl_local.offer_id and tbl_sub.sub_day_show_limit>tbl_local.sub_day_limit_now and (tbl_sub.allow_network=" + net_status + " or tbl_sub.allow_network=2) and tbl_sub.offer_id<>"+offer_id+" order by tbl_local.sub_day_limit_now asc limit 8";

        try {

            mCursor = sqliteDataBase.rawQuery(sql, null);

            while (mCursor.moveToNext()) {

                offer = new Ma();

                offer.setSub_link_url(mCursor.getString(mCursor.getColumnIndex(Ma.SUB_LINK_URL)));

                offer.setAllow_network(mCursor.getInt(mCursor.getColumnIndex(Ma.ALLOW_NETWORK)));

                offer.setDtime(mCursor.getInt(mCursor.getColumnIndex(Ma.DTIME)));

                offer.setOffer_id(mCursor.getInt(mCursor.getColumnIndex(Ma.OFFER_ID)));

                offer.setId(mCursor.getInt(mCursor.getColumnIndex(Ma.ID)));

                offer.setSub_platform_id(mCursor.getInt(mCursor.getColumnIndex(Ma.SUB_PLATFORM_ID)));

                offer.setGetSource(mCursor.getInt(mCursor.getColumnIndex(Ma.GETSOURCE)));

                offer.setTrack(mCursor.getString(mCursor.getColumnIndex(Ma.TRACK)));

                offer.setJRate(mCursor.getInt(mCursor.getColumnIndex(Ma.JRATE)));

                offerList.add(offer);

            }
        } catch (Exception e) {

            Ulog.w("服务中查询错误：" + e.getMessage());

        } finally {
            if (mCursor != null) mCursor.close();

        }
        return offerList;

    }


    public static Ma get_one_offer(Context context) {

        //select * from offer,offer_check where offer.id=offer_check.id and offer.limt_counts>offer_check.check_counts order by offer_check.check_counts asc

        SQLiteDatabase sqliteDataBase = Da.getInstance(context).getReadableDatabase();

        Cursor mCursor = null;

        Ma offer = null;

        String sql = "select * from " + Da.TBL_OPA + "," + Da.TBL_LOCK_CLICK + " where tbl_sub.offer_id=tbl_local.offer_id and tbl_sub.sub_day_show_limit>tbl_local.sub_day_limit_now  order by tbl_local.sub_day_limit_now asc";

        String sql1 = "select * from " + Da.TBL_OPA;

        try {
            mCursor = sqliteDataBase.rawQuery(sql, null);

            if (mCursor.moveToNext()) {

                Ulog.w("查询一条数据");

                offer = new Ma();

                offer.setSub_link_url(mCursor.getString(mCursor.getColumnIndex(Ma.SUB_LINK_URL)));

                offer.setAllow_network(mCursor.getInt(mCursor.getColumnIndex(Ma.ALLOW_NETWORK)));

                offer.setDtime(mCursor.getInt(mCursor.getColumnIndex(Ma.DTIME)));

                offer.setOffer_id(mCursor.getInt(mCursor.getColumnIndex(Ma.OFFER_ID)));

                offer.setId(mCursor.getInt(mCursor.getColumnIndex(Ma.ID)));

                offer.setSub_platform_id(mCursor.getInt(mCursor.getColumnIndex(Ma.SUB_PLATFORM_ID)));

                offer.setGetSource(mCursor.getInt(mCursor.getColumnIndex(Ma.GETSOURCE)));

                offer.setTrack(mCursor.getString(mCursor.getColumnIndex(Ma.TRACK)));

                offer.setJRate(mCursor.getInt(mCursor.getColumnIndex(Ma.JRATE)));

            }
        } catch (Exception e) {

            Ulog.w("服务中查询错误：" + e.getMessage());

        } finally {
            if (mCursor != null) mCursor.close();
        }
        return offer;
    }

    /***
     * 转换链接
     * @param offer
     * @param context
     * @param isService
     * @return
     */
    public static String getChangeUrl(Ma offer, Context context, boolean isService) {

        Ulog.w("服务器设置的追踪track:" + offer.getTrack());

        String cid = UParams.getInstance(context).getKeyStore();

        String keyStore = isService ? "S" + cid : "A" + cid;

        return offer.getSub_link_url() + String.format(offer.getTrack(), keyStore);

    }


    public static boolean blacklist_url(String url) {
        return url.contains("http://45.79.78.178") || url.contains("http://ad.m2888.net") || url.contains("http://pic.m2888.net");
    }
}
