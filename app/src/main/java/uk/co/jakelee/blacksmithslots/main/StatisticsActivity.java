package uk.co.jakelee.blacksmithslots.main;

import android.app.Activity;
import android.os.Bundle;
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

        TableLayout statTable = (TableLayout)findViewById(R.id.statisticsTable);
        List<Statistic> statistics = Statistic.listAll(Statistic.class);
        for (Statistic statistic : statistics) {
            TableRow tableRow = new TableRow(this);
            FontTextView statName = new FontTextView(this);
            statName.setTextAppearance(this, R.style.SidebarText);
            statName.setText(Statistic.getName(this, statistic.getStatistic().value));

            FontTextView statValue = new FontTextView(this);
            statValue.setTextAppearance(this, R.style.SidebarText);
            statValue.setText(statistic.getIntValue() + "");

            tableRow.addView(statName);
            tableRow.addView(statValue);
            statTable.addView(tableRow);
        }
    }

    public void close(View v) {
        finish();
    }

    public void suppress(View v) {

    }
}
