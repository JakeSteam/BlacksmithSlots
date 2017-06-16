package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.LevelHelper;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class SlotChancesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_table);
        ((TextView)findViewById(R.id.activityTitle)).setText(R.string.reward_chances);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = findViewById(R.id.dataTable);

        Intent intent = getIntent();
        Slot selectedSlot = Slot.get(intent.getIntExtra(Constants.INTENT_SLOT, 0));
        if (selectedSlot == null) { finish(); }

        statTable.addView(DisplayHelper.getTableRow(inflater, R.string.bundle_item_select, R.string.bundle_item_chance));

        int vipLevel = LevelHelper.getVipLevel();
        List<ItemBundle> rewards = selectedSlot.getRewards(false, false);
        for (ItemBundle reward : rewards) {
            // If VIP and it's a wildcard, there'll be extra items
            if (vipLevel > 0 && reward.getType() == Enums.Type.Wildcard) {
                statTable.addView(DisplayHelper.getTableRow(inflater, reward.toString(this), Integer.toString(reward.getWeighting()) + " (+" + vipLevel + ")"));
            } else {
                statTable.addView(DisplayHelper.getTableRow(inflater, reward.toString(this), Integer.toString(reward.getWeighting())));
            }
        }
    }
}
