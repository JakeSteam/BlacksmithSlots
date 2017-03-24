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
        setProgress("Tasks", 0);
        createTasks();
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
            inventories.add(new Inventory(Enums.Tier.Bronze, Enums.Type.Ore, 9999));
        Inventory.saveInTx(inventories);
    }

    private void createItems() {
        List<Item> items = new ArrayList<>();
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Ore));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Bar));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Dagger));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Sword));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Longsword));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Bow));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Halfshield));
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
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel1, Enums.Tier.Bronze, Enums.Type.Bar, 1000, true));
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel1, Enums.Tier.Bronze, Enums.Type.Secondary, 1000, true));

            iaps.add(new Iap(Enums.Iap.VipLevel2, true));
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel2, Enums.Tier.Bronze, Enums.Type.Bar, 1000, true));
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel2, Enums.Tier.Bronze, Enums.Type.Secondary, 1000, true));

            iaps.add(new Iap(Enums.Iap.VipLevel3, true));
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel3, Enums.Tier.Bronze, Enums.Type.Bar, 1000, true));
            iapBundles.add(new ItemBundle(Enums.Iap.VipLevel3, Enums.Tier.Bronze, Enums.Type.Secondary, 1000, true));

            // Blacksmith's Pass
            iaps.add(new Iap(Enums.Iap.BlacksmithPass, false));

            // Bundles
            iaps.add(new Iap(Enums.Iap.BronzeBar1000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeBar1000, Enums.Tier.Bronze, Enums.Type.Bar, 1000, true));
            iaps.add(new Iap(Enums.Iap.BronzeBar5000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeBar5000, Enums.Tier.Bronze, Enums.Type.Bar, 5000, true));
            iaps.add(new Iap(Enums.Iap.BronzeBar10000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeBar10000, Enums.Tier.Bronze, Enums.Type.Bar, 10000, true));

            iaps.add(new Iap(Enums.Iap.BronzeSecondary1000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeSecondary1000, Enums.Tier.Bronze, Enums.Type.Secondary, 1000, true));
            iaps.add(new Iap(Enums.Iap.BronzeSecondary5000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeSecondary5000, Enums.Tier.Bronze, Enums.Type.Secondary, 5000, true));
            iaps.add(new Iap(Enums.Iap.BronzeSecondary10000, false));
            iapBundles.add(new ItemBundle(Enums.Iap.BronzeSecondary10000, Enums.Tier.Bronze, Enums.Type.Secondary, 10000, true));
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
            settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.SaveImported, false));
            settings.add(new Setting(Enums.SettingGroup.Gameplay, Enums.Setting.OnlyShowStocked, false));
            settings.add(new Setting(Enums.SettingGroup.Notifications, Enums.Setting.NotificationSounds, true));
            settings.add(new Setting(Enums.SettingGroup.Notifications, Enums.Setting.OrderByTier, false));
            settings.add(new Setting(Enums.SettingGroup.Notifications, Enums.Setting.OrderReversed, false));
        Setting.saveInTx(settings);
    }

    private void createSlots() {
        List<Slot> slots = new ArrayList<>();
        List<ItemBundle> itemBundles = new ArrayList<>();
            slots.add(new Slot(Constants.SLOT_BRONZE_FURNACE, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Enums.SlotType.Furnace, 3, 0, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Ore, 2, false));

            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Ore, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Internal, Enums.Type.MinigameFlip, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Bar, 1, 8));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Bar, 10, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Secondary, 1, 8));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Secondary, 10, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_WEAPON, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Enums.SlotType.Weapon, 3, Constants.SLOT_BRONZE_FURNACE, 2));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Bar, 1, false));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Secondary, 1, false));

            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Dagger, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Sword, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Longsword, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Bow, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_ARMOUR, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Enums.SlotType.Armour, 3, Constants.SLOT_BRONZE_WEAPON, 3));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Bar, 1, false));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Secondary, 1, false));

            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Chainmail, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Platebody, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Halfshield, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.FullShield, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 2));

            slots.add(new Slot(Constants.SLOT_BRONZE_TOOL, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Enums.SlotType.Tool, 3, Constants.SLOT_BRONZE_ARMOUR, 4));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.Bar, 1, false));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.Secondary, 1, false));

            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.Pickaxe, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.Hatchet, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.FishingRod, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.Hammer, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_ACCESSORY, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Enums.SlotType.Accessory, 3, Constants.SLOT_BRONZE_TOOL, 5));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.Bar, 1, false));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.Secondary, 1, false));

            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.Boots, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.Gloves, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.FullHelmet, 1, 1));
            itemBundles.add(new ItemBundle(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));
        Slot.saveInTx(slots);
        ItemBundle.saveInTx(itemBundles);
    }

    private void createStatistics() {
        List<Statistic> statistics = new ArrayList<>();
            statistics.add(new Statistic(Enums.Statistic.Xp, "", "", Constants.STARTING_XP));
            statistics.add(new Statistic(Enums.Statistic.Level, "CgkIoMe6hp0eEAIQBw", "CgkIoMe6hp0eEAIQEA", 1));
            statistics.add(new Statistic(Enums.Statistic.VipLevel, "CgkIoMe6hp0eEAIQDw", "", 3));
            statistics.add(new Statistic(Enums.Statistic.TotalSpins, "CgkIoMe6hp0eEAIQCA", "CgkIoMe6hp0eEAIQEQ", 0));
            statistics.add(new Statistic(Enums.Statistic.QuestsCompleted, "CgkIoMe6hp0eEAIQCQ", "CgkIoMe6hp0eEAIQEg", 0));
            statistics.add(new Statistic(Enums.Statistic.ResourcesGambled, "CgkIoMe6hp0eEAIQCg", "CgkIoMe6hp0eEAIQEw", 0));
            statistics.add(new Statistic(Enums.Statistic.ResourcesWon, "CgkIoMe6hp0eEAIQCw", "CgkIoMe6hp0eEAIQFA", 0));
            statistics.add(new Statistic(Enums.Statistic.AdvertsWatched, "CgkIoMe6hp0eEAIQDA", "CgkIoMe6hp0eEAIQFQ", 0));
            statistics.add(new Statistic(Enums.Statistic.PacksPurchased, "CgkIoMe6hp0eEAIQDQ", "CgkIoMe6hp0eEAIQFg", 0));
            statistics.add(new Statistic(Enums.Statistic.CollectedBonuses, "CgkIoMe6hp0eEAIQDg", "CgkIoMe6hp0eEAIQFw", 0));
            statistics.add(new Statistic(Enums.Statistic.SaveImported, "", "", false));
            statistics.add(new Statistic(Enums.Statistic.LastBonusClaimed, "", "", 0L));
            statistics.add(new Statistic(Enums.Statistic.LastAutosave, "", "", 0L));
            statistics.add(new Statistic(Enums.Statistic.LastAdvertWatched, "", "", 0L));
            statistics.add(new Statistic(Enums.Statistic.LastAdvertWatched, "", "", 0L));
        Statistic.saveInTx(statistics);
    }

    private void createTasks() {
        int position;
        List<Task> tasks = new ArrayList<>();
            tasks.add(new Task(Constants.SLOT_BRONZE_WEAPON, 1, Enums.Statistic.TotalSpins, 2));
            tasks.add(new Task(Constants.SLOT_BRONZE_ARMOUR, 1, Enums.Statistic.TotalSpins, 2));

            position = 1;
            tasks.add(new Task(Constants.SLOT_BRONZE_TOOL, position++, Enums.Tier.Bronze, Enums.Type.FullShield, 10));
            tasks.add(new Task(Constants.SLOT_BRONZE_TOOL, position++, Enums.Tier.Bronze, Enums.Type.Dagger, 10));
            tasks.add(new Task(Constants.SLOT_BRONZE_TOOL, position++, Enums.Statistic.TotalSpins, 5));
            tasks.add(new Task(Constants.SLOT_BRONZE_TOOL, position++, Enums.Statistic.Level, 100));
            tasks.add(new Task(Constants.SLOT_BRONZE_TOOL, position++, Enums.Tier.Bronze, Enums.Type.Ore, 100));

            position = 1;
            tasks.add(new Task(Constants.SLOT_BRONZE_ACCESSORY, position++, Enums.Tier.Bronze, Enums.Type.Bar, 100));
            tasks.add(new Task(Constants.SLOT_BRONZE_ACCESSORY, position++, Enums.Tier.Bronze, Enums.Type.Dagger, 1000));
        Task.saveInTx(tasks);
    }
}
