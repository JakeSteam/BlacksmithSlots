package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.Enums;

@Table(name="c")
public class Upgrade extends SugarRecord {
    @Column(name = "a") private int itemTier;
    @Column(name = "b") private int itemType;
    @Column(name = "c") private int itemsHandedIn;
    @Column(name = "d") private int itemsRequired;
    @Column(name = "e") private long achieved;
    @Column(name = "f") private int boostTier;
    @Column(name = "g") private boolean boostEnabled;

    public Upgrade() {
    }

    public Upgrade(int itemTier, int itemType) {
        this.itemTier = itemTier;
        this.itemType = itemType;
        this.itemsHandedIn = 0;
        this.itemsRequired = 1000;
        this.achieved = 0L;
        this.boostTier = 0;
        this.boostEnabled = false;
    }

    public Upgrade(int itemTier, int itemType, int itemsRequired) {
        this.itemTier = itemTier;
        this.itemType = itemType;
        this.itemsHandedIn = 0;
        this.itemsRequired = itemsRequired;
        this.achieved = 0L;
        this.boostTier = 0;
        this.boostEnabled = false;
    }

    public Enums.Tier getItemTier() {
        return Enums.Tier.get(itemTier);
    }

    public Enums.Type getItemType() {
        return Enums.Type.get(itemType);
    }

    public int getItemsHandedIn() {
        return itemsHandedIn;
    }

    public void setItemsHandedIn(int itemsHandedIn) {
        this.itemsHandedIn = itemsHandedIn;
    }

    public int getItemsRequired() {
        return itemsRequired;
    }

    public long getAchieved() {
        return achieved;
    }

    public void setAchieved() {
        this.achieved = System.currentTimeMillis();
        Statistic.add(Enums.Statistic.TrophiesEarned);
    }

    public boolean isAchieved() {
        return achieved > 0;
    }

    public int getBoostTier() {
        return boostTier;
    }

    public void setBoostTier(int boostTier) {
        this.boostTier = boostTier;
    }

    public boolean isBoostEnabled() {
        return boostEnabled;
    }

    public void setBoostEnabled(boolean boostEnabled) {
        this.boostEnabled = boostEnabled;
    }

    public int getBoostTierUpgradeCost() {
        return (int)Math.pow(Constants.ITEM_TIER_MULTIPLIER, getBoostTier()) * Constants.ITEM_TIER_BASE;
    }

    public int getItemsRemaining() {
        return itemsRequired - itemsHandedIn;
    }

    public static int getBoostTier(ItemBundle itemBundle) {
        return getBoostTier(itemBundle.getTier().value, itemBundle.getType().value);
    }

    public static int getActiveBoostTier(ItemBundle itemBundle) {
        return getActiveBoostTier(itemBundle.getTier().value, itemBundle.getType().value);
    }

    public static int getBoostTier(int tier, int type) {
        Upgrade upgrade = Select.from(Upgrade.class).where(
                Condition.prop("a").eq(tier),
                Condition.prop("b").eq(type)).first();
        if (upgrade != null) {
            return upgrade.getBoostTier();
        }
        return 0;
    }

    public static int getActiveBoostTier(int tier, int type) {
        Upgrade upgrade = Select.from(Upgrade.class).where(
                Condition.prop("a").eq(tier),
                Condition.prop("b").eq(type)).first();
        if (upgrade != null && upgrade.isBoostEnabled()) {
            return upgrade.getBoostTier();
        }
        return 0;
    }
}
