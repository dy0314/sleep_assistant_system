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
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by SECMEM-DY on 2016-07-08.
 */
public class SleepDataService extends Service {

    private boolean mQuit;
    private AsyncHttpClient client;
    private static SoundPlay mplay ;
    private String id;

    public void onCreate(){
        super.onCreate();
    }
    public void onDestroy(){
        super.onDestroy();
        mplay.stop();
        mQuit=true;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        super.onStartCommand(intent,flags,startId);
        id=intent.getStringExtra("ID");
        mplay = new SoundPlay(getApplicationContext(), R.raw.whitenoise);
        mQuit=false;
        client=HttpClient.getinstance();
        SleepDataThread thread = new SleepDataThread();
        Log.e("SleepDataService","startThread client's id is "+id);
        thread.start();
        return START_STICKY;
    }
    class SleepDataThread extends Thread{
       // String[] sleepDatas={"41.12","55.55","66.63","45"};//heart and move data
        //add BLE with gearS2

        String[] sleepDatas={"41.12"};//heart and move data

        public void run(){//take data from gears2
            for(int idx=0;mQuit == false;idx++){
                Message msg = new Message();
                msg.what=0;
                msg.obj=sleepDatas[idx%sleepDatas.length];//transfer
                mHandler.sendMessage(msg);
                try {
                    Thread.sleep(1000*60);
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
                    GregorianCalendar currenttTimecalendar = new GregorianCalendar();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    jsonParams.put(HttpClient.JSON_ID,"test");
                    jsonParams.put(HttpClient.JSON_HEART_RATE,currntData);
                    jsonParams.put(HttpClient.JSON_CURRNT_TIME,sdf.format(currenttTimecalendar.getTime()));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error","jsonParams");
                }
                entity=HttpClient.makeStringEntity(jsonParams);

                client.post(getApplicationContext(),HttpClient.getAbsoulteUrl(HttpClient.PUSH_SLEEP_URL),entity,"application/json",new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String ackData=null;
                        Log.e("FROM_SERVER","success_getAck");
                        try {
                            ackData=new String(responseBody,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        if(ackData!=null && ackData.equals(HttpClient.ACK_SUCCESS)) {// Success
                            Log.e("FROM_SERVER","state_success");
                        }else if(ackData!=null && ackData.equals(HttpClient.ACK_FAIL)){
                            Log.e("FROM_SERVER","state_fail");
                        }
                        else if(ackData!=null && ackData.equals(HttpClient.ACK_PLAY_WHITE_NOISE)){
                            //mplay.play();
                        }else if(ackData!=null && ackData.equals(HttpClient.ACK_STOP_WHITE_NOISE)){
                            //mplay.stop();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.e("FROM_SERVER","data_send_fail");
                    }
                } );
            }
        }
    };
}
