package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.BuildConfig;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.FontTextView;
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;
import uk.co.jakelee.blacksmithslots.helper.DateHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.IncomeHelper;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class StatisticsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_table);
        ((TextView)findViewById(R.id.activityTitle)).setText(R.string.statistics);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout statTable = findViewById(R.id.dataTable);
        for (Enums.StatisticType statisticType : Enums.StatisticType.values()) {
            List<Statistic> statistics = Statistic.find(Statistic.class, "i = " + statisticType.value);
            FontTextView textView = new FontTextView(this);
            textView.setPadding(5, 20, 0, 0);
            textView.setText(Statistic.getTypeName(this, statisticType.value));
            textView.setTextSize(30);
            statTable.addView(textView);

            for (Statistic statistic : statistics) {
                statTable.addView(DisplayHelper.getTableRow(inflater,
                        Statistic.getName(this, statistic.getStatistic().value),
                        statistic.getValue()));
            }

            if (statisticType == Enums.StatisticType.Bonuses) {
                long nextClaim = IncomeHelper.getNextPeriodicClaimTime();
                statTable.addView(DisplayHelper.getTableRow(inflater, getString(R.string.statistic_next_claim_name), (nextClaim > 0 && nextClaim > System.currentTimeMillis() ? DateHelper.timestampToDateTime(nextClaim) : getString(R.string.never))));
            }

            if (statisticType == Enums.StatisticType.Version) {
                statTable.addView(DisplayHelper.getTableRow(inflater, getString(R.string.statistic_version_name), BuildConfig.VERSION_NAME + " (" + BuildConfig.BUILD_TYPE + ")"));
                statTable.addView(DisplayHelper.getTableRow(inflater, getString(R.string.statistic_version_number), "" + BuildConfig.VERSION_CODE));
                statTable.addView(DisplayHelper.getTableRow(inflater, getString(R.string.statistic_version_database), "" + DatabaseHelper.LATEST_PATCH));
            }
        }
    }
}
