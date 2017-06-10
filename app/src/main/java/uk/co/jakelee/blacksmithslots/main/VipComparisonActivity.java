package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.IncomeHelper;
import uk.co.jakelee.blacksmithslots.helper.LevelHelper;
import uk.co.jakelee.blacksmithslots.helper.MusicHelper;

public class VipComparisonActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_comparison);
        ButterKnife.bind(this);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout comparisonTable = (TableLayout)findViewById(R.id.comparisonTable);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int vipLevel = LevelHelper.getVipLevel();
        for (int i = 0; i <= Constants.MAX_VIP_LEVEL; i++) {
            TableRow tableRow = (TableRow)inflater.inflate(R.layout.custom_vip_row, null).findViewById(R.id.dataRow);
            tableRow.setBackgroundColor(ContextCompat.getColor(this, vipLevel == i ? R.color.green : R.color.white));
            ((TextView)tableRow.findViewById(R.id.vipLevel)).setText(Integer.toString(i));
            ((TextView)tableRow.findViewById(R.id.timeBetweenBonuses)).setText(String.format(Locale.ENGLISH, getString(R.string.time_hours), IncomeHelper.getChestCooldownHours(i)));
            ((TextView)tableRow.findViewById(R.id.bonusModifier)).setText("+" + (i * Constants.VIP_LEVEL_MODIFIER) + "%");
            ((TextView)tableRow.findViewById(R.id.advertBonus)).setText(String.format(Locale.ENGLISH, getString(R.string.time_mins), IncomeHelper.getAdvertCooldownMins(i)));
            ((TextView)tableRow.findViewById(R.id.dailyBonus)).setText("+" + (i * Constants.VIP_DAILY_BONUS_MODIFIER) + "%");
            ((TextView)tableRow.findViewById(R.id.autospins)).setText("" + LevelHelper.getAutospinsByVip(i));
            ((TextView)tableRow.findViewById(R.id.bonusWildcards)).setText(Integer.toString(i));
            comparisonTable.addView(tableRow, params);
        }
    }

    @OnClick(R.id.upgradeButton)
    public void upgradeVip() {
        MusicHelper.getInstance(this).setMovingInApp(true);
        startActivity(new Intent(this, ShopActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                .putExtra("vip", true));

    }
}
