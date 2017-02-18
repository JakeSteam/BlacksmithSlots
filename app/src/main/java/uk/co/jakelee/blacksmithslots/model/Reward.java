package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;

public class Reward extends SugarRecord {
    private int slotId;
    private int resourceId;
    private int quantity;
    private int weighting;

    public Reward() {
    }

    public Reward(int slotId, int resourceId, int quantity, int weighting) {
        this.slotId = slotId;
        this.resourceId = resourceId;
        this.quantity = quantity;
        this.weighting = weighting;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
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
}
