package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.constructs.ItemResult;
import uk.co.jakelee.blacksmithslots.constructs.TierRange;
import uk.co.jakelee.blacksmithslots.model.Inventory;
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
        double hours = IncomeHelper.getAdvertCooldownMins(LevelHelper.getVipLevel());
        long millis = DateHelper.hoursToMillis(hours);
        return lastWatched.getLongValue() + millis;
    }

    public static String claimBonus(Context context, boolean claimingPeriodicBonus) {
        List<ItemResult> bonus = getBonus();
        StringBuilder winningsText = new StringBuilder().append("Claimed: ");
        for (ItemResult result : bonus) {
            Inventory.addInventory(result);
            winningsText.append(result.toString(context)).append(", ");
        }

        if (claimingPeriodicBonus) {
            Statistic.add(Enums.Statistic.CollectedBonuses);
            Statistic lastClaimed = Statistic.get(Enums.Statistic.LastBonusClaimed);
            lastClaimed.setLongValue(System.currentTimeMillis());
            lastClaimed.save();
        }

        return winningsText.substring(0, winningsText.length() - 2);
    }

    public static String watchAdvert(Context context, boolean claimingAdvertBonus) {
        List<ItemResult> bonus = getBonus();
        StringBuilder winningsText = new StringBuilder().append("Claimed: ");
        for (ItemResult result : bonus) {
            Inventory.addInventory(result);
            winningsText.append(result.toString(context)).append(", ");
        }

        if (claimingAdvertBonus) {
            Statistic.add(Enums.Statistic.CollectedBonuses);
            Statistic lastClaimed = Statistic.get(Enums.Statistic.LastAdvertWatched);
            lastClaimed.setLongValue(System.currentTimeMillis());
            lastClaimed.save();
        }

        return winningsText.substring(0, winningsText.length() - 2);
    }

    private static List<ItemResult> getBonus() {
        ArrayList<ItemResult> bonus = new ArrayList<>();
        int currentLevel = LevelHelper.getLevel();
        int vipLevel = LevelHelper.getVipLevel();

        List<TierRange> tierRanges = TierRange.getAllRanges();
        for (TierRange tierRange : tierRanges) {
            if (currentLevel >= tierRange.getMin()) {
                int adjustedLevel = Math.min(currentLevel, tierRange.getMax() + 1);
                bonus.add(new ItemResult(tierRange.getTier(), Enums.Type.Bar, (adjustedLevel - tierRange.getMin() + 1) * tierRange.getItemPerLevel()));
            }
        }

        // Apply VIP bonus
        for (ItemResult result : bonus) {
            result.setResourceQuantity(CalculationHelper.increaseByPercentage(result.getResourceQuantity(), vipLevel * Constants.VIP_LEVEL_MODIFIER));
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
