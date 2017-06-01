package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;

@Table(name = "b")
public class Inventory extends SugarRecord {
    @Column(name = "a")
    private int tier;

    @Column(name = "b")
    private int type;

    @Column(name = "c")
    private int quantity;

    public Inventory() {
    }

    public Inventory(Enums.Tier tier, Enums.Type type, int quantity) {
        this.tier = tier.value;
        this.type = type.value;
        this.quantity = quantity;
    }

    public Inventory(int tier, int type, int quantity) {
        this.tier = tier;
        this.type = type;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static void addInventory(ItemBundle result) {
        addInventory(result.getTier(), result.getType(), result.getQuantity());
    }

    public static void addInventory(Enums.Tier tier, Enums.Type type, int quantity) {
        addInventory(tier.value, type.value, quantity);
    }

    public static void addInventory(int tier, int type, int quantity) {
        Inventory inventory = getInventory(tier, type);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.save();
    }

    public static Inventory get(ItemBundle item) {
        return getInventory(item.getTier().value, item.getType().value);
    }

    public static Inventory getInventory(int tier, int type) {
        List<Inventory> inventories = Select.from(Inventory.class).where(
                Condition.prop("a").eq(tier),
                Condition.prop("b").eq(type)
        ).list();

        if (inventories.size() > 0) {
            return inventories.get(0);
        } else {
            return new Inventory(tier, type, 0);
        }
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDrawableId(Context context) {
        return getDrawableId(context, 1);
    }

    public int getDrawableId(Context context, int quantity) {
        return context.getResources().getIdentifier(DisplayHelper.getItemImageFile(tier, type, quantity), "drawable", context.getPackageName());
    }

    public String getName(Context context) {
        return getName(context, tier, type);
    }

    public static String getName(Context context, Enums.Tier tier, Enums.Type type) {
        return getName(context, tier.value, type.value);
    }

    public static String getName(Context context, int tier, int type) {
        return TextHelper.getInstance(context).getText(DisplayHelper.getItemTierString(tier)) +
                " " +
                TextHelper.getInstance(context).getText(DisplayHelper.getItemTypeString(type));
    }

    public static int getUniqueItemCount() {
        return (int)Select.from(Inventory.class).count();
    }
}
