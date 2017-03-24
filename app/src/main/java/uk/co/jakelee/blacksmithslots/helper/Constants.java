package uk.co.jakelee.blacksmithslots.helper;

public class Constants {
    // Game constants
    public final static int ROWS = 5;
    public final static Double LEVEL_MODIFIER = 0.07;
    public final static int STARTING_XP = 205;
    public final static int MESSAGE_LOG_LIMIT = 100;

    public final static int NOTIFICATION_PERIODIC_BONUS = 100;
    public final static int NOTIFICATION_PASS_BONUS = 101;

    public final static int MINIGAME_FLIP = 123;

    // IAP Constants
    public final static int MAX_VIP_LEVEL = 6;
    public final static int CHEST_DEFAULT_COOLDOWN_HOURS = 6;
    public final static double CHEST_COOLDOWN_VIP_REDUCTION = 0.5;
    public final static int ADVERT_DEFAULT_COOLDOWN_HOURS = 1;
    public final static double ADVERT_COOLDOWN_VIP_REDUCTION = 0.15;
    public final static int VIP_LEVEL_MODIFIER = 25;
    public final static int VIP_DAILY_BONUS_MODIFIER = 50;
    public final static int PASS_DAYS = 30;

    // Intents
    public final static String INTENT_SLOT = "uk.co.jakelee.blacksmithslots.slot";

    // Used for income calculations
    public final static int BRONZE_MIN_LEVEL = 1;
    public final static int BRONZE_MAX_LEVEL = 4;
    public final static int BRONZE_PER_LEVEL = 50;
    public final static int IRON_MIN_LEVEL = 5;
    public final static int IRON_MAX_LEVEL = 9;
    public final static int IRON_PER_LEVEL = 60;

    // Slots
    public final static int SLOT_BRONZE_FURNACE = 1;
    public final static int SLOT_BRONZE_WEAPON = 2;
    public final static int SLOT_BRONZE_ARMOUR = 3;
    public final static int SLOT_BRONZE_TOOL = 4;
    public final static int SLOT_BRONZE_ACCESSORY = 5;

    // Slots routes
    public final static int SLOTS_3_MAX_ROUTES = 9;
    public final static int SLOTS_4_MAX_ROUTES = 9;
    public final static int SLOTS_5_MAX_ROUTES = 29;

}
