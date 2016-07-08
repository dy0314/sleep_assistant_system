package com.example.secmem_dy.sleep_assistant;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by SECMEM-DY on 2016-07-08.
 */
public class SleepDataController extends Service {
    public void onCreate(){
        super.onCreate();;
    }
    public void onDestroy(){
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
