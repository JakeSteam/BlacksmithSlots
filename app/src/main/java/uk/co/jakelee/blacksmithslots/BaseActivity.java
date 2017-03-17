package uk.co.jakelee.blacksmithslots;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import de.keyboardsurfer.android.widget.crouton.Crouton;

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

    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    public void closeAlert(View v) {
        Crouton.cancelAllCroutons();
    }

    public void close(View v) {
        finish();
    }

    public void suppress(View v) {

    }
}
