package uk.co.jakelee.blacksmithslots.helper;

import uk.co.jakelee.blacksmithslots.constructs.SlotResult;

public class DisplayHelper {
    public static String getItemNameLookupString(Enums.Tier tier, Enums.Type type) {
        return "item_" + tier + "_" + type;
    }

    public static String getItemImageFile(SlotResult result, boolean useDefault) {
        return getItemImageFile(result.getResourceTier(), result.getResourceType(), 1);
    }

    public static String getItemImageFile(SlotResult result) {
        return getItemImageFile(result.getResourceTier(), result.getResourceType(), result.getResourceMultiplier());
    }

    public static String getItemImageFile(Enums.Tier tier, Enums.Type type) {
        return getItemImageFile(tier, type, 1);
    }

    public static String getItemImageFile(Enums.Tier tier, Enums.Type type, int quantity) {
        return "item_" + tier + "_" + type + (quantity > 1 ? "_" + quantity : "");
    }
}
