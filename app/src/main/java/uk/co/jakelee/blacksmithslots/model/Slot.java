package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.Enums;

public class Slot extends SugarRecord {
    private int slotId;
    private int minimumLevel;
    private int minimumStake;
    private int currentStake;
    private int maximumStake;
    private int minimumRows;
    private int currentRows;
    private int maximumRows;
    private int resourceNeeded;
    private int slots;
    private Enums.Type type;
    private Enums.Tier tier;

    public Slot() {
    }

    public Slot(int slotId, int minimumLevel, int minimumStake, int currentStake, int maximumStake, int minimumRows, int currentRows, int maximumRows, int resourceNeeded, int slots, Enums.Type type, Enums.Tier tier) {
        this.slotId = slotId;
        this.minimumLevel = minimumLevel;
        this.minimumStake = minimumStake;
        this.currentStake = currentStake;
        this.maximumStake = maximumStake;
        this.minimumRows = minimumRows;
        this.currentRows = currentRows;
        this.maximumRows = maximumRows;
        this.resourceNeeded = resourceNeeded;
        this.slots = slots;
        this.type = type;
        this.tier = tier;
    }

    public static Slot get(int slotId) {
        List<Slot> slots = Select.from(Slot.class).where(
                Condition.prop("slot_id").eq(slotId)).list();
        return slots.size() > 0 ? slots.get(0) : null;
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

    public int getMinimumStake() {
        return minimumStake;
    }

    public void setMinimumStake(int minimumStake) {
        this.minimumStake = minimumStake;
    }

    public int getCurrentStake() {
        return currentStake;
    }

    public void setCurrentStake(int currentStake) {
        this.currentStake = currentStake;
    }

    public int getMaximumStake() {
        return maximumStake;
    }

    public void setMaximumStake(int maximumStake) {
        this.maximumStake = maximumStake;
    }

    public int getMinimumRows() {
        return minimumRows;
    }

    public void setMinimumRows(int minimumRows) {
        this.minimumRows = minimumRows;
    }

    public int getCurrentRows() {
        return currentRows;
    }

    public void setCurrentRows(int currentRows) {
        this.currentRows = currentRows;
    }

    public int getMaximumRows() {
        return maximumRows;
    }

    public void setMaximumRows(int maximumRows) {
        this.maximumRows = maximumRows;
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

    public List<Reward> getRewards() {
        return Select.from(Reward.class).where(
                Condition.prop("slot_id").eq(slotId)).list();
    }
}
