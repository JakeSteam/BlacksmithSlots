package uk.co.jakelee.blacksmithslots.helper;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.model.Event;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Resource;
import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class DatabaseHelper {
    public static void testSetup() {
        List<Event> events = new ArrayList<>();
            events.add(new Event(Enums.Event.Spin));
        Event.saveInTx(events);

        List<Inventory> inventories = new ArrayList<>();
            inventories.add(new Inventory(Enums.Tier.Bronze, Enums.Type.Ore, 9999));
        Inventory.saveInTx(inventories);

        List<Resource> resources = new ArrayList<>();
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Ore));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Bar));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Dagger));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Sword));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Longsword));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Bow));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Halfshield));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Fullshield));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Chainmail));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Platebody));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.HalfHelmet));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.FullHelmet));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Boots));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Gloves));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Pickaxe));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Hatchet));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.FishingRod));
            resources.add(new Resource(Enums.Tier.Bronze, Enums.Type.Hammer));

            resources.add(new Resource(Enums.Tier.Internal, Enums.Type.Wildcard));
        Resource.saveInTx(resources);

        List<Slot> slots = new ArrayList<>();
        List<Reward> rewards = new ArrayList<>();
            slots.add(new Slot(Constants.SLOT_BRONZE_FURNACE, 1, 1, 2, 5, 1, 5, Constants.SLOTS_4_MAX_ROUTES, Enums.Tier.Bronze, Enums.Type.Ore, Enums.SlotType.Furnace, 4, 1, 0));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Ore, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Bar, 1, 8));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Bar, 10, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Dagger, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Sword, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_WEAPON, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Enums.Tier.Bronze, Enums.Type.Bar, Enums.SlotType.Weapon, 3, 1, 0));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Dagger, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Sword, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Longsword, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Bow, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_ARMOUR, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Enums.Tier.Bronze, Enums.Type.Bar, Enums.SlotType.Armour, 3, 1, 0));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Chainmail, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Platebody, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Halfshield, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Fullshield, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 2));

            slots.add(new Slot(Constants.SLOT_BRONZE_TOOL, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Enums.Tier.Bronze, Enums.Type.Bar, Enums.SlotType.Tool, 3, 1, 0));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.Pickaxe, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.Hatchet, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.FishingRod, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.Hammer, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_ACCESSORY, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, Enums.Tier.Bronze, Enums.Type.Bar, Enums.SlotType.Accessory, 3, 1, 0));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.Boots, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.Gloves, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.FullHelmet, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));
        Slot.saveInTx(slots);
        Reward.saveInTx(rewards);

        List<Statistic> statistics = new ArrayList<>();
            statistics.add(new Statistic(Constants.STATISTIC_XP, Constants.STARTING_XP));
        Statistic.saveInTx(statistics);
    }
}
