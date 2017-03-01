package uk.co.jakelee.blacksmithslots.constructs;

import uk.co.jakelee.blacksmithslots.helper.Enums;

public class SlotResult {
    private Enums.Tier resourceTier;
    private Enums.Type resourceType;
    private int resourceMultiplier;

    public SlotResult() {
    }

    public SlotResult(Enums.Tier resourceTier, Enums.Type resourceType, int resourceMultiplier) {
        this.resourceTier = resourceTier;
        this.resourceType = resourceType;
        this.resourceMultiplier = resourceMultiplier;
    }

    public Enums.Tier getResourceTier() {
        return resourceTier;
    }

    public void setResourceTier(Enums.Tier resourceTier) {
        this.resourceTier = resourceTier;
    }

    public Enums.Type getResourceType() {
        return resourceType;
    }

    public void setResourceType(Enums.Type resourceType) {
        this.resourceType = resourceType;
    }

    public int getResourceMultiplier() {
        return resourceMultiplier;
    }

    public void setResourceMultiplier(int resourceMultiplier) {
        this.resourceMultiplier = resourceMultiplier;
    }
}
