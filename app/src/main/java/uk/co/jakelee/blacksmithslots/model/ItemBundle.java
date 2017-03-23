package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

import java.util.Locale;

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

    public ItemBundle() {
    }

    public ItemBundle (Enums.Tier tier, Enums.Type type, int quantity) {
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
    }

    public ItemBundle(int identifier, Enums.Tier tier, Enums.Type type, int quantity, int weighting) {
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
        this.identifier = identifier;
        this.weighting = weighting;
        this.bundleType = Enums.ItemBundleType.SlotReward.value;
    }

    public ItemBundle(int identifier, Enums.Tier tier, Enums.Type type, int quantity, boolean isIap) {
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
        this.identifier = identifier;
        this.bundleType = (isIap ? Enums.ItemBundleType.IapReward.value : Enums.ItemBundleType.SlotResource.value);
    }

    public ItemBundle(Enums.Iap iap, Enums.Tier tier, Enums.Type type, int quantity, boolean isIap) {
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
        this.identifier = iap.value;
        this.bundleType = (isIap ? Enums.ItemBundleType.IapReward.value : Enums.ItemBundleType.SlotResource.value);
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
        return String.format(Locale.ENGLISH, "%dx %s", quantity, Item.getName(context, tier, type));
    }
}
