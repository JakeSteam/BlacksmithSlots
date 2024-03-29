package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Task;

public class SlotDialogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_table);
        ((TextView)findViewById(R.id.activityTitle)).setText(R.string.dialog_log);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = findViewById(R.id.dataTable);

        Intent intent = getIntent();
        Slot selectedSlot = Slot.get(intent.getIntExtra(Constants.INTENT_SLOT, 0));
        if (selectedSlot == null) { finish(); }

        statTable.addView(DisplayHelper.getTableRow(inflater, getString(R.string.locked), selectedSlot.getLockedText(this)));

        List<Task> tasks = selectedSlot.getTasks();
        for (Task task : tasks) {
            statTable.addView(DisplayHelper.getTableRow(inflater, String.format(Locale.ENGLISH, getString(R.string.task_description),
                    task.getPosition(),
                    task.toString(this)),
                    task.getText(this)));
        }

        TableRow tableRow = inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);
        ((TextView) tableRow.findViewById(R.id.dataName)).setText(R.string.unlocked);
        ((TextView) tableRow.findViewById(R.id.dataValue)).setText(selectedSlot.getUnlockedText(this));
        statTable.addView(tableRow);
    }
}
