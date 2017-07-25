package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

@Table(name = "m")
public class FarmItem extends SugarRecord {
    @Column(name="a") private int farmId;
    @Column(name="b") private int tier;
    @Column(name="c") private int type;
    @Column(name="d") private boolean purchased;

    public FarmItem() {
    }

    public FarmItem(int farmId, int tier, int type, boolean purchased) {
        this.farmId = farmId;
        this.tier = tier;
        this.type = type;
        this.purchased = purchased;
    }

    public int getFarmId() {
        return farmId;
    }

    public void setFarmId(int farmId) {
        this.farmId = farmId;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }
}
