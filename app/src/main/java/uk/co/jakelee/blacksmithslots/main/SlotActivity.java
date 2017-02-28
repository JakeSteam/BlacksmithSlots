package uk.co.jakelee.blacksmithslots.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;
import uk.co.jakelee.blacksmithslots.helper.LevelHelper;
import uk.co.jakelee.blacksmithslots.helper.SlotHelper;
import uk.co.jakelee.blacksmithslots.model.Slot;

import static uk.co.jakelee.blacksmithslots.helper.LevelHelper.convertLevelToXp;
import static uk.co.jakelee.blacksmithslots.helper.LevelHelper.getLevel;
import static uk.co.jakelee.blacksmithslots.helper.LevelHelper.getXp;

public class SlotActivity extends AppCompatActivity {
    private SlotHelper slotHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);

        SharedPreferences prefs = getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);
        if (prefs.getBoolean("firstRun", true)) {
            DatabaseHelper.testSetup();
            prefs.edit().putBoolean("firstRun", false).apply();
        }

        Slot slot = Slot.get(1);
        if (slot == null) {
            finish();
        } else {
            slotHelper = new SlotHelper(this, slot);
            slotHelper.createWheel();
            slotHelper.createRoutes();
            slotHelper.updateResourceCount();
            slotHelper.afterSpinUpdate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        slotHelper.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onWindowFocusChanged(true);
    }

    public void spin(View v) {
        slotHelper.spin(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            findViewById(R.id.parent).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void increaseStake(View v) {
        slotHelper.increaseStake();
    }

    public void decreaseStake(View v) {
        slotHelper.decreaseStake();
    }

    public void increaseRows(View v) {
        slotHelper.increaseRows();
    }

    public void decreaseRows(View v) {
        slotHelper.decreaseRows();
    }

    public void close(View v) {
        finish();
    }

    public void autospin(View v) {
        AlertDialogHelper.autospin(this, slotHelper);
    }

    public void levelInfo(View v) {
        int nextLevelXP = convertLevelToXp(getLevel() + 1);
        Toast.makeText(this, getXp() + "/" + nextLevelXP + " (" + (LevelHelper.getLevelProgress() / 100d) + "%)", Toast.LENGTH_SHORT).show();
    }
}
