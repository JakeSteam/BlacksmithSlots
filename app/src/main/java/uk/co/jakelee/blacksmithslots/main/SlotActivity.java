package uk.co.jakelee.blacksmithslots.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmithslots.helper.LevelHelper;
import uk.co.jakelee.blacksmithslots.helper.MusicHelper;
import uk.co.jakelee.blacksmithslots.helper.SlotHelper;
import uk.co.jakelee.blacksmithslots.helper.TutorialHelper;
import uk.co.jakelee.blacksmithslots.model.Slot;

import static uk.co.jakelee.blacksmithslots.helper.LevelHelper.convertLevelToXp;
import static uk.co.jakelee.blacksmithslots.helper.LevelHelper.getLevel;
import static uk.co.jakelee.blacksmithslots.helper.LevelHelper.getXp;

public class SlotActivity extends BaseActivity {
    private SlotHelper slotHelper;
    private boolean isFirstInstall = false;
    public static SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);
        prefs = getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);

        Intent intent = getIntent();
        final Slot slot = Slot.get(intent.getIntExtra(Constants.INTENT_SLOT, 0));
        isFirstInstall = intent.getBooleanExtra("isFirstInstall", false);
        if (slot == null) {
            finish();
        } else {
            final SlotActivity activity = this;
            final ProgressDialog alert = new ProgressDialog(this);
            alert.setIndeterminate(true);
            alert.setTitle(getString(R.string.loading_title));
            alert.setMessage(getString(R.string.loading_body));
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

                    if (isFirstInstall && prefs.getInt("tutorialStageCompleted", 0) < 2) {
                        startTutorial();
                    }

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            alert.dismiss();
                        }
                    }, 60);

                }
            }, 50);
        }
        ((TextView)findViewById(R.id.vipLevel)).setText(String.format(Locale.ENGLISH, getString(R.string.vip_level_display), LevelHelper.getVipLevel()));

        if (GooglePlayHelper.shouldAutosave()) {
            GooglePlayHelper.autosave(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (slotHelper != null) {
            slotHelper.updateResourceCount();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(slotHelper != null) {
            slotHelper.pause();
        }
    }

    public void startTutorial() {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        final TutorialHelper th = new TutorialHelper(this, 1);
        th.addTutorial(findViewById(R.id.topBar), R.string.tutorial_slot_1, false, Gravity.BOTTOM);
        th.addTutorial(findViewById(R.id.inventoryContainer), R.string.tutorial_slot_2, false, isLandscape ? (Gravity.RIGHT | Gravity.TOP) : Gravity.TOP);
        th.addTutorial(findViewById(R.id.stakeModifiers), R.string.tutorial_slot_3, false, Gravity.TOP);
        th.addTutorial(findViewById(R.id.autospinButton), R.string.tutorial_slot_4, false, isLandscape ? Gravity.TOP : Gravity.LEFT);
        th.addTutorial(findViewById(R.id.spinButton), R.string.tutorial_slot_5, true, isLandscape ? Gravity.TOP : Gravity.LEFT);
        th.start();
        findViewById(R.id.spinButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                th.next();
                spin(v);
            }
        });
    }

    public void spin(View v) {
        slotHelper.spin(true);
        if (isFirstInstall) {
            isFirstInstall = false;
            prefs.edit().putInt("tutorialStageCompleted", 2).apply();
        }
    }

    public void openLog(View v) {
        MusicHelper.getInstance(this).setMovingInApp(true);
        startActivity(new Intent(this, LogActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void openVip(View v) {
        MusicHelper.getInstance(this).setMovingInApp(true);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        slotHelper.onActivityResult(requestCode, resultCode, data);
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
}
