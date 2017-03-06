package uk.co.jakelee.blacksmithslots.helper;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.model.Event;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Item;
import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Statistic;
import uk.co.jakelee.blacksmithslots.model.Task;

public class DatabaseHelper {
    public static void testSetup() {
        List<Event> events = new ArrayList<>();
            events.add(new Event(Enums.Event.Spin));
        Event.saveInTx(events);

        List<Inventory> inventories = new ArrayList<>();
            inventories.add(new Inventory(Enums.Tier.Bronze, Enums.Type.Ore, 9999));
        Inventory.saveInTx(inventories);

        List<Item> items = new ArrayList<>();
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Ore));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Bar));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Dagger));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Sword));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Longsword));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Bow));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Halfshield));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.FullShield));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Chainmail));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Platebody));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.HalfHelmet));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.FullHelmet));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Boots));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Gloves));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Pickaxe));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Hatchet));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.FishingRod));
            items.add(new Item(Enums.Tier.Bronze, Enums.Type.Hammer));

            items.add(new Item(Enums.Tier.Internal, Enums.Type.Wildcard));
        Item.saveInTx(items);

        List<Slot> slots = new ArrayList<>();
        List<Reward> rewards = new ArrayList<>();
            slots.add(new Slot(Constants.SLOT_BRONZE_FURNACE, 1, 1, 2, 5, 1, 5, Constants.SLOTS_4_MAX_ROUTES, 2, Enums.Tier.Bronze, Enums.Type.Ore, Enums.SlotType.Furnace, 4, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Ore, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Bar, 1, 8));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Bar, 10, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Dagger, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Bronze, Enums.Type.Sword, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_FURNACE, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_WEAPON, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, 1, Enums.Tier.Bronze, Enums.Type.Bar, Enums.SlotType.Weapon, 3, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Dagger, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Sword, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Longsword, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Bronze, Enums.Type.Bow, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_WEAPON, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_ARMOUR, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, 1, Enums.Tier.Bronze, Enums.Type.Bar, Enums.SlotType.Armour, 3, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Chainmail, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Platebody, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.Halfshield, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Bronze, Enums.Type.FullShield, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ARMOUR, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 2));

            slots.add(new Slot(Constants.SLOT_BRONZE_TOOL, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, 1, Enums.Tier.Bronze, Enums.Type.Bar, Enums.SlotType.Tool, 3, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.Pickaxe, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.Hatchet, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.FishingRod, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Bronze, Enums.Type.Hammer, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_TOOL, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));

            slots.add(new Slot(Constants.SLOT_BRONZE_ACCESSORY, 1, 1, 2, 5, 1, 5, Constants.SLOTS_3_MAX_ROUTES, 1, Enums.Tier.Bronze, Enums.Type.Bar, Enums.SlotType.Accessory, 3, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.Boots, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.Gloves, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.HalfHelmet, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Bronze, Enums.Type.FullHelmet, 1, 1));
            rewards.add(new Reward(Constants.SLOT_BRONZE_ACCESSORY, Enums.Tier.Internal, Enums.Type.Wildcard, 1, 1));
        Slot.saveInTx(slots);
        Reward.saveInTx(rewards);

        List<Statistic> statistics = new ArrayList<>();
            statistics.add(new Statistic(Enums.Statistic.Xp, Constants.STARTING_XP));
            statistics.add(new Statistic(Enums.Statistic.Level, 0));
            statistics.add(new Statistic(Enums.Statistic.TotalSpins, 0));
        Statistic.saveInTx(statistics);

        int position;
        List<Task> tasks = new ArrayList<>();
            position = 1;
            tasks.add(new Task(Constants.SLOT_BRONZE_TOOL, position++, Enums.Tier.Bronze, Enums.Type.FullShield, 10));
            tasks.add(new Task(Constants.SLOT_BRONZE_TOOL, position++, Enums.Tier.Bronze, Enums.Type.Dagger, 10));
            tasks.add(new Task(Constants.SLOT_BRONZE_TOOL, position++, Enums.Statistic.TotalSpins, 5));
            tasks.add(new Task(Constants.SLOT_BRONZE_TOOL, position++, Enums.Statistic.Level, 100));
            tasks.add(new Task(Constants.SLOT_BRONZE_TOOL, position++, Enums.Tier.Bronze, Enums.Type.Ore, 100));

            position = 1;
            tasks.add(new Task(Constants.SLOT_BRONZE_ACCESSORY, position++, Enums.Tier.Bronze, Enums.Type.Bar, 100));
            tasks.add(new Task(Constants.SLOT_BRONZE_ACCESSORY, position++, Enums.Tier.Bronze, Enums.Type.Dagger, 1000));
        Task.saveInTx(tasks);
    }
}
