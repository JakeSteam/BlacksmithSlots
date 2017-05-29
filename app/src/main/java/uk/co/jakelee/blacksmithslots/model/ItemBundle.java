package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

import java.util.Locale;

import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;

@Table(name = "d")
public class ItemBundle extends SugarRecord {
    @Column(name = "a")
    private int identifier;

    @Column(name = "b")
    private int tier;

    @Column(name = "c")
    private int type;

    @Column(name = "d")
    private int quantity;

    @Column(name = "e")
    private int weighting;

    @Column(name = "f")
    private int bundleType;

    // Used by Sugar
    public ItemBundle() {
    }

    // Used to move data around
    public ItemBundle (int tier, int type, int quantity) {
        this.tier = tier;
        this.type = type;
        this.quantity = quantity;
    }
    public ItemBundle (Enums.Tier tier, Enums.Type type, int quantity) {
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
    }

    // Used for slot rewards
    public ItemBundle(Enums.Slot slot, Enums.Tier tier, Enums.Type type, int quantity, int weighting) {
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
        this.identifier = slot.value;
        this.weighting = weighting;
        this.bundleType = Enums.ItemBundleType.SlotReward.value;
    }

    // Used by slot resources
    public ItemBundle(Enums.Slot slot, Enums.Tier tier, Enums.Type type, int quantity) {
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
        this.identifier = slot.value;
        this.bundleType = Enums.ItemBundleType.SlotResource.value;
    }


    // Used by pass reward resources
    public ItemBundle(int day, Enums.Tier tier, Enums.Type type, int quantity) {
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
        this.identifier = day;
        this.bundleType = Enums.ItemBundleType.PassReward.value;
    }

    // Used by VIP / BS pass IAPs
    public ItemBundle(Enums.Iap iap, Enums.Tier tier, Enums.Type type, int quantity) {
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
        this.identifier = iap.value;
        this.weighting = 0;
        this.bundleType = Enums.ItemBundleType.IapReward.value;
    }

    // Used by bundle IAPs
    public ItemBundle(Enums.Iap iap, Enums.Tier tier, Enums.Type type, int quantity, int price) {
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
        this.identifier = iap.value;
        this.weighting = price;
        this.bundleType = Enums.ItemBundleType.IapReward.value;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
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

    public String getPrice() {
        return DisplayHelper.centsToDollars(getWeighting());
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }

    public int getBundleType() {
        return bundleType;
    }

    public void setBundleType(int bundleType) {
        this.bundleType = bundleType;
    }

    public String toString(Context context) {
        return String.format(Locale.ENGLISH, "%dx %s", quantity, Inventory.getName(context, tier, type));
    }
}
