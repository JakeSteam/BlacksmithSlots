package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.TaskHelper;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;

@Table(name = "l")
public class Farm extends SugarRecord {
    @Column(name="a") private int farmId;
    @Column(name="b") private int requiredSlot;
    @Column(name="c") private int tier;
    @Column(name="d") private int itemTier;
    @Column(name="e") private int itemType;
    @Column(name="f") private int itemRequirement;
    @Column(name="g") private int itemQuantity;
    @Column(name="h") private long lastClaim;
    @Column(name="i") private int defaultCapacityMultiplier;
    @Column(name="j") private long defaultClaimTime;
    @Column(name="k") private int timesClaimed;

    public Farm() {
    }

    public Farm(Enums.Farm farm, Enums.Slot slot, int itemRequirement, int itemQuantity, int defaultCapacityMultiplier, int claimMinutes) {
        this.farmId = farm.value;
        this.requiredSlot = slot.value;
        this.tier = 1;
        this.itemTier = 0;
        this.itemType = 0;
        this.itemRequirement = itemRequirement;
        this.itemQuantity = itemQuantity;
        this.defaultCapacityMultiplier = defaultCapacityMultiplier;
        this.lastClaim = System.currentTimeMillis();
        this.defaultClaimTime = TimeUnit.MINUTES.toMillis(claimMinutes);
        this.timesClaimed = 0;
    }

    public static Farm get(int farmId) {
        List<Farm> farms = Select.from(Farm.class).where(
                Condition.prop("a").eq(farmId)).list();
        return farms.size() > 0 ? farms.get(0) : null;
    }

    public int getRequiredSlot() {
        return requiredSlot;
    }

    public void setRequiredSlot(int requiredSlot) {
        this.requiredSlot = requiredSlot;
    }

    public int getTier() {
        return TaskHelper.isSlotLocked(requiredSlot) ? 0 : tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getItemTier() {
        return itemTier;
    }

    public void setItemTier(int itemTier) {
        this.itemTier = itemTier;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public long getLastClaim() {
        return lastClaim;
    }

    public void setLastClaim(long lastClaim) {
        this.lastClaim = lastClaim;
    }

    public long getDefaultClaimTime() {
        return defaultClaimTime;
    }

    public void setDefaultClaimTime(long defaultClaimTime) {
        this.defaultClaimTime = defaultClaimTime;
    }

    public int getTimesClaimed() {
        return timesClaimed;
    }

    public void setTimesClaimed(int timesClaimed) {
        this.timesClaimed = timesClaimed;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("farm_" + farmId + "_name");
    }

    public ItemBundle getActiveBundle() {
        return Select.from(ItemBundle.class).where(
                Condition.prop("f").eq(Enums.ItemBundleType.FarmReward.value),
                Condition.prop("a").eq(farmId),
                Condition.prop("b").eq(itemTier),
                Condition.prop("c").eq(itemType)).first();
    }

    // Each tier is +50% or so
    public int getCurrentCapacity() {
        ItemBundle activeBundle = getActiveBundle();
        if (activeBundle == null || tier == 0) {
            return 0;
        }

        return (int)((defaultCapacityMultiplier * itemQuantity) * (Math.pow(2, ((double)tier / 2) - 0.5)));
    }

    // Each tier is -10%
    public long getClaimTime() {
        if (tier == 0) {
            return defaultClaimTime;
        }
        return (long)(defaultClaimTime * (Math.pow(Constants.FARM_TIME_ADJUST, tier - 1)));
    }

    // Usually double capacity
    public int getUpgradeCost() {
        return tier == 0 ? (defaultCapacityMultiplier * itemQuantity) : getCurrentCapacity() * 2;
    }
}
