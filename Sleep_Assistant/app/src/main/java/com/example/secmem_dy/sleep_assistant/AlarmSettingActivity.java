package com.example.secmem_dy.sleep_assistant;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by SECMEM-DY on 2016-07-07.
 */
public class AlarmSettingActivity extends Activity implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {

    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private GregorianCalendar mCalendar;
    //일자 설정 클래스
    private DatePicker mDate;
    //시작 설정 클래스
    private TimePicker mTime;
    private PendingIntent sender;
    private Button startButton;
    private Button cancelButton;

    /*
     * 통지 관련 맴버 변수
     */
    private AsyncHttpClient client;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client =HttpClient.getinstance();
        //알람 매니저를 취득
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mCalendar = new GregorianCalendar();

        //셋 버튼, 리셋버튼의 리스너를 등록
        setContentView(R.layout.alarm);
        startButton = (Button)findViewById(R.id.sleepmode);
        startButton.setOnClickListener (new View.OnClickListener() {
            public void onClick (View v) {
                setAlarm();
                startButton.setEnabled(false);
                cancelButton.setEnabled(true);
            }
        });
        cancelButton = (Button)findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetAlarm();
                startButton.setEnabled(true);
                cancelButton.setEnabled(false);
            }
        });
        startButton.setEnabled(true);
        cancelButton.setEnabled(false);

        mDate = (DatePicker)findViewById(R.id.date_picker);
        mDate.init (mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
        mTime = (TimePicker)findViewById(R.id.time_picker);
        //일시 설정 클래스로 현재 시각을 설정
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTime.setOnTimeChangedListener(this);

    }

    //알람의 설정
    private void setAlarm() {
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        Log.i("HelloAlarmActivity", sdf.format(calendar.getTime()));//time start(currnt) setted
        Log.i("HelloAlarmActivity", sdf.format(mCalendar.getTime()));//time end setted

        StringEntity entity=null;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("starttime",sdf.format(calendar.getTime()));
            jsonParams.put("endtime",sdf.format(mCalendar.getTime()));
            jsonParams.put("id","tester");
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

        DataTransfer mdatatransfer=new DataTransfer();
        if(mdatatransfer.transfer(client,getApplicationContext(),entity,HttpClient.START_SLEEP_URL)){
        //    Toast.makeText(getApplicationContext(),"Time Set Success",Toast.LENGTH_SHORT).show();
        }else
         //   Toast.makeText(getApplicationContext(),"Time Set Failed",Toast.LENGTH_SHORT).show();

        if(mdatatransfer.returnState())
            Toast.makeText(getApplicationContext(),"Time Set Success!!",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(),"Time Set Failed!!",Toast.LENGTH_SHORT).show();
        /*client.post(getApplicationContext(),HttpClient.getAbsoulteUrl(HttpClient.START_SLEEP_URL),entity,"application/json",new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String ackData=null;
                Log.e("FROM_SERVER","success");
                try {
                    ackData=new String(responseBody,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(ackData!=null && ackData.equals("1")) {//Login Success
                    Log.e("FROM_SERVER","ok");
                }else
                    Toast.makeText(getApplicationContext(),"Time Set Failed",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("FROM_SERVER","fail");
            }
        } );
*/
        Intent intent=new Intent(AlarmSettingActivity.this,AlarmReceiver.class);
        sender=PendingIntent.getBroadcast(this,0,intent,0);
        mManager.set(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis(),sender);
        mManager.setRepeating(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis(),1000*2,sender);
    }

    //알람의 해제
    private void resetAlarm() {
        mManager.cancel(sender);
        StringEntity entity=null;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("cancel","tester");
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
        DataTransfer mdatatransfer=new DataTransfer();
        if(mdatatransfer.transfer(client,getApplicationContext(),entity,HttpClient.CANCEL_SLEEP_URL)){
         //   Toast.makeText(getApplicationContext(),"Time Set Success",Toast.LENGTH_SHORT).show();
        }
        //else
          //  Toast.makeText(getApplicationContext(),"Time Set Failed",Toast.LENGTH_SHORT).show();

        /*client.post(getApplicationContext(),HttpClient.getAbsoulteUrl(HttpClient.CANCEL_SLEEP_URL),entity,"application/json",new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String ackData=null;
                Log.e("FROM_SERVER","success");
                try {
                    ackData=new String(responseBody,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(ackData!=null && ackData.equals("1")) {//cancel Success
                    Log.e("FROM_SERVER","ok");
                }else
                    Toast.makeText(getApplicationContext(),"Cancel Failed",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("FROM_SERVER","fail");
            }
        } );*/
    }

    //일자 설정 클래스의 상태변화 리스너
    public void onDateChanged (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set (year, monthOfYear, dayOfMonth, mTime.getCurrentHour(), mTime.getCurrentMinute());
    }
    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged (TimePicker view, int hourOfDay, int minute) {
        mCalendar.set (mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), hourOfDay, minute);
    }
}