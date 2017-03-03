package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;

public class Slot extends SugarRecord {
    private int slotId;
    private int minimumLevel;
    private int minimumStake;
    private int currentStake;
    private int maximumStake;
    private int minimumRows;
    private int currentRows;
    private int maximumRows;
    private Enums.Tier resourceTier;
    private Enums.Type resourceType;
    private Enums.SlotType slotType;
    private int slots;
    private int townId;

    public Slot() {
    }

    public Slot(int slotId, int minimumLevel, int minimumStake, int currentStake, int maximumStake, int minimumRows, int currentRows, int maximumRows, Enums.Tier resourceTier, Enums.Type resourceType, Enums.SlotType slotType, int slots, int townId) {
        this.slotId = slotId;
        this.minimumLevel = minimumLevel;
        this.minimumStake = minimumStake;
        this.currentStake = currentStake;
        this.maximumStake = maximumStake;
        this.minimumRows = minimumRows;
        this.currentRows = currentRows;
        this.maximumRows = maximumRows;
        this.resourceTier = resourceTier;
        this.resourceType = resourceType;
        this.slotType = slotType;
        this.slots = slots;
        this.townId = townId;
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

    public Enums.Tier getResourceTier() {
        return resourceTier;
    }

    public void setResourceTier(Enums.Tier resourceTier) {
        this.resourceTier = resourceTier;
    }

    public Enums.Type getResourceType() {
        return resourceType;
    }

    public void setResourceType(Enums.Type resourceType) {
        this.resourceType = resourceType;
    }

    public Enums.SlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(Enums.SlotType slotType) {
        this.slotType = slotType;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public int getTownId() {
        return townId;
    }

    public void setTownId(int townId) {
        this.townId = townId;
    }

    public List<Reward> getRewards() {
        return Select.from(Reward.class).where(
                Condition.prop("slot_id").eq(slotId)).list();
    }

    public List<Task> getTasks() {
        return Select.from(Task.class).where(
                Condition.prop("slot_id").eq(slotId))
                .orderBy("position ASC")
                .list();
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("slot_" + slotId + "_name");
    }

    public String getLockedText(Context context) {
        return TextHelper.getInstance(context).getText("slot_" + slotId + "_locked");
    }

    public String getUnlockedText(Context context) {
        return TextHelper.getInstance(context).getText("slot_" + slotId + "_unlocked");
    }

    public String getResourceText(Context context) {
        Item resourceItem = Item.get(getResourceTier(), getResourceType());
        if (resourceItem != null) {
            return resourceItem.getName(context);
        } else {
            return "";
        }
    }

    public String getRewardText(Context context) {
        StringBuilder rewardText = new StringBuilder();
        List<Reward> rewards = getRewards();
        for (Reward reward : rewards) {
            Item item = Item.get(reward.getTier(), reward.getType());
            if (item != null && item.getTier() != Enums.Tier.Internal) {
                rewardText.append(item.getName(context));
                rewardText.append(", ");
            }
        }

        String rewardString = rewardText.toString();
        return rewardString.length() > 0 ? rewardString.substring(0, rewardString.length() - 2) : "";
    }
}
