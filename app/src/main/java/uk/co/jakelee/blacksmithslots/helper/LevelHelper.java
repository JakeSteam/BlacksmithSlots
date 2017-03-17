package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.util.Log;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Locale;

import uk.co.jakelee.blacksmithslots.R;
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

    public static int getVipLevel() {
        Statistic vipInfo = Select.from(Statistic.class).where(
                Condition.prop("a").eq(Enums.Statistic.VipLevel.value)).first();

        return vipInfo.getIntValue();
    }

    public static int getXp() {
        Statistic xpInfo = Select.from(Statistic.class).where(
                Condition.prop("a").eq(Enums.Statistic.Xp.value)).first();

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

    public static String addXp(Context context, int xp) {
        Log.d("XP", "Added: " + xp);
        Statistic.add(Enums.Statistic.Xp, xp);

        Statistic savedLevel = Statistic.get(Enums.Statistic.Level);
        int level = LevelHelper.getLevel();
        if (savedLevel != null && savedLevel.getIntValue() < level) {
            Statistic.add(Enums.Statistic.Level, level - savedLevel.getIntValue());
            Log.d("Level", "Levelled up to " + level);
            return String.format(Locale.ENGLISH,
                    context.getString(R.string.alert_levelled_up),
                    level,
                    IncomeHelper.claimBonus(context, false));
        }
        return "";
    }

    public static Enums.Tier getCurrentTierByLevel(int level) {
        if (level <= Constants.BRONZE_MAX_LEVEL) {
            return Enums.Tier.Bronze;
        } else if (level <= Constants.IRON_MAX_LEVEL) {
            return Enums.Tier.Iron;
        }
        return Enums.Tier.Bronze;
    }

    public static int getAutospinsByVip(int vipLevel) {
        return (5 + (10 * vipLevel));
    }
}
