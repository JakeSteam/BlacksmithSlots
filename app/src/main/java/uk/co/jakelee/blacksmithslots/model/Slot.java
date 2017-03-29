package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;

@Table(name = "g")
public class Slot extends SugarRecord {
    @Column(name = "a")
    private int slotId;

    @Column(name = "b")
    private int minimumLevel;

    @Column(name = "c")
    private int minimumStake;

    @Column(name = "d")
    private int currentStake;

    @Column(name = "e")
    private int maximumStake;

    @Column(name = "f")
    private int minimumRows;

    @Column(name = "g")
    private int currentRows;

    @Column(name = "h")
    private int maximumRows;

    @Column(name = "i")
    private int slotType;

    @Column(name = "j")
    private int slots;

    @Column(name = "k")
    private int requiredSlot;

    @Column(name = "l")
    private int person;

    @Column(name = "m")
    private int mapId;

    public Slot() {
    }

    public Slot(int slotId, int minimumLevel, int minimumStake, int currentStake, int maximumStake, int minimumRows, int currentRows, int maximumRows, Enums.SlotType slotType, int slots, int requiredSlot, int person, int mapId) {
        this.slotId = slotId;
        this.minimumLevel = minimumLevel;
        this.minimumStake = minimumStake;
        this.currentStake = currentStake;
        this.maximumStake = maximumStake;
        this.minimumRows = minimumRows;
        this.currentRows = currentRows;
        this.maximumRows = maximumRows;
        this.slotType = slotType.value;
        this.slots = slots;
        this.requiredSlot = requiredSlot;
        this.person = person;
        this.mapId = mapId;
    }

    public static Slot get(int slotId) {
        List<Slot> slots = Select.from(Slot.class).where(
                Condition.prop("a").eq(slotId)).list();
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

    public Enums.SlotType getSlotType() {
        return Enums.SlotType.get(slotType);
    }

    public void setSlotType(Enums.SlotType slotType) {
        this.slotType = slotType.value;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public int getRequiredSlot() {
        return requiredSlot;
    }

    public void setRequiredSlot(int requiredSlot) {
        this.requiredSlot = requiredSlot;
    }

    public int getPerson() {
        return person;
    }

    public void setPerson(int person) {
        this.person = person;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public List<ItemBundle> getRewards() {
        return Select.from(ItemBundle.class).where(
                Condition.prop("a").eq(slotId),
                Condition.prop("f").eq(Enums.ItemBundleType.SlotReward.value)).list();
    }

    public List<ItemBundle> getResources() {
        return Select.from(ItemBundle.class).where(
                Condition.prop("a").eq(slotId),
                Condition.prop("f").eq(Enums.ItemBundleType.SlotResource.value)).list();
    }

    public List<Task> getTasks() {
        return Select.from(Task.class).where(
                Condition.prop("a").eq(slotId))
                .orderBy("b ASC")
                .list();
    }

    public String getMapName(Context context) {
        return TextHelper.getInstance(context).getText("map_" + mapId);
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("slot_" + slotId + "_name");
    }

    public static String getName(Context context, int slotId) {
        return TextHelper.getInstance(context).getText("slot_" + slotId + "_name");
    }

    public String getLockedText(Context context) {
        return TextHelper.getInstance(context).getText("slot_" + slotId + "_locked");
    }

    public String getUnlockedText(Context context) {
        return TextHelper.getInstance(context).getText("slot_" + slotId + "_unlocked");
    }

    public String getResourceText(Context context) {
        StringBuilder resourceText = new StringBuilder();
        List<ItemBundle> itemBundles = getResources();
        for (ItemBundle itemBundle : itemBundles) {
            Item item = Item.get(itemBundle.getTier(), itemBundle.getType());
            if (item != null && item.getTier() != Enums.Tier.Internal) {
                resourceText.append(item.getName(context));
                resourceText.append(", ");
            }
        }

        String resourceString = resourceText.toString();
        return resourceString.length() > 0 ? resourceString.substring(0, resourceString.length() - 2) : "";
    }

    public String getRewardText(Context context) {
        StringBuilder rewardText = new StringBuilder();
        List<ItemBundle> itemBundles = getRewards();
        for (ItemBundle itemBundle : itemBundles) {
            Item item = Item.get(itemBundle.getTier(), itemBundle.getType());
            if (item != null && item.getTier() != Enums.Tier.Internal) {
                rewardText.append(item.getName(context));
                rewardText.append(", ");
            }
        }

        String rewardString = rewardText.toString();
        return rewardString.length() > 0 ? rewardString.substring(0, rewardString.length() - 2) : "";
    }
}
