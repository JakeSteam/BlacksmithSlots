package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.constructs.TierRange;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class IncomeHelper {
    public static boolean canWatchAdvert() {
        return getNextAdvertWatchTime() - System.currentTimeMillis() <= 0;
    }

    public static boolean canClaimPeriodicBonus() {
        return getNextPeriodicClaimTime() - System.currentTimeMillis() <= 0;
    }

    public static long getNextPeriodicClaimTime() {
        Statistic lastClaimed = Statistic.get(Enums.Statistic.LastBonusClaimed);
        double hours = IncomeHelper.getChestCooldownHours(LevelHelper.getVipLevel());
        long millis = DateHelper.hoursToMillis(hours);
        return lastClaimed.getLongValue() + millis;
    }

    public static long getNextAdvertWatchTime() {
        Statistic lastWatched = Statistic.get(Enums.Statistic.LastAdvertWatched);
        double mins = IncomeHelper.getAdvertCooldownMins(LevelHelper.getVipLevel());
        long millis = DateHelper.minsToMillis((long)mins);
        return lastWatched.getLongValue() + millis;
    }

    public static String claimMiscBonus(Context context) {
        return claimBonus(context, getRegularBonus(), false, false);
    }

    public static String claimPeriodicBonus(Context context) {
        return claimBonus(context, getPeriodicBonus(), true, false);
    }

    public static String claimAdvertBonus(Context context) {
        return claimBonus(context, getAdvertBonus(), false, true);
    }

    public static String claimImportBonus(Context context, Integer prestige) {
        return String.format(Locale.ENGLISH, context.getString(R.string.pb_save_import),
                prestige,
                claimBonus(context, getBonus(1 + (prestige > 5 ? 5 : prestige)), false, false));
    }

    private static String claimBonus(Context context, List<ItemBundle> bonus, boolean claimingPeriodicBonus, boolean claimingAdvertBonus) {
        StringBuilder winningsText = new StringBuilder().append(context.getString(R.string.claimed_prefix));
        for (ItemBundle result : bonus) {
            Inventory.addInventory(result);
            winningsText.append(result.toString(context)).append(", ");
        }

        if (claimingPeriodicBonus || claimingAdvertBonus) {
            Statistic.add(Enums.Statistic.CollectedBonuses);
            Statistic lastClaimed = Statistic.get(claimingAdvertBonus ? Enums.Statistic.LastAdvertWatched : Enums.Statistic.LastBonusClaimed);
            lastClaimed.setLongValue(System.currentTimeMillis());
            lastClaimed.save();
        }

        return winningsText.substring(0, winningsText.length() - 2);
    }

    private static List<ItemBundle> getPeriodicBonus() {
        return getBonus(Constants.PERIODIC_REWARD_MODIFIER);
    }

    private static List<ItemBundle> getAdvertBonus() {
        return getBonus(Constants.ADVERT_REWARD_MODIFIER);
    }

    private static List<ItemBundle> getRegularBonus() {
        return getBonus(1.0);
    }

    private static List<ItemBundle> getBonus(double modifier) {
        ArrayList<ItemBundle> bonus = new ArrayList<>();
        int currentLevel = LevelHelper.getLevel();
        int vipLevel = LevelHelper.getVipLevel();

        List<TierRange> tierRanges = TierRange.getAllRanges();
        for (TierRange tierRange : tierRanges) {
            if (currentLevel >= tierRange.getMin()) {
                int adjustedLevel = Math.min(currentLevel, tierRange.getMax() + 1);
                int levelMultiplier = adjustedLevel - tierRange.getMin() + 1;
                int adjustedAmount = (int)Math.ceil(tierRange.getItemPerLevel() * modifier);
                bonus.add(new ItemBundle(tierRange.getTier(), Enums.Type.Bar, levelMultiplier * adjustedAmount));

                if (tierRange.getTier() != Enums.Tier.Gold && tierRange.getTier() != Enums.Tier.Silver) {
                    bonus.add(new ItemBundle(tierRange.getTier(), Enums.Type.Secondary, levelMultiplier * adjustedAmount));
                }
            }
        }

        // Apply VIP bonus
        for (ItemBundle result : bonus) {
            result.setQuantity(CalculationHelper.increaseByPercentage(result.getQuantity(), vipLevel * Constants.VIP_LEVEL_MODIFIER));
        }

        return bonus;
    }

    public static double getChestCooldownHours(int vipLevel) {
        return Constants.CHEST_DEFAULT_COOLDOWN_HOURS - (vipLevel * Constants.CHEST_COOLDOWN_VIP_REDUCTION);
    }

    public static int getAdvertCooldownMins(int vipLevel) {
        return (int)Math.ceil((Constants.ADVERT_DEFAULT_COOLDOWN_HOURS - ((vipLevel * Constants.ADVERT_COOLDOWN_VIP_REDUCTION))) * 60);
    }
}
