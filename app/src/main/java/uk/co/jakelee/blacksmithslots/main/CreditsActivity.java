package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;

public class CreditsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_table);
        ((TextView)findViewById(R.id.activityTitle)).setText(R.string.credits);

        List<Pair<Integer, Integer>> credits = getCredits();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = findViewById(R.id.dataTable);
        for (Pair<Integer, Integer> credit : credits) {
            TableRow tableRow = inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);

            ((TextView) tableRow.findViewById(R.id.dataName)).setMaxLines(1);
            ((TextView) tableRow.findViewById(R.id.dataName)).setTextSize(28);
            ((TextView) tableRow.findViewById(R.id.dataValue)).setLineSpacing(0, 1.5f);
            ((TextView) tableRow.findViewById(R.id.dataValue)).setMaxLines(99);
            statTable.addView(tableRow);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ((TextView) tableRow.findViewById(R.id.dataName)).setText(credit.first);
                ((TextView) tableRow.findViewById(R.id.dataValue)).setText(credit.second);
            } else {
                ((TextView) tableRow.findViewById(R.id.dataValue)).setText(getString(credit.first) + ":\n" + getString(credit.second));
            }
        }
    }

    private List<Pair<Integer, Integer>> getCredits() {
        List<Pair<Integer, Integer>> credits = new ArrayList<>();
        credits.add(new Pair<>(R.string.credits_library_title, R.string.credits_library));
        credits.add(new Pair<>(R.string.credits_graphics_title, R.string.credits_graphics));
        credits.add(new Pair<>(R.string.credits_audio_title, R.string.credits_audio));

        return credits;
    }
}
