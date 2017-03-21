package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Item;
import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class InventoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_inventory);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = (TableLayout)findViewById(R.id.inventoryTable);
        boolean onlyShowStockedItems = Setting.getBoolean(Enums.Setting.OnlyShowStocked);
        List<Inventory> inventories = Inventory.listAll(Inventory.class, "c DESC");
        for (Inventory inventory : inventories) {
            if (onlyShowStockedItems && inventory.getQuantity() <= 0) {
                continue;
            }
            TableRow tableRow = (TableRow)inflater.inflate(R.layout.custom_inventory_row, null).findViewById(R.id.inventoryRow);
            tableRow.setTag(R.id.item_tier, inventory.getTier());
            tableRow.setTag(R.id.item_type, inventory.getType());

            ((ImageView)tableRow.findViewById(R.id.itemImage)).setImageResource(
                    getResources().getIdentifier(DisplayHelper.getItemImageFile(
                            inventory.getTier(),
                            inventory.getType()), "drawable", getPackageName()));
            ((TextView)tableRow.findViewById(R.id.itemInfo)).setText(inventory.getQuantity() + "x " + inventory.getName(this));
            statTable.addView(tableRow);
        }
    }

    public void itemSources(View v) {
        TableRow parent = (TableRow)v.getParent();

        int tier = (int)parent.getTag(R.id.item_tier);
        int type = (int)parent.getTag(R.id.item_type);

        List<Reward> rewards = Select.from(Reward.class).where(
                Condition.prop("b").eq(tier),
                Condition.prop("c").eq(type)
        ).list();

        Set<String> slotNames = new HashSet<>();
        for (Reward reward : rewards) {
            slotNames.add(Slot.get(reward.getSlotId()).getName(this));
        }

        StringBuilder itemUseText = new StringBuilder();
        for (String name : slotNames) {
            itemUseText.append(name);
            itemUseText.append(", ");
        }

        String itemUseString = itemUseText.toString();
        AlertHelper.info(this, Item.getName(this, tier, type) + " is obtained from: " + (itemUseString.length() > 0 ? itemUseString.substring(0, itemUseString.length() - 2) : "Nowhere!?"), false);
    }

    public void itemUses(View v) {
        TableRow parent = (TableRow)v.getParent();

        int tier = (int)parent.getTag(R.id.item_tier);
        int type = (int)parent.getTag(R.id.item_type);

        List<Slot> slots = Select.from(Slot.class).where(
                Condition.prop("j").eq(tier),
                Condition.prop("k").eq(type)
        ).list();

        StringBuilder itemUseText = new StringBuilder();
        for (Slot slot : slots) {
            itemUseText.append(slot.getName(this));
            itemUseText.append(", ");
        }

        String itemUseString = itemUseText.toString();
        AlertHelper.info(this, Item.getName(this, tier, type) + " is used in: " + (itemUseString.length() > 0 ? itemUseString.substring(0, itemUseString.length() - 2) : "Nowhere!?"), false);
    }
}
