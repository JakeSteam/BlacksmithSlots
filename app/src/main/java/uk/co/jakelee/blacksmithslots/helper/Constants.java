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
    public final static int ADVERT_WATCH = 12223;

    // IAP Constants
    public final static int MAX_VIP_LEVEL = 6;
    public final static int CHEST_DEFAULT_COOLDOWN_HOURS = 6;
    public final static double CHEST_COOLDOWN_VIP_REDUCTION = 0.5;
    public final static int ADVERT_DEFAULT_COOLDOWN_HOURS = 1;
    public final static double ADVERT_COOLDOWN_VIP_REDUCTION = 0.15;
    public final static int VIP_LEVEL_MODIFIER = 25;
    public final static int VIP_DAILY_BONUS_MODIFIER = 50;
    public final static int PASS_DAYS = 30;
    public final static int AUTOSPIN_INCREASE = 10;
    public static final int ADVERT_TIMEOUT = 30000;
    public static final double ADVERT_REWARD_MODIFIER = 0.33;
    public static final double PERIODIC_REWARD_MODIFIER = 1.0;

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
    public final static int MAP_1_LUCKY_COIN = 1;
    public final static int MAP_2_FURNACE = 2;

    // Slots routes
    public final static int SLOTS_2_MAX_ROUTES = 5;
    public final static int SLOTS_3_MAX_ROUTES = 9;
    public final static int SLOTS_4_MAX_ROUTES = 9;
    public final static int SLOTS_5_MAX_ROUTES = 11;

}
