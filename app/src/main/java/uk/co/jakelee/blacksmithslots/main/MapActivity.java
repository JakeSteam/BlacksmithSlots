package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;
import uk.co.jakelee.blacksmithslots.helper.TaskHelper;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Task;

public class MapActivity extends AppCompatActivity {
    private int selectedSlot = 0;

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
        if (selectedSlot > 0 && !TaskHelper.isSlotLocked(selectedSlot)) {
            startActivity(new Intent(this, SlotActivity.class)
                    .putExtra(Constants.INTENT_SLOT, selectedSlot)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        }
    }

    public void selectSlot(View v) {
        selectedSlot = Integer.parseInt((String)v.getTag());
        populateSlotInfo();
    }

    private void populateSlotInfo() {
        TextHelper th = TextHelper.getInstance(this);
        if (selectedSlot > 0) {
            Slot slot = Slot.get(selectedSlot);
            if (slot != null) {
                ((TextView) findViewById(R.id.slotTitle)).setText(slot.getName(this));

                if (TaskHelper.isSlotLocked(selectedSlot)) {
                    List<Task> tasks = slot.getTasks();
                    Task currentTask = TaskHelper.getCurrentTask(tasks);

                    ((TextView) findViewById(R.id.slotDescription)).setText(slot.getLockedText(this));
                    ((TextView) findViewById(R.id.taskProgress)).setText("Task " + currentTask.getPosition() + "/" + tasks.size());
                    ((TextView) findViewById(R.id.taskText)).setText(currentTask.getText(this));
                    ((TextView) findViewById(R.id.taskRequirement)).setText(currentTask.getRequirement(this));

                    findViewById(R.id.lockedSlot).setVisibility(View.VISIBLE);
                    findViewById(R.id.unlockedSlot).setVisibility(View.GONE);
                } else {
                    ((TextView) findViewById(R.id.slotDescription)).setText(slot.getUnlockedText(this));
                    ((TextView) findViewById(R.id.slotResource)).setText("Resource: " + slot.getResourceText(this));
                    ((TextView) findViewById(R.id.slotReward)).setText("Rewards: " + slot.getRewardText(this));

                    findViewById(R.id.lockedSlot).setVisibility(View.GONE);
                    findViewById(R.id.unlockedSlot).setVisibility(View.VISIBLE);

                }
            }
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
