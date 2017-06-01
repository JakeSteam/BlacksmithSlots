package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class SlotChancesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_data_table);
        ((TextView)findViewById(R.id.activityTitle)).setText(R.string.reward_chances);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = (TableLayout)findViewById(R.id.dataTable);

        Intent intent = getIntent();
        Slot selectedSlot = Slot.get(intent.getIntExtra(Constants.INTENT_SLOT, 0));
        if (selectedSlot == null) { finish(); }

        TableRow header = (TableRow) inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);
        ((TextView) header.findViewById(R.id.dataName)).setText(R.string.bundle_item_select);
        ((TextView) header.findViewById(R.id.dataValue)).setText(R.string.bundle_item_chance);
        statTable.addView(header);

        List<ItemBundle> rewards = selectedSlot.getRewards(false);
        for (ItemBundle reward : rewards) {
            TableRow lockedMessage = (TableRow) inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);
            ((TextView) lockedMessage.findViewById(R.id.dataName)).setText(reward.toString(this));
            ((TextView) lockedMessage.findViewById(R.id.dataValue)).setText(Integer.toString(reward.getWeighting()));
            statTable.addView(lockedMessage);
        }
    }
}
