package uk.co.jakelee.blacksmithslots.helper;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.SplashScreenActivity;
import uk.co.jakelee.blacksmithslots.model.Iap;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Item;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Message;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Statistic;
import uk.co.jakelee.blacksmithslots.model.Task;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHelper extends AsyncTask<String, String, String> {
    public final static int NO_DATABASE = 0;
    public final static int V0_0_1 = 1;

    public final static int LATEST_PATCH = V0_0_1;

    private SplashScreenActivity callingActivity;
    private TextView progressText;
    private ProgressBar progressBar;

    public DatabaseHelper() {}

    public DatabaseHelper(SplashScreenActivity activity, boolean runningFromMain) {
        this.callingActivity = activity;
        if (runningFromMain) {
            this.progressText = (TextView) activity.findViewById(R.id.progressText);
            this.progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        }
    }

    private void createDatabase() {
        setProgress("Inventory", 0);
        createInventories();
        setProgress("Items", 0);
        createItems();
        setProgress("Shop", 0);
        createShop();
        setProgress("Settings", 0);
        createSettings();
        setProgress("Slots", 0);
        createSlots();
        setProgress("Statistics", 0);
        createStatistics();
    }

    private void setProgress(String currentTask, int percentage) {
        if (progressText != null && progressBar != null) {
            publishProgress(currentTask);
            progressBar.setProgress(percentage);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        SharedPreferences prefs = callingActivity.getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);

        if (prefs.getInt("databaseVersion", DatabaseHelper.NO_DATABASE) <= DatabaseHelper.NO_DATABASE) {
            createDatabase();
            prefs.edit().putInt("databaseVersion", V0_0_1).apply();
            Message.log("Installed database!");
        }

        /*if (prefs.getInt("databaseVersion", DatabaseHelper.NO_DATABASE) < DatabaseHelper.V0_0_2) {
            setProgress("Patch 0.0.2", 60);
            patchTo002();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.V0_0_2).apply();
        }*/
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.length() > 0) {
            Message.log(result);
            AlertHelper.success(callingActivity, result, true);
        }

        if (callingActivity != null) {
            callingActivity.startGame();
        }
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progressText.setText("Installing:\n" + values[0]);
    }

    private void createInventories() {
        List<Inventory> inventories = new ArrayList<>();
            inventories.add(new Inventory(Enums.Tier.None, Enums.Type.LuckyCoin, 100));
        Inventory.saveInTx(inventories);
    }

    private void createItems() {
        List<Item> items = new ArrayList<>();
            items.add(new Item(Enums.Tier.None, Enums.Type.LuckyCoin));

            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Ore));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Bar));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Dagger));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Sword));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Longsword));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Bow));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.HalfShield));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.FullShield));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Chainmail));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Platebody));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.HalfHelmet));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.FullHelmet));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Boots));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Gloves));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Pickaxe));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Hatchet));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.FishingRod));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Hammer));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Secondary));

            items.add(new Item(Enums.Tier.Internal, Enums.Type.MinigameFlip));
            items.add(new Item(Enums.Tier.Internal, Enums.Type.Wildcard));
        Item.saveInTx(items);
    }

    private void createShop() {
        List<Iap> iaps = new ArrayList<>();
        List<ItemBundle> iapBundles = new ArrayList<>();
            // VIP Levels
            iaps.add(new Iap(Enums.Iap.VipLevel1, true));
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel1, Enums.Tier.Bronze, Enums.Type.Bar, 1000));
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel1, Enums.Tier.Bronze, Enums.Type.Secondary, 1000));

            iaps.add(new Iap(Enums.Iap.VipLevel2, true));
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel2, Enums.Tier.Bronze, Enums.Type.Bar, 2000));
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel2, Enums.Tier.Bronze, Enums.Type.Secondary, 2000));

            iaps.add(new Iap(Enums.Iap.VipLevel3, true));
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel3, Enums.Tier.Bronze, Enums.Type.Bar, 2000));
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel3, Enums.Tier.Bronze, Enums.Type.Secondary, 2000));

            iaps.add(new Iap(Enums.Iap.VipLevel4, true));
            iaps.add(new Iap(Enums.Iap.VipLevel5, true));
            iaps.add(new Iap(Enums.Iap.VipLevel6, true));

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

            // Bundles
            iaps.add(new Iap(Enums.Iap.BronzeOre1000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeOre1000, Enums.Tier.Bronze, Enums.Type.Ore, 1000));
            iaps.add(new Iap(Enums.Iap.BronzeOre5000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeOre5000, Enums.Tier.Bronze, Enums.Type.Ore, 5000));
            iaps.add(new Iap(Enums.Iap.BronzeOre10000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeOre10000, Enums.Tier.Bronze, Enums.Type.Ore, 10000));

            iaps.add(new Iap(Enums.Iap.BronzeSecondary1000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeSecondary1000, Enums.Tier.Bronze, Enums.Type.Secondary, 1000));
            iaps.add(new Iap(Enums.Iap.BronzeSecondary5000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeSecondary5000, Enums.Tier.Bronze, Enums.Type.Secondary, 5000));
            iaps.add(new Iap(Enums.Iap.BronzeSecondary10000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeSecondary10000, Enums.Tier.Bronze, Enums.Type.Secondary, 10000));

            iaps.add(new Iap(Enums.Iap.IronOre1000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.IronOre1000, Enums.Tier.Iron, Enums.Type.Ore, 1000));
            iaps.add(new Iap(Enums.Iap.IronOre5000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.IronOre5000, Enums.Tier.Iron, Enums.Type.Ore, 5000));
            iaps.add(new Iap(Enums.Iap.IronOre10000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.IronOre10000, Enums.Tier.Iron, Enums.Type.Ore, 10000));
    
            iaps.add(new Iap(Enums.Iap.IronSecondary1000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.IronSecondary1000, Enums.Tier.Iron, Enums.Type.Secondary, 1000));
            iaps.add(new Iap(Enums.Iap.IronSecondary5000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.IronSecondary5000, Enums.Tier.Iron, Enums.Type.Secondary, 5000));
            iaps.add(new Iap(Enums.Iap.IronSecondary10000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.IronSecondary10000, Enums.Tier.Iron, Enums.Type.Secondary, 10000));
        Iap.saveInTx(iaps);
        ItemBundle.saveInTx(iapBundles);
    }

    private void createSettings() {
        List<Setting> settings = new ArrayList<>();
            settings.add(new Setting(Enums.SettingGroup.Audio, Enums.Setting.Music, true));
            settings.add(new Setting(Enums.SettingGroup.Audio, Enums.Setting.Sound, true));
            settings.add(new Setting(Enums.SettingGroup.Internal, Enums.Setting.AttemptLogin, true));
            settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.AutosaveMinutes, 10));
            settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.OnlyActiveResources, true));
            settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.Language, Enums.Language.English.value));
            settings.add(new Setting(Enums.SettingGroup.Notifications, Enums.Setting.NotificationSounds, true));
            settings.add(new Setting(Enums.SettingGroup.Notifications, Enums.Setting.PeriodicBonusNotification, true));
            settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.SaveImported, false));
            settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.OnlyShowStocked, false));
            settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.OrderByTier, false));
            settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.OrderReversed, false));
            settings.add(new Setting(Enums.SettingGroup.Notifications, Enums.Setting.BlacksmithPassNotification, true));
        Setting.saveInTx(settings);
    }

    private void createSlots() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
            slots.add(new Slot(Enums.Slot.Map1LuckyCoin, 1, 1, 1, 2, Enums.Slot.Map1LuckyCoin, Enums.Person.Map1Mum, Enums.Map.Home));
            itemBundles.add(new ItemBundle(Enums.Slot.Map1LuckyCoin, Enums.Tier.None, Enums.Type.LuckyCoin, 1));

            itemBundles.add(new ItemBundle(Enums.Slot.Map1LuckyCoin, Enums.Tier.Bronze, Enums.Type.Ore, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map1LuckyCoin, Enums.Tier.Bronze, Enums.Type.Secondary, 1, 1));
            //itemBundles.add(new ItemBundle(Enums.Slot.Map1LuckyCoin, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map1LuckyCoin, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Enums.Slot.Map2Furnace, 1, 1, 5, 4, Enums.Slot.Map1LuckyCoin, Enums.Person.Map2Blacksmith, Enums.Map.Neighbourhood));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Furnace, Enums.Tier.Bronze, Enums.Type.Ore, 1));
            tasks.add(new Task(Enums.Slot.Map2Furnace, 1, Enums.Tier.Bronze, Enums.Type.Ore, 15));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Furnace, Enums.Tier.Bronze, Enums.Type.Bar, 1, 9));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Furnace, Enums.Tier.Bronze, Enums.Type.Ore, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Furnace, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Enums.Slot.Map2Accessories, 1, 1, 5, 3, Enums.Slot.Map2Furnace, Enums.Person.Map2Blacksmith, Enums.Map.Neighbourhood));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.Bar, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.Secondary, 1));
            tasks.add(new Task(Enums.Slot.Map2Accessories, 1, Enums.Tier.Bronze, Enums.Type.Bar, 20));
            tasks.add(new Task(Enums.Slot.Map2Accessories, 2, Enums.Tier.Bronze, Enums.Type.Secondary, 20));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.Boots, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.Gloves, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Bronze, Enums.Type.FullHelmet, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Accessories, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Enums.Slot.Map2Tools, 1, 1, 5, 3, Enums.Slot.Map2Furnace, Enums.Person.Map2Blacksmith, Enums.Map.Neighbourhood));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.Bar, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.Secondary, 1));
            tasks.add(new Task(Enums.Slot.Map2Tools, 1, Enums.Tier.Bronze, Enums.Type.Boots, 10));
            tasks.add(new Task(Enums.Slot.Map2Tools, 2, Enums.Tier.Bronze, Enums.Type.Gloves, 10));
            tasks.add(new Task(Enums.Slot.Map2Tools, 3, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 10));
            tasks.add(new Task(Enums.Slot.Map2Tools, 4, Enums.Tier.Bronze, Enums.Type.FullHelmet, 10));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.Pickaxe, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.Hatchet, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.FishingRod, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Bronze, Enums.Type.Hammer, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Tools, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Enums.Slot.Map2Weapons, 1, 1, 5, 3, Enums.Slot.Map2Furnace, Enums.Person.Map2Blacksmith, Enums.Map.Neighbourhood));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Bar, 2));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Secondary, 2));
            tasks.add(new Task(Enums.Slot.Map2Weapons, 1, Enums.Tier.Bronze, Enums.Type.FishingRod, 7));
            tasks.add(new Task(Enums.Slot.Map2Weapons, 2, Enums.Statistic.Level, 2));
            tasks.add(new Task(Enums.Slot.Map2Weapons, 3, Enums.Statistic.TotalSpins, 30));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Dagger, 2, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Sword, 2, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Longsword, 2, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Bronze, Enums.Type.Bow, 2, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Weapons, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Enums.Slot.Map2Armour, 1, 1, 5, 3, Enums.Slot.Map2Furnace, Enums.Person.Map2Blacksmith, Enums.Map.Neighbourhood));
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
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Bronze, Enums.Type.Chainmail, 2, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Bronze, Enums.Type.Platebody, 2, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Bronze, Enums.Type.HalfShield, 2, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Bronze, Enums.Type.FullShield, 2, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Bronze, Enums.Type.Bar, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map2Armour, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 3));

            slots.add(new Slot(Enums.Slot.Map3Secondary, 1, 1, 5, 3, Enums.Slot.Map2Armour, Enums.Person.Map2Blacksmith, Enums.Map.Forest));
            itemBundles.add(new ItemBundle(Enums.Slot.Map3Secondary, Enums.Tier.Bronze, Enums.Type.Bar, 3));
            tasks.add(new Task(Enums.Slot.Map3Secondary, 1, Enums.Statistic.Level, 3));
            itemBundles.add(new ItemBundle(Enums.Slot.Map3Secondary, Enums.Tier.Bronze, Enums.Type.Secondary, 1, 1));
            itemBundles.add(new ItemBundle(Enums.Slot.Map3Secondary, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));
        Task.saveInTx(tasks);
        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
    }

    private void createStatistics() {
        List<Statistic> statistics = new ArrayList<>();
            statistics.add(new Statistic(Enums.StatisticType.Progress, Enums.Statistic.Xp, "", "", Constants.STARTING_XP));
            statistics.add(new Statistic(Enums.StatisticType.Progress, Enums.Statistic.Level, "CgkIoMe6hp0eEAIQBw", "CgkIoMe6hp0eEAIQEA", 1));
            statistics.add(new Statistic(Enums.StatisticType.Progress, Enums.Statistic.VipLevel, "CgkIoMe6hp0eEAIQDw", "", 0));
            statistics.add(new Statistic(Enums.StatisticType.Events, Enums.Statistic.TotalSpins, "CgkIoMe6hp0eEAIQCA", "CgkIoMe6hp0eEAIQEQ", 0));
            statistics.add(new Statistic(Enums.StatisticType.Events, Enums.Statistic.QuestsCompleted, "CgkIoMe6hp0eEAIQCQ", "CgkIoMe6hp0eEAIQEg", 0));
            statistics.add(new Statistic(Enums.StatisticType.Events, Enums.Statistic.ResourcesGambled, "CgkIoMe6hp0eEAIQCg", "CgkIoMe6hp0eEAIQEw", 0));
            statistics.add(new Statistic(Enums.StatisticType.Events, Enums.Statistic.ResourcesWon, "CgkIoMe6hp0eEAIQCw", "CgkIoMe6hp0eEAIQFA", 0));
            statistics.add(new Statistic(Enums.StatisticType.Events, Enums.Statistic.AdvertsWatched, "CgkIoMe6hp0eEAIQDA", "CgkIoMe6hp0eEAIQFQ", 0));
            statistics.add(new Statistic(Enums.StatisticType.Misc, Enums.Statistic.PacksPurchased, "CgkIoMe6hp0eEAIQDQ", "CgkIoMe6hp0eEAIQFg", 0));
            statistics.add(new Statistic(Enums.StatisticType.Bonuses, Enums.Statistic.CollectedBonuses, "CgkIoMe6hp0eEAIQDg", "CgkIoMe6hp0eEAIQFw", 0));
            statistics.add(new Statistic(Enums.StatisticType.Misc, Enums.Statistic.SaveImported, "", "", false));
            statistics.add(new Statistic(Enums.StatisticType.Bonuses, Enums.Statistic.LastBonusClaimed, "", "", 0L));
            statistics.add(new Statistic(Enums.StatisticType.Misc, Enums.Statistic.LastAutosave, "", "", 0L));
            statistics.add(new Statistic(Enums.StatisticType.Misc, Enums.Statistic.LastAdvertWatched, "", "", 0L));
            statistics.add(new Statistic(Enums.StatisticType.BlacksmithPass, Enums.Statistic.CurrentPassClaimedDay, "", "", 0));
            statistics.add(new Statistic(Enums.StatisticType.BlacksmithPass, Enums.Statistic.HighestPassClaimedDay, "", "", 0));
            statistics.add(new Statistic(Enums.StatisticType.BlacksmithPass, Enums.Statistic.TotalPassDaysClaimed, "", "", 0));
            statistics.add(new Statistic(Enums.StatisticType.BlacksmithPass, Enums.Statistic.ExtraPassMonths, "", "", 0));
        Statistic.saveInTx(statistics);
    }
}
