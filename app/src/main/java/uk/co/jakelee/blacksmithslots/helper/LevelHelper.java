package uk.co.jakelee.blacksmithslots.helper;

import android.util.Log;

import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmithslots.model.Statistic;

public class LevelHelper {
    public static int convertXpToLevel(int xp) {
        // Level = 0.05 * sqrt(xp)
        return (int) (Constants.LEVEL_MODIFIER * Math.sqrt(xp));
    }

    public static int convertLevelToXp(int level) {
        // XP = (Level / 0.05) ^ 2
        return (int) Math.pow(level / Constants.LEVEL_MODIFIER, 2);
    }

    public static int getXp() {
        Statistic xpInfo = Select.from(Statistic.class).where(
                Condition.prop("statistic_id").eq(Constants.STATISTIC_XP)).first();

        return xpInfo.getIntValue();
    }

    public static int getLevel() {
        return convertXpToLevel(getXp());
    }

    public static int getLevelProgress() {
        int currentXP = getXp();
        int currentLevelXP = convertLevelToXp(getLevel());
        int nextLevelXP = convertLevelToXp(getLevel() + 1);

        double neededXP = nextLevelXP - currentLevelXP;
        double earnedXP = nextLevelXP - currentXP;

        return 10000 - (int) Math.ceil((earnedXP / neededXP) * 10000);
    }

    public static void addXp(int xp) {
        Log.d("XP", "Added: " + xp);
        Statistic xpInfo = Select.from(Statistic.class).where(Condition.prop("statistic_id").eq(Constants.STATISTIC_XP)).first();

        xpInfo.setIntValue(xpInfo.getIntValue() + xp);
        xpInfo.save();
    }
}
