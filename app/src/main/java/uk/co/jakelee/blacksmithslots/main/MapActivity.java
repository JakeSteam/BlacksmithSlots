package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.games.quest.QuestUpdateListener;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import hotchemi.android.rate.AppRate;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.MapPagerAdapter;
import uk.co.jakelee.blacksmithslots.components.ViewPagerIndicator;
import uk.co.jakelee.blacksmithslots.helper.AdvertHelper;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;
import uk.co.jakelee.blacksmithslots.helper.DateHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmithslots.helper.IncomeHelper;
import uk.co.jakelee.blacksmithslots.helper.LanguageHelper;
import uk.co.jakelee.blacksmithslots.helper.NotificationHelper;
import uk.co.jakelee.blacksmithslots.helper.Runnables;
import uk.co.jakelee.blacksmithslots.helper.TaskHelper;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;
import uk.co.jakelee.blacksmithslots.model.Item;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Task;

import static uk.co.jakelee.blacksmithslots.R.id.mapName;

public class MapActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        QuestUpdateListener {
    public static SharedPreferences prefs;
    private int selectedSlot = 1;
    private Handler handler = new Handler();

    @BindView(mapName) TextView mapTextView;
    @BindView(R.id.noSlotSelected) RelativeLayout noSlotSidebar;
    @BindView(R.id.superlockedSlot) RelativeLayout superLockedSlot;
    @BindView(R.id.lockedSlot) RelativeLayout lockedSlot;
    @BindView(R.id.unlockedSlot) RelativeLayout unlockedSlot;
    @BindView(R.id.watchAdvert) TextView watchAdvert;
    @BindView(R.id.claimBonus) TextView claimBonus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map);
        prefs = getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);
        LanguageHelper.updateLanguage(getApplicationContext());
        ButterKnife.bind(this);

        ratingPrompt();

        GooglePlayHelper.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                .build();
        tryGoogleLogin();

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                String mapName = TextHelper.getInstance(getApplicationContext()).getText(DisplayHelper.getMapString(position + 1));
                mapTextView.setText(mapName);
                selectedSlot = 0;
                loadSidebar(null);
            }
        };

        ViewPager mapPager = (ViewPager) findViewById(R.id.townScroller);
        mapPager.setAdapter(new MapPagerAdapter(this));

        ViewPagerIndicator indicator = (ViewPagerIndicator)findViewById(R.id.view_pager_indicator);
        indicator.setupWithViewPager(mapPager);
        indicator.addOnPageChangeListener(pageChangeListener);

        if (IncomeHelper.getNextPeriodicClaimTime() - System.currentTimeMillis() > 0) {
            setPeriodicBonusUnclaimable();
        }
        if (IncomeHelper.getNextAdvertWatchTime() - System.currentTimeMillis() > 0) {
            setAdvertUnclaimable();
        }

        mapTextView.setText(R.string.map_1);
    }

    private void setPeriodicBonusUnclaimable() {
        claimBonus.setBackgroundResource(R.drawable.box_orange);
        handler.post(Runnables.updateTimeToPeriodicBonusClaim(handler, claimBonus));
    }

    public void setAdvertUnclaimable() {
        watchAdvert.setBackgroundResource(R.drawable.box_orange);
        handler.post(Runnables.updateTimeToWatchAdvert(handler, watchAdvert));
    }

    @Override
    protected void onStop() {
        super.onStop();

        boolean notificationSound = Setting.getBoolean(Enums.Setting.NotificationSounds);
        if (Setting.getBoolean(Enums.Setting.PeriodicBonusNotification)) {
            NotificationHelper.addBonusNotification(this, notificationSound);
        }
        if (Setting.getBoolean(Enums.Setting.BlacksmithPassNotification)) {
            NotificationHelper.addBlacksmithPassNotification(this, notificationSound);
        }
        handler.removeCallbacks(null);
    }

    public void tryGoogleLogin() {
        // If we've got all we need, and we need to sign in, or it is first run.
        if (GooglePlayHelper.AreGooglePlayServicesInstalled(this)
                && !GooglePlayHelper.IsConnected()
                && !GooglePlayHelper.mGoogleApiClient.isConnecting()
                && (Setting.getBoolean(Enums.Setting.AttemptLogin) || prefs.getInt("databaseVersion", DatabaseHelper.NO_DATABASE) <= DatabaseHelper.NO_DATABASE)) {
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

    public void openShop(View v) {
        startActivity(new Intent(this, ShopActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void claimPeriodicBonus(View v) {
        if (IncomeHelper.canClaimPeriodicBonus()) {
            AlertHelper.success(this, IncomeHelper.claimPeriodicBonus(this), true);
            setPeriodicBonusUnclaimable();
        } else {
            AlertHelper.error(this, String.format(Locale.ENGLISH,
                    getString(R.string.error_bonus_not_ready),
                    DateHelper.timestampToDetailedTime(IncomeHelper.getNextPeriodicClaimTime() - System.currentTimeMillis())),
                    false);
        }
    }

    public void rewardAdvertItems(View v) {
        if (IncomeHelper.canWatchAdvert()) {
            AdvertHelper.getInstance(this).showAdvert(this);
        } else {
            AlertHelper.error(this, String.format(Locale.ENGLISH,
                    getString(R.string.error_advert_not_ready),
                    DateHelper.timestampToDetailedTime(IncomeHelper.getNextAdvertWatchTime() - System.currentTimeMillis())),
                    false);
        }
    }

    public void rewardAdvertItems() {
        AlertHelper.success(this, "Advert watch verified! " + IncomeHelper.claimAdvertBonus(this), true);
        setAdvertUnclaimable();
    }

    public void loadSidebar(View v) {
        noSlotSidebar.setVisibility(View.VISIBLE);
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
                    populateSlotInfoSuperlocked(slot);
                } else if (TaskHelper.isSlotLocked(selectedSlot)) {
                    populateSlotInfoLocked(slot);
                } else {
                    populateSlotInfoUnlocked(slot);
                }
            }
        }
    }

    private void populateSlotInfoSuperlocked(Slot slot) {
        ((TextView) findViewById(R.id.slotDescription)).setText("Slot \"" + Slot.get(slot.getRequiredSlot()).getName(this) + "\" needs unlocking first!");
        superLockedSlot.setVisibility(View.VISIBLE);
        lockedSlot.setVisibility(View.GONE);
        unlockedSlot.setVisibility(View.GONE);
    }

    private void populateSlotInfoLocked(Slot slot) {
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

        superLockedSlot.setVisibility(View.GONE);
        lockedSlot.setVisibility(View.VISIBLE);
        unlockedSlot.setVisibility(View.GONE);
    }

    private void populateSlotInfoUnlocked(Slot slot) {
        ((TextView) findViewById(R.id.slotDescription)).setText(slot.getUnlockedText(this));

        populateItemContainer(R.id.resourceContainer, slot.getResources());
        populateItemContainer(R.id.rewardContainer, slot.getRewards());

        superLockedSlot.setVisibility(View.GONE);
        lockedSlot.setVisibility(View.GONE);
        unlockedSlot.setVisibility(View.VISIBLE);
    }

    private void populateItemContainer(int id, List<ItemBundle> items) {
        LinearLayout rewardContainer = (LinearLayout)findViewById(id);
        rewardContainer.removeAllViews();
        for (ItemBundle itemBundle : items) {
            rewardContainer.addView(DisplayHelper.createImageView(this,
                    DisplayHelper.getItemImageFile(itemBundle.getTier().value, itemBundle.getType().value, itemBundle.getQuantity()),
                    30,
                    30,
                    itemBundle.getQuantity() + "x " + Item.getName(this, itemBundle.getTier(), itemBundle.getType())));
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
        if (requestCode == Constants.ADVERT_WATCH) {
            if (resultCode > 0) {
                rewardAdvertItems();
            }
        } else {
            GooglePlayHelper.ActivityResult(this, requestCode, resultCode);
        }
    }
}
