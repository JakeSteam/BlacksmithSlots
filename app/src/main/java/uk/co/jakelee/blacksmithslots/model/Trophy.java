package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

import uk.co.jakelee.blacksmithslots.helper.Enums;

@Table(name="c")
public class Trophy extends SugarRecord {
    @Column(name = "a") private int itemTier;
    @Column(name = "b") private int itemType;
    @Column(name = "c") private int itemsHandedIn;
    @Column(name = "d") private int itemsRequired;
    @Column(name = "e") private long achieved;

    public Trophy() {
    }

    public Trophy(int itemTier, int itemType) {
        this.itemTier = itemTier;
        this.itemType = itemType;
        this.itemsHandedIn = 0;
        this.itemsRequired = 1000;
        this.achieved = 0L;
    }

    public Trophy(int itemTier, int itemType, int itemsRequired) {
        this.itemTier = itemTier;
        this.itemType = itemType;
        this.itemsHandedIn = 0;
        this.itemsRequired = itemsRequired;
        this.achieved = 0L;
    }

    public Trophy(int itemTier, int itemType, int itemsHandedIn, int itemsRequired, long achieved) {
        this.itemTier = itemTier;
        this.itemType = itemType;
        this.itemsHandedIn = itemsHandedIn;
        this.itemsRequired = itemsRequired;
        this.achieved = achieved;
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

    public int getItemsRemaining() {
        return itemsRequired - itemsHandedIn;
    }
}
