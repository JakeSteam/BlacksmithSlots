package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;

@Table(name = "g")
public class Slot extends SugarRecord {
    @Column(name = "a")
    private int slotId;

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

    public Slot(Enums.Slot slot, int minimumStake, int maximumStake, int slots, Enums.Slot requiredSlot, Enums.Person person, Enums.Map map) {
        this.slotId = slot.value;
        this.minimumStake = minimumStake;
        this.currentStake = minimumStake;
        this.maximumStake = maximumStake;
        this.minimumRows = 1;
        this.currentRows = minimumRows;
        this.maximumRows = getMaxRowsBySlots(slots);
        this.slots = slots;
        this.requiredSlot = requiredSlot == null ? 0 : requiredSlot.value;
        this.person = person.value;
        this.mapId = map.value;
    }

    private static int getMaxRowsBySlots(int slot) {
        switch (slot) {
            case 2: return Constants.SLOTS_2_MAX_ROUTES;
            case 3: return Constants.SLOTS_3_MAX_ROUTES;
            case 4: return Constants.SLOTS_4_MAX_ROUTES;
            case 5: return Constants.SLOTS_5_MAX_ROUTES;
            default: return Constants.SLOTS_3_MAX_ROUTES;
        }
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

    public List<ItemBundle> getRewards(boolean applyWeights) {
        List<ItemBundle> weightedItemBundles = new ArrayList<>();
        List<ItemBundle> rawItemBundles = Select.from(ItemBundle.class).where(
                Condition.prop("a").eq(slotId),
                Condition.prop("f").eq(Enums.ItemBundleType.SlotReward.value)).list();
        if (!applyWeights) {
            return rawItemBundles;
        }

        for (ItemBundle bundle : rawItemBundles) {
            for (int i = 0; i < bundle.getWeighting(); i++) {
                weightedItemBundles.add(bundle);
            }
        }
        return weightedItemBundles;
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
}
