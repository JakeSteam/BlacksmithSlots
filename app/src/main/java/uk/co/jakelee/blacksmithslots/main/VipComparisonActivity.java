package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import uk.co.jakelee.blacksmithslots.MainActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.LevelHelper;

public class VipComparisonActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vip_comparison);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout comparisonTable = (TableLayout)findViewById(R.id.comparisonTable);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int vipLevel = LevelHelper.getVipLevel();
        for (int i = 0; i <= Constants.MAX_VIP_LEVEL; i++) {
            TableRow tableRow = (TableRow)inflater.inflate(R.layout.custom_vip_row, null).findViewById(R.id.dataRow);
            tableRow.setBackgroundColor(ContextCompat.getColor(this, vipLevel == i ? R.color.green : R.color.white));
            ((TextView)tableRow.findViewById(R.id.vipLevel)).setText(Integer.toString(i));
            ((TextView)tableRow.findViewById(R.id.timeBetweenBonuses)).setText("3 hours");
            ((TextView)tableRow.findViewById(R.id.advertBonus)).setText("Half hour");
            ((TextView)tableRow.findViewById(R.id.bonusModifier)).setText("+" + (i * Constants.VIP_LEVEL_MODIFIER) + "%");
            ((TextView)tableRow.findViewById(R.id.test)).setText("Test");
            comparisonTable.addView(tableRow, params);
        }
    }

    public void close(View v) {
        finish();
    }

    public void suppress(View v) {

    }
}
