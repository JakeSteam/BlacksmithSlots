package uk.co.jakelee.blacksmithslots.helper;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Resource;
import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class DatabaseHelper {
    public static void testSetup() {
        List<Inventory> inventories = new ArrayList<>();
            inventories.add(new Inventory(Constants.BRONZE_ORE, 100));
        Inventory.saveInTx(inventories);

        List<Resource> resources = new ArrayList<>();
            resources.add(new Resource(Constants.BRONZE_ORE));
            resources.add(new Resource(Constants.BRONZE_BAR));
        Resource.saveInTx(resources);

        List<Reward> rewards = new ArrayList<>();
            rewards.add(new Reward(Constants.BRONZE_CRAFTING, Constants.BRONZE_ORE, 3, 2));
            rewards.add(new Reward(Constants.BRONZE_CRAFTING, Constants.BRONZE_BAR, 3, 9));
            rewards.add(new Reward(Constants.BRONZE_CRAFTING, Constants.BRONZE_DAGGER, 3, 1));
            rewards.add(new Reward(Constants.BRONZE_CRAFTING, Constants.BRONZE_SWORD, 3, 1));
        Reward.saveInTx(rewards);

        List<Slot> slots = new ArrayList<>();
            slots.add(new Slot(Constants.BRONZE_CRAFTING, 1, Constants.BRONZE_ORE, 3, Enums.Type.Crafting, Enums.Tier.Bronze));
        Slot.saveInTx(slots);

        List<Statistic> statistics = new ArrayList<>();
            statistics.add(new Statistic(Constants.STATISTIC_XP, 100));
        Statistic.saveInTx(statistics);
    }
}
