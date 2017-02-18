package uk.co.jakelee.blacksmithslots.constructs;

public class SlotResult {
    private int resourceId;
    private int resourceMultiplier;

    public SlotResult() {
    }

    public SlotResult(int resourceId, int resourceQuantity) {
        this.resourceId = resourceId;
        this.resourceMultiplier = resourceQuantity;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceMultiplier() {
        return resourceMultiplier;
    }

    public void setResourceMultiplier(int resourceMultiplier) {
        this.resourceMultiplier = resourceMultiplier;
    }
}
