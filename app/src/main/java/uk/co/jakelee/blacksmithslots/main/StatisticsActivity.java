package uk.co.jakelee.blacksmithslots.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class StatisticsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TableLayout statTable = (TableLayout)findViewById(R.id.statisticsTable);
        List<Statistic> statistics = Statistic.listAll(Statistic.class);
        for (Statistic statistic : statistics) {
            TableRow tableRow = new TableRow(this);
            TextView statName = new TextView(this);
            statName.setTextAppearance(this, R.style.SidebarText);
            statName.setText(Statistic.getName(this, statistic.getStatistic().value));

            TextView statValue = new TextView(this);
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

    @Override
    protected void onResume() {
        super.onResume();
        onWindowFocusChanged(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            findViewById(R.id.parent).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
