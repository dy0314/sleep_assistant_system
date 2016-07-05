package com.example.secmem_dy.sleep_assistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by SECMEM-DY on 2016-07-04.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String	TAG_LOG	= "AlarmReceiver";

    private static SoundPlay mplay = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Receiver",Toast.LENGTH_LONG).show();
        try {
            if(mplay == null) {
                mplay = new SoundPlay(context, R.raw.alarm);
            }
            mplay.play();
        }catch( Exception e ) {
            Log.i("TAG_LOG","sound play error");
        }
    }
}
