package uk.co.jakelee.blacksmithslots.helper;

import java.util.concurrent.TimeUnit;

import uk.co.jakelee.blacksmithslots.model.Statistic;

public class MinigameHelper {
    public static int getCurrentCharges() {
        Statistic lastClaim = Statistic.get(Enums.Statistic.MinigameMemoryLastClaim);
        long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - lastClaim.getLongValue());
        long charges = totalMinutes / Constants.MINUTES_PER_CHARGE;

        int maxCharges = getMaxCharges();
        return charges > getMaxCharges() ? maxCharges : (int)charges;
    }

    public static int getMinsToNextCharge() {
        Statistic lastClaim = Statistic.get(Enums.Statistic.MinigameMemoryLastClaim);
        long totalMinutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - lastClaim.getLongValue());
        return (int)(Constants.MINUTES_PER_CHARGE - (totalMinutes % Constants.MINUTES_PER_CHARGE));
    }

    public static int getMaxCharges() {
        return 5;
    }

    public static void useCharge() {
        Statistic.add(Enums.Statistic.MinigameMemory);
        Statistic lastClaim = Statistic.get(Enums.Statistic.MinigameMemoryLastClaim);

        long millisToRemove = TimeUnit.MINUTES.toMillis((getCurrentCharges() - 1) * Constants.MINUTES_PER_CHARGE);
        long adjustedLastClaimTime = System.currentTimeMillis() - millisToRemove;
        lastClaim.setLongValue(adjustedLastClaimTime);
        lastClaim.save();
    }
}
