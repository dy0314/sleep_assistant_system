package com.example.secmem_dy.sleep_assistant;

import com.loopj.android.http.AsyncHttpClient;
/**
 * Created by SECMEM-DY on 2016-07-05.
 */
public class HttpClient {
    public static final String SERVER_URL="http://211.189.20.136:3000/";
    public static final String LOGIN_URL="login";
    public static final String START_SLEEP_URL="startSleep";
    public static final String CANCEL_SLEEP_URL="cancelSleep";
    public static final String SLEEP_DATA_URL="pushSleep";

    public static final String JSON_START_TIME="starttime";
    public static final String JSON_END_TIME="endtime";
    public static final String JSON_CANCEL="cancel";
    public static final String JSON_ID="id";
    public static final String JSON_PWD="pwd";
    public static final String JSON_HEART_RATE="heartRate";
    public static final String JSON_MOVE="move";
    public static final String JSON_CURRNT_TIME="currnttime";

    public static final String ACK_SUCCESS="1";
    public static final String ACK_FAIL="2";

    private static AsyncHttpClient client = new AsyncHttpClient();
    public static AsyncHttpClient getinstance(){
        return HttpClient.client;
    }
    public static String getAbsoulteUrl(String relativeUrl) {
        return SERVER_URL + relativeUrl;
    }
}
