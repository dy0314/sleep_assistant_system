package com.example.secmem_dy.sleep_assistant;

/**
 * Created by SECMEM-DY on 2016-07-04.
 */import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlay {
    MediaPlayer mp = null;
    public SoundPlay ( Context context, int id ) {
        mp = MediaPlayer.create(context, id);
    }
    public void play() {
        mp.seekTo(0);
        mp.start();
    }
}
