package uk.co.jakelee.blacksmithslots.helper;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.model.Resource;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class DatabaseHelper {
    public static void testSetup() {
        List<Slot> slots = new ArrayList<>();
            slots.add(new Slot(1, 1, Constants.BRONZE_BAR, Enums.Type.Misc, Enums.Tier.Bronze));
            slots.add(new Slot(2, 1, Constants.BRONZE_BAR, Enums.Type.Weapon, Enums.Tier.Bronze));
            slots.add(new Slot(3, 1, Constants.BRONZE_BAR, Enums.Type.Armour, Enums.Tier.Bronze));
        Slot.saveInTx(slots);

        List<Resource> resources = new ArrayList<>();
            resources.add(new Resource(Constants.BRONZE_BAR));
        Resource.saveInTx(resources);
    }
}
