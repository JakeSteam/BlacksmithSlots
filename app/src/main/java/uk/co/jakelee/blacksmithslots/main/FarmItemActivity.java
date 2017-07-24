package uk.co.jakelee.blacksmithslots.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.model.Farm;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;

public class FarmItemActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_table);
        ((TextView)findViewById(R.id.activityTitle)).setText(R.string.select_item);
        ((TextView)findViewById(R.id.activitySubtitle)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.activitySubtitle)).setText(R.string.select_item_desc);

        Intent intent = getIntent();
        int farmId = intent.getIntExtra(Constants.INTENT_FARM, 0);
        if (farmId == 0) { finish(); }

        displayFarmItems(farmId);
    }

    public void displayFarmItems(final int farmId) {
        final FarmItemActivity activity = this;
        final Farm farm = Farm.get(farmId);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = findViewById(R.id.dataTable);
        statTable.removeAllViews();

        List<ItemBundle> farmItems =  Select.from(ItemBundle.class).where(
                Condition.prop("f").eq(Enums.ItemBundleType.FarmReward.value),
                Condition.prop("a").eq(farmId)).list();
        for (final ItemBundle farmItem : farmItems) {
            TableRow itemRow = inflater.inflate(R.layout.custom_farm_item_row, null).findViewById(R.id.itemRow);

            final boolean isSelected = farmItem.getTier().value == farm.getItemTier() && farmItem.getType().value == farm.getItemType();
            final boolean isUnlocked = farmItem.getQuantity() > 0;
            int itemImage = getResources().getIdentifier(DisplayHelper.getItemImageFile(farmItem.getTier().value, farmItem.getType().value), "drawable", getPackageName());
            ((ImageView)itemRow.findViewById(R.id.itemImage)).setImageResource(itemImage);

            ((TextView)itemRow.findViewById(R.id.itemName)).setText(Inventory.getName(this, farmItem.getTier(), farmItem.getType()));
            ((TextView)itemRow.findViewById(R.id.itemStatus)).setText(isSelected ? "Active" : isUnlocked ? "Select" : "Unlock");
            itemRow.findViewById(R.id.itemStatus).setBackgroundResource(isSelected ? R.drawable.box_green : isUnlocked ? R.drawable.box_yellow : R.drawable.box_orange);
            itemRow.findViewById(R.id.itemStatus).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isSelected && isUnlocked) {
                        farm.setItemTier(farmItem.getTier().value);
                        farm.setItemType(farmItem.getType().value);
                        farm.save();
                        displayFarmItems(farmId);
                    } else if (!isUnlocked) {
                        AlertDialogHelper.confirmFarmItemUnlock(activity, farm, farmItem);
                    }
                }
            });
            statTable.addView(itemRow);
        }
    }
}
