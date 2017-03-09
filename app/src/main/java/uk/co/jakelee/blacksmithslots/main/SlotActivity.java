package uk.co.jakelee.blacksmithslots.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slot);

        Slot slot = Slot.get(getIntent().getIntExtra(Constants.INTENT_SLOT, 0));
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

    public void spin(View v) {
        slotHelper.spin(true);
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
