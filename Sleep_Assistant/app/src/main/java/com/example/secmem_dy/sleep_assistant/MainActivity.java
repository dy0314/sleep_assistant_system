package com.example.secmem_dy.sleep_assistant;

import java.util.Calendar;
import java.util.GregorianCalendar;
import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends Activity implements OnDateChangedListener, OnTimeChangedListener {

    // 알람 메니저
    private AlarmManager mManager;
    // 설정 일시
    private GregorianCalendar mCalendar;
    //일자 설정 클래스
    private DatePicker mDate;
    //시작 설정 클래스
    private TimePicker mTime;
    private PendingIntent sender;

    /*
     * 통지 관련 맴버 변수
     */
    private NotificationManager mNotification;
    private AsyncHttpClient client;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client =HttpClient.getinstance();
        //알람 매니저를 취득
        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //현재 시각을 취득
        mCalendar = new GregorianCalendar();

        //셋 버튼, 리셋버튼의 리스너를 등록
        setContentView(R.layout.activity_main);
        Button set_button = (Button)findViewById(R.id.set);
        set_button.setOnClickListener (new View.OnClickListener() {
            public void onClick (View v) {
                setAlarm();
            }
        });
        Button stop_button;
        stop_button = (Button)findViewById(R.id.reset);
        stop_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                resetAlarm();
            }
        });

        //일시 설정 클래스로 현재 시각을 설정
        mDate = (DatePicker)findViewById(R.id.date_picker);
        mDate.init (mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
        mTime = (TimePicker)findViewById(R.id.time_picker);

        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTime.setOnTimeChangedListener(this);
    }

    //알람의 설정
    private void setAlarm() {

        RequestParams params = new RequestParams();
        params.put("id","test");
        params.put("pwd","testpwd");
        HttpClient.get("", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.i("Node","success");
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.i("Node","fail");
            }
        } );

        Intent intent=new Intent(MainActivity.this,AlarmReceiver.class);
        sender=PendingIntent.getBroadcast(this,0,intent,0);
        //  Calendar calendar  =Calendar.getInstance();
        //  calendar.setTimeInMillis(System.currentTimeMillis());
        //  calendar.add(Calendar.SECOND,5);
        mManager.set(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis(),sender);
        mManager.setRepeating(AlarmManager.RTC_WAKEUP,mCalendar.getTimeInMillis(),1000*2,sender);
//        mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), sender);
        Log.i("HelloAlarmActivity", mCalendar.getTime().toString());
    }

    //알람의 해제
    private void resetAlarm() {
        /*Intent intentstop = new Intent(this, AlarmReceiver.class);
        PendingIntent senderstop = PendingIntent.getBroadcast(this,
                1234567, intentstop, 0);
        AlarmManager alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManagerstop.cancel(senderstop);*/
        mManager.cancel(sender);
    }
    //일자 설정 클래스의 상태변화 리스너
    public void onDateChanged (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set (year, monthOfYear, dayOfMonth, mTime.getCurrentHour(), mTime.getCurrentMinute());
        // Log.i("HelloAlarmActivity", mCalendar.getTime().toString());
    }
    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged (TimePicker view, int hourOfDay, int minute) {
        mCalendar.set (mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), hourOfDay, minute);
        //  Log.i("HelloAlarmActivity",mCalendar.getTime().toString());
    }
}