package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.games.quest.QuestUpdateListener;

import java.util.List;
import java.util.Locale;

import hotchemi.android.rate.AppRate;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.CustomPagerAdapter;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmithslots.helper.IncomeHelper;
import uk.co.jakelee.blacksmithslots.helper.LanguageHelper;
import uk.co.jakelee.blacksmithslots.helper.NotificationHelper;
import uk.co.jakelee.blacksmithslots.helper.TaskHelper;
import uk.co.jakelee.blacksmithslots.model.Item;
import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Task;

public class MapActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        QuestUpdateListener {
    public static SharedPreferences prefs;

    private int selectedSlot = 1;
    private ViewPager mViewPager;
    private CustomPagerAdapter mCustomPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map);
        prefs = getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);
        LanguageHelper.updateLanguage(getApplicationContext());

        ratingPrompt();

        GooglePlayHelper.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                .build();
        tryGoogleLogin();


        mCustomPagerAdapter = new CustomPagerAdapter(this);

        mViewPager = (ViewPager) findViewById(R.id.townScroller);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                selectedSlot = 0;
                loadSidebar(null);
            }
        });
        mViewPager.setAdapter(mCustomPagerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((TextView)findViewById(R.id.settings)).setText(R.string.settings);
        ((TextView)findViewById(R.id.statistics)).setText(R.string.statistics);
        ((TextView)findViewById(R.id.inventory)).setText(R.string.inventory);
        ((TextView)findViewById(R.id.claimBonus)).setText(R.string.claim_bonus);
    }

    @Override
    protected void onStop() {
        super.onStop();

        boolean notificationSound = Setting.getBoolean(Enums.Setting.NotificationSounds);
        if (Setting.getBoolean(Enums.Setting.PeriodicBonusNotification)) {
            NotificationHelper.addBonusNotification(this, notificationSound);
        }
    }

    public void tryGoogleLogin() {
        // If we've got all we need, and we need to sign in, or it is first run.
        boolean a = GooglePlayHelper.AreGooglePlayServicesInstalled(this);
        boolean b = !GooglePlayHelper.IsConnected();
        boolean c = !GooglePlayHelper.mGoogleApiClient.isConnecting();
        boolean d = Setting.getBoolean(Enums.Setting.AttemptLogin);
        boolean e = prefs.getInt("databaseVersion", DatabaseHelper.NO_DATABASE) <= DatabaseHelper.NO_DATABASE;

        if (a && b && c && (d || e)) {
            GooglePlayHelper.mGoogleApiClient.connect();
        }
    }

    private void ratingPrompt() {
        AppRate.with(this)
                .setInstallDays(3)
                .setLaunchTimes(3)
                .setRemindInterval(3)
                .setShowLaterButton(true)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);
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

    public void openSettings(View v) {
        startActivity(new Intent(this, SettingsActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void openStatistics(View v) {
        startActivity(new Intent(this, StatisticsActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void openInventory(View v) {
        startActivity(new Intent(this, InventoryActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void claimPeriodicBonus(View v) {
        if (IncomeHelper.canClaimPeriodicBonus()) {
            AlertHelper.success(this, IncomeHelper.claimBonus(this, true), true);
        } else {
            AlertHelper.error(this, R.string.error_bonus_not_ready, false);
        }
    }

    public void loadSidebar(View v) {
        findViewById(R.id.noSlotSelected).setVisibility(View.VISIBLE);
    }

    public void handIn(View v) {
        Task task = Task.findById(Task.class, (long)v.getTag());
        if (task.getTier() != null && task.itemsCanBeSubmitted()) {
            task.submitItems();
            AlertHelper.success(this, R.string.alert_items_submitted, true);
        } else if (task.isCompleteable()) {
            AlertHelper.success(this, String.format(Locale.ENGLISH, getString(R.string.log_task),
                    task.toString(this),
                    Slot.getName(this, selectedSlot)), true);
            task.setCompleted(System.currentTimeMillis());
            task.save();
        } else {
            AlertHelper.error(this, R.string.alert_unfinished_task, false);
        }
        populateSlotInfo();
    }

    private void populateSlotInfo() {
        if (selectedSlot > 0) {
            findViewById(R.id.noSlotSelected).setVisibility(View.GONE);
            Slot slot = Slot.get(selectedSlot);
            if (slot != null) {
                ((TextView) findViewById(R.id.slotTitle)).setText(slot.getName(this));
                ((ImageView) findViewById(R.id.person)).setImageResource(getResources().getIdentifier(DisplayHelper.getPersonImageFile(slot.getPerson()), "drawable", getPackageName()));

                if (slot.getRequiredSlot() > 0 && TaskHelper.isSlotLocked(Slot.get(slot.getRequiredSlot()).getSlotId())) {
                    ((TextView) findViewById(R.id.slotDescription)).setText("Slot \"" + Slot.get(slot.getRequiredSlot()).getName(this) + "\" needs unlocking first!");
                    findViewById(R.id.superlockedSlot).setVisibility(View.VISIBLE);
                    findViewById(R.id.lockedSlot).setVisibility(View.GONE);
                    findViewById(R.id.unlockedSlot).setVisibility(View.GONE);
                } else if (TaskHelper.isSlotLocked(selectedSlot)) {
                    List<Task> tasks = slot.getTasks();
                    Task currentTask = TaskHelper.getCurrentTask(tasks);
                    if (currentTask.getStarted() == 0) {
                        currentTask.setStarted(System.currentTimeMillis());
                        currentTask.save();
                    }

                    ((TextView) findViewById(R.id.slotDescription)).setText(slot.getLockedText(this));
                    ((TextView) findViewById(R.id.taskProgress)).setText(String.format(Locale.ENGLISH, getString(R.string.task_completion),
                            currentTask.getPosition(),
                            tasks.size()));
                    ((TextView) findViewById(R.id.taskText)).setText(currentTask.getText(this));
                    ((TextView) findViewById(R.id.taskRequirement)).setText(currentTask.toString(this));
                    findViewById(R.id.handInButton).setTag(currentTask.getId());

                    findViewById(R.id.superlockedSlot).setVisibility(View.GONE);
                    findViewById(R.id.lockedSlot).setVisibility(View.VISIBLE);
                    findViewById(R.id.unlockedSlot).setVisibility(View.GONE);
                } else {
                    ((TextView) findViewById(R.id.slotDescription)).setText(slot.getUnlockedText(this));

                    LinearLayout resourceContainer = (LinearLayout)findViewById(R.id.resourceContainer);
                    resourceContainer.removeAllViews();
                    resourceContainer.addView(DisplayHelper.createImageView(this,
                            DisplayHelper.getItemImageFile(slot.getResourceTier().value, slot.getResourceType().value),
                            30,
                            30,
                            slot.getResourceQuantity() + "x " + Item.getName(this, slot.getResourceTier(), slot.getResourceType())));

                    LinearLayout rewardContainer = (LinearLayout)findViewById(R.id.rewardContainer);
                    rewardContainer.removeAllViews();
                    List<Reward> rewards = slot.getRewards();
                    for (Reward reward : rewards) {
                        rewardContainer.addView(DisplayHelper.createImageView(this,
                                DisplayHelper.getItemImageFile(reward.getTier().value, reward.getType().value, reward.getQuantityMultiplier()),
                                30,
                                30,
                                reward.getQuantityMultiplier() + "x " + Item.getName(this, reward.getTier(), reward.getType())));
                    }

                    findViewById(R.id.superlockedSlot).setVisibility(View.GONE);
                    findViewById(R.id.lockedSlot).setVisibility(View.GONE);
                    findViewById(R.id.unlockedSlot).setVisibility(View.VISIBLE);

                }
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (GooglePlayHelper.IsConnected()) {
            Games.Quests.registerQuestUpdateListener(GooglePlayHelper.mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        GooglePlayHelper.ConnectionFailed(this, connectionResult);
    }

    @Override
    public void onConnectionSuspended(int i) {
        GooglePlayHelper.mGoogleApiClient.connect();
    }

    public void onQuestCompleted(Quest quest) {
        AlertHelper.success(this, R.string.alert_quest_completed, true);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        GooglePlayHelper.ActivityResult(this, requestCode, resultCode);
    }
}
