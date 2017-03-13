package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.constructs.ItemResult;
import uk.co.jakelee.blacksmithslots.constructs.TierRange;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class IncomeHelper {
    public static boolean canClaimAdvertBonus() {
        return true;
    }

    public static boolean canClaimPeriodicBonus() {
        Statistic lastClaimed = Statistic.get(Enums.Statistic.LastBonusClaimed);
        Long nextClaimTime = lastClaimed.getLongValue() + Constants.BONUS_DELAY;
        return nextClaimTime - System.currentTimeMillis() <= 0;
    }

    public static String claimAdvertBonus(Context context) {
        List<ItemResult> bonus = getAdvertBonus();
        return "";
    }

    public static String claimLevelUpBonus(Context context, int levelAchieved) {
        List<ItemResult> bonus = getLevelUpBonus();
        return "";
    }

    public static String claimPeriodicBonus(Context context) {
        List<ItemResult> bonus = getPeriodicBonus();
        StringBuilder winningsText = new StringBuilder().append("Claimed: ");
        for (ItemResult result : bonus) {
            Inventory.addInventory(result);
            winningsText.append(result.toString(context)).append(", ");
        }

        Statistic.add(Enums.Statistic.CollectedBonuses);
        Statistic lastClaimed = Statistic.get(Enums.Statistic.LastBonusClaimed);
        lastClaimed.setLongValue(System.currentTimeMillis());
        lastClaimed.save();

        return winningsText.substring(0, winningsText.length() - 2);
    }

    public static List<ItemResult> getAdvertBonus() {
        return new ArrayList<>();
    }

    public static List<ItemResult> getLevelUpBonus() {
        return new ArrayList<>();
    }

    private static List<ItemResult> getPeriodicBonus() {
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
}
