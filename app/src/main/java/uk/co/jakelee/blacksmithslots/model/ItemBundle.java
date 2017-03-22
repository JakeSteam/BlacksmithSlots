package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

import uk.co.jakelee.blacksmithslots.helper.Enums;

@Table(name = "d")
public class ItemBundle extends SugarRecord {
    @Column(name = "a")
    private int slotId;

    @Column(name = "b")
    private int tier;

    @Column(name = "c")
    private int type;

    @Column(name = "d")
    private int quantity;

    @Column(name = "e")
    private int weighting;

    @Column(name = "f")
    private boolean isReward;

    public ItemBundle() {
    }

    public ItemBundle(int slotId, Enums.Tier tier, Enums.Type type, int quantity, int weighting) {
        this.slotId = slotId;
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
        this.weighting = weighting;
        this.isReward = true;
    }

    public ItemBundle(int slotId, Enums.Tier tier, Enums.Type type, int quantity) {
        this.slotId = slotId;
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
        this.isReward = false;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public Enums.Tier getTier() {
        return Enums.Tier.get(tier);
    }

    public void setTier(Enums.Tier tier) {
        this.tier = tier.value;
    }

    public Enums.Type getType() {
        return Enums.Type.get(type);
    }

    public void setType(Enums.Type type) {
        this.type = type.value;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }

    public boolean isReward() {
        return isReward;
    }

    public void setReward(boolean reward) {
        isReward = reward;
    }
}
