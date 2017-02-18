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
            resources.add(new Resource(Constants.BRONZE_DAGGER));
            resources.add(new Resource(Constants.BRONZE_SWORD));
        Resource.saveInTx(resources);

        List<Reward> rewards = new ArrayList<>();
            rewards.add(new Reward(Constants.BRONZE_CRAFTING, Constants.BRONZE_ORE, 5, 1));
            rewards.add(new Reward(Constants.BRONZE_CRAFTING, Constants.BRONZE_BAR, 2, 8));
            rewards.add(new Reward(Constants.BRONZE_CRAFTING, Constants.BRONZE_BAR, 10, 1));
            rewards.add(new Reward(Constants.BRONZE_CRAFTING, Constants.BRONZE_DAGGER, 1, 1));
            rewards.add(new Reward(Constants.BRONZE_CRAFTING, Constants.BRONZE_SWORD, 1, 1));
        Reward.saveInTx(rewards);

        List<Slot> slots = new ArrayList<>();
            slots.add(new Slot(Constants.BRONZE_CRAFTING, 1, Constants.BRONZE_ORE, 2, Enums.Type.Crafting, Enums.Tier.Bronze));
        Slot.saveInTx(slots);

        List<Statistic> statistics = new ArrayList<>();
            statistics.add(new Statistic(Constants.STATISTIC_XP, 100));
        Statistic.saveInTx(statistics);
    }
}
