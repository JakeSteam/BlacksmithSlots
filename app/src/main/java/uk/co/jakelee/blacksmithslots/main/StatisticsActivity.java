package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.DateHelper;
import uk.co.jakelee.blacksmithslots.helper.IncomeHelper;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class StatisticsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_statistics);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = (TableLayout)findViewById(R.id.statisticsTable);
        List<Statistic> statistics = Statistic.listAll(Statistic.class);
        for (Statistic statistic : statistics) {
            TableRow tableRow = (TableRow)inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);
            ((TextView)tableRow.findViewById(R.id.dataName)).setText(Statistic.getName(this, statistic.getStatistic().value));
            ((TextView)tableRow.findViewById(R.id.dataValue)).setText(statistic.getValue());
            statTable.addView(tableRow);
        }

        TableRow tableRow = (TableRow)inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);
        ((TextView)tableRow.findViewById(R.id.dataName)).setText("Next Bonus Claim");
        ((TextView)tableRow.findViewById(R.id.dataValue)).setText(DateHelper.timestampToString(IncomeHelper.getNextPeriodicClaimTime()));
        statTable.addView(tableRow);
    }
}
