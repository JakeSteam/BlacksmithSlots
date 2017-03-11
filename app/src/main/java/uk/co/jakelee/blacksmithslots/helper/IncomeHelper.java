package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.constructs.ItemResult;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class IncomeHelper {
    public static List<ItemResult> getPeriodicBonus() {
        ArrayList<ItemResult> bonus = new ArrayList<>();
        int currentLevel = LevelHelper.getLevel();
        int vipLevel = LevelHelper.getVipLevel();

        if (currentLevel >= Constants.BRONZE_MIN_LEVEL) {
            int adjustedLevel = Math.min(currentLevel, Constants.BRONZE_MAX_LEVEL);
            bonus.add(new ItemResult(Enums.Tier.Bronze, Enums.Type.Bar, (adjustedLevel - Constants.BRONZE_MIN_LEVEL + 1) * Constants.BRONZE_PER_LEVEL));
        }

        if (currentLevel >= Constants.IRON_MIN_LEVEL) {
            int adjustedLevel = Math.min(currentLevel, Constants.IRON_MAX_LEVEL);
            bonus.add(new ItemResult(Enums.Tier.Iron, Enums.Type.Bar, (adjustedLevel - Constants.IRON_MIN_LEVEL + 1) * Constants.IRON_PER_LEVEL));
        }

        // Apply VIP bonus
        for (ItemResult result : bonus) {
            result.setResourceQuantity(increaseByPercentage(result.getResourceQuantity(), vipLevel * Constants.VIP_LEVEL_MODIFIER));
        }

        return bonus;
    }

    private static int increaseByPercentage(int number, int percent) {
        double percentMultiplier = percent + 100;
        return (int)Math.ceil(number * (percentMultiplier / 100));
    }

    public static boolean canClaimBonus() {
        Statistic lastClaimed = Statistic.get(Enums.Statistic.LastBonusClaimed);
        Long nextClaimTime = lastClaimed.getLongValue() + Constants.BONUS_DELAY;
        return nextClaimTime - System.currentTimeMillis() <= 0;
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
}
