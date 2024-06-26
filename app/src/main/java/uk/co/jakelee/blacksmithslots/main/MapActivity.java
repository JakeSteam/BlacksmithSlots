package uk.co.jakelee.blacksmithslots.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import uk.co.jakelee.blacksmithslots.helper.MinigameHelper;
import uk.co.jakelee.blacksmithslots.helper.MusicHelper;
import uk.co.jakelee.blacksmithslots.helper.NotificationHelper;
import uk.co.jakelee.blacksmithslots.helper.Runnables;
import uk.co.jakelee.blacksmithslots.helper.SoundHelper;
import uk.co.jakelee.blacksmithslots.helper.TaskHelper;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;
import uk.co.jakelee.blacksmithslots.model.Farm;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Task;
import uk.co.jakelee.blacksmithslots.tourguide.Overlay;
import uk.co.jakelee.blacksmithslots.tourguide.Pointer;
import uk.co.jakelee.blacksmithslots.tourguide.ToolTip;
import uk.co.jakelee.blacksmithslots.tourguide.TourGuide;

import static android.view.View.GONE;
import static uk.co.jakelee.blacksmithslots.model.Setting.getBoolean;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

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
    @BindView(R.id.farmLayout)
    RelativeLayout farmLayout;
    @BindView(R.id.slotUnlockedDescription)
    TextView unlockedDescription;
    @BindView(R.id.watchAdvert)
    TextView watchAdvert;
    @BindView(R.id.winItems)
    TextView winItems;
    @BindView(R.id.farmItems)
    TextView farmItems;
    @BindView(R.id.claimBonus)
    TextView claimBonus;
    @BindView(R.id.googlePlayRow)
    LinearLayout googlePlayRow;
    @BindView(R.id.googlePlayLoginRow)
    LinearLayout googlePlayLoginRow;
    @BindView(R.id.leftArrow) ImageView leftArrow;
    @BindView(R.id.rightArrow) ImageView rightArrow;
    private int selectedSlot = 1;
    private int selectedFarm = 0;
    private final Handler handler = new Handler();
    private MapPagerAdapter mapPagerAdapter;
    public AdvertHelper advertHelper;
    private boolean isFirstInstall = false;
    private TourGuide mTutorialHandler;

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
        updateWinItemsButton();
        updateFarmItemsButton();
    }

    private void updateWinItemsButton() {
        int currentCharges = MinigameHelper.getCurrentCharges();
        int maxCharges = MinigameHelper.getMaxCharges();
        winItems.setBackgroundResource(currentCharges > 0 ? R.drawable.box_green : R.drawable.box_orange);
        winItems.setText(String.format(Locale.ENGLISH, getString(R.string.win_items_button),
                currentCharges,
                maxCharges));
    }

    private void updateFarmItemsButton() {
        int unclaimedItems = Farm.getUnclaimedItems();
        farmItems.setBackgroundResource(unclaimedItems > 0 ? R.drawable.box_green : R.drawable.box_orange);
        farmItems.setText(String.format(Locale.ENGLISH, getString(R.string.farm_items),
                unclaimedItems));
    }

    private void createMapPager() {
        mapPagerAdapter = new MapPagerAdapter(this);
        mapPager.setAdapter(mapPagerAdapter);

        ViewPagerIndicator indicator = findViewById(R.id.view_pager_indicator);
        indicator.setupWithViewPager(mapPager);
        indicator.addOnPageChangeListener(mapChangeListener());
        mapPager.setCurrentItem(prefs.getInt("savedMapPosition", 1), false);
    }

    @OnClick(R.id.farmItems)
    public void farmItems() {
        mapPager.setCurrentItem(0, true);
        if (!prefs.getBoolean("seenFarmInfo", false)) {
            AlertHelper.info(this, getString(R.string.farm_tutorial), false);
            prefs.edit().putBoolean("seenFarmInfo", true).apply();
        }
    }

    @NonNull
    private ViewPager.SimpleOnPageChangeListener mapChangeListener() {
        return new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                SoundHelper.playSound(getApplicationContext(), SoundHelper.swipeSounds);
                leftArrow.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                rightArrow.setVisibility(position == (MapPagerAdapter.townLayouts.length - 1) ? View.INVISIBLE : View.VISIBLE);

                String mapName = TextHelper.getInstance(getApplicationContext()).getText(DisplayHelper.getMapString(position));
                mapTextView.setText(mapName);
                selectedSlot = 0;
                selectedFarm = 0;
                handler.removeCallbacks(populateFarmRunnable);
                loadSidebar(null);

                if (isFirstInstall) {
                    isFirstInstall = false;
                    endTutorial();
                    prefs.edit().putInt("tutorialStageCompleted", 3).apply();
                }

                if (position == 0) {
                    updateFarmCounts();
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

        if (selectedFarm > 0) {
            populateFarmInfo();
        }

        updateWinItemsButton();
    }

    private void updateText() {
        ((TextView) findViewById(R.id.inventory)).setText(R.string.inventory);
        ((TextView) findViewById(R.id.displayHint)).setText(R.string.hints);
        ((TextView) findViewById(R.id.claimBonus)).setText(R.string.claim_bonus);
        ((TextView) findViewById(R.id.openShop)).setText(R.string.shop);
        ((TextView) findViewById(R.id.openTrophy)).setText(R.string.trophies);
        ((TextView) findViewById(R.id.openCredits)).setText(R.string.credits);
        ((TextView) findViewById(R.id.watchAdvert)).setText(R.string.watch_advert);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Runnable everyMinute = new Runnable() {
            @Override
            public void run() {
                GooglePlayHelper.UpdateAchievements();
                updateFarmItemsButton();
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
        if (isFirstInstall) {
            runTutorial(3);
        }
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
        if (getBoolean(Enums.Setting.FarmNotification)) {
            NotificationHelper.addFarmNotification(this, notificationSound);
        }

        handler.removeCallbacks(null);
        prefs.edit().putInt("savedMapPosition", mapPager.getCurrentItem()).apply();
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

    public void selectFarm(View v) {
        selectedSlot = 0;
        selectedFarm = Integer.parseInt((String) v.getTag());
        findViewById(R.id.slotInfo).setVisibility(View.GONE);
        handler.post(populateFarmRunnable);
    }

    @NonNull
    private Runnable populateFarmRunnable = new Runnable() {
        @Override
        public void run() {
            populateFarmInfo();
            handler.postDelayed(this, 1000);
        }
    };

    public void selectSlot(View v) {
        handler.removeCallbacks(populateFarmRunnable);
        selectedFarm = 0;
        selectedSlot = Integer.parseInt((String) v.getTag());
        findViewById(R.id.slotInfo).setVisibility(View.VISIBLE);
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
        if (MinigameHelper.getCurrentCharges() > 0) {
            MusicHelper.getInstance(this).setMovingInApp(true);
            startActivityForResult(new Intent(this, MinigameMemoryActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT), Constants.MINIGAME_MEMORY);
        } else {
            AlertHelper.error(this, String.format(Locale.ENGLISH,
                    getString(R.string.minigame_memory_next_charge),
                    MinigameHelper.getMinsToNextCharge()), false);
        }
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
            advertHelper.showAdvert(this);
        } else {
            AlertHelper.error(this, String.format(Locale.ENGLISH,
                    getString(R.string.error_advert_not_ready),
                    DateHelper.timestampToTime(IncomeHelper.getNextAdvertWatchTime() - System.currentTimeMillis())),
                    false);
        }
    }

    @OnClick({R.id.lockedClose, R.id.unlockedClose, R.id.superlockedClose, R.id.farmClose})
    public void loadSidebar(View v) {
        noSlotSidebar.setVisibility(View.VISIBLE);
        handler.removeCallbacks(populateFarmRunnable);
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

    @OnClick(R.id.changeButton)
    public void changeItem() {
        if (selectedFarm > 0) {
            startActivity(new Intent(this, FarmItemActivity.class)
                    .putExtra(Constants.INTENT_FARM, selectedFarm)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        }
    }

    @OnClick(R.id.upgradeButton)
    public void upgradeFarm() {
        Farm farm = Farm.get(selectedFarm);
        if (selectedFarm > 0 && farm != null) {
            AlertDialogHelper.confirmFarmUpgrade(this, farm, new ItemBundle(farm.getItemTier(), farm.getItemType(), farm.getItemQuantity()));
        }
    }

    @OnClick(R.id.claimButton)
    public void claimFarmItems() {
        Farm farm = Farm.get(selectedFarm);
        if (selectedFarm > 0 && farm != null) {
            if (farm.claim()) {
                populateFarmInfo();
                updateFarmItemsButton();
                updateFarmCounts();
                AlertHelper.success(this, String.format(Locale.ENGLISH, getString(R.string.farm_claim_items), farm.getName(this)), true);
            } else {
                AlertHelper.error(this, getString(R.string.error_farm_claim_none), false);
            }
        }
    }

    private void updateFarmCounts() {
        List<Farm> farms = Farm.listAll(Farm.class);
        for (int i = 1; i <= farms.size(); i++) {
            int indicatorId = getResources().getIdentifier("farm" + i + "indicator", "id", getPackageName());
            TextView indicator = findViewById(indicatorId);
            if (indicator != null && (farms.get(i - 1).getItemTier() > 0 || farms.get(i - 1).getItemType() > 0)) {
                indicator.setText(Integer.toString(farms.get(i - 1).getEarnedQuantity()));
            }
        }
    }

    public void populateFarmInfo() {
        if (selectedFarm > 0) {
            findViewById(R.id.noSlotSelected).setVisibility(GONE);
            superLockedSlot.setVisibility(GONE);
            lockedSlot.setVisibility(GONE);
            unlockedSlot.setVisibility(GONE);
            farmLayout.setVisibility(View.VISIBLE);
            Farm farm = Farm.get(selectedFarm);
            if (farm != null) {
                ((TextView) findViewById(R.id.farmTitle)).setText(String.format(Locale.ENGLISH, getString(R.string.farm_title), farm.getTier(), farm.getName(this)));

                findViewById(R.id.upgradeButton).setVisibility(View.GONE);
                findViewById(R.id.claimButton).setVisibility(View.GONE);
                findViewById(R.id.changeButton).setVisibility(View.GONE);
                if (TaskHelper.isSlotLocked(farm.getRequiredSlot())) {
                    ((TextView) findViewById(R.id.farmDesc)).setText(String.format(Locale.ENGLISH, getString(R.string.farm_locked_desc), Slot.getName(this, farm.getRequiredSlot())));
                } else if (farm.getItemTier() == 0 && farm.getItemType() == 0) {
                    findViewById(R.id.changeButton).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.farmDesc)).setText(R.string.farm_unlocked_unselected_desc);
                } else {
                    findViewById(R.id.changeButton).setVisibility(View.VISIBLE);
                    findViewById(R.id.upgradeButton).setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.upgradeButton)).setText(String.format(Locale.ENGLISH, "Upgrade (%1$dx)", farm.getUpgradeCost()));
                    findViewById(R.id.claimButton).setVisibility(View.VISIBLE);

                    int earnedCapacity = farm.getEarnedQuantity();
                    int currentCapacity = farm.getCurrentCapacity();
                    ((TextView) findViewById(R.id.farmDesc)).setText(String.format(Locale.ENGLISH, getString(R.string.farm_unlocked_desc),
                            earnedCapacity,
                            currentCapacity,
                            Inventory.getName(this, farm.getItemTier(), farm.getItemType()),
                            farm.getItemQuantity(),
                            DateHelper.timestampToShortTime(farm.getClaimTime()),
                            earnedCapacity == currentCapacity ? "N/A" : DateHelper.timestampToShortTime(farm.getTimeToNextEarn())));
                }
            }
        }
    }

    private void populateSlotInfo() {
        farmLayout.setVisibility(GONE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == Constants.ADVERT_WATCH) {
            if (resultCode > 0) {
                displayAdvertSuccess();
            }
        } else if (requestCode == Constants.MINIGAME_MEMORY) {
            if (resultCode > 0) {
                AlertHelper.success(this, getString(R.string.minigame_memory_earned) + intent.getStringExtra("winningsString"), true);
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
