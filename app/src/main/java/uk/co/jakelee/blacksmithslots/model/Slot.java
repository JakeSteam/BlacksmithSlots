package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmithslots.helper.Enums;

public class Slot extends SugarRecord {
    private int slotId;
    private int minimumLevel;
    private int resourceNeeded;
    private int slots;
    private Enums.Type type;
    private Enums.Tier tier;

    public Slot() {
    }

    public Slot(int slotId, int minimumLevel, int resourceNeeded, int slots, Enums.Type type, Enums.Tier tier) {
        this.slotId = slotId;
        this.minimumLevel = minimumLevel;
        this.resourceNeeded = resourceNeeded;
        this.slots = slots;
        this.type = type;
        this.tier = tier;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getMinimumLevel() {
        return minimumLevel;
    }

    public void setMinimumLevel(int minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    public int getResourceNeeded() {
        return resourceNeeded;
    }

    public void setResourceNeeded(int resourceNeeded) {
        this.resourceNeeded = resourceNeeded;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public Enums.Type getType() {
        return type;
    }

    public void setType(Enums.Type type) {
        this.type = type;
    }

    public Enums.Tier getTier() {
        return tier;
    }

    public void setTier(Enums.Tier tier) {
        this.tier = tier;
    }
}
