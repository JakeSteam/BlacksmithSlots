package uk.co.jakelee.blacksmithslots.helper;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.model.Resource;
import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class DatabaseHelper {
    public static void testSetup() {
        List<Resource> resources = new ArrayList<>();
            resources.add(new Resource(Constants.BRONZE_ORE));
            resources.add(new Resource(Constants.BRONZE_BAR));
        Resource.saveInTx(resources);

        List<Reward> rewards = new ArrayList<>();
            rewards.add(new Reward(Constants.BRONZE_CRAFTING, Constants.BRONZE_ORE, 10, 10));
            rewards.add(new Reward(Constants.BRONZE_CRAFTING, Constants.BRONZE_BAR, 10, 10));
        Reward.saveInTx(rewards);

        List<Slot> slots = new ArrayList<>();
            slots.add(new Slot(Constants.BRONZE_CRAFTING, 1, Constants.BRONZE_ORE, 5, Enums.Type.Crafting, Enums.Tier.Bronze));
        Slot.saveInTx(slots);

        List<Statistic> statistics = new ArrayList<>();
            statistics.add(new Statistic(Constants.STATISTIC_XP, 100));
        Statistic.saveInTx(statistics);
    }
}
