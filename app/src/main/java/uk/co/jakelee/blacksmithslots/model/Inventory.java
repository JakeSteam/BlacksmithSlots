package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.TextHelper;

public class Inventory extends SugarRecord {
    private int itemId;
    private int quantity;

    public Inventory() {
    }

    public Inventory(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static void addInventory(int itemId, int quantity) {
        Inventory inventory = getInventory(itemId);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.save();
    }

    public static Inventory getInventory(int itemId) {
        List<Inventory> inventories = Select.from(Inventory.class).where(
                Condition.prop("item_id").eq(itemId)).list();

        if (inventories.size() > 0) {
            return inventories.get(0);
        } else {
            return new Inventory(itemId, 0);
        }
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("resource_" + itemId);
    }

    public static String getName(Context context, int resourceId) {
        return TextHelper.getInstance(context).getText("resource_" + resourceId);
    }

    public int getDrawableId(Context context) {
        return context.getResources().getIdentifier("item_" + itemId, "drawable", context.getPackageName());
    }
}
