package uk.co.jakelee.blacksmithslots.constructs;

public class SlotResult {
    private int resourceId;
    private int resourceQuantity;

    public SlotResult() {
    }

    public SlotResult(int resourceId, int resourceQuantity) {
        this.resourceId = resourceId;
        this.resourceQuantity = resourceQuantity;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getResourceQuantity() {
        return resourceQuantity;
    }

    public void setResourceQuantity(int resourceQuantity) {
        this.resourceQuantity = resourceQuantity;
    }
}
