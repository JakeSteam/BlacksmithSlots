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
            resources.add(new Resource(Constants.RES_BRONZE_LONGSWORD ));
            resources.add(new Resource(Constants.RES_BRONZE_BOW));
            resources.add(new Resource(Constants.RES_BRONZE_HALFSHIELD));
            resources.add(new Resource(Constants.RES_BRONZE_FULLSHIELD));
            resources.add(new Resource(Constants.RES_BRONZE_CHAINMAIL ));
            resources.add(new Resource(Constants.RES_BRONZE_PLATEBODY ));
            resources.add(new Resource(Constants.RES_BRONZE_HALFHELMET));
            resources.add(new Resource(Constants.RES_BRONZE_FULLHELMET));
            resources.add(new Resource(Constants.RES_BRONZE_BOOTS));
            resources.add(new Resource(Constants.RES_BRONZE_GLOVES));
            resources.add(new Resource(Constants.RES_BRONZE_PICKAXE));
            resources.add(new Resource(Constants.RES_BRONZE_HATCHET));
            resources.add(new Resource(Constants.RES_BRONZE_FISHINGROD));
            resources.add(new Resource(Constants.RES_BRONZE_HAMMER));

            resources.add(new Resource(Constants.RES_WILDCARD));
        Resource.saveInTx(resources);

        List<Slot> slots = new ArrayList<>();
        List<Reward> rewards = new ArrayList<>();
            slots.add(new Slot(Constants.SLOT_BRONZE_FURNACE, 1, 1, 2, 5, 1, 5, Constants.SLOTS_4_MAX_ROUTES, Constants.RES_BRONZE_ORE, 4, Enums.Type.Furnace, Enums.Tier.Bronze));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Constants.RES_BRONZE_ORE, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Constants.RES_BRONZE_BAR, 1, 8));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Constants.RES_BRONZE_BAR, 10, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Constants.RES_BRONZE_DAGGER, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Constants.RES_BRONZE_SWORD, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Constants.RES_WILDCARD, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_WEAPON, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Constants.RES_BRONZE_BAR, 3, Enums.Type.Weapon, Enums.Tier.Bronze));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Constants.RES_BRONZE_DAGGER, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Constants.RES_BRONZE_SWORD, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Constants.RES_BRONZE_LONGSWORD, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Constants.RES_BRONZE_BOW, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Constants.RES_WILDCARD, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_ARMOUR, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Constants.RES_BRONZE_BAR, 3, Enums.Type.Armour, Enums.Tier.Bronze));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Constants.RES_BRONZE_CHAINMAIL, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Constants.RES_BRONZE_PLATEBODY, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Constants.RES_BRONZE_HALFSHIELD, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Constants.RES_BRONZE_FULLSHIELD, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Constants.RES_WILDCARD, 1, 2));

            slots.add(new Slot(Constants.SLOT_BRONZE_TOOL, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Constants.RES_BRONZE_BAR, 3, Enums.Type.Tool, Enums.Tier.Bronze));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Constants.RES_BRONZE_PICKAXE, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Constants.RES_BRONZE_HATCHET, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Constants.RES_BRONZE_FISHINGROD, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Constants.RES_BRONZE_HAMMER, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Constants.RES_WILDCARD, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_ACCESSORY, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Constants.RES_BRONZE_BAR, 3, Enums.Type.Accessory, Enums.Tier.Bronze));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Constants.RES_BRONZE_BOOTS, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Constants.RES_BRONZE_GLOVES, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Constants.RES_BRONZE_HALFHELMET, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Constants.RES_BRONZE_FULLHELMET, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Constants.RES_WILDCARD, 1, 1));
        Slot.saveInTx(slots);
        Reward.saveInTx(rewards);

        List<Statistic> statistics = new ArrayList<>();
            statistics.add(new Statistic(Constants.STATISTIC_XP, Constants.STARTING_XP));
        Statistic.saveInTx(statistics);
    }
}
