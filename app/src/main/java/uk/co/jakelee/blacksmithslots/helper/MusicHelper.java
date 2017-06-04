package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.content.Intent;

import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.service.MusicService;

public class MusicHelper {
    private static MusicHelper mhInstance = null;
    private boolean movingInApp = false;
    private Context context;
    private static Intent musicService;
    private boolean musicServiceIsStarted = false;

    public MusicHelper(Context context) {
        this.context = context;
    }

    public static MusicHelper getInstance(Context ctx) {
        if (mhInstance == null) {
            mhInstance = new MusicHelper(ctx.getApplicationContext());
            musicService = new Intent(ctx.getApplicationContext(), MusicService.class);
        }
        return mhInstance;
    }

    public void checkMusic() {
        new Thread(new Runnable() {
            public void run() {
                if (Setting.getBoolean(Enums.Setting.Music) && !musicServiceIsStarted) {
                    context.startService(musicService);
                    musicServiceIsStarted = true;
                } else if (!Setting.getBoolean(Enums.Setting.Music) && musicServiceIsStarted) {
                    context.stopService(musicService);
                    musicServiceIsStarted = false;
                }
            }
        }).start();
    }

    public void stopMusic() {
        if (musicServiceIsStarted) {
            context.stopService(musicService);
            musicServiceIsStarted = false;
        }
    }
    public boolean isMovingInApp() {
        return movingInApp;
    }

    public void setMovingInApp(boolean movingInApp) {
        this.movingInApp = movingInApp;
    }
}
