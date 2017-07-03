package uk.co.jakelee.blacksmithslots.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    private MediaPlayer player;

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

            int songId = intent.getIntExtra("songId", 0);
            if (songId > 0) {
                player = MediaPlayer.create(this, songId);
                player.setLooping(true);
                player.setVolume(volume, volume);
                player.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            try {
                player.stop();
                player.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
}
