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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

       // mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
       // mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
       // mTime.setOnTimeChangedListener(this);

    }

    //알람의 설정
    private void setAlarm() {
        Intent intent=new Intent(MainActivity.this,AlarmReceiver.class);
        sender=PendingIntent.getBroadcast(this,0,intent,0);
        Calendar calendar  =Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND,5);
        mManager.set(AlarmManager.RTC,calendar.getTimeInMillis(),sender);
//        mManager.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), sender);
        Log.i("HelloAlarmActivity", calendar.getTime().toString());
    }

    //알람의 해제
    private void resetAlarm() {
//      mManager.cancel(pendingIntent());
        mManager.cancel(sender);
    }
    //알람의 설정 시각에 발생하는 인텐트 작성
   /* private PendingIntent pendingIntent() {
        Toast.makeText(getApplicationContext(), "토스트메시지입니다.", Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(), Pending.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        return pi;
    }*/
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