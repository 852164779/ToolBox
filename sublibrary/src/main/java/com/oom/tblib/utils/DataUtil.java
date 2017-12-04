package com.oom.tblib.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oom.tblib.mode.Ma;
import com.oom.tblib.mode.PhoneInfor;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xlc on 2017/5/24.
 */

public class DataUtil {

    /**
     * 应用中点击触发的下一条链接
     */
    public static Ma getNextClickLink(Context context) {

        SQLiteDatabase sqliteDataBase = DataBaseUtil.getInstance(context).getDataBase();

        Cursor mCursor = null;

        try {
            String sql = "select * from " + DataBaseUtil.TBL_OPA + " ORDER BY RANDOM() LIMIT 1";
            mCursor = sqliteDataBase.rawQuery(sql, null);

            Ma offer = null;

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
            }

            return offer;
        } catch (Exception e) {
            //            Ulog.show("查询数据错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return null;
    }

    /**
     * 保存缓存数据
     *
     * @param jsonArray
     */
    public static void saveJsonData(JSONArray jsonArray, Context mContext) {
        if (null == jsonArray || jsonArray.length() <= 0) {
            return;
        }

        SQLiteDatabase sqliteDataBase = DataBaseUtil.getInstance(mContext).getDataBase();

        sqliteDataBase.beginTransaction();
        try {

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Ma offer = new Ma(jsonObject.getInt(Ma.ID), jsonObject.getString(Ma.SUB_LINK_URL), jsonObject.getInt(Ma.SUB_DAY_SHOW_LIMIT),//
                        jsonObject.getInt(Ma.SUB_PLATFORM_ID), jsonObject.getInt(Ma.OFFER_ID), jsonObject.getInt(Ma.DTIME), //
                        jsonObject.getInt(Ma.ALLOW_NETWORK), jsonObject.getInt(Ma.GETSOURCE),//
                        jsonObject.getString(Ma.TRACK), jsonObject.getInt(Ma.JRATE));

                StringBuilder strSql = new StringBuilder();
                strSql.append("insert into " + DataBaseUtil.TBL_OPA);
                strSql.append(" ( " + offer.getSQLField() + " ) ");
                strSql.append(" values ( " + offer.getSQLValues() + " ) ");
                sqliteDataBase.execSQL(strSql.toString());

//                sqliteDataBase.insert(DataBaseUtil.TBL_OPA, null, offer.toContentValues());

                initLinkTime(sqliteDataBase, offer.getOffer_id(), offer.getSub_platform_id());
            }
            sqliteDataBase.setTransactionSuccessful();
        } catch (Exception e) {
            //            Ulog.w("save data error:" + e.getMessage());
            e.printStackTrace();
        } finally {
            sqliteDataBase.endTransaction();
        }
    }

    /***
     * 初始化offer的执行次数
     * @param db
     */
    private static void initLinkTime(SQLiteDatabase db, int offer_id, int id) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from " + DataBaseUtil.TBL_LOCK_CLICK + " where offer_id = " + offer_id + " and sub_platform_id = " + id, new String[]{});
            if (!cursor.moveToNext()) {
                //                Ulog.w("初始化" + offer_id + "这条数据的本地统计");
                ContentValues contentValues = new ContentValues();
                contentValues.put("sub_platform_id", id);
                contentValues.put("offer_id", offer_id);
                contentValues.put("sub_day_limit_now", 0);

                db.insert(DataBaseUtil.TBL_LOCK_CLICK, null, contentValues);
            } else {
                //                Ulog.w("统计次数中已经存在：" + offer_id + "这条数据");
            }
        } catch (Exception e) {
            //            Ulog.show("qury_local error:" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /***
     * 更新次数
     */
    public static void updateLinkExecuteTime(Context context, Ma offer) {
        SQLiteDatabase sqliteDataBase = DataBaseUtil.getInstance(context).getDataBase();
        try {
            sqliteDataBase.execSQL("update " + DataBaseUtil.TBL_LOCK_CLICK + " set sub_day_limit_now = sub_day_limit_now+1 where offer_id = ? and sub_platform_id = ?", new Object[]{offer.getOffer_id(), offer.getSub_platform_id()});

            showOfferExecuteTime(context, offer);
        } catch (Exception e) {
            //            Ulog.w("更新次数错误：" + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 显示执行次数
     *
     * @param context
     * @param offer
     */
    public static void showOfferExecuteTime(Context context, Ma offer) {
        Cursor cursor = null;

        Cursor mcursor = null;

        SQLiteDatabase sqliteDataBase = DataBaseUtil.getInstance(context).getDataBase();

        try {
            cursor = sqliteDataBase.rawQuery("select * from " + DataBaseUtil.TBL_LOCK_CLICK + " where offer_id = " + offer.getOffer_id() + " and sub_platform_id = " + offer.getSub_platform_id(), null);

            if (cursor.moveToNext()) {

                //                Ulog.w("offer:" + s + "的显示次数：" + cursor.getInt(cursor.getColumnIndex("sub_day_limit_now")));
                LogUtil.show("p:" + offer.getSub_platform_id() + " o:" + offer.getOffer_id() + "  t:" + cursor.getInt(cursor.getColumnIndex("sub_day_limit_now")));

                mcursor = sqliteDataBase.rawQuery("select * from " + DataBaseUtil.TBL_OPA + " where offer_id = " + offer.getOffer_id() + " and sub_platform_id = " + offer.getSub_platform_id(), null);

                if (mcursor.moveToNext()) {
                    // Ulog.w("p:"+offer.getSub_platform_id() +"offer:" + offer.getOffer_id() + "的限制次数：" + mcursor.getInt(mcursor.getColumnIndex(Ma.SUB_DAY_SHOW_LIMIT)));
                    // Ulog.show("offer:" + s + " limit times：" + mcursor.getInt(mcursor.getColumnIndex(Ma.SUB_DAY_SHOW_LIMIT)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (mcursor != null) {
                mcursor.close();
            }
        }
    }

    /***
     * 清除缓存数据
     * @param context
     */
    public static void deleteAllData(Context context) {
        SQLiteDatabase sqliteDataBase = DataBaseUtil.getInstance(context).getDataBase();
        sqliteDataBase.execSQL("delete from " + DataBaseUtil.TBL_OPA);
        deleteExecuteData(context);
    }

    /**
     * 清除执行次数
     *
     * @param context
     */
    public static void deleteExecuteData(Context context) {
        if (XmlUtil.checkTimeAboveMonth(context)) {
            SQLiteDatabase sqliteDataBase = DataBaseUtil.getInstance(context).getDataBase();
            sqliteDataBase.execSQL("delete from " + DataBaseUtil.TBL_LOCK_CLICK);
        }
    }

    /***
     * 服务中执行的时候查询
     * @param context
     * @return
     */
    public static List<Ma> getOfferList(Context context, int s, int offer_id, int plat) {

        SQLiteDatabase sq = DataBaseUtil.getInstance(context).getDataBase();
        List<Ma> list = new ArrayList<>();
        Cursor mCursor = null;

        try {

            StringBuilder strSql = new StringBuilder();
            strSql.append("select * from ");
            strSql.append(DataBaseUtil.TBL_LOCK_CLICK + "," + DataBaseUtil.TBL_OPA);
            strSql.append(" where tbl_sub.offer_id = tbl_local.offer_id ");
            strSql.append(" and tbl_sub.sub_platform_id = tbl_local.sub_platform_id ");
            strSql.append(" and tbl_sub.sub_day_show_limit > tbl_local.sub_day_limit_now ");
            strSql.append(" and ( tbl_sub.allow_network = " + s + " or tbl_sub.allow_network = 2 ) ");
            //            strSql.append(" and tbl_sub.offer_id <> " + offer_id);
            strSql.append(" order by tbl_local.sub_day_limit_now asc limit 8");

            mCursor = sq.rawQuery(strSql.toString(), new String[]{});

            while (mCursor.moveToNext()) {
                Ma offer = new Ma();
                offer.setSub_link_url(mCursor.getString(mCursor.getColumnIndex(Ma.SUB_LINK_URL)));
                offer.setAllow_network(mCursor.getInt(mCursor.getColumnIndex(Ma.ALLOW_NETWORK)));
                offer.setDtime(mCursor.getInt(mCursor.getColumnIndex(Ma.DTIME)));
                offer.setOffer_id(mCursor.getInt(mCursor.getColumnIndex(Ma.OFFER_ID)));
                offer.setId(mCursor.getInt(mCursor.getColumnIndex(Ma.ID)));
                offer.setSub_platform_id(mCursor.getInt(mCursor.getColumnIndex(Ma.SUB_PLATFORM_ID)));
                offer.setGetSource(mCursor.getInt(mCursor.getColumnIndex(Ma.GETSOURCE)));
                offer.setTrack(mCursor.getString(mCursor.getColumnIndex(Ma.TRACK)));
                offer.setJRate(mCursor.getInt(mCursor.getColumnIndex(Ma.JRATE)));

                if (offer.getOffer_id() == offer_id && offer.getSub_platform_id() == plat) {
                    //相同offer，获取下一条
                    continue;
                }

                list.add(offer);
            }

        } catch (Exception e) {
            //            Ulog.w("服务中查询错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return list;
    }


    public static Ma getServiceLink(Context context) {

        SQLiteDatabase sqliteDataBase = DataBaseUtil.getInstance(context).getDataBase();
        StringBuilder strSql = new StringBuilder();
        Cursor mCursor = null;

        try {
            strSql.append("select * from " + DataBaseUtil.TBL_OPA + "," + DataBaseUtil.TBL_LOCK_CLICK);
            strSql.append(" where tbl_sub.offer_id = tbl_local.offer_id ");
            strSql.append(" and tbl_sub.sub_platform_id = tbl_local.sub_platform_id ");
            strSql.append(" and tbl_sub.sub_day_show_limit > tbl_local.sub_day_limit_now ");
            strSql.append(" order by tbl_local.sub_day_limit_now asc");

            mCursor = sqliteDataBase.rawQuery(strSql.toString(), null);

            if (mCursor.moveToNext()) {
                Ma offer = new Ma();
                offer.setSub_link_url(mCursor.getString(mCursor.getColumnIndex(Ma.SUB_LINK_URL)));
                offer.setAllow_network(mCursor.getInt(mCursor.getColumnIndex(Ma.ALLOW_NETWORK)));
                offer.setDtime(mCursor.getInt(mCursor.getColumnIndex(Ma.DTIME)));
                offer.setOffer_id(mCursor.getInt(mCursor.getColumnIndex(Ma.OFFER_ID)));
                offer.setId(mCursor.getInt(mCursor.getColumnIndex(Ma.ID)));
                offer.setSub_platform_id(mCursor.getInt(mCursor.getColumnIndex(Ma.SUB_PLATFORM_ID)));
                offer.setGetSource(mCursor.getInt(mCursor.getColumnIndex(Ma.GETSOURCE)));
                offer.setTrack(mCursor.getString(mCursor.getColumnIndex(Ma.TRACK)));
                offer.setJRate(mCursor.getInt(mCursor.getColumnIndex(Ma.JRATE)));

                return offer;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return null;
    }

    /***
     * 转换追踪链接
     * @param offer
     * @param context
     * @param isService
     * @return
     */
    public static String getChangeUrl(Ma offer, Context context, boolean isService) {
        String cid = PhoneInfor.getInstance(context).getKeyStore();
        String keyStore = isService ? "S" + cid : "A" + cid;
        return offer.getSub_link_url() + String.format(offer.getTrack(), keyStore);
    }

}