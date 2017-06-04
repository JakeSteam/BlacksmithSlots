package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.util.Random;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.model.Setting;

public class SoundHelper {
    public static final int[] swipeSounds = {R.raw.swipe};
    public static final int[] coinSounds = {R.raw.coins1, R.raw.coins2, R.raw.coins3, R.raw.coins4, R.raw.coins5, R.raw.coins6};
    public static final int[] diceSounds = {R.raw.dice};
    public static final int[] chestSounds = {R.raw.chest};
    public static final int[] spinSounds = {R.raw.spin};

    // If an array is passed, pick one at random to play.
    public static void playSound(Context context, int[] sounds) {
        int soundID = sounds[new Random().nextInt(sounds.length)];
        playSound(context, soundID);
    }

    private static void playSound(Context context, int soundID) {
        // Only play if the user has sounds enabled.
        if (Setting.getBoolean(Enums.Setting.Sound)) {
            try {
                MediaPlayer mediaPlayer = MediaPlayer.create(context, soundID);
                mediaPlayer.start();
            } catch (Exception e) {
                Log.d("BS", e.toString());
            }
        }
    }
}
