package uk.co.jakelee.blacksmithslots;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.View;


import de.keyboardsurfer.android.widget.crouton.Crouton;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.MusicHelper;
import uk.co.jakelee.blacksmithslots.model.Setting;

public class BaseActivity extends Activity {
    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //noinspection ResourceType
        setRequestedOrientation(Setting.get(Enums.Setting.Orientation).getIntValue());
        MusicHelper.getInstance(this).setMovingInApp(false);
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!MusicHelper.getInstance(this).isMovingInApp()) {
            MusicHelper.getInstance(this).stopMusic();
        }
    }

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    private void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        // new activity anim, old activity anim
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    private void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void closeAlert(View v) {
        Crouton.cancelAllCroutons();
    }

    public void close(View v) {
        MusicHelper.getInstance(this).setMovingInApp(true);
        finish();
    }

    public void suppress(View v) {
    }
}
