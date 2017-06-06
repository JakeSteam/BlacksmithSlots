package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.SplashScreenActivity;
import uk.co.jakelee.blacksmithslots.model.Achievement;
import uk.co.jakelee.blacksmithslots.model.Iap;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Message;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Statistic;
import uk.co.jakelee.blacksmithslots.model.Task;
import uk.co.jakelee.blacksmithslots.model.Trophy;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHelper extends AsyncTask<String, String, String> {
    public final static int NO_DATABASE = 0;
    public final static int V0_0_1 = 1;
    //public final static int V0_0_2 = 2;

    public final static int LATEST_PATCH = V0_0_1;

    private Context context;
    private SplashScreenActivity callingActivity;
    private TextView progressText;
    private ProgressBar progressBar;

    public DatabaseHelper() {
    }

    public DatabaseHelper(SplashScreenActivity activity) {
        this.context = activity;
        this.callingActivity = activity;
        this.progressText = (TextView) activity.findViewById(R.id.progressText);
        this.progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
    }

    public DatabaseHelper(Context context) {
        this.context = context;
    }

    private void createDatabase() {
        MusicHelper.getInstance(context).playIfPossible(R.raw.time_passes);
        long start = System.currentTimeMillis();
        setProgress("Settings", 15);
        createSettings();
        setProgress("Inventory", 30);
        createInventories();
        setProgress("Shop", 45);
        createShop();
        setProgress("Maps", 60);
        createSlotsMap1();
        setProgress("Statistics", 75);
        createStatistics();
        setProgress("Achievements", 90);
        createAchievements();
        Log.d("TimeTaken", "Main db: " + (System.currentTimeMillis() - start));
    }

    private void setProgress(String currentTask, int percentage) {
        if (progressText != null && progressBar != null) {
            publishProgress(percentage + "%: " + currentTask);
            progressBar.setProgress(percentage);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        SharedPreferences prefs = context.getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);
        boolean appliedDbChanges = false;

        if (prefs.getInt("databaseVersion", DatabaseHelper.NO_DATABASE) <= DatabaseHelper.NO_DATABASE) {
            callingActivity.startIntro();
            createDatabase();
            prefs.edit().putInt("databaseVersion", V0_0_1).apply();
            appliedDbChanges = true;
            callingActivity.isFirstInstall = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    createOtherMaps();
                }
            }).start();
        }

        /*if (prefs.getInt("databaseVersion", DatabaseHelper.NO_DATABASE) < DatabaseHelper.V0_0_2) {
            if (!isFirstInstall) {
                callingActivity.setStoryText("This is a shiny new version, with new features!");
            }
            setProgress("Patch 0.0.2", 30);
            //patchTo002();
            appliedDbChanges = true;
            prefs.edit().putInt("databaseVersion", DatabaseHelper.V0_0_2).apply();
        }*/

        if (appliedDbChanges) {
            setProgress("Game installed!", 100);
        } else {
            callingActivity.startGame();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.length() > 0) {
            Message.log(result);
            AlertHelper.success(callingActivity, result, true);
        }

        if (callingActivity != null) {
            callingActivity.enableStartButton();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progressText.setText(values[0]);
    }

    private void createInventories() {
        List<Inventory> inventories = new ArrayList<>();
        inventories.add(new Inventory(Enums.Tier.None, Enums.Type.LuckyCoin, 100));
        inventories.add(new Inventory(Enums.Tier.Bronze, Enums.Type.Hatchet, 1));
        Inventory.saveInTx(inventories);
    }

    private void createShop() {
        List<Iap> iaps = new ArrayList<>();
        List<ItemBundle> iapBundles = new ArrayList<>();
        // VIP Levels
        iaps.add(new Iap(Enums.Iap.VipLevel1, true));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel1, Enums.Tier.Bronze, Enums.Type.Bar, 1000));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel1, Enums.Tier.Bronze, Enums.Type.Secondary, 1000));

        iaps.add(new Iap(Enums.Iap.VipLevel2, true));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel2, Enums.Tier.Iron, Enums.Type.Bar, 1000));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel2, Enums.Tier.Iron, Enums.Type.Secondary, 1000));

        iaps.add(new Iap(Enums.Iap.VipLevel3, true));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel3, Enums.Tier.Steel, Enums.Type.Bar, 1000));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel3, Enums.Tier.Steel, Enums.Type.Secondary, 1000));

        iaps.add(new Iap(Enums.Iap.VipLevel4, true));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel4, Enums.Tier.Mithril, Enums.Type.Bar, 1000));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel4, Enums.Tier.Mithril, Enums.Type.Secondary, 1000));

        iaps.add(new Iap(Enums.Iap.VipLevel5, true));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel5, Enums.Tier.None, Enums.Type.GemRed, 75));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel5, Enums.Tier.None, Enums.Type.GemBlue, 75));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel5, Enums.Tier.None, Enums.Type.GemGreen, 75));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel5, Enums.Tier.None, Enums.Type.GemOrange, 75));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel5, Enums.Tier.None, Enums.Type.GemYellow, 75));

        iaps.add(new Iap(Enums.Iap.VipLevel6, true));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel6, Enums.Tier.Bronze, Enums.Type.Bar, 500));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel6, Enums.Tier.Bronze, Enums.Type.Secondary, 500));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel6, Enums.Tier.Iron, Enums.Type.Bar, 500));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel6, Enums.Tier.Iron, Enums.Type.Secondary, 500));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel6, Enums.Tier.Steel, Enums.Type.Bar, 500));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel6, Enums.Tier.Steel, Enums.Type.Secondary, 500));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel6, Enums.Tier.Mithril, Enums.Type.Bar, 500));
        iapBundles.add(new ItemBundle(Enums.Iap.VipLevel6, Enums.Tier.Mithril, Enums.Type.Secondary, 500));

        // Blacksmith's Pass
        iaps.add(new Iap(Enums.Iap.BlacksmithPass, 0, 0, false));
        iapBundles.add(new ItemBundle(1, Enums.Tier.Bronze, Enums.Type.Ore, 100));
        iapBundles.add(new ItemBundle(2, Enums.Tier.Bronze, Enums.Type.Ore, 120));
        iapBundles.add(new ItemBundle(3, Enums.Tier.Bronze, Enums.Type.Ore, 130));
        iapBundles.add(new ItemBundle(4, Enums.Tier.Bronze, Enums.Type.Ore, 140));
        iapBundles.add(new ItemBundle(5, Enums.Tier.Bronze, Enums.Type.Ore, 150));
        iapBundles.add(new ItemBundle(6, Enums.Tier.Bronze, Enums.Type.Ore, 160));
        iapBundles.add(new ItemBundle(7, Enums.Tier.Bronze, Enums.Type.Ore, 170));
        iapBundles.add(new ItemBundle(8, Enums.Tier.Bronze, Enums.Type.Ore, 180));
        iapBundles.add(new ItemBundle(9, Enums.Tier.Bronze, Enums.Type.Ore, 190));
        iapBundles.add(new ItemBundle(10, Enums.Tier.Bronze, Enums.Type.Ore, 250));
        iapBundles.add(new ItemBundle(11, Enums.Tier.Iron, Enums.Type.Ore, 100));
        iapBundles.add(new ItemBundle(12, Enums.Tier.Iron, Enums.Type.Ore, 120));
        iapBundles.add(new ItemBundle(13, Enums.Tier.Iron, Enums.Type.Ore, 130));
        iapBundles.add(new ItemBundle(14, Enums.Tier.Iron, Enums.Type.Ore, 140));
        iapBundles.add(new ItemBundle(15, Enums.Tier.Iron, Enums.Type.Ore, 150));
        iapBundles.add(new ItemBundle(16, Enums.Tier.Iron, Enums.Type.Ore, 160));
        iapBundles.add(new ItemBundle(17, Enums.Tier.Iron, Enums.Type.Ore, 170));
        iapBundles.add(new ItemBundle(18, Enums.Tier.Iron, Enums.Type.Ore, 180));
        iapBundles.add(new ItemBundle(19, Enums.Tier.Iron, Enums.Type.Ore, 190));
        iapBundles.add(new ItemBundle(20, Enums.Tier.Iron, Enums.Type.Ore, 250));
        iapBundles.add(new ItemBundle(20, Enums.Tier.Bronze, Enums.Type.Ore, 250));
        iapBundles.add(new ItemBundle(21, Enums.Tier.Iron, Enums.Type.Ore, 100));
        iapBundles.add(new ItemBundle(22, Enums.Tier.Iron, Enums.Type.Ore, 120));
        iapBundles.add(new ItemBundle(23, Enums.Tier.Iron, Enums.Type.Ore, 130));
        iapBundles.add(new ItemBundle(24, Enums.Tier.Iron, Enums.Type.Ore, 140));
        iapBundles.add(new ItemBundle(25, Enums.Tier.Iron, Enums.Type.Ore, 150));
        iapBundles.add(new ItemBundle(26, Enums.Tier.Iron, Enums.Type.Ore, 160));
        iapBundles.add(new ItemBundle(27, Enums.Tier.Iron, Enums.Type.Ore, 170));
        iapBundles.add(new ItemBundle(28, Enums.Tier.Iron, Enums.Type.Ore, 180));
        iapBundles.add(new ItemBundle(29, Enums.Tier.Bronze, Enums.Type.Ore, 190));
        iapBundles.add(new ItemBundle(29, Enums.Tier.Iron, Enums.Type.Ore, 190));
        iapBundles.add(new ItemBundle(29, Enums.Tier.Steel, Enums.Type.Ore, 190));
        iapBundles.add(new ItemBundle(30, Enums.Tier.Bronze, Enums.Type.Ore, 250));
        iapBundles.add(new ItemBundle(30, Enums.Tier.Iron, Enums.Type.Ore, 250));
        iapBundles.add(new ItemBundle(30, Enums.Tier.Steel, Enums.Type.Ore, 250));
        iapBundles.add(new ItemBundle(31, Enums.Tier.Steel, Enums.Type.Ore, 1000));

        // Bundles
        iaps.add(new Iap(Enums.Iap.BronzeOre1000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.BronzeOre1000, Enums.Tier.Bronze, Enums.Type.Ore, 1000, 149));
        iaps.add(new Iap(Enums.Iap.BronzeOre5000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.BronzeOre5000, Enums.Tier.Bronze, Enums.Type.Ore, 5000, 399));
        iaps.add(new Iap(Enums.Iap.BronzeOre10000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.BronzeOre10000, Enums.Tier.Bronze, Enums.Type.Ore, 10000, 649));

        iaps.add(new Iap(Enums.Iap.BronzeSecondary1000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.BronzeSecondary1000, Enums.Tier.Bronze, Enums.Type.Secondary, 1000, 149));
        iaps.add(new Iap(Enums.Iap.BronzeSecondary5000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.BronzeSecondary5000, Enums.Tier.Bronze, Enums.Type.Secondary, 5000, 399));
        iaps.add(new Iap(Enums.Iap.BronzeSecondary10000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.BronzeSecondary10000, Enums.Tier.Bronze, Enums.Type.Secondary, 10000, 649));

        iaps.add(new Iap(Enums.Iap.IronOre1000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.IronOre1000, Enums.Tier.Iron, Enums.Type.Ore, 1000, 199));
        iaps.add(new Iap(Enums.Iap.IronOre5000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.IronOre5000, Enums.Tier.Iron, Enums.Type.Ore, 5000, 499));
        iaps.add(new Iap(Enums.Iap.IronOre10000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.IronOre10000, Enums.Tier.Iron, Enums.Type.Ore, 10000, 799));

        iaps.add(new Iap(Enums.Iap.IronSecondary1000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.IronSecondary1000, Enums.Tier.Iron, Enums.Type.Secondary, 1000, 199));
        iaps.add(new Iap(Enums.Iap.IronSecondary5000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.IronSecondary5000, Enums.Tier.Iron, Enums.Type.Secondary, 5000, 499));
        iaps.add(new Iap(Enums.Iap.IronSecondary10000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.IronSecondary10000, Enums.Tier.Iron, Enums.Type.Secondary, 10000, 799));

        iaps.add(new Iap(Enums.Iap.SteelOre1000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.SteelOre1000, Enums.Tier.Steel, Enums.Type.Ore, 1000, 249));
        iaps.add(new Iap(Enums.Iap.SteelOre5000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.SteelOre5000, Enums.Tier.Steel, Enums.Type.Ore, 5000, 649));
        iaps.add(new Iap(Enums.Iap.SteelOre10000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.SteelOre10000, Enums.Tier.Steel, Enums.Type.Ore, 10000, 999));

        iaps.add(new Iap(Enums.Iap.SteelSecondary1000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.SteelSecondary1000, Enums.Tier.Steel, Enums.Type.Secondary, 1000, 249));
        iaps.add(new Iap(Enums.Iap.SteelSecondary5000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.SteelSecondary5000, Enums.Tier.Steel, Enums.Type.Secondary, 5000, 649));
        iaps.add(new Iap(Enums.Iap.SteelSecondary10000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.SteelSecondary10000, Enums.Tier.Steel, Enums.Type.Secondary, 10000, 999));

        iaps.add(new Iap(Enums.Iap.MithrilOre1000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.MithrilOre1000, Enums.Tier.Mithril, Enums.Type.Ore, 1000, 299));
        iaps.add(new Iap(Enums.Iap.MithrilOre5000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.MithrilOre5000, Enums.Tier.Mithril, Enums.Type.Ore, 5000, 749));
        iaps.add(new Iap(Enums.Iap.MithrilOre10000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.MithrilOre10000, Enums.Tier.Mithril, Enums.Type.Ore, 10000, 1199));

        iaps.add(new Iap(Enums.Iap.MithrilSecondary1000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.MithrilSecondary1000, Enums.Tier.Mithril, Enums.Type.Secondary, 1000, 299));
        iaps.add(new Iap(Enums.Iap.MithrilSecondary5000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.MithrilSecondary5000, Enums.Tier.Mithril, Enums.Type.Secondary, 5000, 749));
        iaps.add(new Iap(Enums.Iap.MithrilSecondary10000, false));
        iapBundles.add(new ItemBundle(Enums.Iap.MithrilSecondary10000, Enums.Tier.Mithril, Enums.Type.Secondary, 10000, 1199));

        iaps.add(new Iap(Enums.Iap.GemBlue, false));
        iapBundles.add(new ItemBundle(Enums.Iap.GemBlue, Enums.Tier.None, Enums.Type.GemBlue, 75, 149));
        iaps.add(new Iap(Enums.Iap.GemGreen, false));
        iapBundles.add(new ItemBundle(Enums.Iap.GemGreen, Enums.Tier.None, Enums.Type.GemGreen, 75, 149));
        iaps.add(new Iap(Enums.Iap.GemOrange, false));
        iapBundles.add(new ItemBundle(Enums.Iap.GemOrange, Enums.Tier.None, Enums.Type.GemOrange, 75, 149));
        iaps.add(new Iap(Enums.Iap.GemRed, false));
        iapBundles.add(new ItemBundle(Enums.Iap.GemRed, Enums.Tier.None, Enums.Type.GemRed, 75, 149));
        iaps.add(new Iap(Enums.Iap.GemYellow, false));
        iapBundles.add(new ItemBundle(Enums.Iap.GemYellow, Enums.Tier.None, Enums.Type.GemYellow, 75, 149));

        Iap.saveInTx(iaps);
        ItemBundle.saveInTx(iapBundles);
    }

    private void createSettings() {
        List<Setting> settings = new ArrayList<>();
        settings.add(new Setting(Enums.SettingGroup.Audio, Enums.Setting.Music, true));
        settings.add(new Setting(Enums.SettingGroup.Audio, Enums.Setting.Sound, true));
        settings.add(new Setting(Enums.SettingGroup.Internal, Enums.Setting.AttemptLogin, true));
        settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.OnlyActiveResources, true));
        settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.Language, Enums.Language.English.value));
        settings.add(new Setting(Enums.SettingGroup.Notifications, Enums.Setting.NotificationSounds, true));
        settings.add(new Setting(Enums.SettingGroup.Notifications, Enums.Setting.PeriodicBonusNotification, true));
        settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.SaveImported, false));
        settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.OnlyShowStocked, false));
        settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.OrderByTier, false));
        settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.OrderReversed, false));
        settings.add(new Setting(Enums.SettingGroup.Notifications, Enums.Setting.BlacksmithPassNotification, true));
        settings.add(new Setting(Enums.SettingGroup.GooglePlay, Enums.Setting.PlayLogout, true));
        settings.add(new Setting(Enums.SettingGroup.GooglePlay, Enums.Setting.Autosave, false));
        Setting.saveInTx(settings);
    }

    private void createSlotsMap1() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        slots.add(new Slot(Enums.Slot.Map1Mom, 1, 2, 2, Enums.Person.Mom, Enums.Map.Home));
        itemBundles.add(new ItemBundle(Enums.Slot.Map1Mom, Enums.Tier.None, Enums.Type.LuckyCoin, 1));
        tasks.add(new Task(Enums.Slot.Map1Mom, 1, Enums.Tier.Bronze, Enums.Type.Hatchet, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map1Mom, Enums.Tier.Bronze, Enums.Type.Ore, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map1Mom, Enums.Tier.Bronze, Enums.Type.Secondary, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map1Mom, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));
        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createOtherMaps() {
        long start = System.currentTimeMillis();
        createSlotsMap2();
        createSlotsMap3();
        createSlotsMap4();
        createSlotsMap5();
        createSlotsMap6();
        createSlotsMap7();
        createSlotsMap8();
        createSlotsMap9();
        createSlotsMap10();
        createSlotsMap11();
        createSlotsMap12();
        createSlotsMap13();
        createSlotsMap14();
        createSlotsMap15();
        createSlotsMap16();
        createSlotsMap17();
        createSlotsMap18();
        createSlotsMap19();
        createSlotsMap20();
        Log.d("TimeTaken", "Maps 2-20: " + (System.currentTimeMillis() - start));
        Log.d("TimeTaken", "--");
    }

    private void createSlotsMap2() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        slots.add(new Slot(Enums.Slot.Map2Furnace, 1, 5, 4, Enums.Person.LowLevelBlacksmith, Enums.Map.Neighbourhood));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Furnace, Enums.Tier.Bronze, Enums.Type.Ore, 1));
        tasks.add(new Task(Enums.Slot.Map2Furnace, 1, Enums.Tier.Bronze, Enums.Type.Ore, 15));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Furnace, Enums.Tier.Bronze, Enums.Type.Bar, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Furnace, Enums.Tier.Bronze, Enums.Type.Ore, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Furnace, Enums.Tier.Bronze, Enums.Type.Ore, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Furnace, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Furnace, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map2Accessories, 1, 5, 3, Enums.Person.Woman, Enums.Map.Neighbourhood));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map2Accessories, 1, Enums.Tier.Bronze, Enums.Type.Bar, 20));
        tasks.add(new Task(Enums.Slot.Map2Accessories, 2, Enums.Tier.Bronze, Enums.Type.Secondary, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.Boots, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.Gloves, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.FullHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map2Tools, 1, 7, 5, Enums.Person.Man, Enums.Map.Neighbourhood));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map2Tools, 1, Enums.Tier.Bronze, Enums.Type.Boots, 20));
        tasks.add(new Task(Enums.Slot.Map2Tools, 2, Enums.Tier.Bronze, Enums.Type.Gloves, 20));
        tasks.add(new Task(Enums.Slot.Map2Tools, 3, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 20));
        tasks.add(new Task(Enums.Slot.Map2Tools, 4, Enums.Tier.Bronze, Enums.Type.FullHelmet, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.Pickaxe, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.Hatchet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.FishingRod, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.Hammer, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 8));

        slots.add(new Slot(Enums.Slot.Map2Weapons, 1, 8, 3, Enums.Person.LowLevelBlacksmith, Enums.Map.Neighbourhood));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Bar, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Secondary, 2));
        tasks.add(new Task(Enums.Slot.Map2Weapons, 1, Enums.Tier.Bronze, Enums.Type.FishingRod, 7));
        tasks.add(new Task(Enums.Slot.Map2Weapons, 2, Enums.Statistic.Level, 2));
        tasks.add(new Task(Enums.Slot.Map2Weapons, 3, Enums.Statistic.TotalSpins, 30));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Dagger, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Sword, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Longsword, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Bow, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map2Armour, 1, 9, 2, Enums.Person.LowLevelBlacksmith, Enums.Map.Neighbourhood));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Bronze, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Bronze, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map2Armour, 1, Enums.Statistic.CollectedBonuses, 1));
        tasks.add(new Task(Enums.Slot.Map2Armour, 2, Enums.Tier.Bronze, Enums.Type.Boots, 5));
        tasks.add(new Task(Enums.Slot.Map2Armour, 3, Enums.Tier.Bronze, Enums.Type.Gloves, 5));
        tasks.add(new Task(Enums.Slot.Map2Armour, 4, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 5));
        tasks.add(new Task(Enums.Slot.Map2Armour, 5, Enums.Tier.Bronze, Enums.Type.FullHelmet, 5));
        tasks.add(new Task(Enums.Slot.Map2Armour, 6, Enums.Tier.Bronze, Enums.Type.Pickaxe, 5));
        tasks.add(new Task(Enums.Slot.Map2Armour, 7, Enums.Tier.Bronze, Enums.Type.Hatchet, 5));
        tasks.add(new Task(Enums.Slot.Map2Armour, 8, Enums.Tier.Bronze, Enums.Type.FishingRod, 5));
        tasks.add(new Task(Enums.Slot.Map2Armour, 9, Enums.Tier.Bronze, Enums.Type.Hammer, 5));
        tasks.add(new Task(Enums.Slot.Map2Armour, 10, Enums.Tier.Bronze, Enums.Type.Dagger, 5));
        tasks.add(new Task(Enums.Slot.Map2Armour, 11, Enums.Tier.Bronze, Enums.Type.Sword, 5));
        tasks.add(new Task(Enums.Slot.Map2Armour, 12, Enums.Tier.Bronze, Enums.Type.Longsword, 5));
        tasks.add(new Task(Enums.Slot.Map2Armour, 13, Enums.Tier.Bronze, Enums.Type.Bow, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Bronze, Enums.Type.Chainmail, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Bronze, Enums.Type.Platebody, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Bronze, Enums.Type.HalfShield, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Bronze, Enums.Type.FullShield, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 4));
        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap3() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        slots.add(new Slot(Enums.Slot.Map3Mouse, 1, 10, 3, Enums.Person.Mouse, Enums.Map.Forest));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse, Enums.Tier.Bronze, Enums.Type.Gloves, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse, Enums.Tier.Bronze, Enums.Type.Boots, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse, Enums.Tier.Bronze, Enums.Type.FullHelmet, 1));
        tasks.add(new Task(Enums.Slot.Map3Mouse, 1, Enums.Statistic.Level, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse, Enums.Tier.PartialFood, Enums.Type.Apple, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse, Enums.Tier.PartialFood, Enums.Type.Apple, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse, Enums.Tier.PartialFood, Enums.Type.Lime, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse, Enums.Tier.PartialFood, Enums.Type.Lime, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse, Enums.Tier.PartialFood, Enums.Type.Orange, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse, Enums.Tier.PartialFood, Enums.Type.Orange, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 15));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map3Snail, 1, 15, 4, Enums.Person.Snail, Enums.Map.Forest));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Snail, Enums.Tier.Bronze, Enums.Type.Pickaxe, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Snail, Enums.Tier.Bronze, Enums.Type.Hatchet, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Snail, Enums.Tier.Bronze, Enums.Type.Hammer, 1));
        tasks.add(new Task(Enums.Slot.Map3Snail, 1, Enums.Tier.PartialFood, Enums.Type.Apple, 40));
        tasks.add(new Task(Enums.Slot.Map3Snail, 2, Enums.Tier.PartialFood, Enums.Type.Lime, 40));
        tasks.add(new Task(Enums.Slot.Map3Snail, 3, Enums.Tier.PartialFood, Enums.Type.Orange, 40));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Snail, Enums.Tier.PartialFood, Enums.Type.Peach, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Snail, Enums.Tier.PartialFood, Enums.Type.Peach, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Snail, Enums.Tier.PartialFood, Enums.Type.Pineapple, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Snail, Enums.Tier.PartialFood, Enums.Type.Pineapple, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Snail, Enums.Tier.PartialFood, Enums.Type.Banana, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Snail, Enums.Tier.PartialFood, Enums.Type.Banana, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Snail, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 13));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Snail, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 9));

        slots.add(new Slot(Enums.Slot.Map3Human, 1, 15, 5, Enums.Person.Caveman, Enums.Map.Forest));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Human, Enums.Tier.Bronze, Enums.Type.HalfShield, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Human, Enums.Tier.Bronze, Enums.Type.FishingRod, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Human, Enums.Tier.Bronze, Enums.Type.Dagger, 1));
        tasks.add(new Task(Enums.Slot.Map3Human, 1, Enums.Statistic.CollectedBonuses, 1));
        tasks.add(new Task(Enums.Slot.Map3Human, 2, Enums.Tier.PartialFood, Enums.Type.Banana, 50));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Human, Enums.Tier.PartialFood, Enums.Type.Steak, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Human, Enums.Tier.PartialFood, Enums.Type.Steak, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Human, Enums.Tier.PartialFood, Enums.Type.Potato, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Human, Enums.Tier.PartialFood, Enums.Type.Potato, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Human, Enums.Tier.PartialFood, Enums.Type.Egg, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Human, Enums.Tier.PartialFood, Enums.Type.Egg, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Human, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 18));

        slots.add(new Slot(Enums.Slot.Map3Frog, 1, 15, 4, Enums.Person.Frog, Enums.Map.Forest));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Frog, Enums.Tier.Bronze, Enums.Type.Sword, 15));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Frog, Enums.Tier.Bronze, Enums.Type.Longsword, 15));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Frog, Enums.Tier.Bronze, Enums.Type.Bow, 15));
        tasks.add(new Task(Enums.Slot.Map3Frog, 1, Enums.Tier.PartialFood, Enums.Type.Apple, 50));
        tasks.add(new Task(Enums.Slot.Map3Frog, 2, Enums.Tier.PartialFood, Enums.Type.Peach, 50));
        tasks.add(new Task(Enums.Slot.Map3Frog, 3, Enums.Tier.PartialFood, Enums.Type.Steak, 50));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Frog, Enums.Tier.PartialFood, Enums.Type.Steak, 20, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Frog, Enums.Tier.PartialFood, Enums.Type.Steak, 100, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Frog, Enums.Tier.PartialFood, Enums.Type.Potato, 20, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Frog, Enums.Tier.PartialFood, Enums.Type.Potato, 100, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Frog, Enums.Tier.PartialFood, Enums.Type.Egg, 20, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Frog, Enums.Tier.PartialFood, Enums.Type.Egg, 100, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Frog, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 13));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Frog, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 6));

        slots.add(new Slot(Enums.Slot.Map3Mouse2, 1, 15, 2, Enums.Person.Mouse2, Enums.Map.Forest));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse2, Enums.Tier.Bronze, Enums.Type.Chainmail, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse2, Enums.Tier.Bronze, Enums.Type.Platebody, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse2, Enums.Tier.Bronze, Enums.Type.FullShield, 1));
        tasks.add(new Task(Enums.Slot.Map3Mouse2, 1, Enums.Statistic.TotalSpins, 1000));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse2, Enums.Tier.PartialFood, Enums.Type.Cherry, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse2, Enums.Tier.PartialFood, Enums.Type.Cherry, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse2, Enums.Tier.PartialFood, Enums.Type.Watermelon, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse2, Enums.Tier.PartialFood, Enums.Type.Watermelon, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse2, Enums.Tier.PartialFood, Enums.Type.Grapes, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse2, Enums.Tier.PartialFood, Enums.Type.Grapes, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map3Mouse2, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap4() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        slots.add(new Slot(Enums.Slot.Map4Fruit, 1, 15, 3, Enums.Person.Man, Enums.Map.Marketplace));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit, Enums.Tier.PartialFood, Enums.Type.Apple, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit, Enums.Tier.PartialFood, Enums.Type.Lime, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit, Enums.Tier.PartialFood, Enums.Type.Orange, 1));
        tasks.add(new Task(Enums.Slot.Map4Fruit, 1, Enums.Tier.Bronze, Enums.Type.Bar, 20));
        tasks.add(new Task(Enums.Slot.Map4Fruit, 2, Enums.Statistic.ResourcesWon, 3000));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit, Enums.Tier.None, Enums.Type.Apple, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit, Enums.Tier.None, Enums.Type.Lime, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit, Enums.Tier.None, Enums.Type.Orange, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit, Enums.Tier.None, Enums.Type.GemYellow, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 10));

        slots.add(new Slot(Enums.Slot.Map4Fruit2, 1, 15, 5, Enums.Person.Woman, Enums.Map.Marketplace));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit2, Enums.Tier.PartialFood, Enums.Type.Peach, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit2, Enums.Tier.PartialFood, Enums.Type.Pineapple, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit2, Enums.Tier.PartialFood, Enums.Type.Banana, 1));
        tasks.add(new Task(Enums.Slot.Map4Fruit2, 1, Enums.Tier.None, Enums.Type.Apple, 20));
        tasks.add(new Task(Enums.Slot.Map4Fruit2, 2, Enums.Tier.None, Enums.Type.Lime, 20));
        tasks.add(new Task(Enums.Slot.Map4Fruit2, 3, Enums.Tier.None, Enums.Type.Orange, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit2, Enums.Tier.None, Enums.Type.Peach, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit2, Enums.Tier.None, Enums.Type.Pineapple, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit2, Enums.Tier.None, Enums.Type.Banana, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit2, Enums.Tier.None, Enums.Type.GemOrange, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit2, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 16));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit2, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 5));

        slots.add(new Slot(Enums.Slot.Map4Fruit3, 2, 10, 3, Enums.Person.Man, Enums.Map.Marketplace));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit3, Enums.Tier.PartialFood, Enums.Type.Cherry, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit3, Enums.Tier.PartialFood, Enums.Type.Watermelon, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit3, Enums.Tier.PartialFood, Enums.Type.Grapes, 1));
        tasks.add(new Task(Enums.Slot.Map4Fruit3, 1, Enums.Statistic.Level, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit3, Enums.Tier.None, Enums.Type.Cherry, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit3, Enums.Tier.None, Enums.Type.Watermelon, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit3, Enums.Tier.None, Enums.Type.Grapes, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit3, Enums.Tier.None, Enums.Type.GemGreen, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit3, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Fruit3, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 10));

        slots.add(new Slot(Enums.Slot.Map4Purple, 2, 5, 2, Enums.Person.PurpleBlob, Enums.Map.Marketplace));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Purple, Enums.Tier.Iron, Enums.Type.Bar, 20));
        tasks.add(new Task(Enums.Slot.Map4Purple, 1, Enums.Statistic.Level, 20));
        tasks.add(new Task(Enums.Slot.Map4Purple, 2, Enums.Tier.Mithril, Enums.Type.Platebody, 1000));
        tasks.add(new Task(Enums.Slot.Map4Purple, 3, Enums.Tier.Mithril, Enums.Type.Longsword, 1000));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Purple, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Purple, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Purple, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Purple, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 10));

        slots.add(new Slot(Enums.Slot.Map4Guard, 1, 5, 2, Enums.Slot.Map4Fruit3, Enums.Person.Guard, Enums.Map.Marketplace));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Guard, Enums.Tier.PartialFood, Enums.Type.Steak, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Guard, Enums.Tier.PartialFood, Enums.Type.Potato, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Guard, Enums.Tier.PartialFood, Enums.Type.Egg, 1));
        tasks.add(new Task(Enums.Slot.Map4Guard, 1, Enums.Tier.PartialFood, Enums.Type.Steak, 35));
        tasks.add(new Task(Enums.Slot.Map4Guard, 2, Enums.Tier.PartialFood, Enums.Type.Potato, 35));
        tasks.add(new Task(Enums.Slot.Map4Guard, 3, Enums.Tier.PartialFood, Enums.Type.Egg, 35));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Guard, Enums.Tier.None, Enums.Type.Steak, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Guard, Enums.Tier.None, Enums.Type.Potato, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Guard, Enums.Tier.None, Enums.Type.Egg, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map4Guard, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 4));
        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap5() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        slots.add(new Slot(Enums.Slot.Map5Gate, 1, 30, 3, Enums.Person.Guard, Enums.Map.Castle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.PartialFood, Enums.Type.Apple, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.PartialFood, Enums.Type.Lime, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.PartialFood, Enums.Type.Orange, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.PartialFood, Enums.Type.Peach, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.PartialFood, Enums.Type.Pineapple, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.PartialFood, Enums.Type.Banana, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.PartialFood, Enums.Type.Cherry, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.PartialFood, Enums.Type.Watermelon, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.PartialFood, Enums.Type.Grapes, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.Iron, Enums.Type.Ore, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.Iron, Enums.Type.Ore, 50, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.Iron, Enums.Type.Secondary, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.Iron, Enums.Type.Secondary, 50, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 8));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Gate, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map5Vault, 1, 5, 3, Enums.Person.Guard, Enums.Map.Castle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Vault, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 2));
        tasks.add(new Task(Enums.Slot.Map5Vault, 1, Enums.Tier.Bronze, Enums.Type.Sword, 30));
        tasks.add(new Task(Enums.Slot.Map5Vault, 2, Enums.Tier.Bronze, Enums.Type.Platebody, 30));
        tasks.add(new Task(Enums.Slot.Map5Vault, 3, Enums.Tier.Bronze, Enums.Type.FullHelmet, 30));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Vault, Enums.Tier.Iron, Enums.Type.Ore, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Vault, Enums.Tier.None, Enums.Type.GemYellow, 1, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Vault, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));

        slots.add(new Slot(Enums.Slot.Map5Worm, 1, 5, 4, Enums.Person.Worm, Enums.Map.Castle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Worm, Enums.Tier.Bronze, Enums.Type.Gloves, 1));
        tasks.add(new Task(Enums.Slot.Map5Worm, 1, Enums.Tier.None, Enums.Type.Apple, 150));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Worm, Enums.Tier.Iron, Enums.Type.Ore, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Worm, Enums.Tier.Iron, Enums.Type.Secondary, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Worm, Enums.Tier.Bronze, Enums.Type.Ore, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Worm, Enums.Tier.Bronze, Enums.Type.Secondary, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Worm, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Worm, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map5Study, 1, 5, 3, Enums.Person.GroupOfPeople, Enums.Map.Castle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Study, Enums.Tier.None, Enums.Type.Steak, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Study, Enums.Tier.None, Enums.Type.Potato, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Study, Enums.Tier.None, Enums.Type.Egg, 1));
        tasks.add(new Task(Enums.Slot.Map5Study, 1, Enums.Tier.Bronze, Enums.Type.Hatchet, 30));
        tasks.add(new Task(Enums.Slot.Map5Study, 2, Enums.Statistic.Level, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Study, Enums.Tier.Iron, Enums.Type.Hatchet, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Study, Enums.Tier.Iron, Enums.Type.Secondary, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Study, Enums.Tier.Iron, Enums.Type.Secondary, 10, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map5Study, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap6() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        slots.add(new Slot(Enums.Slot.Map6Exit, 1, 6, 3, Enums.Person.Guard, Enums.Map.Mines));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Exit, Enums.Tier.Bronze, Enums.Type.Longsword, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Exit, Enums.Tier.None, Enums.Type.Steak, 3));
        tasks.add(new Task(Enums.Slot.Map6Exit, 1, Enums.Statistic.ResourcesGambled, 1700));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Exit, Enums.Tier.Iron, Enums.Type.Ore, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Exit, Enums.Tier.Iron, Enums.Type.Secondary, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Exit, Enums.Tier.None, Enums.Type.GemGreen, 1, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Exit, Enums.Tier.None, Enums.Type.GemRed, 1, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Exit, Enums.Tier.None, Enums.Type.GemBlue, 1, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Exit, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 11));

        slots.add(new Slot(Enums.Slot.Map6Gems, 1, 6, 2, Enums.Person.MineWorker, Enums.Map.Mines));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Gems, Enums.Tier.Iron, Enums.Type.Ore, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Gems, Enums.Tier.Iron, Enums.Type.Secondary, 3));
        tasks.add(new Task(Enums.Slot.Map6Gems, 1, Enums.Tier.Iron, Enums.Type.Ore, 300));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Gems, Enums.Tier.Iron, Enums.Type.Ore, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Gems, Enums.Tier.Iron, Enums.Type.Ore, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Gems, Enums.Tier.Iron, Enums.Type.Secondary, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Gems, Enums.Tier.Iron, Enums.Type.Secondary, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Gems, Enums.Tier.None, Enums.Type.GemOrange, 1, 15));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Gems, Enums.Tier.None, Enums.Type.GemGreen, 1, 7));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Gems, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 7));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Gems, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 10));

        slots.add(new Slot(Enums.Slot.Map6Elitist, 1, 7, 3, Enums.Person.MineWorker, Enums.Map.Mines));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Elitist, Enums.Tier.Bronze, Enums.Type.Ore, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Elitist, Enums.Tier.Iron, Enums.Type.Ore, 10));
        tasks.add(new Task(Enums.Slot.Map6Elitist, 1, Enums.Tier.Bronze, Enums.Type.Pickaxe, 100));
        tasks.add(new Task(Enums.Slot.Map6Elitist, 2, Enums.Tier.Bronze, Enums.Type.FishingRod, 100));
        tasks.add(new Task(Enums.Slot.Map6Elitist, 3, Enums.Tier.Bronze, Enums.Type.Hatchet, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Elitist, Enums.Tier.Bronze, Enums.Type.Ore, 4, 19));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Elitist, Enums.Tier.Iron, Enums.Type.Ore, 4, 15));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Elitist, Enums.Tier.Steel, Enums.Type.Ore, 4, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Elitist, Enums.Tier.Mithril, Enums.Type.Ore, 4, 7));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Elitist, Enums.Tier.Adamant, Enums.Type.Ore, 4, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Elitist, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 20));

        slots.add(new Slot(Enums.Slot.Map6Armoury, 1, 7, 4, Enums.Person.MineWorker, Enums.Map.Mines));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Bronze, Enums.Type.Chainmail, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Bronze, Enums.Type.Dagger, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Bronze, Enums.Type.HalfShield, 1));
        tasks.add(new Task(Enums.Slot.Map6Armoury, 1, Enums.Tier.Bronze, Enums.Type.Hammer, 20));
        tasks.add(new Task(Enums.Slot.Map6Armoury, 2, Enums.Tier.Bronze, Enums.Type.Sword, 20));
        tasks.add(new Task(Enums.Slot.Map6Armoury, 3, Enums.Tier.Bronze, Enums.Type.Gloves, 20));
        tasks.add(new Task(Enums.Slot.Map6Armoury, 4, Enums.Tier.Bronze, Enums.Type.Bow, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Iron, Enums.Type.Ore, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Iron, Enums.Type.Ore, 5, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Iron, Enums.Type.Ore, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Iron, Enums.Type.Secondary, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Iron, Enums.Type.Secondary, 5, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Iron, Enums.Type.Secondary, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 11));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Armoury, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map6Smeltery, 1, 20, 5, Enums.Person.LowLevelBlacksmith, Enums.Map.Mines));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Smeltery, Enums.Tier.Iron, Enums.Type.Ore, 1));
        tasks.add(new Task(Enums.Slot.Map6Smeltery, 1, Enums.Tier.Iron, Enums.Type.Ore, 150));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Smeltery, Enums.Tier.Iron, Enums.Type.Bar, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Smeltery, Enums.Tier.Iron, Enums.Type.Ore, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Smeltery, Enums.Tier.Iron, Enums.Type.Ore, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Smeltery, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 7));
        itemBundles.add(new ItemBundle(Enums.Slot.Map6Smeltery, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 4));
        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap7() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        slots.add(new Slot(Enums.Slot.Map7Nook, 1, 8, 3, Enums.Person.MidLevelBlacksmith, Enums.Map.DeepMines));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Nook, Enums.Tier.Iron, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Nook, Enums.Tier.Iron, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map7Nook, 1, Enums.Tier.Iron, Enums.Type.Bar, 20));
        tasks.add(new Task(Enums.Slot.Map7Nook, 2, Enums.Tier.Iron, Enums.Type.Secondary, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Nook, Enums.Tier.Iron, Enums.Type.Boots, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Nook, Enums.Tier.Iron, Enums.Type.Gloves, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Nook, Enums.Tier.Iron, Enums.Type.HalfHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Nook, Enums.Tier.Iron, Enums.Type.FullHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Nook, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 8));

        slots.add(new Slot(Enums.Slot.Map7Gateman, 1, 4, 2, Enums.Person.Guard, Enums.Map.DeepMines));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Gateman, Enums.Tier.Iron, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Gateman, Enums.Tier.Iron, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map7Gateman, 1, Enums.Statistic.Level, 7));
        tasks.add(new Task(Enums.Slot.Map7Gateman, 2, Enums.Tier.Bronze, Enums.Type.Secondary, 100));
        tasks.add(new Task(Enums.Slot.Map7Gateman, 3, Enums.Tier.None, Enums.Type.Apple, 50));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Gateman, Enums.Tier.Iron, Enums.Type.Pickaxe, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Gateman, Enums.Tier.Iron, Enums.Type.Hatchet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Gateman, Enums.Tier.Iron, Enums.Type.FishingRod, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Gateman, Enums.Tier.Iron, Enums.Type.HalfHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Gateman, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Gateman, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map7Weapon, 1, 12, 3, Enums.Person.MidLevelBlacksmith, Enums.Map.DeepMines));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Weapon, Enums.Tier.Iron, Enums.Type.Bar, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Weapon, Enums.Tier.Iron, Enums.Type.Secondary, 2));
        tasks.add(new Task(Enums.Slot.Map7Weapon, 1, Enums.Statistic.TotalSpins, 3000));
        tasks.add(new Task(Enums.Slot.Map7Weapon, 2, Enums.Statistic.CollectedBonuses, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Weapon, Enums.Tier.Iron, Enums.Type.Dagger, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Weapon, Enums.Tier.Iron, Enums.Type.Sword, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Weapon, Enums.Tier.Iron, Enums.Type.Longsword, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Weapon, Enums.Tier.Iron, Enums.Type.Bow, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Weapon, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Weapon, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map7Interchange, 1, 12, 4, Enums.Person.Guard, Enums.Map.DeepMines));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Interchange, Enums.Tier.Iron, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Interchange, Enums.Tier.Iron, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 1, Enums.Tier.Iron, Enums.Type.Boots, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 2, Enums.Tier.Iron, Enums.Type.Gloves, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 3, Enums.Tier.Iron, Enums.Type.HalfHelmet, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 4, Enums.Tier.Iron, Enums.Type.FullHelmet, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 5, Enums.Tier.Iron, Enums.Type.Pickaxe, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 6, Enums.Tier.Iron, Enums.Type.Hatchet, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 7, Enums.Tier.Iron, Enums.Type.FishingRod, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 8, Enums.Tier.Iron, Enums.Type.Hammer, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 9, Enums.Tier.Iron, Enums.Type.Dagger, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 10, Enums.Tier.Iron, Enums.Type.Sword, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 11, Enums.Tier.Iron, Enums.Type.Longsword, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 12, Enums.Tier.Iron, Enums.Type.Bow, 10));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 13, Enums.Tier.None, Enums.Type.Apple, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 14, Enums.Tier.None, Enums.Type.Lime, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 15, Enums.Tier.None, Enums.Type.Orange, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 16, Enums.Tier.None, Enums.Type.Peach, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 17, Enums.Tier.None, Enums.Type.Pineapple, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 18, Enums.Tier.None, Enums.Type.Banana, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 19, Enums.Tier.None, Enums.Type.Cherry, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 20, Enums.Tier.None, Enums.Type.Watermelon, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 21, Enums.Tier.None, Enums.Type.Grapes, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 22, Enums.Tier.None, Enums.Type.Egg, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 23, Enums.Tier.None, Enums.Type.Potato, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 24, Enums.Tier.None, Enums.Type.Steak, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 25, Enums.Tier.Bronze, Enums.Type.Boots, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 26, Enums.Tier.Bronze, Enums.Type.Gloves, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 27, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 28, Enums.Tier.Bronze, Enums.Type.FullHelmet, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 29, Enums.Tier.Bronze, Enums.Type.Pickaxe, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 30, Enums.Tier.Bronze, Enums.Type.Hatchet, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 31, Enums.Tier.Bronze, Enums.Type.FishingRod, 20));
        tasks.add(new Task(Enums.Slot.Map7Interchange, 32, Enums.Tier.Bronze, Enums.Type.Hammer, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Interchange, Enums.Tier.Iron, Enums.Type.Chainmail, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Interchange, Enums.Tier.Iron, Enums.Type.Platebody, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Interchange, Enums.Tier.Iron, Enums.Type.HalfShield, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Interchange, Enums.Tier.Iron, Enums.Type.FullShield, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Interchange, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 11));
        itemBundles.add(new ItemBundle(Enums.Slot.Map7Interchange, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 6));
        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap8() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map8Exit, 1, 12, 3, Enums.Person.Guard, Enums.Map.RuinedVillage));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exit, Enums.Tier.Iron, Enums.Type.Longsword, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exit, Enums.Tier.Iron, Enums.Type.FullHelmet, 10));
        tasks.add(new Task(Enums.Slot.Map8Exit, 1, Enums.Statistic.TotalSpins, 3500));
        tasks.add(new Task(Enums.Slot.Map8Exit, 2, Enums.Tier.Iron, Enums.Type.Hammer, 150));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exit, Enums.Tier.Iron, Enums.Type.Ore, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exit, Enums.Tier.Iron, Enums.Type.Secondary, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exit, Enums.Tier.None, Enums.Type.GemGreen, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exit, Enums.Tier.None, Enums.Type.GemBlue, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exit, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 7));

        slots.add(new Slot(Enums.Slot.Map8Resident, 1, 12, 4, Enums.Person.Man, Enums.Map.RuinedVillage));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Resident, Enums.Tier.Iron, Enums.Type.Ore, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Resident, Enums.Tier.Iron, Enums.Type.Secondary, 4));
        tasks.add(new Task(Enums.Slot.Map8Resident, 1, Enums.Statistic.Level, 8));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Resident, Enums.Tier.Silver, Enums.Type.Ore, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Resident, Enums.Tier.Gold, Enums.Type.Ore, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Resident, Enums.Tier.None, Enums.Type.Egg, 1, 7));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Resident, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 7));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Resident, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 10));

        slots.add(new Slot(Enums.Slot.Map8Bronzed, 1, 12, 3, Enums.Person.LowLevelBlacksmith, Enums.Map.RuinedVillage));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Bronzed, Enums.Tier.Iron, Enums.Type.Ore, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Bronzed, Enums.Tier.Iron, Enums.Type.Secondary, 2));
        tasks.add(new Task(Enums.Slot.Map8Bronzed, 1, Enums.Tier.Bronze, Enums.Type.Platebody, 20));
        tasks.add(new Task(Enums.Slot.Map8Bronzed, 2, Enums.Tier.Bronze, Enums.Type.Hatchet, 20));
        tasks.add(new Task(Enums.Slot.Map8Bronzed, 3, Enums.Tier.Bronze, Enums.Type.Gloves, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Bronzed, Enums.Tier.Bronze, Enums.Type.Ore, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Bronzed, Enums.Tier.Bronze, Enums.Type.Secondary, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Bronzed, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));

        slots.add(new Slot(Enums.Slot.Map8Exotic, 1, 30, 3, Enums.Person.MineWorker, Enums.Map.RuinedVillage));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exotic, Enums.Tier.Iron, Enums.Type.Ore, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exotic, Enums.Tier.Iron, Enums.Type.Secondary, 10));
        tasks.add(new Task(Enums.Slot.Map8Exotic, 1, Enums.Tier.Iron, Enums.Type.Ore, 200));
        tasks.add(new Task(Enums.Slot.Map8Exotic, 2, Enums.Tier.Iron, Enums.Type.Secondary, 200));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exotic, Enums.Tier.Steel, Enums.Type.Ore, 4, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exotic, Enums.Tier.Steel, Enums.Type.Secondary, 4, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exotic, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Exotic, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map8Lifer, 1, 5, 3, Enums.Person.OldMan, Enums.Map.RuinedVillage));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Lifer, Enums.Tier.Iron, Enums.Type.FullShield, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Lifer, Enums.Tier.Iron, Enums.Type.Sword, 1));
        tasks.add(new Task(Enums.Slot.Map8Lifer, 1, Enums.Tier.Iron, Enums.Type.Sword, 10));
        tasks.add(new Task(Enums.Slot.Map8Lifer, 2, Enums.Tier.Iron, Enums.Type.FullShield, 10));
        tasks.add(new Task(Enums.Slot.Map8Lifer, 3, Enums.Statistic.TotalSpins, 4000));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Lifer, Enums.Tier.Iron, Enums.Type.FullShield, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Lifer, Enums.Tier.Iron, Enums.Type.Sword, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Lifer, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));

        slots.add(new Slot(Enums.Slot.Map8Store, 1, 5, 3, Enums.Person.Woman, Enums.Map.RuinedVillage));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Store, Enums.Tier.Iron, Enums.Type.Platebody, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Store, Enums.Tier.Iron, Enums.Type.FullHelmet, 3));
        tasks.add(new Task(Enums.Slot.Map8Store, 1, Enums.Tier.Iron, Enums.Type.Bow, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Store, Enums.Tier.Steel, Enums.Type.Ore, 3, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Store, Enums.Tier.Steel, Enums.Type.Secondary, 3, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Store, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map8Store, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap9() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map9Left, 1, 5, 2, Enums.Person.MidLevelBlacksmith, Enums.Map.HauntedPort));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Left, Enums.Tier.Steel, Enums.Type.Ore, 1));
        tasks.add(new Task(Enums.Slot.Map9Left, 1, Enums.Tier.Steel, Enums.Type.Ore, 70));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Left, Enums.Tier.Steel, Enums.Type.Bar, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Left, Enums.Tier.Steel, Enums.Type.Ore, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Left, Enums.Tier.Steel, Enums.Type.Ore, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Left, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 6));

        slots.add(new Slot(Enums.Slot.Map9Right, 1, 5, 3, Enums.Person.MidLevelBlacksmith, Enums.Map.HauntedPort));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Right, Enums.Tier.Steel, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Right, Enums.Tier.Steel, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map9Right, 1, Enums.Tier.Steel, Enums.Type.Bar, 100));
        tasks.add(new Task(Enums.Slot.Map9Right, 2, Enums.Tier.Steel, Enums.Type.Secondary, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Right, Enums.Tier.Steel, Enums.Type.Boots, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Right, Enums.Tier.Steel, Enums.Type.Gloves, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Right, Enums.Tier.Steel, Enums.Type.HalfHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Right, Enums.Tier.Steel, Enums.Type.FullHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Right, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Right, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map9Fish, 1, 45, 4, Enums.Person.Fisherman, Enums.Map.HauntedPort));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Fish, Enums.Tier.None, Enums.Type.Steak, 1));
        tasks.add(new Task(Enums.Slot.Map9Fish, 1, Enums.Tier.None, Enums.Type.Steak, 150));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Fish, Enums.Tier.PartialFood, Enums.Type.Fish, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Fish, Enums.Tier.PartialFood, Enums.Type.Fish, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Fish, Enums.Tier.None, Enums.Type.Fish, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Fish, Enums.Tier.None, Enums.Type.Fish, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Fish, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 8));

        slots.add(new Slot(Enums.Slot.Map9Tool, 1, 5, 5, Enums.Person.Fisherman, Enums.Map.HauntedPort));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Tool, Enums.Tier.Steel, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Tool, Enums.Tier.Steel, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map9Tool, 1, Enums.Tier.Bronze, Enums.Type.FishingRod, 100));
        tasks.add(new Task(Enums.Slot.Map9Tool, 2, Enums.Tier.Iron, Enums.Type.FishingRod, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Tool, Enums.Tier.Steel, Enums.Type.Pickaxe, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Tool, Enums.Tier.Steel, Enums.Type.Hatchet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Tool, Enums.Tier.Steel, Enums.Type.FishingRod, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Tool, Enums.Tier.Steel, Enums.Type.Hammer, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Tool, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 9));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Tool, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map9Captain, 1, 5, 3, Enums.Person.Sailor, Enums.Map.HauntedPort));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Captain, Enums.Tier.Steel, Enums.Type.Bar, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Captain, Enums.Tier.Steel, Enums.Type.Secondary, 2));
        tasks.add(new Task(Enums.Slot.Map9Captain, 1, Enums.Tier.PartialFood, Enums.Type.Pineapple, 20));
        tasks.add(new Task(Enums.Slot.Map9Captain, 2, Enums.Tier.PartialFood, Enums.Type.Grapes, 20));
        tasks.add(new Task(Enums.Slot.Map9Captain, 3, Enums.Tier.PartialFood, Enums.Type.Apple, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Captain, Enums.Tier.Steel, Enums.Type.Dagger, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Captain, Enums.Tier.Steel, Enums.Type.Sword, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Captain, Enums.Tier.Steel, Enums.Type.Longsword, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Captain, Enums.Tier.Steel, Enums.Type.Bow, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Captain, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 10));

        slots.add(new Slot(Enums.Slot.Map9Amour, 1, 5, 5, Enums.Person.Woman, Enums.Map.HauntedPort));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Amour, Enums.Tier.Steel, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Amour, Enums.Tier.Steel, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map9Amour, 1, Enums.Tier.None, Enums.Type.Egg, 20));
        tasks.add(new Task(Enums.Slot.Map9Amour, 2, Enums.Tier.None, Enums.Type.Banana, 35));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Amour, Enums.Tier.Steel, Enums.Type.Chainmail, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Amour, Enums.Tier.Steel, Enums.Type.Platebody, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Amour, Enums.Tier.Steel, Enums.Type.HalfShield, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Amour, Enums.Tier.Steel, Enums.Type.FullShield, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Amour, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Amour, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Amour, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map9Charon, 1, 5, 3, Enums.Person.Sailor, Enums.Map.HauntedPort));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Charon, Enums.Tier.None, Enums.Type.Steak, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Charon, Enums.Tier.None, Enums.Type.Fish, 1));
        tasks.add(new Task(Enums.Slot.Map9Charon, 1, Enums.Statistic.Level, 9));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Charon, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Charon, Enums.Tier.Iron, Enums.Type.HalfHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Charon, Enums.Tier.Steel, Enums.Type.HalfHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map9Charon, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 7));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap10() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map10Stocked, 1, 5, 2, Enums.Person.Man, Enums.Map.Expanse));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Stocked, Enums.Tier.Steel, Enums.Type.FishingRod, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Stocked, Enums.Tier.Steel, Enums.Type.Dagger, 1));
        tasks.add(new Task(Enums.Slot.Map10Stocked, 1, Enums.Tier.Steel, Enums.Type.Dagger, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Stocked, Enums.Tier.None, Enums.Type.Fish, 3, 6));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Stocked, Enums.Tier.PartialFood, Enums.Type.Fish, 3, 6));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Stocked, Enums.Tier.Steel, Enums.Type.Gloves, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Stocked, Enums.Tier.None, Enums.Type.GemYellow, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Stocked, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));

        slots.add(new Slot(Enums.Slot.Map10Pale, 1, 5, 3, Enums.Person.Man, Enums.Map.Expanse));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Pale, Enums.Tier.None, Enums.Type.GemGreen, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Pale, Enums.Tier.None, Enums.Type.GemRed, 1));
        tasks.add(new Task(Enums.Slot.Map10Pale, 1, Enums.Tier.None, Enums.Type.GemYellow, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Pale, Enums.Tier.None, Enums.Type.GemYellow, 1, 6));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Pale, Enums.Tier.None, Enums.Type.Fish, 1, 6));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Pale, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 4));

        slots.add(new Slot(Enums.Slot.Map10Old, 1, 5, 4, Enums.Person.LowLevelBlacksmith, Enums.Map.Expanse));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Old, Enums.Tier.Bronze, Enums.Type.Ore, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Old, Enums.Tier.Bronze, Enums.Type.Secondary, 10));
        tasks.add(new Task(Enums.Slot.Map10Old, 1, Enums.Tier.Bronze, Enums.Type.Ore, 200));
        tasks.add(new Task(Enums.Slot.Map10Old, 2, Enums.Tier.Bronze, Enums.Type.Secondary, 200));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Old, Enums.Tier.Bronze, Enums.Type.Bar, 10, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Old, Enums.Tier.Bronze, Enums.Type.Dagger, 10, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Old, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Old, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map10Endless, 1, 5, 3, Enums.Person.Man, Enums.Map.Expanse));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Endless, Enums.Tier.None, Enums.Type.Watermelon, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Endless, Enums.Tier.None, Enums.Type.Grapes, 3));
        tasks.add(new Task(Enums.Slot.Map10Endless, 1, Enums.Tier.None, Enums.Type.Potato, 19));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Endless, Enums.Tier.None, Enums.Type.Lime, 1, 6));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Endless, Enums.Tier.None, Enums.Type.Orange, 1, 6));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Endless, Enums.Tier.None, Enums.Type.Steak, 1, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Endless, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map10Endless, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap11() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map11Contact, 1, 5, 3, Enums.Person.Man, Enums.Map.Isolates));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Contact, Enums.Tier.Steel, Enums.Type.Bar, 10));
        tasks.add(new Task(Enums.Slot.Map11Contact, 1, Enums.Tier.Steel, Enums.Type.Bar, 100));
        tasks.add(new Task(Enums.Slot.Map11Contact, 2, Enums.Statistic.Level, 11));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Contact, Enums.Tier.None, Enums.Type.SandRed, 1, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Contact, Enums.Tier.None, Enums.Type.SandBlue, 1, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Contact, Enums.Tier.None, Enums.Type.SandGreen, 1, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Contact, Enums.Tier.None, Enums.Type.SandYellow, 1, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Contact, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Contact, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map11Red, 1, 5, 3, Enums.Person.RedNinja, Enums.Map.Isolates));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Red, Enums.Tier.None, Enums.Type.SandRed, 1));
        tasks.add(new Task(Enums.Slot.Map11Red, 1, Enums.Tier.None, Enums.Type.SandRed, 50));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Red, Enums.Tier.Steel, Enums.Type.Boots, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Red, Enums.Tier.Steel, Enums.Type.Bow, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Red, Enums.Tier.Steel, Enums.Type.Chainmail, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Red, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));

        slots.add(new Slot(Enums.Slot.Map11Blue, 1, 5, 5, Enums.Person.BlueNinja, Enums.Map.Isolates));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Blue, Enums.Tier.None, Enums.Type.SandBlue, 1));
        tasks.add(new Task(Enums.Slot.Map11Blue, 1, Enums.Tier.None, Enums.Type.SandBlue, 50));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Blue, Enums.Tier.Steel, Enums.Type.Dagger, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Blue, Enums.Tier.Steel, Enums.Type.FishingRod, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Blue, Enums.Tier.Steel, Enums.Type.FullHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Blue, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 9));

        slots.add(new Slot(Enums.Slot.Map11Yellow, 1, 5, 3, Enums.Person.YellowNinja, Enums.Map.Isolates));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Yellow, Enums.Tier.None, Enums.Type.SandYellow, 1));
        tasks.add(new Task(Enums.Slot.Map11Yellow, 1, Enums.Tier.None, Enums.Type.SandYellow, 50));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Yellow, Enums.Tier.Steel, Enums.Type.FullShield, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Yellow, Enums.Tier.Steel, Enums.Type.Gloves, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Yellow, Enums.Tier.Steel, Enums.Type.HalfHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Yellow, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Yellow, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map11Green, 1, 5, 3, Enums.Person.GreenNinja, Enums.Map.Isolates));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Green, Enums.Tier.None, Enums.Type.SandGreen, 1));
        tasks.add(new Task(Enums.Slot.Map11Green, 1, Enums.Tier.None, Enums.Type.SandGreen, 50));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Green, Enums.Tier.Steel, Enums.Type.HalfShield, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Green, Enums.Tier.Steel, Enums.Type.Hammer, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Green, Enums.Tier.Steel, Enums.Type.Hatchet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Green, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map11Green, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap12() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map12Rupert, 1, 5, 4, Enums.Person.Man, Enums.Map.Camp));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Rupert, Enums.Tier.Bronze, Enums.Type.Ore, 1));
        tasks.add(new Task(Enums.Slot.Map12Rupert, 1, Enums.Tier.Steel, Enums.Type.Gloves, 44));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Rupert, Enums.Tier.Bronze, Enums.Type.Boots, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Rupert, Enums.Tier.Bronze, Enums.Type.Dagger, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Rupert, Enums.Tier.None, Enums.Type.GemYellow, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Rupert, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 7));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Rupert, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map12Ellen, 1, 5, 3, Enums.Person.Woman, Enums.Map.Camp));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Ellen, Enums.Tier.Bronze, Enums.Type.Bar, 1));
        tasks.add(new Task(Enums.Slot.Map12Ellen, 1, Enums.Tier.Steel, Enums.Type.FishingRod, 44));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Ellen, Enums.Tier.Bronze, Enums.Type.Chainmail, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Ellen, Enums.Tier.Bronze, Enums.Type.FishingRod, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Ellen, Enums.Tier.None, Enums.Type.GemOrange, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Ellen, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Ellen, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map12Daniel, 1, 5, 3, Enums.Person.Man, Enums.Map.Camp));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Daniel, Enums.Tier.Bronze, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map12Daniel, 1, Enums.Tier.Steel, Enums.Type.Platebody, 44));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Daniel, Enums.Tier.Bronze, Enums.Type.Sword, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Daniel, Enums.Tier.Bronze, Enums.Type.Platebody, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Daniel, Enums.Tier.None, Enums.Type.GemGreen, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Daniel, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Daniel, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map12Pete, 1, 5, 3, Enums.Person.Man, Enums.Map.Camp));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Pete, Enums.Tier.Iron, Enums.Type.Ore, 1));
        tasks.add(new Task(Enums.Slot.Map12Pete, 1, Enums.Tier.Steel, Enums.Type.Pickaxe, 44));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Pete, Enums.Tier.Bronze, Enums.Type.Pickaxe, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Pete, Enums.Tier.Bronze, Enums.Type.Longsword, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Pete, Enums.Tier.None, Enums.Type.GemBlue, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Pete, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));

        slots.add(new Slot(Enums.Slot.Map12Lucy, 1, 5, 3, Enums.Person.Woman, Enums.Map.Camp));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Lucy, Enums.Tier.Steel, Enums.Type.Bar, 1));
        tasks.add(new Task(Enums.Slot.Map12Lucy, 1, Enums.Tier.Steel, Enums.Type.HalfHelmet, 44));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Lucy, Enums.Tier.Bronze, Enums.Type.HalfShield, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Lucy, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Lucy, Enums.Tier.None, Enums.Type.GemRed, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Lucy, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));

        slots.add(new Slot(Enums.Slot.Map12Chef, 1, 10, 3, Enums.Person.Chef, Enums.Map.Camp));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Chef, Enums.Tier.Steel, Enums.Type.Bar, 1));
        tasks.add(new Task(Enums.Slot.Map12Chef, 1, Enums.Tier.None, Enums.Type.Steak, 44));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Chef, Enums.Tier.PartialFood, Enums.Type.Banana, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Chef, Enums.Tier.PartialFood, Enums.Type.Watermelon, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Chef, Enums.Tier.PartialFood, Enums.Type.Grapes, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Chef, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map12Chef, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap13() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map13Deranged, 1, 20, 5, Enums.Person.Guard, Enums.Map.UselessRiches));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Deranged, Enums.Tier.Steel, Enums.Type.Bar, 1));
        tasks.add(new Task(Enums.Slot.Map13Deranged, 1, Enums.Tier.Iron, Enums.Type.Longsword, 55));
        tasks.add(new Task(Enums.Slot.Map13Deranged, 2, Enums.Tier.Iron, Enums.Type.Chainmail, 55));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Deranged, Enums.Tier.Silver, Enums.Type.Ore, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Deranged, Enums.Tier.Steel, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Deranged, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 8));

        slots.add(new Slot(Enums.Slot.Map13Miner, 5, 25, 4, Enums.Person.MineWorker, Enums.Map.UselessRiches));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Miner, Enums.Tier.Steel, Enums.Type.Bar, 1));
        tasks.add(new Task(Enums.Slot.Map13Miner, 1, Enums.Tier.Iron, Enums.Type.Boots, 55));
        tasks.add(new Task(Enums.Slot.Map13Miner, 2, Enums.Tier.Iron, Enums.Type.FullShield, 55));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Miner, Enums.Tier.Gold, Enums.Type.Ore, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Miner, Enums.Tier.Steel, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Miner, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Miner, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map13Distorted, 1, 5, 3, Enums.Person.OldMan, Enums.Map.UselessRiches));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Distorted, Enums.Tier.Silver, Enums.Type.Ore, 1));
        tasks.add(new Task(Enums.Slot.Map13Distorted, 1, Enums.Tier.Silver, Enums.Type.Ore, 155));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Distorted, Enums.Tier.Silver, Enums.Type.Bar, 1, 15));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Distorted, Enums.Tier.Steel, Enums.Type.Bar, 1, 15));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Distorted, Enums.Tier.Silver, Enums.Type.Ore, 1, 12));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Distorted, Enums.Tier.Silver, Enums.Type.Ore, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Distorted, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 14));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Distorted, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map13Sailor, 1, 30, 5, Enums.Person.Fisherman, Enums.Map.UselessRiches));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Sailor, Enums.Tier.Gold, Enums.Type.Ore, 1));
        tasks.add(new Task(Enums.Slot.Map13Sailor, 1, Enums.Tier.Gold, Enums.Type.Ore, 155));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Sailor, Enums.Tier.Gold, Enums.Type.Bar, 1, 15));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Sailor, Enums.Tier.Steel, Enums.Type.Bar, 1, 15));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Sailor, Enums.Tier.Gold, Enums.Type.Ore, 1, 12));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Sailor, Enums.Tier.Gold, Enums.Type.Ore, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Sailor, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 26));

        slots.add(new Slot(Enums.Slot.Map13Kitchen, 1, 5, 3, Enums.Person.Chef, Enums.Map.UselessRiches));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Kitchen, Enums.Tier.Silver, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Kitchen, Enums.Tier.Gold, Enums.Type.Bar, 1));
        tasks.add(new Task(Enums.Slot.Map13Kitchen, 1, Enums.Statistic.Level, 13));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Kitchen, Enums.Tier.None, Enums.Type.Egg, 2, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Kitchen, Enums.Tier.None, Enums.Type.Cherry, 2, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Kitchen, Enums.Tier.None, Enums.Type.Pineapple, 2, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Kitchen, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map13Kitchen, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap14() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map14Frankie, 1, 15, 2, Enums.Person.Mercenary, Enums.Map.Mercenaria));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Frankie, Enums.Tier.Silver, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Frankie, Enums.Tier.None, Enums.Type.GemYellow, 1));
        tasks.add(new Task(Enums.Slot.Map14Frankie, 1, Enums.Statistic.TotalSpins, 6500));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Frankie, Enums.Tier.Silver, Enums.Type.GemYellow, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Frankie, Enums.Tier.Silver, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Frankie, Enums.Tier.None, Enums.Type.GemYellow, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Frankie, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Frankie, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map14Bobbie, 1, 15, 3, Enums.Person.Mercenary, Enums.Map.Mercenaria));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Bobbie, Enums.Tier.Silver, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Bobbie, Enums.Tier.None, Enums.Type.GemOrange, 1));
        tasks.add(new Task(Enums.Slot.Map14Bobbie, 1, Enums.Tier.Silver, Enums.Type.Bar, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Bobbie, Enums.Tier.Silver, Enums.Type.GemOrange, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Bobbie, Enums.Tier.Silver, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Bobbie, Enums.Tier.None, Enums.Type.GemOrange, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Bobbie, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 2));

        slots.add(new Slot(Enums.Slot.Map14Danny, 1, 12, 2, Enums.Person.Mercenary, Enums.Map.Mercenaria));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Danny, Enums.Tier.Silver, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Danny, Enums.Tier.None, Enums.Type.GemGreen, 1));
        tasks.add(new Task(Enums.Slot.Map14Danny, 1, Enums.Tier.Gold, Enums.Type.Bar, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Danny, Enums.Tier.Silver, Enums.Type.GemGreen, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Danny, Enums.Tier.Silver, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Danny, Enums.Tier.None, Enums.Type.GemGreen, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Danny, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Danny, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map14Jimmy, 1, 13, 3, Enums.Person.Mercenary, Enums.Map.Mercenaria));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Jimmy, Enums.Tier.Silver, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Jimmy, Enums.Tier.None, Enums.Type.GemBlue, 1));
        tasks.add(new Task(Enums.Slot.Map14Jimmy, 1, Enums.Tier.Silver, Enums.Type.Bar, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Jimmy, Enums.Tier.Silver, Enums.Type.GemBlue, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Jimmy, Enums.Tier.Silver, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Jimmy, Enums.Tier.None, Enums.Type.GemBlue, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14Jimmy, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 2));

        slots.add(new Slot(Enums.Slot.Map14BigTony, 1, 5, 5, Enums.Person.Mercenary, Enums.Map.Mercenaria));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14BigTony, Enums.Tier.Silver, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14BigTony, Enums.Tier.None, Enums.Type.GemRed, 1));
        tasks.add(new Task(Enums.Slot.Map14BigTony, 1, Enums.Tier.Silver, Enums.Type.Bar, 100));
        tasks.add(new Task(Enums.Slot.Map14BigTony, 2, Enums.Tier.Gold, Enums.Type.Bar, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14BigTony, Enums.Tier.Silver, Enums.Type.GemRed, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14BigTony, Enums.Tier.Silver, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14BigTony, Enums.Tier.None, Enums.Type.GemRed, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14BigTony, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map14BigTony, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap15() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map15Red, 1, 15, 3, Enums.Person.RedBook, Enums.Map.Library));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Red, Enums.Tier.None, Enums.Type.GemRed, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Red, Enums.Tier.Gold, Enums.Type.Bar, 1));
        tasks.add(new Task(Enums.Slot.Map15Red, 1, Enums.Tier.None, Enums.Type.GemRed, 50));
        tasks.add(new Task(Enums.Slot.Map15Red, 2, Enums.Tier.None, Enums.Type.Cherry, 50));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Red, Enums.Tier.None, Enums.Type.BookRed, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Red, Enums.Tier.None, Enums.Type.BookBlack, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Red, Enums.Tier.Mithril, Enums.Type.Ore, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Red, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 4));

        slots.add(new Slot(Enums.Slot.Map15Yellow, 1, 15, 3, Enums.Person.YellowBook, Enums.Map.Library));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Yellow, Enums.Tier.None, Enums.Type.GemYellow, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Yellow, Enums.Tier.Silver, Enums.Type.Bar, 1));
        tasks.add(new Task(Enums.Slot.Map15Yellow, 1, Enums.Tier.None, Enums.Type.GemYellow, 50));
        tasks.add(new Task(Enums.Slot.Map15Yellow, 2, Enums.Tier.None, Enums.Type.Banana, 50));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Yellow, Enums.Tier.None, Enums.Type.BookYellow, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Yellow, Enums.Tier.None, Enums.Type.BookGrey, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Yellow, Enums.Tier.Mithril, Enums.Type.Secondary, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Yellow, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 4));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Yellow, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map15Green, 1, 15, 2, Enums.Person.GreenBook, Enums.Map.Library));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Green, Enums.Tier.None, Enums.Type.GemGreen, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Green, Enums.Tier.Steel, Enums.Type.Bar, 1));
        tasks.add(new Task(Enums.Slot.Map15Green, 1, Enums.Tier.None, Enums.Type.GemGreen, 50));
        tasks.add(new Task(Enums.Slot.Map15Green, 2, Enums.Tier.None, Enums.Type.Lime, 50));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Green, Enums.Tier.None, Enums.Type.BookGreen, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Green, Enums.Tier.None, Enums.Type.BookBrown, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Green, Enums.Tier.Mithril, Enums.Type.Ore, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Green, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));

        slots.add(new Slot(Enums.Slot.Map15Blue, 1, 15, 3, Enums.Person.BlueBook, Enums.Map.Library));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Blue, Enums.Tier.None, Enums.Type.GemBlue, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Blue, Enums.Tier.Iron, Enums.Type.Bar, 1));
        tasks.add(new Task(Enums.Slot.Map15Blue, 1, Enums.Tier.None, Enums.Type.GemBlue, 50));
        tasks.add(new Task(Enums.Slot.Map15Blue, 2, Enums.Tier.None, Enums.Type.Grapes, 50));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Blue, Enums.Tier.None, Enums.Type.BookBlue, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Blue, Enums.Tier.None, Enums.Type.BookPink, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Blue, Enums.Tier.Mithril, Enums.Type.Secondary, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Blue, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 4));

        slots.add(new Slot(Enums.Slot.Map15Robot, 1, 15, 5, Enums.Person.Robot, Enums.Map.Library));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Robot, Enums.Tier.None, Enums.Type.BookRed, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Robot, Enums.Tier.None, Enums.Type.BookBlack, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Robot, Enums.Tier.None, Enums.Type.BookYellow, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Robot, Enums.Tier.None, Enums.Type.BookGrey, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Robot, Enums.Tier.None, Enums.Type.BookBlue, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Robot, Enums.Tier.None, Enums.Type.BookPink, 1));
        tasks.add(new Task(Enums.Slot.Map15Robot, 1, Enums.Tier.None, Enums.Type.BookRed, 66));
        tasks.add(new Task(Enums.Slot.Map15Robot, 2, Enums.Tier.None, Enums.Type.BookBlack, 66));
        tasks.add(new Task(Enums.Slot.Map15Robot, 3, Enums.Tier.None, Enums.Type.BookYellow, 66));
        tasks.add(new Task(Enums.Slot.Map15Robot, 4, Enums.Tier.None, Enums.Type.BookGrey, 66));
        tasks.add(new Task(Enums.Slot.Map15Robot, 5, Enums.Tier.None, Enums.Type.BookBlue, 66));
        tasks.add(new Task(Enums.Slot.Map15Robot, 6, Enums.Tier.None, Enums.Type.BookPink, 66));
        itemBundles.add(new ItemBundle(Enums.Slot.Map15Robot, Enums.Tier.None, Enums.Type.BookCollection, 1, 10));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap16() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map16Furnace, 1, 50, 3, Enums.Person.HighLevelBlacksmith, Enums.Map.HauntedCorridors));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Furnace, Enums.Tier.Mithril, Enums.Type.Ore, 1));
        tasks.add(new Task(Enums.Slot.Map16Furnace, 1, Enums.Tier.Mithril, Enums.Type.Ore, 30));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Furnace, Enums.Tier.Mithril, Enums.Type.Bar, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Furnace, Enums.Tier.Mithril, Enums.Type.Ore, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Furnace, Enums.Tier.Mithril, Enums.Type.Ore, 10, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Furnace, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));

        slots.add(new Slot(Enums.Slot.Map16Accessories, 1, 15, 3, Enums.Person.Ghost, Enums.Map.HauntedCorridors));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Accessories, Enums.Tier.Mithril, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Accessories, Enums.Tier.Mithril, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map16Accessories, 1, Enums.Tier.Mithril, Enums.Type.Bar, 20));
        tasks.add(new Task(Enums.Slot.Map16Accessories, 2, Enums.Tier.Mithril, Enums.Type.Secondary, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Accessories, Enums.Tier.Mithril, Enums.Type.Boots, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Accessories, Enums.Tier.Mithril, Enums.Type.Gloves, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Accessories, Enums.Tier.Mithril, Enums.Type.HalfHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Accessories, Enums.Tier.Mithril, Enums.Type.FullHelmet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Accessories, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Accessories, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map16Tools, 1, 5, 4, Enums.Person.Skeleton, Enums.Map.HauntedCorridors));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Tools, Enums.Tier.Mithril, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Tools, Enums.Tier.Mithril, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map16Tools, 1, Enums.Tier.Mithril, Enums.Type.Boots, 20));
        tasks.add(new Task(Enums.Slot.Map16Tools, 2, Enums.Tier.Mithril, Enums.Type.Gloves, 20));
        tasks.add(new Task(Enums.Slot.Map16Tools, 3, Enums.Tier.Mithril, Enums.Type.HalfHelmet, 20));
        tasks.add(new Task(Enums.Slot.Map16Tools, 4, Enums.Tier.Mithril, Enums.Type.FullHelmet, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Tools, Enums.Tier.Mithril, Enums.Type.Pickaxe, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Tools, Enums.Tier.Mithril, Enums.Type.Hatchet, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Tools, Enums.Tier.Mithril, Enums.Type.FishingRod, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Tools, Enums.Tier.Mithril, Enums.Type.Hammer, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Tools, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 8));

        slots.add(new Slot(Enums.Slot.Map16Weapons, 1, 5, 3, Enums.Person.Ghost2, Enums.Map.HauntedCorridors));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Weapons, Enums.Tier.Mithril, Enums.Type.Bar, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Weapons, Enums.Tier.Mithril, Enums.Type.Secondary, 2));
        tasks.add(new Task(Enums.Slot.Map16Weapons, 1, Enums.Tier.Mithril, Enums.Type.FishingRod, 13));
        tasks.add(new Task(Enums.Slot.Map16Weapons, 2, Enums.Statistic.Level, 16));
        tasks.add(new Task(Enums.Slot.Map16Weapons, 3, Enums.Statistic.TotalSpins, 7900));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Weapons, Enums.Tier.Mithril, Enums.Type.Dagger, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Weapons, Enums.Tier.Mithril, Enums.Type.Sword, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Weapons, Enums.Tier.Mithril, Enums.Type.Longsword, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Weapons, Enums.Tier.Mithril, Enums.Type.Bow, 2, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Weapons, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));

        slots.add(new Slot(Enums.Slot.Map16Armour, 1, 5, 5, Enums.Person.Skeleton2, Enums.Map.HauntedCorridors));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Armour, Enums.Tier.Mithril, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Armour, Enums.Tier.Mithril, Enums.Type.Secondary, 1));
        tasks.add(new Task(Enums.Slot.Map16Armour, 1, Enums.Statistic.CollectedBonuses, 30));
        tasks.add(new Task(Enums.Slot.Map16Armour, 2, Enums.Tier.Mithril, Enums.Type.Boots, 5));
        tasks.add(new Task(Enums.Slot.Map16Armour, 3, Enums.Tier.Mithril, Enums.Type.Gloves, 5));
        tasks.add(new Task(Enums.Slot.Map16Armour, 4, Enums.Tier.Mithril, Enums.Type.HalfHelmet, 5));
        tasks.add(new Task(Enums.Slot.Map16Armour, 5, Enums.Tier.Mithril, Enums.Type.FullHelmet, 5));
        tasks.add(new Task(Enums.Slot.Map16Armour, 6, Enums.Tier.Mithril, Enums.Type.Pickaxe, 5));
        tasks.add(new Task(Enums.Slot.Map16Armour, 7, Enums.Tier.Mithril, Enums.Type.Hatchet, 5));
        tasks.add(new Task(Enums.Slot.Map16Armour, 8, Enums.Tier.Mithril, Enums.Type.FishingRod, 5));
        tasks.add(new Task(Enums.Slot.Map16Armour, 9, Enums.Tier.Mithril, Enums.Type.Hammer, 5));
        tasks.add(new Task(Enums.Slot.Map16Armour, 10, Enums.Tier.Mithril, Enums.Type.Dagger, 5));
        tasks.add(new Task(Enums.Slot.Map16Armour, 11, Enums.Tier.Mithril, Enums.Type.Sword, 5));
        tasks.add(new Task(Enums.Slot.Map16Armour, 12, Enums.Tier.Mithril, Enums.Type.Longsword, 5));
        tasks.add(new Task(Enums.Slot.Map16Armour, 13, Enums.Tier.Mithril, Enums.Type.Bow, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Armour, Enums.Tier.Mithril, Enums.Type.Chainmail, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Armour, Enums.Tier.Mithril, Enums.Type.Platebody, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Armour, Enums.Tier.Mithril, Enums.Type.HalfShield, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Armour, Enums.Tier.Mithril, Enums.Type.FullShield, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Armour, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 17));
        itemBundles.add(new ItemBundle(Enums.Slot.Map16Armour, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 5));


        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap17() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map17Blue, 1, 5, 3, Enums.Person.JewellerBlue, Enums.Map.UndeadJewellers));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Blue, Enums.Tier.Gold, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Blue, Enums.Tier.None, Enums.Type.GemBlue, 1));
        tasks.add(new Task(Enums.Slot.Map17Blue, 1, Enums.Tier.Gold, Enums.Type.Bar, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Blue, Enums.Tier.Gold, Enums.Type.GemBlue, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Blue, Enums.Tier.Gold, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Blue, Enums.Tier.None, Enums.Type.GemBlue, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Blue, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Blue, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Blue, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 3));

        slots.add(new Slot(Enums.Slot.Map17Yellow, 1, 5, 3, Enums.Slot.Map16Armour, Enums.Person.Jeweller, Enums.Map.UndeadJewellers));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Yellow, Enums.Tier.Gold, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Yellow, Enums.Tier.None, Enums.Type.GemYellow, 1));
        tasks.add(new Task(Enums.Slot.Map17Yellow, 1, Enums.Tier.Gold, Enums.Type.Bar, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Yellow, Enums.Tier.Gold, Enums.Type.GemYellow, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Yellow, Enums.Tier.Gold, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Yellow, Enums.Tier.None, Enums.Type.GemYellow, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Yellow, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Yellow, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 1));

        slots.add(new Slot(Enums.Slot.Map17Orange, 1, 5, 3, Enums.Slot.Map16Armour, Enums.Person.JewellerOrange, Enums.Map.UndeadJewellers));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Orange, Enums.Tier.Gold, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Orange, Enums.Tier.None, Enums.Type.GemOrange, 1));
        tasks.add(new Task(Enums.Slot.Map17Orange, 1, Enums.Tier.Gold, Enums.Type.Bar, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Orange, Enums.Tier.Gold, Enums.Type.GemOrange, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Orange, Enums.Tier.Gold, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Orange, Enums.Tier.None, Enums.Type.GemOrange, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Orange, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 2));

        slots.add(new Slot(Enums.Slot.Map17Green, 1, 5, 3, Enums.Slot.Map16Armour, Enums.Person.JewellerGreen, Enums.Map.UndeadJewellers));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Green, Enums.Tier.Gold, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Green, Enums.Tier.None, Enums.Type.GemGreen, 1));
        tasks.add(new Task(Enums.Slot.Map17Green, 1, Enums.Tier.Gold, Enums.Type.Bar, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Green, Enums.Tier.Gold, Enums.Type.GemGreen, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Green, Enums.Tier.Gold, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Green, Enums.Tier.None, Enums.Type.GemGreen, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Green, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Green, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        slots.add(new Slot(Enums.Slot.Map17Red, 1, 5, 2, Enums.Slot.Map17Blue, Enums.Person.JewellerRed, Enums.Map.UndeadJewellers));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Red, Enums.Tier.Gold, Enums.Type.Bar, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Red, Enums.Tier.None, Enums.Type.GemRed, 1));
        tasks.add(new Task(Enums.Slot.Map17Red, 1, Enums.Tier.Gold, Enums.Type.GemYellow, 10));
        tasks.add(new Task(Enums.Slot.Map17Red, 2, Enums.Tier.Gold, Enums.Type.GemOrange, 10));
        tasks.add(new Task(Enums.Slot.Map17Red, 3, Enums.Tier.Gold, Enums.Type.GemGreen, 10));
        tasks.add(new Task(Enums.Slot.Map17Red, 4, Enums.Tier.Gold, Enums.Type.GemBlue, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Red, Enums.Tier.Gold, Enums.Type.GemRed, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Red, Enums.Tier.Gold, Enums.Type.Bar, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Red, Enums.Tier.None, Enums.Type.GemRed, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map17Red, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 2));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap18() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map18Watchers, 1, 5, 4, Enums.Person.GroupOfPeople, Enums.Map.NoTurningBack));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Watchers, Enums.Tier.None, Enums.Type.SandRed, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Watchers, Enums.Tier.None, Enums.Type.SandBlue, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Watchers, Enums.Tier.None, Enums.Type.SandYellow, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Watchers, Enums.Tier.None, Enums.Type.SandGreen, 1));
        tasks.add(new Task(Enums.Slot.Map18Watchers, 1, Enums.Tier.Gold, Enums.Type.SandRed, 10));
        tasks.add(new Task(Enums.Slot.Map18Watchers, 2, Enums.Tier.Gold, Enums.Type.SandBlue, 10));
        tasks.add(new Task(Enums.Slot.Map18Watchers, 3, Enums.Tier.Gold, Enums.Type.SandYellow, 10));
        tasks.add(new Task(Enums.Slot.Map18Watchers, 4, Enums.Tier.Gold, Enums.Type.SandGreen, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Watchers, Enums.Tier.Gold, Enums.Type.Bar, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Watchers, Enums.Tier.Silver, Enums.Type.Bar, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Watchers, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 4));

        slots.add(new Slot(Enums.Slot.Map18Guard, 1, 40, 2, Enums.Person.Guard, Enums.Map.NoTurningBack));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Guard, Enums.Tier.Gold, Enums.Type.Bar, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Guard, Enums.Tier.Silver, Enums.Type.Bar, 1));
        tasks.add(new Task(Enums.Slot.Map18Guard, 1, Enums.Tier.Gold, Enums.Type.GemYellow, 10));
        tasks.add(new Task(Enums.Slot.Map18Guard, 2, Enums.Tier.Gold, Enums.Type.GemOrange, 10));
        tasks.add(new Task(Enums.Slot.Map18Guard, 3, Enums.Tier.Gold, Enums.Type.GemGreen, 10));
        tasks.add(new Task(Enums.Slot.Map18Guard, 4, Enums.Tier.Gold, Enums.Type.GemBlue, 10));
        tasks.add(new Task(Enums.Slot.Map18Guard, 5, Enums.Tier.Gold, Enums.Type.GemRed, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Guard, Enums.Tier.Gold, Enums.Type.GemYellow, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Guard, Enums.Tier.Gold, Enums.Type.GemOrange, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Guard, Enums.Tier.Gold, Enums.Type.GemGreen, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Guard, Enums.Tier.Gold, Enums.Type.GemBlue, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Guard, Enums.Tier.Gold, Enums.Type.GemRed, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Guard, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Guard, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 10));

        slots.add(new Slot(Enums.Slot.Map18Bronze, 2, 5, 5, Enums.Person.BronzeAdventurer, Enums.Map.NoTurningBack));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Bronze, Enums.Tier.Bronze, Enums.Type.Bar, 15));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 1, Enums.Tier.Bronze, Enums.Type.Bar, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 2, Enums.Tier.Bronze, Enums.Type.Boots, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 3, Enums.Tier.Bronze, Enums.Type.Bow, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 4, Enums.Tier.Bronze, Enums.Type.Chainmail, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 5, Enums.Tier.Bronze, Enums.Type.Dagger, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 6, Enums.Tier.Bronze, Enums.Type.FishingRod, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 7, Enums.Tier.Bronze, Enums.Type.FullHelmet, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 8, Enums.Tier.Bronze, Enums.Type.FullShield, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 9, Enums.Tier.Bronze, Enums.Type.Gloves, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 10, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 11, Enums.Tier.Bronze, Enums.Type.HalfShield, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 12, Enums.Tier.Bronze, Enums.Type.Hammer, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 13, Enums.Tier.Bronze, Enums.Type.Hatchet, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 14, Enums.Tier.Bronze, Enums.Type.Longsword, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 15, Enums.Tier.Bronze, Enums.Type.Ore, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 16, Enums.Tier.Bronze, Enums.Type.Pickaxe, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 17, Enums.Tier.Bronze, Enums.Type.Platebody, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 18, Enums.Tier.Bronze, Enums.Type.Secondary, 35));
        tasks.add(new Task(Enums.Slot.Map18Bronze, 19, Enums.Tier.Bronze, Enums.Type.Sword, 35));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Bronze, Enums.Tier.Bronze, Enums.Type.Bar, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Bronze, Enums.Tier.Bronze, Enums.Type.Secondary, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Bronze, Enums.Tier.None, Enums.Type.Lime, 5, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Bronze, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 11));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Bronze, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map18Iron, 3, 5, 3, Enums.Person.IronAdventurer, Enums.Map.NoTurningBack));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Iron, Enums.Tier.Iron, Enums.Type.Bar, 15));
        tasks.add(new Task(Enums.Slot.Map18Iron, 1, Enums.Tier.Iron, Enums.Type.Bar, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 2, Enums.Tier.Iron, Enums.Type.Boots, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 3, Enums.Tier.Iron, Enums.Type.Bow, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 4, Enums.Tier.Iron, Enums.Type.Chainmail, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 5, Enums.Tier.Iron, Enums.Type.Dagger, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 6, Enums.Tier.Iron, Enums.Type.FishingRod, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 7, Enums.Tier.Iron, Enums.Type.FullHelmet, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 8, Enums.Tier.Iron, Enums.Type.FullShield, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 9, Enums.Tier.Iron, Enums.Type.Gloves, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 10, Enums.Tier.Iron, Enums.Type.HalfHelmet, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 11, Enums.Tier.Iron, Enums.Type.HalfShield, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 12, Enums.Tier.Iron, Enums.Type.Hammer, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 13, Enums.Tier.Iron, Enums.Type.Hatchet, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 14, Enums.Tier.Iron, Enums.Type.Longsword, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 15, Enums.Tier.Iron, Enums.Type.Ore, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 16, Enums.Tier.Iron, Enums.Type.Pickaxe, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 17, Enums.Tier.Iron, Enums.Type.Platebody, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 18, Enums.Tier.Iron, Enums.Type.Secondary, 35));
        tasks.add(new Task(Enums.Slot.Map18Iron, 19, Enums.Tier.Iron, Enums.Type.Sword, 35));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Iron, Enums.Tier.Iron, Enums.Type.Bar, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Iron, Enums.Tier.Iron, Enums.Type.Secondary, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Iron, Enums.Tier.None, Enums.Type.Grapes, 5, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Iron, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));

        slots.add(new Slot(Enums.Slot.Map18Steel, 4, 5, 3, Enums.Person.SteelAdventurer, Enums.Map.NoTurningBack));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Steel, Enums.Tier.Steel, Enums.Type.Bar, 15));
        tasks.add(new Task(Enums.Slot.Map18Steel, 1, Enums.Tier.Steel, Enums.Type.Bar, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 2, Enums.Tier.Steel, Enums.Type.Boots, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 3, Enums.Tier.Steel, Enums.Type.Bow, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 4, Enums.Tier.Steel, Enums.Type.Chainmail, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 5, Enums.Tier.Steel, Enums.Type.Dagger, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 6, Enums.Tier.Steel, Enums.Type.FishingRod, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 7, Enums.Tier.Steel, Enums.Type.FullHelmet, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 8, Enums.Tier.Steel, Enums.Type.FullShield, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 9, Enums.Tier.Steel, Enums.Type.Gloves, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 10, Enums.Tier.Steel, Enums.Type.HalfHelmet, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 11, Enums.Tier.Steel, Enums.Type.HalfShield, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 12, Enums.Tier.Steel, Enums.Type.Hammer, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 13, Enums.Tier.Steel, Enums.Type.Hatchet, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 14, Enums.Tier.Steel, Enums.Type.Longsword, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 15, Enums.Tier.Steel, Enums.Type.Ore, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 16, Enums.Tier.Steel, Enums.Type.Pickaxe, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 17, Enums.Tier.Steel, Enums.Type.Platebody, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 18, Enums.Tier.Steel, Enums.Type.Secondary, 35));
        tasks.add(new Task(Enums.Slot.Map18Steel, 19, Enums.Tier.Steel, Enums.Type.Sword, 35));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Steel, Enums.Tier.Steel, Enums.Type.Bar, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Steel, Enums.Tier.Steel, Enums.Type.Secondary, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Steel, Enums.Tier.None, Enums.Type.Cherry, 5, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Steel, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Steel, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 3));

        slots.add(new Slot(Enums.Slot.Map18Mithril, 5, 15, 3, Enums.Person.MithrilAdventurer, Enums.Map.NoTurningBack));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Mithril, Enums.Tier.Mithril, Enums.Type.Bar, 15));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 1, Enums.Tier.Mithril, Enums.Type.Bar, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 2, Enums.Tier.Mithril, Enums.Type.Boots, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 3, Enums.Tier.Mithril, Enums.Type.Bow, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 4, Enums.Tier.Mithril, Enums.Type.Chainmail, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 5, Enums.Tier.Mithril, Enums.Type.Dagger, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 6, Enums.Tier.Mithril, Enums.Type.FishingRod, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 7, Enums.Tier.Mithril, Enums.Type.FullHelmet, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 8, Enums.Tier.Mithril, Enums.Type.FullShield, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 9, Enums.Tier.Mithril, Enums.Type.Gloves, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 10, Enums.Tier.Mithril, Enums.Type.HalfHelmet, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 11, Enums.Tier.Mithril, Enums.Type.HalfShield, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 12, Enums.Tier.Mithril, Enums.Type.Hammer, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 13, Enums.Tier.Mithril, Enums.Type.Hatchet, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 14, Enums.Tier.Mithril, Enums.Type.Longsword, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 15, Enums.Tier.Mithril, Enums.Type.Ore, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 16, Enums.Tier.Mithril, Enums.Type.Pickaxe, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 17, Enums.Tier.Mithril, Enums.Type.Platebody, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 18, Enums.Tier.Mithril, Enums.Type.Secondary, 35));
        tasks.add(new Task(Enums.Slot.Map18Mithril, 19, Enums.Tier.Mithril, Enums.Type.Sword, 35));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Mithril, Enums.Tier.Mithril, Enums.Type.Bar, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Mithril, Enums.Tier.Mithril, Enums.Type.Secondary, 5, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Mithril, Enums.Tier.None, Enums.Type.Potato, 5, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Mithril, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));

        slots.add(new Slot(Enums.Slot.Map18Purple, 1, 5, 3, Enums.Person.PurpleBlob, Enums.Map.NoTurningBack));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Purple, Enums.Tier.Mithril, Enums.Type.Bar, 15));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Purple, Enums.Tier.Steel, Enums.Type.Bar, 15));
        tasks.add(new Task(Enums.Slot.Map18Purple, 1, Enums.Tier.Bronze, Enums.Type.Bar, 1000));
        tasks.add(new Task(Enums.Slot.Map18Purple, 2, Enums.Tier.Iron, Enums.Type.Bar, 1000));
        tasks.add(new Task(Enums.Slot.Map18Purple, 3, Enums.Tier.Steel, Enums.Type.Bar, 1000));
        tasks.add(new Task(Enums.Slot.Map18Purple, 4, Enums.Tier.Mithril, Enums.Type.Bar, 1000));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Purple, Enums.Tier.Iron, Enums.Type.Bar, 1, 8));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Purple, Enums.Tier.Bronze, Enums.Type.Bar, 1, 8));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Purple, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map18Purple, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 3));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap19() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map19Touch, 1, 5, 3, Enums.Person.PillarGuardian, Enums.Map.Battle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Touch, Enums.Tier.Bronze, Enums.Type.Bar, 400));
        tasks.add(new Task(Enums.Slot.Map19Touch, 1, Enums.Tier.Bronze, Enums.Type.Gloves, 100));
        tasks.add(new Task(Enums.Slot.Map19Touch, 2, Enums.Tier.Iron, Enums.Type.Gloves, 100));
        tasks.add(new Task(Enums.Slot.Map19Touch, 3, Enums.Tier.Steel, Enums.Type.Gloves, 100));
        tasks.add(new Task(Enums.Slot.Map19Touch, 4, Enums.Tier.Mithril, Enums.Type.Gloves, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Touch, Enums.Tier.Adamant, Enums.Type.Gloves, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Touch, Enums.Tier.Steel, Enums.Type.Gloves, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Touch, Enums.Tier.Mithril, Enums.Type.Gloves, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Touch, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 7));

        slots.add(new Slot(Enums.Slot.Map19Travel, 1, 5, 3, Enums.Slot.Map18Purple, Enums.Person.PillarGuardian, Enums.Map.Battle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Travel, Enums.Tier.Iron, Enums.Type.Bar, 400));
        tasks.add(new Task(Enums.Slot.Map19Travel, 1, Enums.Tier.Bronze, Enums.Type.Boots, 100));
        tasks.add(new Task(Enums.Slot.Map19Travel, 2, Enums.Tier.Iron, Enums.Type.Boots, 100));
        tasks.add(new Task(Enums.Slot.Map19Travel, 3, Enums.Tier.Steel, Enums.Type.Boots, 100));
        tasks.add(new Task(Enums.Slot.Map19Travel, 4, Enums.Tier.Mithril, Enums.Type.Boots, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Travel, Enums.Tier.Adamant, Enums.Type.Boots, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Travel, Enums.Tier.Steel, Enums.Type.Boots, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Travel, Enums.Tier.Mithril, Enums.Type.Boots, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Travel, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 8));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Travel, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map19Hunger, 1, 5, 3, Enums.Slot.Map18Purple, Enums.Person.PillarGuardian, Enums.Map.Battle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Hunger, Enums.Tier.Steel, Enums.Type.Bar, 400));
        tasks.add(new Task(Enums.Slot.Map19Hunger, 1, Enums.Tier.None, Enums.Type.Pineapple, 100));
        tasks.add(new Task(Enums.Slot.Map19Hunger, 2, Enums.Tier.None, Enums.Type.Orange, 100));
        tasks.add(new Task(Enums.Slot.Map19Hunger, 3, Enums.Tier.None, Enums.Type.Potato, 100));
        tasks.add(new Task(Enums.Slot.Map19Hunger, 4, Enums.Tier.None, Enums.Type.Banana, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Hunger, Enums.Tier.None, Enums.Type.ForbiddenFood, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Hunger, Enums.Tier.None, Enums.Type.Grapes, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Hunger, Enums.Tier.None, Enums.Type.Peach, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Hunger, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 10));

        slots.add(new Slot(Enums.Slot.Map19Defence, 1, 5, 3, Enums.Slot.Map18Purple, Enums.Person.PillarGuardian, Enums.Map.Battle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Defence, Enums.Tier.Mithril, Enums.Type.Bar, 400));
        tasks.add(new Task(Enums.Slot.Map19Defence, 1, Enums.Tier.Bronze, Enums.Type.FullShield, 100));
        tasks.add(new Task(Enums.Slot.Map19Defence, 2, Enums.Tier.Iron, Enums.Type.FullShield, 100));
        tasks.add(new Task(Enums.Slot.Map19Defence, 3, Enums.Tier.Steel, Enums.Type.FullShield, 100));
        tasks.add(new Task(Enums.Slot.Map19Defence, 4, Enums.Tier.Mithril, Enums.Type.FullShield, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Defence, Enums.Tier.Adamant, Enums.Type.FullShield, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Defence, Enums.Tier.Steel, Enums.Type.FullShield, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Defence, Enums.Tier.Mithril, Enums.Type.FullShield, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Defence, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));

        slots.add(new Slot(Enums.Slot.Map19Melee, 1, 5, 3, Enums.Slot.Map18Purple, Enums.Person.PillarGuardian, Enums.Map.Battle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Melee, Enums.Tier.Bronze, Enums.Type.Secondary, 400));
        tasks.add(new Task(Enums.Slot.Map19Melee, 1, Enums.Tier.Bronze, Enums.Type.Longsword, 100));
        tasks.add(new Task(Enums.Slot.Map19Melee, 2, Enums.Tier.Iron, Enums.Type.Longsword, 100));
        tasks.add(new Task(Enums.Slot.Map19Melee, 3, Enums.Tier.Steel, Enums.Type.Longsword, 100));
        tasks.add(new Task(Enums.Slot.Map19Melee, 4, Enums.Tier.Mithril, Enums.Type.Longsword, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Melee, Enums.Tier.Adamant, Enums.Type.Longsword, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Melee, Enums.Tier.Steel, Enums.Type.Longsword, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Melee, Enums.Tier.Mithril, Enums.Type.Longsword, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Melee, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));

        slots.add(new Slot(Enums.Slot.Map19Archery, 1, 5, 3, Enums.Slot.Map18Purple, Enums.Person.PillarGuardian, Enums.Map.Battle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Archery, Enums.Tier.Iron, Enums.Type.Secondary, 400));
        tasks.add(new Task(Enums.Slot.Map19Archery, 1, Enums.Tier.Bronze, Enums.Type.Bow, 100));
        tasks.add(new Task(Enums.Slot.Map19Archery, 2, Enums.Tier.Iron, Enums.Type.Bow, 100));
        tasks.add(new Task(Enums.Slot.Map19Archery, 3, Enums.Tier.Steel, Enums.Type.Bow, 100));
        tasks.add(new Task(Enums.Slot.Map19Archery, 4, Enums.Tier.Mithril, Enums.Type.Bow, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Archery, Enums.Tier.Adamant, Enums.Type.Bow, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Archery, Enums.Tier.Steel, Enums.Type.Bow, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Archery, Enums.Tier.Mithril, Enums.Type.Bow, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Archery, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Archery, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map19Protection, 1, 5, 3, Enums.Slot.Map18Purple, Enums.Person.PillarGuardian, Enums.Map.Battle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Protection, Enums.Tier.Steel, Enums.Type.Secondary, 400));
        tasks.add(new Task(Enums.Slot.Map19Protection, 1, Enums.Tier.Bronze, Enums.Type.Platebody, 100));
        tasks.add(new Task(Enums.Slot.Map19Protection, 2, Enums.Tier.Iron, Enums.Type.Platebody, 100));
        tasks.add(new Task(Enums.Slot.Map19Protection, 3, Enums.Tier.Steel, Enums.Type.Platebody, 100));
        tasks.add(new Task(Enums.Slot.Map19Protection, 4, Enums.Tier.Mithril, Enums.Type.Platebody, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Protection, Enums.Tier.Adamant, Enums.Type.Platebody, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Protection, Enums.Tier.Steel, Enums.Type.Platebody, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Protection, Enums.Tier.Mithril, Enums.Type.Platebody, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Protection, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));

        slots.add(new Slot(Enums.Slot.Map19Power, 1, 5, 3, Enums.Slot.Map18Purple, Enums.Person.PillarGuardian, Enums.Map.Battle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Power, Enums.Tier.Gold, Enums.Type.GemGreen, 30));
        tasks.add(new Task(Enums.Slot.Map19Power, 1, Enums.Tier.Gold, Enums.Type.GemYellow, 30));
        tasks.add(new Task(Enums.Slot.Map19Power, 2, Enums.Tier.Gold, Enums.Type.GemOrange, 30));
        tasks.add(new Task(Enums.Slot.Map19Power, 3, Enums.Tier.Gold, Enums.Type.GemBlue, 30));
        tasks.add(new Task(Enums.Slot.Map19Power, 4, Enums.Tier.Gold, Enums.Type.GemRed, 30));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Power, Enums.Tier.Gold, Enums.Type.GemPurple, 1, 20));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Power, Enums.Tier.Gold, Enums.Type.GemBlue, 1, 3));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Power, Enums.Tier.Gold, Enums.Type.GemRed, 1, 2));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Power, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 5));

        slots.add(new Slot(Enums.Slot.Map19Boss, 1, 5, 3, Enums.Slot.Map18Purple, Enums.Person.Boss, Enums.Map.Battle));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Boss, Enums.Tier.Bronze, Enums.Type.Ore, 1));
        tasks.add(new Task(Enums.Slot.Map19Boss, 1, Enums.Tier.Adamant, Enums.Type.Gloves, 1));
        tasks.add(new Task(Enums.Slot.Map19Boss, 2, Enums.Tier.Adamant, Enums.Type.Boots, 1));
        tasks.add(new Task(Enums.Slot.Map19Boss, 3, Enums.Tier.None, Enums.Type.ForbiddenFood, 1));
        tasks.add(new Task(Enums.Slot.Map19Boss, 4, Enums.Tier.Adamant, Enums.Type.FullShield, 1));
        tasks.add(new Task(Enums.Slot.Map19Boss, 5, Enums.Tier.Adamant, Enums.Type.Longsword, 1));
        tasks.add(new Task(Enums.Slot.Map19Boss, 6, Enums.Tier.Adamant, Enums.Type.Bow, 1));
        tasks.add(new Task(Enums.Slot.Map19Boss, 7, Enums.Tier.Adamant, Enums.Type.Platebody, 1));
        tasks.add(new Task(Enums.Slot.Map19Boss, 8, Enums.Tier.Gold, Enums.Type.GemPurple, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Boss, Enums.Tier.None, Enums.Type.LuckyCoin, 1, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Boss, Enums.Tier.None, Enums.Type.GemOrange, 100, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Boss, Enums.Tier.None, Enums.Type.GemYellow, 100, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Boss, Enums.Tier.None, Enums.Type.GemGreen, 100, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Boss, Enums.Tier.None, Enums.Type.GemBlue, 100, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Boss, Enums.Tier.None, Enums.Type.GemRed, 100, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Boss, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 6));
        itemBundles.add(new ItemBundle(Enums.Slot.Map19Boss, Enums.Tier.Internal, Enums.Type.MinigameChest, 1, 6));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createSlotsMap20() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();

        slots.add(new Slot(Enums.Slot.Map20PixelBlacksmith, 1, 5, 3, Enums.Person.PixelBlacksmith, Enums.Map.TheEnd));
        itemBundles.add(new ItemBundle(Enums.Slot.Map20PixelBlacksmith, Enums.Tier.None, Enums.Type.BookCollection, 1));
        tasks.add(new Task(Enums.Slot.Map20PixelBlacksmith, 1, Enums.Tier.Gold, Enums.Type.GemYellow, 100));
        tasks.add(new Task(Enums.Slot.Map20PixelBlacksmith, 2, Enums.Tier.None, Enums.Type.Fish, 100));
        tasks.add(new Task(Enums.Slot.Map20PixelBlacksmith, 3, Enums.Tier.PartialFood, Enums.Type.Potato, 100));
        tasks.add(new Task(Enums.Slot.Map20PixelBlacksmith, 4, Enums.Tier.Steel, Enums.Type.Hammer, 100));
        tasks.add(new Task(Enums.Slot.Map20PixelBlacksmith, 5, Enums.Tier.Bronze, Enums.Type.Platebody, 100));
        tasks.add(new Task(Enums.Slot.Map20PixelBlacksmith, 6, Enums.Tier.None, Enums.Type.LuckyCoin, 100));
        itemBundles.add(new ItemBundle(Enums.Slot.Map20PixelBlacksmith, Enums.Tier.Mithril, Enums.Type.Bar, 100, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map20PixelBlacksmith, Enums.Tier.Silver, Enums.Type.Bar, 100, 7));
        itemBundles.add(new ItemBundle(Enums.Slot.Map20PixelBlacksmith, Enums.Tier.Gold, Enums.Type.Bar, 100, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map20PixelBlacksmith, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map20PixelBlacksmith, Enums.Tier.Internal, Enums.Type.MinigameDice, 1, 6));

        slots.add(new Slot(Enums.Slot.Map20TradesEntrance, 1, 5, 4, Enums.Person.PixelBlacksmith, Enums.Map.TheEnd));
        itemBundles.add(new ItemBundle(Enums.Slot.Map20TradesEntrance, Enums.Tier.Gold, Enums.Type.GemPurple, 1));
        tasks.add(new Task(Enums.Slot.Map20TradesEntrance, 1, Enums.Statistic.SaveImported, 1));
        itemBundles.add(new ItemBundle(Enums.Slot.Map20TradesEntrance, Enums.Tier.Adamant, Enums.Type.Ore, 100, 10));
        itemBundles.add(new ItemBundle(Enums.Slot.Map20TradesEntrance, Enums.Tier.Adamant, Enums.Type.Bar, 100, 7));
        itemBundles.add(new ItemBundle(Enums.Slot.Map20TradesEntrance, Enums.Tier.None, Enums.Type.GemRed, 100, 5));
        itemBundles.add(new ItemBundle(Enums.Slot.Map20TradesEntrance, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 10));

        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
        Task.saveInTx(tasks);
    }

    private void createAchievements() {
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new Achievement(100, Enums.Statistic.TotalSpins, "CgkIoMe6hp0eEAIQGA"));
        achievements.add(new Achievement(1000, Enums.Statistic.TotalSpins, "CgkIoMe6hp0eEAIQGQ"));
        achievements.add(new Achievement(10000, Enums.Statistic.TotalSpins, "CgkIoMe6hp0eEAIQGg"));
        achievements.add(new Achievement(5, Enums.Statistic.Level, "CgkIoMe6hp0eEAIQGw"));
        achievements.add(new Achievement(10, Enums.Statistic.Level, "CgkIoMe6hp0eEAIQHA"));
        achievements.add(new Achievement(15, Enums.Statistic.Level, "CgkIoMe6hp0eEAIQHQ"));
        achievements.add(new Achievement(20, Enums.Statistic.Level, "CgkIoMe6hp0eEAIQHg"));
        achievements.add(new Achievement(500, Enums.Statistic.ResourcesGambled, "CgkIoMe6hp0eEAIQHw"));
        achievements.add(new Achievement(5000, Enums.Statistic.ResourcesGambled, "CgkIoMe6hp0eEAIQIA"));
        achievements.add(new Achievement(10000, Enums.Statistic.ResourcesGambled, "CgkIoMe6hp0eEAIQIQ"));
        achievements.add(new Achievement(1, Enums.Statistic.MinigameChest, "CgkIoMe6hp0eEAIQAQ"));
        achievements.add(new Achievement(10, Enums.Statistic.MinigameChest, "CgkIoMe6hp0eEAIQIg"));
        achievements.add(new Achievement(100, Enums.Statistic.MinigameChest, "CgkIoMe6hp0eEAIQIw"));
        achievements.add(new Achievement(1, Enums.Statistic.MinigameDice, "CgkIoMe6hp0eEAIQAg"));
        achievements.add(new Achievement(10, Enums.Statistic.MinigameDice, "CgkIoMe6hp0eEAIQJA"));
        achievements.add(new Achievement(100, Enums.Statistic.MinigameDice, "CgkIoMe6hp0eEAIQJQ"));
        achievements.add(new Achievement(1, Enums.Statistic.MinigameFlip, "CgkIoMe6hp0eEAIQAw"));
        achievements.add(new Achievement(10, Enums.Statistic.MinigameFlip, "CgkIoMe6hp0eEAIQJg"));
        achievements.add(new Achievement(100, Enums.Statistic.MinigameFlip, "CgkIoMe6hp0eEAIQJw"));
        achievements.add(new Achievement(1, Enums.Statistic.CollectedBonuses, "CgkIoMe6hp0eEAIQBA"));
        achievements.add(new Achievement(10, Enums.Statistic.CollectedBonuses, "CgkIoMe6hp0eEAIQKA"));
        achievements.add(new Achievement(30, Enums.Statistic.CollectedBonuses, "CgkIoMe6hp0eEAIQKQ"));
        achievements.add(new Achievement(1, Enums.Statistic.VipLevel, "CgkIoMe6hp0eEAIQBQ"));
        Achievement.saveInTx(achievements);
    }

    private void createTrophies() {
        List<Trophy> trophies = new ArrayList<>();
        // Add bronze to mithril items
        for (int i = Enums.Tier.Bronze.value; i <= Enums.Tier.Mithril.value; i++) {
            trophies.add(new Trophy(i, 1));
            trophies.add(new Trophy(i, 2));
            trophies.add(new Trophy(i, 3));
            trophies.add(new Trophy(i, 4));
            trophies.add(new Trophy(i, 5));
            trophies.add(new Trophy(i, 6));
            trophies.add(new Trophy(i, 7));
            trophies.add(new Trophy(i, 8));
            trophies.add(new Trophy(i, 9));
            trophies.add(new Trophy(i, 10));
            trophies.add(new Trophy(i, 11));
            trophies.add(new Trophy(i, 12));
            trophies.add(new Trophy(i, 13));
            trophies.add(new Trophy(i, 14));
            trophies.add(new Trophy(i, 15));
            trophies.add(new Trophy(i, 16));
            trophies.add(new Trophy(i, 17));
            trophies.add(new Trophy(i, 18));
            trophies.add(new Trophy(i, 19));
        }

        // Add adamant items
        trophies.add(new Trophy(5, 1, 10));
        trophies.add(new Trophy(5, 2, 10));
        trophies.add(new Trophy(5, 5, 10));
        trophies.add(new Trophy(5, 6, 10));
        trophies.add(new Trophy(5, 8, 10));
        trophies.add(new Trophy(5, 10, 10));
        trophies.add(new Trophy(5, 13, 10));
        trophies.add(new Trophy(5, 14, 10));

        // Add silver and gold items
        for (int i = Enums.Tier.Silver.value; i <= Enums.Tier.Gold.value; i++) {
            trophies.add(new Trophy(i, 1));
            trophies.add(new Trophy(i, 2));
            trophies.add(new Trophy(i, 35));
            trophies.add(new Trophy(i, 36));
            trophies.add(new Trophy(i, 37));
            trophies.add(new Trophy(i, 38));
            trophies.add(new Trophy(i, 39));
        }

        // Gold purple ring
        trophies.add(new Trophy(7, 40, 10));

        // Lucky coin
        trophies.add(new Trophy(0, 20));

        int[] foodTiers = { Enums.Tier.None.value, Enums.Tier.PartialFood.value};
        for (int tier : foodTiers) {
            trophies.add(new Trophy(tier, 21));
            trophies.add(new Trophy(tier, 22));
            trophies.add(new Trophy(tier, 23));
            trophies.add(new Trophy(tier, 24));
            trophies.add(new Trophy(tier, 25));
            trophies.add(new Trophy(tier, 26));
            trophies.add(new Trophy(tier, 27));
            trophies.add(new Trophy(tier, 28));
            trophies.add(new Trophy(tier, 29));
            trophies.add(new Trophy(tier, 30));
            trophies.add(new Trophy(tier, 31));
            trophies.add(new Trophy(tier, 32));
            trophies.add(new Trophy(tier, 33));
        }

        // Gems / powders / books
        trophies.add(new Trophy(0, 34));
        trophies.add(new Trophy(0, 35));
        trophies.add(new Trophy(0, 36));
        trophies.add(new Trophy(0, 37));
        trophies.add(new Trophy(0, 38));
        trophies.add(new Trophy(0, 39));
        trophies.add(new Trophy(0, 40, 100));
        trophies.add(new Trophy(0, 41));
        trophies.add(new Trophy(0, 42));
        trophies.add(new Trophy(0, 43));
        trophies.add(new Trophy(0, 44));
        trophies.add(new Trophy(0, 45, 100));
        trophies.add(new Trophy(0, 46, 100));
        trophies.add(new Trophy(0, 47, 100));
        trophies.add(new Trophy(0, 48, 100));
        trophies.add(new Trophy(0, 49, 100));
        trophies.add(new Trophy(0, 50, 100));
        trophies.add(new Trophy(0, 51, 100));
        trophies.add(new Trophy(0, 52, 100));
        trophies.add(new Trophy(0, 53, 100));

        Trophy.saveInTx(trophies);
    }

    private void createStatistics() {
        List<Statistic> statistics = new ArrayList<>();
        statistics.add(new Statistic(Enums.StatisticType.Progress, Enums.Statistic.Xp, "", "", Constants.STARTING_XP));
        statistics.add(new Statistic(Enums.StatisticType.Progress, Enums.Statistic.Level, "CgkIoMe6hp0eEAIQBw", "CgkIoMe6hp0eEAIQEA", 1, 0));
        statistics.add(new Statistic(Enums.StatisticType.Progress, Enums.Statistic.VipLevel, "CgkIoMe6hp0eEAIQDw", "", 0, 0));
        statistics.add(new Statistic(Enums.StatisticType.Events, Enums.Statistic.TotalSpins, "CgkIoMe6hp0eEAIQCA", "CgkIoMe6hp0eEAIQEQ", 0, 0));
        statistics.add(new Statistic(Enums.StatisticType.Events, Enums.Statistic.QuestsCompleted, "", "", 0, 0));
        statistics.add(new Statistic(Enums.StatisticType.Events, Enums.Statistic.ResourcesGambled, "CgkIoMe6hp0eEAIQCg", "CgkIoMe6hp0eEAIQEw", 0, 0));
        statistics.add(new Statistic(Enums.StatisticType.Events, Enums.Statistic.ResourcesWon, "CgkIoMe6hp0eEAIQCw", "CgkIoMe6hp0eEAIQFA", 0, 0));
        statistics.add(new Statistic(Enums.StatisticType.Events, Enums.Statistic.AdvertsWatched, "CgkIoMe6hp0eEAIQDA", "CgkIoMe6hp0eEAIQFQ", 0, 0));
        statistics.add(new Statistic(Enums.StatisticType.Misc, Enums.Statistic.PacksPurchased, "CgkIoMe6hp0eEAIQDQ", "CgkIoMe6hp0eEAIQFg", 0, 0));
        statistics.add(new Statistic(Enums.StatisticType.Bonuses, Enums.Statistic.CollectedBonuses, "CgkIoMe6hp0eEAIQDg", "CgkIoMe6hp0eEAIQFw", 0, 0));
        statistics.add(new Statistic(Enums.StatisticType.Misc, Enums.Statistic.SaveImported, "", "", false));
        statistics.add(new Statistic(Enums.StatisticType.Bonuses, Enums.Statistic.LastBonusClaimed, "", "", 0L));
        statistics.add(new Statistic(Enums.StatisticType.Misc, Enums.Statistic.LastAutosave, "", "", 0L));
        statistics.add(new Statistic(Enums.StatisticType.Misc, Enums.Statistic.LastAdvertWatched, "", "", 0L));
        statistics.add(new Statistic(Enums.StatisticType.Misc, Enums.Statistic.TrophiesEarned, "", "CgkIoMe6hp0eEAIQKg", 0, 0));
        statistics.add(new Statistic(Enums.StatisticType.BlacksmithPass, Enums.Statistic.CurrentPassClaimedDay, "", "", 0));
        statistics.add(new Statistic(Enums.StatisticType.BlacksmithPass, Enums.Statistic.HighestPassClaimedDay, "", "", 0));
        statistics.add(new Statistic(Enums.StatisticType.BlacksmithPass, Enums.Statistic.TotalPassDaysClaimed, "", "", 0));
        statistics.add(new Statistic(Enums.StatisticType.BlacksmithPass, Enums.Statistic.ExtraPassMonths, "", "", 0));
        statistics.add(new Statistic(Enums.StatisticType.Minigames, Enums.Statistic.MinigameChest, "", "", 0, 0));
        statistics.add(new Statistic(Enums.StatisticType.Minigames, Enums.Statistic.MinigameDice, "", "", 0, 0));
        statistics.add(new Statistic(Enums.StatisticType.Minigames, Enums.Statistic.MinigameFlip, "", "", 0, 0));
        Statistic.saveInTx(statistics);
    }
}
