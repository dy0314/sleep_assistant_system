package com.example.secmem_dy.sleep_assistant;

/**
 * Created by SECMEM-DY on 2016-07-04.
 */import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

public class SoundPlay {
    MediaPlayer mp = null;

    public SoundPlay ( Context context, int id ) {
        mp = MediaPlayer.create(context, id);
    }
    public void play() {
        Log.e("SoundPlay","play");
        mp.seekTo(0);
        mp.start();
    }
    public void stop(){
        Log.e("SoundPlay","stop");
        mp.stop();
        mp.release();
    }
}
