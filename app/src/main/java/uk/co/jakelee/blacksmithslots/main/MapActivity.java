package uk.co.jakelee.blacksmithslots.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.tapjoy.TJPlacement;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hotchemi.android.rate.AppRate;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.MapPagerAdapter;
import uk.co.jakelee.blacksmithslots.components.ViewPagerIndicator;
import uk.co.jakelee.blacksmithslots.helper.AdvertHelper;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DateHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmithslots.helper.IncomeHelper;
import uk.co.jakelee.blacksmithslots.helper.LanguageHelper;
import uk.co.jakelee.blacksmithslots.helper.MusicHelper;
import uk.co.jakelee.blacksmithslots.helper.NotificationHelper;
import uk.co.jakelee.blacksmithslots.helper.Runnables;
import uk.co.jakelee.blacksmithslots.helper.SoundHelper;
import uk.co.jakelee.blacksmithslots.helper.TaskHelper;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Task;
import uk.co.jakelee.blacksmithslots.tourguide.Overlay;
import uk.co.jakelee.blacksmithslots.tourguide.Pointer;
import uk.co.jakelee.blacksmithslots.tourguide.ToolTip;
import uk.co.jakelee.blacksmithslots.tourguide.TourGuide;

import static android.view.View.GONE;
import static uk.co.jakelee.blacksmithslots.model.Setting.getBoolean;

