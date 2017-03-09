package uk.co.jakelee.blacksmithslots.helper;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class IncomeHelper {
    public static List<Reward> getPeriodicBonus() {
        int currentLevel = LevelHelper.getLevel();
        int vipLevel = LevelHelper.getVipLevel();

        // Reward ores / bars etc
        return new ArrayList<>();
    }
}
