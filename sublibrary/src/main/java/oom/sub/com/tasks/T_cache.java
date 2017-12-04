package oom.sub.com.tasks;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import oom.sub.com.http.H_okhttp;
import oom.sub.com.s.S_service;
import oom.sub.com.utils.UParams;
import oom.sub.com.utils.Ujs;
import oom.sub.com.utils.Ulog;
import oom.sub.com.utils.Ut;
import oom.sub.com.utils.UtilSave;

/**
 * Created by xlc on 2017/5/24.
 */

public class T_cache extends AsyncTask<Void, Integer, Boolean> {

    private S_service aservice;

    public int getCache_status() {
        return cache_status;
    }

    public void setCache_status(int cache_status) {
        this.cache_status = cache_status;
    }

    private int cache_status = CH_CACHE_STATUS_START;

    public static final int CH_CACHE_STATUS_DOING = -1;

    public static final int CH_CACHE_STATUS_SUCCESS = -2;

    public static final int CH_CACHE_STATUS_START = -4;

    private int is_b_list = 0;

    public T_cache(S_service aservice) {
        setCache_status(CH_CACHE_STATUS_START);
        this.aservice = aservice;
        SharedPreferences sp = aservice.getSharedPreferences("black_list", 0);
        is_b_list = sp.getInt("b_l", 0);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean cache_success = false;
        setCache_status(CH_CACHE_STATUS_DOING);
        try {
            String datas = H_okhttp.getCaseData(aservice, UParams.getInstance(aservice).getHashMap());
//                        Ulog.show("CacheTask data:" + datas);
            if (!TextUtils.isEmpty(datas)) {
                JSONObject jsonObject = new JSONObject(datas);
                String status = jsonObject.getString("status");

                //黑名单
                if (Integer.parseInt(status) == -1) {
                    //                                        Ulog.w("黑名单 记录时间，满足下次间隔缓存时间后再次判断");
                    //                                        Ulog.show("black list save time");
                    Ulog.show("b l");
                    is_b_list = -1;
                    //清空所有数据
                    UtilSave.delete_all(aservice);
                    Ut.save_status(aservice);
                    return false;
                }

                is_b_list = 1;
                //                                Ulog.w("CacheTask status: 请求状态" + status);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                //                Ulog.show("数据：" + jsonArray);
                //保存缓存状态
                if (jsonArray.length() > 0) {
                    //清空所有数据
                    UtilSave.delete_all(aservice);

                    Ulog.show("c success");
                    //                                        Ulog.w("缓存成功");

                    cache_success = true;
                    Ut.save_status(aservice);
                }

                //保存数据
                UtilSave.save(jsonArray, aservice);
            }
        } catch (Exception e) {
            // Ulog.show("cache error");
            // Ulog.w("缓存操作出错：" + e.getMessage());
            e.printStackTrace();
        }
        return cache_success;
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if ( Ujs.getInstance(aservice).check_d_js_time() && aBoolean) {
            if ( Ujs.getInstance(aservice).getJsCacheStatus() != Ujs.JS_CACHE_STATUS_DOING) {
                //                                Ulog.w("缓存offer结束后，满足下载js文件条件");
                //                                                Ulog.show("after cache，do download js");
                Ulog.show("d j");
                new T_djs(aservice).executeOnExecutor(H_okhttp.executorService);
            }
        }

        Ut.save_b_list(aservice, is_b_list);
        aservice.check_black_list();
    }
}