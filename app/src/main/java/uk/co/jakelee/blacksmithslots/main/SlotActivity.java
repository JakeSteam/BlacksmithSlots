package uk.co.jakelee.blacksmithslots.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import uk.co.jakelee.blacksmithslots.MainActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.LevelHelper;
import uk.co.jakelee.blacksmithslots.helper.SlotHelper;
import uk.co.jakelee.blacksmithslots.model.Slot;

import static uk.co.jakelee.blacksmithslots.helper.LevelHelper.convertLevelToXp;
import static uk.co.jakelee.blacksmithslots.helper.LevelHelper.getLevel;
import static uk.co.jakelee.blacksmithslots.helper.LevelHelper.getXp;

public class SlotActivity extends MainActivity {
    private SlotHelper slotHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slot);

        final Slot slot = Slot.get(getIntent().getIntExtra(Constants.INTENT_SLOT, 0));
        if (slot == null) {
            finish();
        } else {
            final SlotActivity activity = this;
            final ProgressDialog alert = new ProgressDialog(this);
            alert.setIndeterminate(true);
            alert.setTitle("Loading, hang on!");
            alert.setMessage("Fetching resources from the warehouse...");
            alert.show();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    slotHelper = new SlotHelper(activity, handler, slot);
                    slotHelper.createWheel();
                    slotHelper.createRoutes();
                    slotHelper.updateResourceCount();
                    slotHelper.afterSpinUpdate();
                    alert.dismiss();
                }
            }, 50);
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

    public void openLog(View v) {
        startActivity(new Intent(this, LogActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void openVip(View v) {
        startActivity(new Intent(this, VipComparisonActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
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
        AlertHelper.info(this, getXp() + "/" + nextLevelXP + " (" + (LevelHelper.getLevelProgress() / 100d) + "%)", false);
    }
}
