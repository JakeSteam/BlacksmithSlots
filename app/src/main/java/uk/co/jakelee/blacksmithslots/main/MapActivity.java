package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.List;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;
import uk.co.jakelee.blacksmithslots.helper.TaskHelper;
import uk.co.jakelee.blacksmithslots.model.Task;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SharedPreferences prefs = getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);
        if (prefs.getBoolean("firstRun", true)) {
            DatabaseHelper.testSetup();

            // Temporarily start the quest
            List<Task> stats = Task.listAll(Task.class);
            for (Task stat : stats) {
                stat.setStarted(System.currentTimeMillis());
                stat.save();
            }
            prefs.edit().putBoolean("firstRun", false).apply();
        }
    }

    public void openSlot(View v) {
        int slot = Integer.parseInt((String)v.getTag());
        if (TaskHelper.isSlotUnlocked(slot)) {
            startActivity(new Intent(this, SlotActivity.class)
                    .putExtra(Constants.INTENT_SLOT, slot)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            findViewById(R.id.mainScroller).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
