package uk.co.jakelee.blacksmithslots.helper;

import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Statistic;
import uk.co.jakelee.blacksmithslots.model.Task;

public class PrestigeHelper {

    public static boolean canPrestige() {
        return Statistic.get(Enums.Statistic.Level).getIntValue() >= Constants.PRESTIGE_LEVEL &&
                Statistic.get(Enums.Statistic.VipLevel).getIntValue() >= 1;
    }

    public static void prestigeGame() {
        if (canPrestige()) {
            Inventory.deleteAll(Inventory.class);
            DatabaseHelper.createInventories();

            Statistic xp = Statistic.get(Enums.Statistic.Xp);
            xp.setIntValue(Constants.STARTING_XP);
            xp.save();

            Statistic level = Statistic.get(Enums.Statistic.Level);
            level.setIntValue(1);
            level.save();

            Statistic prestige = Statistic.get(Enums.Statistic.Prestiges);
            prestige.setIntValue(prestige.getIntValue() + 1);
            prestige.save();

            Task.executeQuery("UPDATE i SET g = f, h = 0, i = 0");
        }
    }
}
