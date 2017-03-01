package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;

public class Inventory extends SugarRecord {
    private Enums.Tier tier;
    private Enums.Type type;
    private int quantity;

    public Inventory() {
    }

    public Inventory(Enums.Tier tier, Enums.Type type, int quantity) {
        this.tier = tier;
        this.type = type;
        this.quantity = quantity;
    }

    public Inventory(Enums.Tier tier, Enums.Type type) {
        this.tier = tier;
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static void addInventory(Enums.Tier tier, Enums.Type type, int quantity) {
        Inventory inventory = getInventory(tier, type);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.save();
    }

    public static Inventory getInventory(Enums.Tier tier, Enums.Type type) {
        List<Inventory> inventories = Select.from(Inventory.class).where(
                Condition.prop("type").eq(type),
                Condition.prop("tier").eq(tier)
        ).list();

        if (inventories.size() > 0) {
            return inventories.get(0);
        } else {
            return new Inventory(tier, type, 0);
        }
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText(DisplayHelper.getItemImageFile(tier, type));
    }

    public static String getName(Context context, Enums.Tier tier, Enums.Type type) {
        return TextHelper.getInstance(context).getText(DisplayHelper.getItemImageFile(tier, type));
    }

    public int getDrawableId(Context context) {
        return context.getResources().getIdentifier(DisplayHelper.getItemImageFile(tier, type), "drawable", context.getPackageName());
    }
}
