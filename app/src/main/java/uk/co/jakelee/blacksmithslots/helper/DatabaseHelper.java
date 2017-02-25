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
            inventories.add(new Inventory(Constants.RES_BRONZE_ORE, 9999));
        Inventory.saveInTx(inventories);

        List<Resource> resources = new ArrayList<>();
            resources.add(new Resource(Constants.RES_BRONZE_ORE));
            resources.add(new Resource(Constants.RES_BRONZE_BAR));
            resources.add(new Resource(Constants.RES_BRONZE_DAGGER));
            resources.add(new Resource(Constants.RES_BRONZE_SWORD));
            resources.add(new Resource(Constants.RES_WILDCARD));
        Resource.saveInTx(resources);

        List<Reward> rewards = new ArrayList<>();
            rewards.add(new Reward(Constants.SLOT_BRONZE_CRAFTING, Constants.RES_BRONZE_ORE, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_CRAFTING, Constants.RES_BRONZE_BAR, 1, 8));
            rewards.add(new Reward(Constants.SLOT_BRONZE_CRAFTING, Constants.RES_BRONZE_BAR, 10, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_CRAFTING, Constants.RES_BRONZE_DAGGER, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_CRAFTING, Constants.RES_BRONZE_SWORD, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_CRAFTING, Constants.RES_WILDCARD, 1, 1));
        Reward.saveInTx(rewards);

        List<Slot> slots = new ArrayList<>();
            slots.add(new Slot(Constants.SLOT_BRONZE_CRAFTING, 1, 1, 2, 5, 1, 5, Constants.SLOTS_4_MAX_ROUTES, Constants.RES_BRONZE_ORE, 4, Enums.Type.Crafting, Enums.Tier.Bronze));
        Slot.saveInTx(slots);

        List<Statistic> statistics = new ArrayList<>();
            statistics.add(new Statistic(Constants.STATISTIC_XP, 100));
        Statistic.saveInTx(statistics);
    }
}
