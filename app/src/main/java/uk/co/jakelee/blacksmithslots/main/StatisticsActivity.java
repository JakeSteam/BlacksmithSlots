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
import uk.co.jakelee.blacksmithslots.components.FontTextView;
import uk.co.jakelee.blacksmithslots.helper.DateHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.IncomeHelper;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class StatisticsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_data_table);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = (TableLayout)findViewById(R.id.dataTable);
        for (Enums.StatisticType statisticType : Enums.StatisticType.values()) {
            List<Statistic> statistics = Statistic.find(Statistic.class, "i = " + statisticType.value);
            FontTextView textView = new FontTextView(this);
            textView.setPadding(5, 20, 0, 0);
            textView.setText(Statistic.getTypeName(this, statisticType.value));
            textView.setTextSize(30);
            statTable.addView(textView);

            for (Statistic statistic : statistics) {
                TableRow tableRow = (TableRow) inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);
                ((TextView) tableRow.findViewById(R.id.dataName)).setText(Statistic.getName(this, statistic.getStatistic().value));
                ((TextView) tableRow.findViewById(R.id.dataValue)).setText(statistic.getValue());
                statTable.addView(tableRow);
            }

            if (statisticType == Enums.StatisticType.Bonuses) {
                TableRow tableRow = (TableRow)inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);
                ((TextView)tableRow.findViewById(R.id.dataName)).setText(R.string.statistic_next_claim_name);

                long nextClaim = IncomeHelper.getNextPeriodicClaimTime();
                ((TextView)tableRow.findViewById(R.id.dataValue)).setText(nextClaim > 0 && nextClaim > System.currentTimeMillis() ? DateHelper.timestampToDateTime(nextClaim) : "Never!");
                statTable.addView(tableRow);
            }
        }
    }
}
