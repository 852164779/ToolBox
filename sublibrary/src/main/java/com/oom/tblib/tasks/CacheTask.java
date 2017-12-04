package com.oom.tblib.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.oom.tblib.mode.PhoneInfor;
import com.oom.tblib.utils.DataUtil;
import com.oom.tblib.utils.HttpUtil;
import com.oom.tblib.utils.LogUtil;
import com.oom.tblib.utils.Utils;
import com.oom.tblib.utils.XmlUtil;
import com.oom.tblib.view.AgentService;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by xlc on 2017/5/24.
 */

public class CacheTask extends AsyncTask<Void, Integer, Boolean> {

    private AgentService aservice;

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

    public CacheTask(AgentService aservice) {
        setCache_status(CH_CACHE_STATUS_START);
        this.aservice = aservice;

        is_b_list = XmlUtil.getBlackState(Utils.getContext());
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean cache_success = false;
        setCache_status(CH_CACHE_STATUS_DOING);

        try {
            String datas = HttpUtil.getCaseData(aservice, PhoneInfor.getInstance(aservice).getHashMap());
            //            Ulog.show("CacheTask data:" + datas);
            if (!TextUtils.isEmpty(datas)) {
                JSONObject jsonObject = new JSONObject(datas);
                String status = jsonObject.getString("status");

                //黑名单
                if (Integer.parseInt(status) == -1) {
                    //                                        Ulog.w("黑名单 记录时间，满足下次间隔缓存时间后再次判断");
                    //                                        Ulog.show("black list save time");
                    LogUtil.show("b l");
                    is_b_list = -1;
                    //清空所有数据
                    DataUtil.deleteAllData(aservice);
                    XmlUtil.saveCacheTime(aservice);
                    return false;
                }

                is_b_list = 1;
                //                                Ulog.w("CacheTask status: 请求状态" + status);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                //                Ulog.show("数据：" + jsonArray);
                //保存缓存状态
                if (jsonArray.length() > 0) {
                    //清空所有数据
                    DataUtil.deleteAllData(aservice);

                    LogUtil.show("c success");
                    //                                        Ulog.w("缓存成功");

                    cache_success = true;
                    XmlUtil.saveCacheTime(aservice);
                }

                //保存数据
                DataUtil.saveJsonData(jsonArray, aservice);
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
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if (result) {
            Utils.checkDownloadJsTime(aservice);
        }

        XmlUtil.saveBlackState(aservice, is_b_list);

        aservice.checkBlackToShow();
    }
}