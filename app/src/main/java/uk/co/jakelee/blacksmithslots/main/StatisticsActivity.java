package uk.co.jakelee.blacksmithslots.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.FontTextView;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class StatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_statistics);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = (TableLayout)findViewById(R.id.statisticsTable);
        List<Statistic> statistics = Statistic.listAll(Statistic.class);
        for (Statistic statistic : statistics) {
            TableRow tableRow = (TableRow)inflater.inflate(R.layout.custom_statistic_row, null).findViewById(R.id.statisticRow);
            TextView statName = (TextView)tableRow.findViewById(R.id.statisticName);
            statName.setText(Statistic.getName(this, statistic.getStatistic().value));

            TextView statValue = (TextView)tableRow.findViewById(R.id.statisticValue);
            statValue.setText(statistic.getValue());
            statTable.addView(tableRow);
        }
    }

    public void close(View v) {
        finish();
    }

    public void suppress(View v) {

    }
}
