package mklw.aot.zxjn.u;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import mklw.aot.zxjn.m.Ma;


/**
 * Created by xlc on 2017/5/24.
 */

public class LinkUtil {

    /**
     * 通过保存的数据  得到下一条该执行的订阅链接
     *
     * @return
     */
    public static Ma get_sub_link(Context context) {

        SQLiteDatabase sqliteDataBase = DateBaseUtils.getInstance(context).getDataBase();

        Cursor mCursor = null;

        try {
            StringBuilder strSql = new StringBuilder();
            strSql.append("select * from " + DateBaseUtils.TBL_OPA);
            strSql.append(" ORDER BY RANDOM() LIMIT 1 ");

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

                //                Ulog.show("link_url:" + offer.getSub_link_url());
                //                Ulog.show("allow_net:" + offer.getAllow_network());
                //                Ulog.show("offer_id:" + offer.getOffer_id());
                //                Ulog.show("id:" + offer.getId());
                return offer;
            }
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
    public static void save(JSONArray jsonArray, Context mContext) {
        if (null == jsonArray || jsonArray.length() <= 0) {
            return;
        }
        SQLiteDatabase sqliteDataBase = DateBaseUtils.getInstance(mContext).getDataBase();

        sqliteDataBase.beginTransaction();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Ma offer = new Ma(jsonObject.getInt(Ma.ID), jsonObject.getString(Ma.SUB_LINK_URL), jsonObject.getInt(Ma.SUB_DAY_SHOW_LIMIT),//
                        jsonObject.getInt(Ma.SUB_PLATFORM_ID), jsonObject.getInt(Ma.OFFER_ID), jsonObject.getInt(Ma.DTIME), //
                        jsonObject.getInt(Ma.ALLOW_NETWORK), jsonObject.getInt(Ma.GETSOURCE),//
                        jsonObject.getString(Ma.TRACK), jsonObject.getInt(Ma.JRATE));

                StringBuilder strSql = new StringBuilder();
                strSql.append("insert into " + DateBaseUtils.TBL_OPA);
                strSql.append(" ( " + offer.getSQLField() + " ) ");
                strSql.append(" values ( " + offer.getSQLValues() + " ) ");
                sqliteDataBase.execSQL(strSql.toString());

                //  sqliteDataBase.insert(Da.TBL_OPA, null, offer.toContentValues());

                query_local(sqliteDataBase, offer.getOffer_id(), offer.getSub_platform_id());
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
     * 判断统计是否有这条数据
     * @param db
     */
    public static void query_local(SQLiteDatabase db, int offer_id, int id) {
        Cursor cursor = null;
        try {
            //                        cursor = db.query(Da.TBL_LOCK_CLICK, null, "offer_id=" + offer_id, null, null, null, null);
            cursor = db.rawQuery("select * from " + DateBaseUtils.TBL_LOCK_CLICK + " where offer_id = " + offer_id + " and sub_platform_id = " + id, new String[]{});
            if (!cursor.moveToNext()) {

                //                Ulog.w("初始化" + offer_id + "这条数据的本地统计");

                ContentValues contentValues = new ContentValues();

                contentValues.put("sub_platform_id", id);

                contentValues.put("offer_id", offer_id);

                contentValues.put("sub_day_limit_now", 0);

                db.insert(DateBaseUtils.TBL_LOCK_CLICK, null, contentValues);

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
     */
    public static void updataExecuteTime(Context context, Ma offer) {
        //        Ulog.w("更新本地统计次数");
        try {

            StringBuilder strSql = new StringBuilder();
            strSql.append("update " + DateBaseUtils.TBL_LOCK_CLICK);
            strSql.append(" set sub_day_limit_now = sub_day_limit_now + 1 ");
            strSql.append(" where offer_id = ? ");
            strSql.append(" and sub_platform_id = ? ");

            DateBaseUtils.getInstance(context).getDataBase().execSQL(strSql.toString(), new Object[]{offer.getOffer_id(), offer.getSub_platform_id()});

            getOfferExecuteTime(context, offer);

        } catch (Exception e) {
            //            Ulog.w("更新次数错误：" + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void getOfferExecuteTime(Context context, Ma offer) {
        Cursor cursor = null;

        Cursor mcursor = null;

        SQLiteDatabase sqliteDataBase = DateBaseUtils.getInstance(context).getDataBase();

        try {
            cursor = sqliteDataBase.rawQuery("select * from " + DateBaseUtils.TBL_LOCK_CLICK + " where offer_id = " + offer.getOffer_id() + " and sub_platform_id = " + offer.getSub_platform_id(), null);

            if (cursor.moveToNext()) {

                //                Ulog.w("offer:" + s + "的显示次数：" + cursor.getInt(cursor.getColumnIndex("sub_day_limit_now")));
                StringBuilder timeStr = new StringBuilder();
                timeStr.append("p:" + offer.getSub_platform_id());
                timeStr.append("   o:" + cursor.getInt(cursor.getColumnIndex(Ma.OFFER_ID)));
                timeStr.append("   t:" + cursor.getInt(cursor.getColumnIndex(Ma.SUB_DAY_LIMIT_NOW)));
                Ulog.show(timeStr.toString());

                mcursor = sqliteDataBase.rawQuery("select * from " + DateBaseUtils.TBL_OPA + " where offer_id = " + offer.getOffer_id() + " and sub_platform_id = " + offer.getSub_platform_id(), null);
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
    public static void delete_all(Context context) {
        SQLiteDatabase sqliteDataBase = DateBaseUtils.getInstance(context).getDataBase();
        sqliteDataBase.execSQL("delete from " + DateBaseUtils.TBL_OPA);
        delete_local_data(context);
    }

    public static void delete_local_data(Context context) {
        if (checkTimeAboveMonth(context)) {
            SQLiteDatabase sqliteDataBase = DateBaseUtils.getInstance(context).getDataBase();
            sqliteDataBase.execSQL("delete from " + DateBaseUtils.TBL_LOCK_CLICK);
        }
    }

    private static boolean checkTimeAboveMonth(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("_c_month", 0);
        SharedPreferences.Editor editor = preferences.edit();
        int lastMonth = preferences.getInt("asd", 0);
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        editor.putInt("asd", currentMonth);
        editor.apply();
        if (currentMonth != lastMonth) {
            return true;
        }
        return false;
    }

    /***
     * 服务中执行的时候查询
     * @param context
     * @return
     */
    public static List<Ma> getOfferList(Context context, Ma sub) {
        SQLiteDatabase sq = DateBaseUtils.getInstance(context).getDataBase();
        List<Ma> list = new ArrayList<>();
        Cursor mCursor = null;

        try {
            StringBuilder strSql = new StringBuilder();
            strSql.append("select * from ");
            strSql.append(DateBaseUtils.TBL_LOCK_CLICK + "," + DateBaseUtils.TBL_OPA);
            strSql.append(" where tbl_sub.sub_platform_id = tbl_local.sub_platform_id ");
            strSql.append(" and tbl_sub.offer_id = tbl_local.offer_id ");
            strSql.append(" and tbl_sub.sub_day_show_limit > tbl_local.sub_day_limit_now ");
            strSql.append(" and ( tbl_sub.allow_network = " + sub.getAllow_network() + " or tbl_sub.allow_network = 2 ) ");
            strSql.append(" order by tbl_local.sub_day_limit_now asc limit 9");

            mCursor = sq.rawQuery(strSql.toString(), new String[]{});

            while (mCursor.moveToNext()) {
                Ma offer = new Ma(
                        //                        mCursor.getInt(mCursor.getColumnIndex(Ma.ID)),mCursor.getString(mCursor.getColumnIndex(Ma.SUB_LINK_URL)),
                        //                        mCursor.getInt(mCursor.getColumnIndex(Ma.SUB_DAY_SHOW_LIMIT)), mCursor.getInt(mCursor.getColumnIndex(Ma.SUB_PLATFORM_ID)),
                        //                        mCursor.getInt(mCursor.getColumnIndex(Ma.OFFER_ID)),mCursor.getInt(mCursor.getColumnIndex(Ma.DTIME)),
                        //                        mCursor.getInt(mCursor.getColumnIndex(Ma.ALLOW_NETWORK)),mCursor.getInt(mCursor.getColumnIndex(Ma.GETSOURCE)),
                        //                        mCursor.getString(mCursor.getColumnIndex(Ma.TRACK)),mCursor.getInt(mCursor.getColumnIndex(Ma.JRATE))
                );

                offer.setSub_link_url(mCursor.getString(mCursor.getColumnIndex(Ma.SUB_LINK_URL)));
                offer.setAllow_network(mCursor.getInt(mCursor.getColumnIndex(Ma.ALLOW_NETWORK)));
                offer.setDtime(mCursor.getInt(mCursor.getColumnIndex(Ma.DTIME)));
                offer.setOffer_id(mCursor.getInt(mCursor.getColumnIndex(Ma.OFFER_ID)));
                offer.setId(mCursor.getInt(mCursor.getColumnIndex(Ma.ID)));
                offer.setSub_platform_id(mCursor.getInt(mCursor.getColumnIndex(Ma.SUB_PLATFORM_ID)));
                offer.setGetSource(mCursor.getInt(mCursor.getColumnIndex(Ma.GETSOURCE)));
                offer.setTrack(mCursor.getString(mCursor.getColumnIndex(Ma.TRACK)));
                offer.setJRate(mCursor.getInt(mCursor.getColumnIndex(Ma.JRATE)));

                if (offer.getOffer_id() == sub.getOffer_id() && offer.getSub_platform_id() == sub.getSub_platform_id()) {
                    //相同offer，获取下一条
                    continue;
                }

                if (list.size() < 8) {
                    list.add(offer);
                }
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


    public static List<Ma> getNewOfferList(Context context, Ma sub) {
        SQLiteDatabase sq = DateBaseUtils.getInstance(context).getDataBase();
        List<Ma> list = new ArrayList<>();
        try {
            StringBuilder strSql = new StringBuilder();
            strSql.append("select * from ");
            strSql.append(DateBaseUtils.TBL_LOCK_CLICK + "," + DateBaseUtils.TBL_OPA);
            strSql.append(" where tbl_sub.sub_platform_id = tbl_local.sub_platform_id ");
            strSql.append(" and tbl_sub.offer_id = tbl_local.offer_id ");
            strSql.append(" and tbl_sub.sub_day_show_limit > tbl_local.sub_day_limit_now ");
            strSql.append(" and ( tbl_sub.allow_network = " + sub.getAllow_network() + " or tbl_sub.allow_network = 2 ) ");

            StringBuilder highSql = new StringBuilder();
            highSql.append(strSql.toString());
            highSql.append(" and tbl_sub.level = 1 ");
            highSql.append(" order by tbl_local.sub_day_limit_now asc limit 4");
            List<Ma> highList = getList(sq.rawQuery(highSql.toString(), new String[]{}), sub);

            StringBuilder normalSql = new StringBuilder();
            normalSql.append(strSql.toString());
            normalSql.append(" and tbl_sub.level = 2 ");
            normalSql.append(" order by tbl_local.sub_day_limit_now asc limit 8");
            List<Ma> normalList = getList(sq.rawQuery(normalSql.toString(), new String[]{}), sub);

            StringBuilder lowerSql = new StringBuilder();
            lowerSql.append(strSql.toString());
            lowerSql.append(" and tbl_sub.level = 3 ");
            lowerSql.append(" order by tbl_local.sub_day_limit_now asc limit 8");
            List<Ma> lowerList = getList(sq.rawQuery(lowerSql.toString(), new String[]{}), sub);

            list.addAll(highList);

            int normalLen = 8 - highList.size();
            if (lowerList != null && lowerList.size() > 0) {

                int normalTime = -1;
                if (normalList == null || normalList.size() == 0) {
                    normalList = new ArrayList<>();
                    normalLen = 0;

                    if (highList != null && highList.size() != 0) {
                        normalTime = getNormalListTime(highList, highList.size());
                    }
                } else {
                    normalTime = getNormalListTime(normalList, 8 - highList.size() - 1);
                }

                // int lowerTime = lowerList.get(0).getSub_day_limit_now();

                if (normalTime == -1) {
                    return lowerList;
                }

                if (lowerList.get(0).getSub_day_limit_now() < normalTime - 1) {
                    normalLen = 8 - highList.size() - 1;
                } else {
                    lowerList = null;
                }

            }

            normalLen = normalList.size() > normalLen ? normalLen : normalList.size();

            for (int i = 0; i < normalLen; i++) {
                list.add(normalList.get(i));
            }

            if (lowerList != null && lowerList.size() > 0) {
                list.add(lowerList.get(0));
            }

        } catch (Exception e) {
            //            Ulog.w("服务中查询错误：" + e.getMessage());
            e.printStackTrace();
        } finally {

        }

        return list;
    }

    public static int getNormalListTime(List<Ma> list, int org) {
        if (list == null | list.size() == 0 | org == 0) {
            return 0;
        }

        int result = 0;
        int len = list.size() > org ? org : list.size();
        for (int i = 0; i < len; i++) {
            result = result + list.get(i).getSub_day_limit_now();
        }

        return result / len;
    }

    public static List<Ma> getList(Cursor cursor, Ma sub) {
        List<Ma> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            Ma offer = new Ma();

            offer.setSub_link_url(cursor.getString(cursor.getColumnIndex(Ma.SUB_LINK_URL)));
            offer.setAllow_network(cursor.getInt(cursor.getColumnIndex(Ma.ALLOW_NETWORK)));
            offer.setDtime(cursor.getInt(cursor.getColumnIndex(Ma.DTIME)));
            offer.setOffer_id(cursor.getInt(cursor.getColumnIndex(Ma.OFFER_ID)));
            offer.setId(cursor.getInt(cursor.getColumnIndex(Ma.ID)));
            offer.setSub_platform_id(cursor.getInt(cursor.getColumnIndex(Ma.SUB_PLATFORM_ID)));
            offer.setGetSource(cursor.getInt(cursor.getColumnIndex(Ma.GETSOURCE)));
            offer.setTrack(cursor.getString(cursor.getColumnIndex(Ma.TRACK)));
            offer.setJRate(cursor.getInt(cursor.getColumnIndex(Ma.JRATE)));

            if (offer.getOffer_id() == sub.getOffer_id() && offer.getSub_platform_id() == sub.getSub_platform_id()) {
                //相同offer，获取下一条
                continue;
            }
            list.add(offer);
        }

        try {
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
        }

        return list;
    }


    public static Ma get_one_offer(Context context) {

        SQLiteDatabase sqliteDataBase = DateBaseUtils.getInstance(context).getDataBase();

        Cursor mCursor = null;

        Ma offer = null;

        String sql = "select * from " + DateBaseUtils.TBL_OPA + "," + DateBaseUtils.TBL_LOCK_CLICK + " where tbl_sub.offer_id=tbl_local.offer_id and " + "tbl_sub.sub_platform_id=tbl_local.sub_platform_id and tbl_sub.sub_day_show_limit>tbl_local.sub_day_limit_now " + "order by tbl_local.sub_day_limit_now asc";

        try {
            mCursor = sqliteDataBase.rawQuery(sql, null);

            if (mCursor.moveToNext()) {

                //                Ulog.w("查询一条数据");

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

            //            Ulog.w("服务中查询错误：" + e.getMessage());
            e.printStackTrace();
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
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
        //        Ulog.w("服务器设置的追踪track:" + offer.getTrack());
        String cid = PhoneInforUtils.getInstance(context).getKeyStore();
        String keyStore = isService ? "S" + cid : "A" + cid;
        return offer.getSub_link_url() + String.format(offer.getTrack(), keyStore);
    }

    public static boolean check_url(String url) {

        ArrayList<String> blackUrl = new ArrayList<>();
        //        blackUrl.add("http://45.79.78.178");
        //blackUrl.add("DC7880AB1E12F9A1B1DD48623F490D8D8C0516B862894D3DD9CD4ADB2A7BDBB4");
        //blackUrl.add("aHR0cDovLzQ1Ljc5Ljc4LjE3OA==");
        //blackUrl.add("3HiAqx4S+aGx3UhiP0kNjYwFFrhiiU092c1K2yp727Q=");
        blackUrl.add("tX3sgkDyfw3OLz/wZMXaH1FUJ6djieAudkhGBUbbTTE=");

        //        blackUrl.add("http://ad.m2888.net");
        //blackUrl.add("58106AF96F83ECCED203ACEAE241B53801195607BE65E5C8E17BC8F1CAB9A13C");
        //blackUrl.add("aHR0cDovLzQ1Ljc5Ljc4LjE3OA==");
        //blackUrl.add("WBBq+W+D7M7SA6zq4kG1OAEZVge+ZeXI4XvI8cq5oTw=");
        blackUrl.add("n29Xu0uGchC/gONZLsbBSJ1CjIfMHvr1HIHEMsfKito=");

        //        blackUrl.add("http://pic.m2888.net");
        //blackUrl.add("77A0BE43BB13B01891DC520F32603F874B73928CDA99232C32004339791B96E9");
        //blackUrl.add("aHR0cDovLzQ1Ljc5Ljc4LjE3OA==");
        //blackUrl.add("d6C+Q7sTsBiR3FIPMmA/h0tzkozamSMsMgBDOXkbluk=");
        blackUrl.add("V8s9h6mlGcRdAqUEvvRgNaCh8w2vs77IQ+uRFDUxWeU=");

        for (int i = 0; i < blackUrl.size(); i++) {
            //            if (url.contains(blackUrl.get(i)) {
            //            if (url.contains(AESUtils.decode(blackUrl.get(i)))) {
            //            if (url.contains(new String(Base64.decode(blackUrl.get(i).getBytes(),Base64.DEFAULT)))) {
            //            if (url.contains(H_encode.decrypt(blackUrl.get(i), AESUtils.keyBytes))) {//秘匙：abcdefgabcdefg12
            if (url.contains(EncodeUtils.deCrypt(blackUrl.get(i), EncodeUtils.keyBytes))) {//秘匙：qsedfgzogn56sd16
                return true;
            }
        }
        return false;
    }
}