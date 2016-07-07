package com.example.secmem_dy.sleep_assistant;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by SECMEM-DY on 2016-07-05.
 */
public class HttpClient {
    public static final String SERVER_URL="http://211.189.20.136:3000/test";
    private static AsyncHttpClient client = new AsyncHttpClient();
    public static AsyncHttpClient getinstance(){
        return HttpClient.client;
    }

    public static String getAbsoulteUrl(String relativeUrl) {
        return SERVER_URL + relativeUrl;
    }
}
