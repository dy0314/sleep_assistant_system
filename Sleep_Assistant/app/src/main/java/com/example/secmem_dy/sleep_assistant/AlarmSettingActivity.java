package com.example.secmem_dy.sleep_assistant;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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

/**
 * Created by SECMEM-DY on 2016-07-07.
 */
public class AlarmSettingActivity extends Activity implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    // 알람 메니저
    private AlarmManager mManager;
    private AlarmManager postManager;
    // 설정 일시
    private GregorianCalendar mCalendar;
    //일자 설정 클래스
    private DatePicker mDate;
    //시작 설정 클래스
    private TimePicker mTime;
    private PendingIntent sender;
    private PendingIntent preSender;

    private Button startButton;
    private Button cancelButton;
    private Button showButton;
    private AsyncHttpClient client;

    private long startMiliTime;
    private long endMiliTime;
    private String id;


    private ConsumerService mConsumerService = null;
    private boolean mIsBound = false;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mConsumerService = ((ConsumerService.LocalBinder) service).getService();
            Toast.makeText(getApplicationContext(),"onServiceConnected",Toast.LENGTH_SHORT).show();
            //updateTextView("onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mConsumerService = null;
            mIsBound = false;
            Toast.makeText(getApplicationContext(),"onServiceDisconnected",Toast.LENGTH_SHORT).show();
            //updateTextView("onServiceDisconnected");
        }
    };


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent =getIntent();
        id=intent.getStringExtra("ID");

        client =HttpClient.getinstance();
        //알람 매니저를 취득
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        postManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mCalendar = new GregorianCalendar();

        setContentView(R.layout.alarm);
        showButton=(Button)findViewById(R.id.show);
        showButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(AlarmSettingActivity.this,ShowDataActivity.class);
                intent.putExtra("ID",id);
                startActivity(intent);
            }
        });
        startButton = (Button)findViewById(R.id.sleepmode);
        startButton.setOnClickListener (new View.OnClickListener() {
            public void onClick (View v) {
                setAlarm();
            }
        });
        cancelButton = (Button)findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancelAlarm();
            }
        });
        startButton.setEnabled(true);
        cancelButton.setEnabled(false);

        //시간 설정
        mDate = (DatePicker)findViewById(R.id.date_picker);
        mDate.init (mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);

        mTime = (TimePicker)findViewById(R.id.time_picker);
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTime.setOnTimeChangedListener(this);

        // Bind service 주로 서비스가 앱 내에서 백그라운드 작업을 담당하는 경우에 선호
        intent=new Intent(getApplicationContext(),ConsumerService.class);
        intent.putExtra("ID",id);
        mIsBound = bindService(new Intent(AlarmSettingActivity.this, ConsumerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        // Clean up connections
        if (mIsBound == true && mConsumerService != null) {
            if (mConsumerService.closeConnection() == false) {

            }
        }
        // Un-bind service
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
        super.onDestroy();
    }

    public void mOnClick(View v) {
        switch (v.getId()) {
           /* case R.id.buttonConnect: {
                if (mIsBound == true && mConsumerService != null) {
                    Toast.makeText(getApplicationContext(), "connect request", Toast.LENGTH_LONG).show();
                    mConsumerService.findPeers();
                }
                break;
            }
            case R.id.buttonDisconnect: {
                if (mIsBound == true && mConsumerService != null) {
                    if (mConsumerService.closeConnection() == false) {
                       // updateTextView("Disconnected");
                        Toast.makeText(getApplicationContext(), R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
                       // mMessageAdapter.clear();
                    }
                }
                break;
            }*/
            case R.id.buttonSend: {
                if (mIsBound == true && mConsumerService != null) {
                    if (mConsumerService.sendData("Hello Accessory!")) {
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }
            default:
        }
    }

    public void setAllButton(boolean state){
        startButton.setEnabled(!state);
        cancelButton.setEnabled(state);
        mDate.setEnabled(!state);
        mTime.setEnabled(!state);
    }
    //알람의 설정
    private void setAlarm() {
        GregorianCalendar startTimecalendar = new GregorianCalendar();
        SimpleDateFormat   sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        startMiliTime=startTimecalendar.getTimeInMillis();
        endMiliTime=mCalendar.getTimeInMillis();
        if(startMiliTime>=endMiliTime){
            Toast.makeText(getApplicationContext(),"알람을 설정할 수 없습니다.",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i("TimeSet", startMiliTime+"= start time"+sdf.format(startTimecalendar.getTime()));//time start(currnt) setted
        Log.i("TimeSet", endMiliTime+"= end time"+sdf.format(mCalendar.getTime()));//time end setted

        StringEntity entity=null;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put(HttpClient.JSON_START_TIME,sdf.format(startTimecalendar.getTime()));//set "yyyy-MM-dd HH:mm:ss" format
            jsonParams.put(HttpClient.JSON_END_TIME,sdf.format(mCalendar.getTime()));
            jsonParams.put(HttpClient.JSON_ID,id);//수정해야함 진짜 id로
            Log.e("FROM_SERVER","send ID is" + id);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Error","jsonParams");
        }
        entity=HttpClient.makeStringEntity(jsonParams);

        client.post(getApplicationContext(),HttpClient.getAbsoulteUrl(HttpClient.START_SLEEP_URL),entity,"application/json",new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String ackData=null;
                try {
                    ackData=new String(responseBody,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Log.e("FROM_SERVER","ackData:"+ackData);
                if(ackData!=null && ackData.equals(HttpClient.ACK_SUCCESS)) {//Alarm Request Success
                    Intent intent = new Intent(AlarmSettingActivity.this, AlarmReceiver.class);
                    sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);//request code를 다르게 설정해야 모든 알람이 정상적으로 울린다.
                    preSender = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);

                    mManager.set(AlarmManager.RTC_WAKEUP, endMiliTime, sender);
                    if(endMiliTime>=startMiliTime+1000*60*30)
                        postManager.set(AlarmManager.RTC_WAKEUP,endMiliTime-1000*60*30, preSender);//30분 전 울릴 알람을 등록
                    else
                        Toast.makeText(getApplicationContext(),"can't set 30min post Alarm",Toast.LENGTH_SHORT).show();

                    Log.e("FROM_SERVER","set_alarm_succes");

                    setAllButton(true);
                    if (mIsBound == true && mConsumerService != null) {
                        Toast.makeText(getApplicationContext(), "connect request", Toast.LENGTH_LONG).show();
                        mConsumerService.findPeers();
                    }

                    //start Bluetooth service
                }else if(ackData!=null && ackData.equals(HttpClient.ACK_FAIL))
                    Toast.makeText(getApplicationContext(),"Time Set Failed",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("FROM_SERVER","set_alarm_fail");
            }
        } );
    }
    //알람의 해제
    private void cancelAlarm() {
        if (mIsBound == true && mConsumerService != null) {
            if (mConsumerService.closeConnection() == false) {
                // updateTextView("Disconnected");
                Toast.makeText(getApplicationContext(), R.string.ConnectionAlreadyDisconnected, Toast.LENGTH_LONG).show();
                // mMessageAdapter.clear();
            }
        }

        StringEntity entity=null;
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put(HttpClient.JSON_CANCEL,id);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Error","jsonParams");
        }
        entity=HttpClient.makeStringEntity(jsonParams);

        client.post(getApplicationContext(),HttpClient.getAbsoulteUrl(HttpClient.CANCEL_SLEEP_URL),entity,"application/json",new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String ackData=null;
                try {
                    ackData=new String(responseBody,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(ackData!=null && ackData.equals(HttpClient.ACK_SUCCESS)) {//cancel Success
                    mManager.cancel(sender);
                    mManager.cancel(preSender);
                    Log.e("FROM_SERVER","success_cancel");
                    Intent intent=new Intent(getApplicationContext(),SleepDataService.class);
                    stopService(intent);

                    setAllButton(false);
                }else if(ackData!=null && ackData.equals(HttpClient.ACK_FAIL))
                    Toast.makeText(getApplicationContext(),"fail_cancel",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("FROM_SERVER","fail_cancel2");
            }
        } );
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