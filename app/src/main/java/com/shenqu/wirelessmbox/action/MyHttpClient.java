package com.shenqu.wirelessmbox.action;

import com.shenqu.wirelessmbox.tools.JLLog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by JongLim on 2016/11/24.
 */
class MyHttpClient {
    private static final String TAG = MyHttpClient.class.getSimpleName();
    private static final OkHttpClient client = new OkHttpClient();

//    /**
//     * HttpClient 同步的Post请求
//     *
//     * @param url 接口地址
//     * @param params NameValuePair数组，用于存储欲传送的参数
//     * @return
//     */
//    static String post(String url, List<NameValuePair> params) {
//        String result = null;
//        if (url != null && !params.isEmpty()) {
//            try {
//                DefaultHttpClient client = new DefaultHttpClient();
//                HttpPost httpPost = new HttpPost(url);
//                httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));//设置编码
//
//                long startTime = System.currentTimeMillis();
//                HttpResponse response = client.execute(httpPost);
//                JLLog.LOGD(TAG, "( " + (System.currentTimeMillis() - startTime) + "ms ) " + params.get(1).toString());
//
//                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                    JLLog.LOGE(TAG, "Method failed:" + response.getStatusLine());
//                    result = EntityUtils.toString(response.getEntity());
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }

    /**
     * okhttp3  同步的Post请求
     *
     * @param url
     * @param params post的参数
     * @return
     */
    static String post(String url, HashMap<String, String> params){
        String result = null;

        //初始化， 创建requestBody，即参数列表
        if(params != null && params.size() > 0) {

            FormBody.Builder builder = new FormBody.Builder();
            for (Object o : params.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                if (entry.getValue() != null) {
                    builder.add((String) entry.getKey(), (String) entry.getValue());
                }
            }
            RequestBody requestBody = builder.build();
            Request request = new Request.Builder().url(url).post(requestBody).build();

            //执行post请求
            long startTime = System.currentTimeMillis();
            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //JLLog.LOGI(TAG, "( " + (System.currentTimeMillis() - startTime) + "ms ) " + params.get("JSONREQ"));
        }

        return result;
    }
}