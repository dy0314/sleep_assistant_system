package com.example.secmem_dy.sleep_assistant;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by SECMEM-DY on 2016-07-05.
 */
public class HttpClient {
    private static final String SERVER_URL="http://211.189.20.136:3000/test";
    private static AsyncHttpClient client = new AsyncHttpClient();
    public static AsyncHttpClient getinstance(){
        return HttpClient.client;
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler reponseHandler) {
        client.get(getAbsoulteUrl(url), params, reponseHandler);
    }
    public static void post(String url, RequestParams params, AsyncHttpResponseHandler reponseHandler) {
        client.post(getAbsoulteUrl(url), params, reponseHandler);
    }
    private static String getAbsoulteUrl(String relativeUrl) {
        return SERVER_URL + relativeUrl;
    }
}
