package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class InventoryActivity extends BaseActivity {
    @BindView(R.id.dataTable) TableLayout inventoryTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_table);
        ButterKnife.bind(this);
        ((TextView)findViewById(R.id.activityTitle)).setText(R.string.inventory);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        boolean onlyShowStockedItems = Setting.getBoolean(Enums.Setting.OnlyShowStocked);
        boolean orderByTier = Setting.getBoolean(Enums.Setting.OrderByTier);
        boolean reverseOrder = Setting.getBoolean(Enums.Setting.OrderReversed);
        List<Inventory> inventories = Inventory.listAll(Inventory.class, (orderByTier ? "a " : "c ") + (reverseOrder ? "ASC" : "DESC"));
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
            inventoryTable.addView(tableRow);
        }
    }

    public void itemSources(View v) {
        TableRow parent = (TableRow)v.getParent();
        getItemInfo((int)parent.getTag(R.id.item_tier), (int)parent.getTag(R.id.item_type), true);
    }

    public void itemUses(View v) {
        TableRow parent = (TableRow)v.getParent();
        getItemInfo((int)parent.getTag(R.id.item_tier), (int)parent.getTag(R.id.item_type), false);
    }

    private void getItemInfo(int tier, int type, boolean gettingSources) {
        List<ItemBundle> itemBundles = Select.from(ItemBundle.class).where(
                Condition.prop("b").eq(tier),
                Condition.prop("c").eq(type),
                Condition.prop("f").eq(gettingSources ? Enums.ItemBundleType.SlotReward.value : Enums.ItemBundleType.SlotResource.value)
        ).list();

        Set<String> slotNames = new HashSet<>();
        for (ItemBundle itemBundle : itemBundles) {
            slotNames.add(Slot.get(itemBundle.getIdentifier()).getName(this));
        }

        StringBuilder itemUseText = new StringBuilder();
        for (String name : slotNames) {
            itemUseText.append("\"");
            itemUseText.append(name);
            itemUseText.append("\"");
            itemUseText.append(", ");
        }

        String itemString = itemUseText.toString();
        String itemFinalString = (itemString.length() > 0 ? itemString.substring(0, itemString.length() - 2) : getString(R.string.alert_item_unknown));

        AlertHelper.info(this, String.format(Locale.ENGLISH,
                getString(gettingSources ? R.string.alert_item_source : R.string.alert_item_use),
                Inventory.getName(this, tier, type),
                itemFinalString),
            false);
    }
}
