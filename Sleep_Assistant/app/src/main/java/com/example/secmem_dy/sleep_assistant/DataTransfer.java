package com.example.secmem_dy.sleep_assistant;
import android.content.Context;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by SECMEM-DY on 2016-07-07.
 */
public class DataTransfer {
    public final String  TAG="FROM_SERVER";
    public boolean res;
    public boolean transfer(AsyncHttpClient client,Context context, StringEntity entity ,String requestURL){
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
        client.post(context,HttpClient.getAbsoulteUrl(requestURL),entity,"application/json",new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String ackData=null;
                Log.e(TAG,"success");
                try {
                    ackData=new String(responseBody,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(ackData!=null && ackData.equals("1")) {//Login Success
                    Log.e(TAG, "ok");
                    res=true;
                }
                else
                    res=false;
                  //  Toast.makeText(context,"Time Set Failed",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG,"fail");
            }
        } );

        if(res)
            Log.e(TAG,"-true");
        else
            Log.e(TAG,"-false");
        return res;
    }
    public boolean returnState(){
        return res;
    }

}
