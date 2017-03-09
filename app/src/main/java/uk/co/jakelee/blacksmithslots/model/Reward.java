package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

import uk.co.jakelee.blacksmithslots.helper.Enums;

@Table(name = "c")
public class Reward extends SugarRecord {
    @Column(name = "a")
    private int slotId;

    @Column(name = "b")
    private int tier;

    @Column(name = "c")
    private int type;

    @Column(name = "d")
    private int quantityMultiplier;

    @Column(name = "e")
    private int weighting;

    public Reward() {
    }

    public Reward(int slotId, Enums.Tier tier, Enums.Type type, int quantityMultiplier, int weighting) {
        this.slotId = slotId;
        this.tier = tier.value;
        this.type = type.value;
        this.quantityMultiplier = quantityMultiplier;
        this.weighting = weighting;
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

    public int getQuantityMultiplier() {
        return quantityMultiplier;
    }

    public void setQuantityMultiplier(int quantityMultiplier) {
        this.quantityMultiplier = quantityMultiplier;
    }

    public int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }
}
