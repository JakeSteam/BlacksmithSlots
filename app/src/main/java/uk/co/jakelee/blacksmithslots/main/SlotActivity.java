package uk.co.jakelee.blacksmithslots.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Locale;

import uk.co.jakelee.blacksmithslots.BaseActivity;
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

public class SlotActivity extends BaseActivity {
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
                    slotHelper.setBackground();
                    slotHelper.createWheel();
                    slotHelper.createRoutes();
                    slotHelper.updateResourceCount();
                    slotHelper.afterSpinUpdate();
                    alert.dismiss();
                }
            }, 500);
        }
        ((TextView)findViewById(R.id.vipLevel)).setText(String.format(Locale.ENGLISH, getString(R.string.vip_level_display), LevelHelper.getVipLevel()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (slotHelper != null) {
            slotHelper.updateResourceCount();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(slotHelper != null) {
            slotHelper.pause();
        }
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

    public void autospin(View v) {
        if (slotHelper.autospinsLeft > 0) {
            slotHelper.autospinsLeft = 0;
            slotHelper.afterSpinUpdate();
            AlertHelper.info(this, R.string.alert_autospin_cancelled, false);
        } else {
            AlertDialogHelper.autospin(this, slotHelper);
        }
    }

    public void levelInfo(View v) {
        int currentLevel = getLevel();
        int nextLevelXP = convertLevelToXp(currentLevel + 1);
        AlertHelper.info(this, String.format(Locale.ENGLISH, getString(R.string.alert_level_progress),
                (LevelHelper.getLevelProgress() / 100d),
                currentLevel + 1,
                getXp(),
                nextLevelXP), false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        slotHelper.onActivityResult(requestCode, resultCode, data);
    }
}