public class MapActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static SharedPreferences prefs;
    @BindView(R.id.townScroller)
    ViewPager mapPager;
    @BindView(R.id.mapName)
    TextView mapTextView;
    @BindView(R.id.noSlotSelected)
    LinearLayout noSlotSidebar;
    @BindView(R.id.superlockedSlot)
    RelativeLayout superLockedSlot;
    @BindView(R.id.slotSuperlockedDescription)
    TextView superLockedDescription;
    @BindView(R.id.lockedSlot)
    RelativeLayout lockedSlot;
    @BindView(R.id.slotLockedDescription)
    TextView lockedDescription;
    @BindView(R.id.taskRequirement)
    TextView lockedTaskRequirement;
    @BindView(R.id.taskProgressBar)
    ProgressBar lockedTaskProgressBar;
    @BindView(R.id.taskProgressText)
    TextView lockedTaskProgressText;
    @BindView(R.id.unlockedSlot)
    RelativeLayout unlockedSlot;
    @BindView(R.id.slotUnlockedDescription)
    TextView unlockedDescription;
    @BindView(R.id.watchAdvert)
    TextView watchAdvert;
    @BindView(R.id.claimBonus)
    TextView claimBonus;
    @BindView(R.id.googlePlayRow)
    LinearLayout googlePlayRow;
    @BindView(R.id.googlePlayLoginRow)
    LinearLayout googlePlayLoginRow;
    private int selectedSlot = 1;
    private final Handler handler = new Handler();
    private MapPagerAdapter mapPagerAdapter;
    public AdvertHelper advertHelper;
    private boolean isFirstInstall = false;
    private TourGuide mTutorialHandler;
    private TJPlacement advertPlacement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageHelper.updateLanguage(getApplicationContext());
        setContentView(R.layout.activity_map);
        prefs = getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);
        ButterKnife.bind(this);

        createGooglePlayClient();
        GooglePlayHelper.tryGoogleLogin(this, false);

        ratingPrompt();
        createMapPager();
        checkBonuses();

        advertHelper = AdvertHelper.getInstance(this);
        advertPlacement = new TJPlacement(this, "WatchAdvert", advertHelper);

        mapTextView.setText(R.string.map_1);

        tryStartTutorial();
    }

    private void tryStartTutorial() {
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("isFirstInstall", false)) {
            if (!Constants.DEBUG_UNLOCK_ALL && prefs.getInt("tutorialStageCompleted", 0) < 1) {
                runTutorial(1);
            }
            isFirstInstall = true;
        }
    }

    private void checkBonuses() {
        if (IncomeHelper.getNextPeriodicClaimTime() - System.currentTimeMillis() > 0) {
            setPeriodicBonusUnclaimable();
        }
        if (IncomeHelper.getNextAdvertWatchTime() - System.currentTimeMillis() > 0) {
            setAdvertUnclaimable();
        }
    }

    private void createMapPager() {
        mapPagerAdapter = new MapPagerAdapter(this);
        mapPager.setAdapter(mapPagerAdapter);

        ViewPagerIndicator indicator = findViewById(R.id.view_pager_indicator);
        indicator.setupWithViewPager(mapPager);
        indicator.addOnPageChangeListener(mapChangeListener());
    }

    @NonNull
    private ViewPager.SimpleOnPageChangeListener mapChangeListener() {
        return new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                SoundHelper.playSound(getApplicationContext(), SoundHelper.swipeSounds);
                findViewById(R.id.leftArrow).setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                findViewById(R.id.rightArrow).setVisibility(position == (MapPagerAdapter.townLayouts.length - 1) ? View.INVISIBLE : View.VISIBLE);

                String mapName = TextHelper.getInstance(getApplicationContext()).getText(DisplayHelper.getMapString(position + 1));
                mapTextView.setText(mapName);
                selectedSlot = 0;
                loadSidebar(null);

                if (isFirstInstall) {
                    isFirstInstall = false;
                    endTutorial();
                    prefs.edit().putInt("tutorialStageCompleted", 3).apply();
                }
            }
        };
    }

    private void createGooglePlayClient() {
        GooglePlayHelper.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onConnected(null);
        MusicHelper.getInstance(this).playIfPossible(R.raw.village_consort);
        updateText();

        if (isFirstInstall) {
            int stage = prefs.getInt("tutorialStageCompleted", 0);
            if (!Constants.DEBUG_UNLOCK_ALL && stage == 2) {
                runTutorial(6);
            }
        }
    }

    private void updateText() {
        ((TextView)findViewById(R.id.inventory)).setText(R.string.inventory);
        ((TextView)findViewById(R.id.displayHint)).setText(R.string.hints);
        ((TextView)findViewById(R.id.claimBonus)).setText(R.string.claim_bonus);
        ((TextView)findViewById(R.id.openShop)).setText(R.string.shop);
        ((TextView)findViewById(R.id.openTrophy)).setText(R.string.trophies);
        ((TextView)findViewById(R.id.openCredits)).setText(R.string.credits);
        ((TextView)findViewById(R.id.watchAdvert)).setText(R.string.watch_advert);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Runnable everyMinute = new Runnable() {
            @Override
            public void run() {
                GooglePlayHelper.UpdateAchievements();
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND * DateHelper.SECONDS_IN_MINUTE);
            }
        };
        handler.postDelayed(everyMinute, DateHelper.MILLISECONDS_IN_SECOND * DateHelper.SECONDS_IN_MINUTE);
    }

    private void runTutorial(int stage) {
        findViewById(R.id.firstSlot).setVisibility(View.VISIBLE);

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        switch (stage) {
            case 1:
                runTutorial(getString(R.string.tutorial_map_1), findViewById(R.id.title), isLandscape ? Gravity.LEFT : Gravity.TOP, true);
                break;
            //case 2: runTutorial("The world can be navigated by swiping left and right, there's plenty of areas to be explored in your quest to defeat the Purple!", findViewById(R.id.settings), Gravity.LEFT, true); break;
            case 3:
                runTutorial(getString(R.string.tutorial_map_3), findViewById(R.id.firstSlot), Gravity.RIGHT, true);
                break;
            case 4:
                runTutorial(getString(R.string.tutorial_map_4), findViewById(R.id.openSlot), isLandscape ? (Gravity.LEFT | Gravity.TOP) : Gravity.TOP, true);
                break;
            case 5:
                runTutorial(getString(R.string.tutorial_map_5), findViewById(R.id.openSlot), isLandscape ? (Gravity.LEFT | Gravity.TOP) : Gravity.TOP, true);
                break;
            case 6:
                runTutorial(getString(R.string.tutorial_map_6), findViewById(R.id.navigationBar), Gravity.TOP, true);
                break;
        }
    }

    private void endTutorial() {
        if (mTutorialHandler != null) {
            try {
                mTutorialHandler.cleanUp();
            } catch (RuntimeException e) {

            }
        }
    }

    private void runTutorial(String text, View view, int gravity, boolean insideClickable) {
        endTutorial();
        ToolTip toolTip = new ToolTip()
                .setDescription(text)
                .setBackgroundColor(Color.parseColor("#AAae6c37"))
                .setShadow(true)
                .setGravity(gravity);
        mTutorialHandler = TourGuide.init(this).with(TourGuide.Technique.CLICK)
                .setToolTip(toolTip)
                .setOverlay(new Overlay()
                        .setStyle(Overlay.Style.ROUNDED_RECTANGLE)
                        .disableClick(true)
                        .disableClickThroughHole(!insideClickable))
                .setPointer(new Pointer())
                .playOn(view);
    }

    public void clickTitle(View v) {
        runTutorial(3);
    }

    @OnClick(R.id.googlePlayLoginRow)
    public void tryLogin() {
        GooglePlayHelper.tryGoogleLogin(this, true);
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

        boolean notificationSound = getBoolean(Enums.Setting.NotificationSounds);
        if (getBoolean(Enums.Setting.PeriodicBonusNotification)) {
            NotificationHelper.addBonusNotification(this, notificationSound);
        }
        if (getBoolean(Enums.Setting.BlacksmithPassNotification)) {
            NotificationHelper.addBlacksmithPassNotification(this, notificationSound);
        }

        GooglePlayHelper.mGoogleApiClient.disconnect();
        handler.removeCallbacks(null);
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

    @OnClick(R.id.openSlot)
    public void openSlot() {
        if (selectedSlot > 0 && !TaskHelper.isSlotLocked(selectedSlot)) {
            MusicHelper.getInstance(this).setMovingInApp(true);
            startActivity(new Intent(this, SlotActivity.class)
                    .putExtra(Constants.INTENT_SLOT, selectedSlot)
                    .putExtra("isFirstInstall", isFirstInstall)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            if (isFirstInstall) {
                endTutorial();
                prefs.edit().putInt("tutorialStageCompleted", 1).apply();
            }
        }
    }

    public void selectSlot(View v) {
        selectedSlot = Integer.parseInt((String) v.getTag());
        populateSlotInfo();
        if (isFirstInstall) {
            runTutorial(4);
        }
    }

    @OnClick(R.id.openShop)
    public void openShop() {
        AlertDialogHelper.openOverlayActivity(this, ShopActivity.class);
    }

    @OnClick(R.id.openTrophy)
    public void openTrophy() {
        AlertDialogHelper.openOverlayActivity(this, TrophyActivity.class);
    }

    @OnClick(R.id.settings)
    public void openSettings() {
        AlertDialogHelper.openOverlayActivity(this, SettingsActivity.class);
    }

    @OnClick(R.id.statistics)
    public void openStatistics() {
        AlertDialogHelper.openOverlayActivity(this, StatisticsActivity.class);
    }

    @OnClick(R.id.inventory)
    public void openInventory() {
        AlertDialogHelper.openOverlayActivity(this, InventoryActivity.class);
    }

    @OnClick(R.id.winItems)
    public void winItems() {
        AlertDialogHelper.openOverlayActivity(this, MinigameMemoryActivity.class);
    }

    @OnClick(R.id.openCredits)
    public void openCredits() {
        AlertDialogHelper.openOverlayActivity(this, CreditsActivity.class);
    }

    @OnClick(R.id.displayHint)
    public void displayHint() {
        int numTips = 25;
        int thisHint = prefs.getInt("nextHint", 1);
        if (thisHint > numTips) {
            thisHint = 1;
        }

        String hint = TextHelper.getInstance(this).getText(DisplayHelper.getHintString(thisHint));
        String hintMessage = getString(R.string.hint) + " " + thisHint + "/" + numTips + ": " + hint;
        AlertHelper.info(this, hintMessage, false);
        prefs.edit().putInt("nextHint", ++thisHint).apply();
    }

    @OnClick(R.id.leftArrow)
    public void moveLeft() {
        if (mapPager.getCurrentItem() > 0) {
            mapPager.setCurrentItem(mapPager.getCurrentItem() - 1, true);
        }
    }

    @OnClick(R.id.rightArrow)
    public void moveRight() {
        if (mapPager.getCurrentItem() < mapPager.getAdapter().getCount()) {
            mapPager.setCurrentItem(mapPager.getCurrentItem() + 1, true);
        }
    }

    @OnClick(R.id.claimBonus)
    public void claimPeriodicBonus(View v) {
        if (IncomeHelper.canClaimPeriodicBonus()) {
            AlertHelper.success(this, IncomeHelper.claimPeriodicBonus(this), true);
            setPeriodicBonusUnclaimable();
        } else {
            AlertHelper.error(this, String.format(Locale.ENGLISH,
                    getString(R.string.error_bonus_not_ready),
                    DateHelper.timestampToTime(IncomeHelper.getNextPeriodicClaimTime() - System.currentTimeMillis())),
                    false);
        }
    }

    @OnClick(R.id.openRewardChances)
    public void openRewardChances() {
        if (selectedSlot > 0 && !TaskHelper.isSlotLocked(selectedSlot)) {
            MusicHelper.getInstance(this).setMovingInApp(true);
            startActivity(new Intent(this, SlotChancesActivity.class)
                    .putExtra(Constants.INTENT_SLOT, selectedSlot)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        }
    }

    @OnClick(R.id.openSlotDialog)
    public void openSlotDialog() {
        if (selectedSlot > 0 && !TaskHelper.isSlotLocked(selectedSlot)) {
            MusicHelper.getInstance(this).setMovingInApp(true);
            startActivity(new Intent(this, SlotDialogActivity.class)
                    .putExtra(Constants.INTENT_SLOT, selectedSlot)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        }
    }

    @OnClick(R.id.watchAdvert)
    public void rewardAdvertItems(View v) {
        if (IncomeHelper.canWatchAdvert()) {
            AlertHelper.info(this, getString(R.string.advert_load_start), false);
            advertHelper.showAdvert(this, advertPlacement);
        } else {
            AlertHelper.error(this, String.format(Locale.ENGLISH,
                    getString(R.string.error_advert_not_ready),
                    DateHelper.timestampToTime(IncomeHelper.getNextAdvertWatchTime() - System.currentTimeMillis())),
                    false);
        }
    }

    @OnClick({R.id.lockedClose, R.id.unlockedClose, R.id.superlockedClose})
    public void loadSidebar(View v) {
        noSlotSidebar.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.handInButton)
    public void handIn(View v) {
        Task task = Task.findById(Task.class, (long) v.getTag());
        if (task.getTier() != null && task.itemsCanBeSubmitted()) {
            handInItemTask(task);
        } else if (task.statisticIsAchieved()) {
            handInStatisticTask(task);
        } else {
            AlertHelper.error(this, R.string.alert_unfinished_task, false);
        }

        if (isFirstInstall) {
            runTutorial(5);
        }
        populateSlotInfo();
    }

    public void displayAdvertSuccess() {
        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, getString(R.string.advert_watch_verified) + " " + IncomeHelper.claimAdvertBonus(activity), Toast.LENGTH_LONG).show();
                Log.d("TJ", "Reward Alerted");
            }
        });
    }

    private void handInStatisticTask(Task task) {
        AlertHelper.success(this, String.format(Locale.ENGLISH, getString(R.string.alert_statistic_achieved),
                task.toString(this),
                Slot.getName(this, selectedSlot)), true);
        task.setCompleted(System.currentTimeMillis());
        task.save();
        mapPagerAdapter.notifyDataSetChanged();
    }

    private void handInItemTask(Task task) {
        task.submitItems();
        if (task.getRemaining() == 0) {
            AlertHelper.success(this, String.format(Locale.ENGLISH, getString(R.string.alert_items_submitted),
                    task.toString(this),
                    Slot.getName(this, selectedSlot)), true);
            task.setCompleted(System.currentTimeMillis());
        }
        task.save();
        mapPagerAdapter.notifyDataSetChanged();
    }

    private void populateSlotInfo() {
        if (selectedSlot > 0) {
            findViewById(R.id.noSlotSelected).setVisibility(GONE);
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
        Slot requiredSlot = Slot.get(slot.getRequiredSlot());
        if (requiredSlot != null) {
            superLockedDescription.setText(String.format(Locale.ENGLISH, getString(R.string.slot_superlocked), requiredSlot.getName(this)));
        }
        superLockedSlot.setVisibility(View.VISIBLE);
        lockedSlot.setVisibility(GONE);
        unlockedSlot.setVisibility(GONE);
    }

    private void populateSlotInfoLocked(Slot slot) {
        List<Task> tasks = slot.getTasks();
        Task currentTask = TaskHelper.getCurrentTask(tasks);
        if (currentTask.getStarted() == 0) {
            currentTask.setStarted(System.currentTimeMillis());
            currentTask.save();
        }

        lockedDescription.setText(slot.getLockedText(this));
        lockedTaskRequirement.setText(String.format(Locale.ENGLISH, getString(R.string.task_completion),
                currentTask.getPosition(),
                tasks.size(),
                currentTask.getText(this)));
        lockedTaskProgressText.setText(currentTask.toString(this));
        lockedTaskProgressBar.setMax(currentTask.getTarget());
        lockedTaskProgressBar.setProgress(currentTask.getTarget() - currentTask.getRemaining());
        findViewById(R.id.handInButton).setTag(currentTask.getId());

        superLockedSlot.setVisibility(GONE);
        lockedSlot.setVisibility(View.VISIBLE);
        unlockedSlot.setVisibility(GONE);
    }

    private void populateSlotInfoUnlocked(Slot slot) {
        unlockedDescription.setText(slot.getUnlockedText(this));

        mapPagerAdapter.populateItemContainer(R.id.resourceContainer, slot.getResources(), this);
        mapPagerAdapter.populateItemContainer(R.id.rewardContainer, slot.getRewards(false, false), this);

        superLockedSlot.setVisibility(GONE);
        lockedSlot.setVisibility(GONE);
        unlockedSlot.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        boolean isConnected = GooglePlayHelper.IsConnected();

        googlePlayLoginRow.setVisibility(isConnected ? GONE : View.VISIBLE);
        googlePlayRow.setVisibility(isConnected ? View.VISIBLE : GONE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        GooglePlayHelper.ConnectionFailed(this, connectionResult);
    }

    @Override
    public void onConnectionSuspended(int i) {
        GooglePlayHelper.mGoogleApiClient.connect();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constants.ADVERT_WATCH) {
            if (resultCode > 0) {
                advertHelper.rewardAdvertItems(this);
            }
        } else {
            onConnected(null);
            GooglePlayHelper.ActivityResult(this, requestCode, resultCode);
        }
    }

    @OnClick(R.id.playAchievements)
    public void openAchievements() {
        if (GooglePlayHelper.IsConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(GooglePlayHelper.mGoogleApiClient), GooglePlayHelper.RC_ACHIEVEMENTS);
        }
    }

    @OnClick(R.id.playLeaderboard)
    public void openLeaderboards() {
        if (GooglePlayHelper.IsConnected()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(GooglePlayHelper.mGoogleApiClient), GooglePlayHelper.RC_LEADERBOARDS);
        }
    }

    @OnClick(R.id.playCloudSave)
    public void openCloudSave() {
        if (GooglePlayHelper.IsConnected()) {
            AlertDialogHelper.openOverlayActivity(this, CloudSaveActivity.class);
        }
    }
}
