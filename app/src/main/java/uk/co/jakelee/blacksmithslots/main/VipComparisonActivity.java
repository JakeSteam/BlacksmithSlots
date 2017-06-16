package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
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
        TableLayout comparisonTable = findViewById(R.id.comparisonTable);
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int vipLevel = LevelHelper.getVipLevel();
        for (int i = 0; i <= Constants.MAX_VIP_LEVEL; i++) {
            TableRow tableRow = inflater.inflate(R.layout.custom_vip_row, null).findViewById(R.id.dataRow);
            tableRow.setBackgroundColor(ContextCompat.getColor(this, vipLevel == i ? R.color.green : R.color.white));
            ((TextView)tableRow.findViewById(R.id.vipLevel)).setText(Integer.toString(i));
            ((TextView)tableRow.findViewById(R.id.timeBetweenBonuses)).setText(String.format(Locale.ENGLISH, getString(R.string.time_hours), IncomeHelper.getChestCooldownHours(i)));
            ((TextView)tableRow.findViewById(R.id.bonusModifier)).setText("+" + (i * Constants.VIP_LEVEL_MODIFIER) + "%");
            ((TextView)tableRow.findViewById(R.id.advertBonus)).setText(String.format(Locale.ENGLISH, getString(R.string.time_mins), IncomeHelper.getAdvertCooldownMins(i)));
            ((TextView)tableRow.findViewById(R.id.dailyBonus)).setText("+" + (i * Constants.VIP_DAILY_BONUS_MODIFIER) + "%");
            ((TextView)tableRow.findViewById(R.id.autospins)).setText("" + LevelHelper.getAutospinsByVip(i));
            ((TextView)tableRow.findViewById(R.id.bonusWildcards)).setText(Integer.toString(i));
            ((ImageView)tableRow.findViewById(R.id.subredditFlair)).setImageResource(getVipResource(i));
            comparisonTable.addView(tableRow, params);
        }
    }

    private int getVipResource(int vipLevel) {
        return getResources().getIdentifier("vip_" + vipLevel, "drawable", getPackageName());
    }

    @OnClick(R.id.upgradeButton)
    public void upgradeVip() {
        MusicHelper.getInstance(this).setMovingInApp(true);
        startActivity(new Intent(this, ShopActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                .putExtra("vip", true));
    }

    @OnClick({R.id.vipLevel, R.id.chestRestock, R.id.chestBoost, R.id.advertRestock, R.id.dailyBonus, R.id.maxAutospins, R.id.extraWildcards, R.id.subredditFlair})
    public void displayColumnInfo(View v) {
        AlertHelper.info(this, getString(getColumnInfo(v.getId())), false);
    }

    private int getColumnInfo(int viewId) {
        switch (viewId) {
            case R.id.vipLevel: return R.string.empty;
            case R.id.chestRestock: return R.string.chest_restock_desc;
            case R.id.chestBoost: return R.string.chest_boost_desc;
            case R.id.advertRestock: return R.string.advert_restock_desc;
            case R.id.dailyBonus: return R.string.daily_bonus_desc;
            case R.id.maxAutospins: return R.string.max_autospins_desc;
            case R.id.extraWildcards: return R.string.extra_wildcards_desc;
            case R.id.subredditFlair: return R.string.subreddit_flair_desc;
            default: return R.string.empty;
        }
    }
}
