package uk.co.jakelee.blacksmithslots.constructs;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.Enums;

public class TierRange {
    private Enums.Tier tier;
    private int min;
    private int max;
    private int itemPerLevel;

    public TierRange(Enums.Tier tier, int min, int max, int itemPerLevel) {
        this.tier = tier;
        this.min = min;
        this.max = max;
        this.itemPerLevel = itemPerLevel;
    }

    public Enums.Tier getTier() {
        return tier;
    }

    public void setTier(Enums.Tier tier) {
        this.tier = tier;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getItemPerLevel() {
        return itemPerLevel;
    }

    public void setItemPerLevel(int itemPerLevel) {
        this.itemPerLevel = itemPerLevel;
    }

    public static List<TierRange> getAllRanges() {
        List<TierRange> ranges = new ArrayList<>();
        ranges.add(new TierRange(Enums.Tier.Bronze, Constants.BRONZE_MIN_LEVEL, Constants.BRONZE_MAX_LEVEL, Constants.BRONZE_PER_LEVEL));
        ranges.add(new TierRange(Enums.Tier.Iron, Constants.IRON_MIN_LEVEL, Constants.IRON_MAX_LEVEL, Constants.IRON_PER_LEVEL));
        ranges.add(new TierRange(Enums.Tier.Steel, Constants.STEEL_MIN_LEVEL, Constants.STEEL_MAX_LEVEL, Constants.STEEL_PER_LEVEL));
        ranges.add(new TierRange(Enums.Tier.Mithril, Constants.MITHRIL_MIN_LEVEL, Constants.MITHRIL_MAX_LEVEL, Constants.MITHRIL_PER_LEVEL));
        ranges.add(new TierRange(Enums.Tier.Silver, Constants.SILVER_MIN_LEVEL, Constants.SILVER_MAX_LEVEL, Constants.SILVER_PER_LEVEL));
        ranges.add(new TierRange(Enums.Tier.Gold, Constants.GOLD_MIN_LEVEL, Constants.GOLD_MAX_LEVEL, Constants.GOLD_PER_LEVEL));
        return ranges;
    }
}
