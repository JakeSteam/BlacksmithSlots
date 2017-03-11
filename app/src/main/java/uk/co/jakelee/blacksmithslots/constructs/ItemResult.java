package uk.co.jakelee.blacksmithslots.constructs;

import android.content.Context;

import java.util.Locale;

import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.model.Item;

public class ItemResult {
    private Enums.Tier resourceTier;
    private Enums.Type resourceType;
    private int resourceQuantity;

    public ItemResult() {
    }

    public ItemResult(Enums.Tier resourceTier, Enums.Type resourceType, int resourceMultiplier) {
        this.resourceTier = resourceTier;
        this.resourceType = resourceType;
        this.resourceQuantity = resourceMultiplier;
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

    public int getResourceQuantity() {
        return resourceQuantity;
    }

    public void setResourceQuantity(int resourceQuantity) {
        this.resourceQuantity = resourceQuantity;
    }

    public String toString(Context context) {
        return String.format(Locale.ENGLISH, "%dx %s", resourceQuantity, Item.getName(context, resourceTier, resourceType));
    }
}
