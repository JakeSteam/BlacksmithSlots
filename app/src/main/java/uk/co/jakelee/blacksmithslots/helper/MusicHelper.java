package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.content.Intent;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.service.MusicService;

public class MusicHelper {
    private static MusicHelper mhInstance = null;
    private boolean movingInApp = false;
    private int currentTrack = 0;
    private Context context;
    private static Intent musicService;
    private boolean musicServiceIsStarted = false;

    public MusicHelper(Context context) {
        this.context = context;
    }

    public static MusicHelper getInstance(Context ctx) {
        if (mhInstance == null) {
            mhInstance = new MusicHelper(ctx.getApplicationContext());
        }
        return mhInstance;
    }

    public void playIfPossible(final int trackToPlay) {
        new Thread(new Runnable() {
            public void run() {
                // If music should be playing, and it isn't, or is the wrong track, fix that!
                if ((isIntroMusic(R.raw.time_passes) || Setting.getBoolean(Enums.Setting.Music)) &&
                        (trackToPlay != currentTrack || !musicServiceIsStarted)) {
                    musicService = new Intent(context.getApplicationContext(), MusicService.class)
                        .putExtra("songId", trackToPlay);
                    currentTrack = trackToPlay;
                    context.startService(musicService);
                    musicServiceIsStarted = true;
                } else if (!Setting.getBoolean(Enums.Setting.Music) && musicServiceIsStarted) {
                    context.stopService(musicService);
                    musicServiceIsStarted = false;
                }
            }
        }).start();
    }

    private boolean isIntroMusic(int track) {
        return track == R.raw.time_passes;
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

    public boolean isMusicServiceIsStarted() {
        return musicServiceIsStarted;
    }
}
