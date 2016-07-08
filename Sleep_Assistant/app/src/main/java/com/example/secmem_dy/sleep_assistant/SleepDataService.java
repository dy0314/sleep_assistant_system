package com.example.secmem_dy.sleep_assistant;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import android.os.Handler;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by SECMEM-DY on 2016-07-08.
 */
public class SleepDataService extends Service {
    private boolean mQuit;
    private AsyncHttpClient client;
    public void onCreate(){
        super.onCreate();
    }
    public void onDestroy(){
        super.onDestroy();
        mQuit=true;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        super.onStartCommand(intent,flags,startId);
        mQuit=false;
        client=HttpClient.getinstance();
        SleepDataThread thread = new SleepDataThread();
        Log.e("SleepDataService","startThread");
        thread.start();
        return START_STICKY;
    }
    class SleepDataThread extends Thread{
        SleepDataService mParent;
        //Handler mHandler;
        String[] sleepDatas={"41.12","55.55","66.63","45"};

        public void run(){//take data from gears2
            for(int idx=0;mQuit == false;idx++){
                Message msg = new Message();
                msg.what=0;
                msg.obj=sleepDatas[idx%sleepDatas.length];
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {//send to server
            if(msg.what==0){
                String currntData=(String)msg.obj;
                Log.e("SEND_SERVER",currntData);

                StringEntity entity=null;
                JSONObject jsonParams = new JSONObject();
                try {
                    jsonParams.put(HttpClient.JSON_HEART_RATE,currntData);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error","jsonParams");
                }
                try {
                    entity = new StringEntity(jsonParams.toString());
                } catch (UnsupportedEncodingException e) {
                    Log.e("Error","StringEntity");
                    e.printStackTrace();
                }

                client.post(getApplicationContext(),HttpClient.getAbsoulteUrl(HttpClient.SLEEP_DATA_URL),entity,"application/json",new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String ackData=null;
                        Log.e("FROM_SERVER","success");
                        try {
                            ackData=new String(responseBody,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if(ackData!=null && ackData.equals(HttpClient.ACK_SUCCESS)) {//cancel Success
                            Log.e("FROM_SERVER","ok");
                        }else if(ackData!=null && ackData.equals(HttpClient.ACK_FAIL))
                            Toast.makeText(getApplicationContext(),"Cancel Failed",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("FROM_SERVER","fail");
                    }
                } );
            }
        }
    };
}
