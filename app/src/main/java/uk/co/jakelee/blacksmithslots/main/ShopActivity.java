package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DateHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.IapHelper;
import uk.co.jakelee.blacksmithslots.model.Iap;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class ShopActivity extends BaseActivity {
    private boolean haveSetupTabs = false;
    private int selectedTabId = R.id.vipTabTab;
    SharedPreferences prefs;

    @BindView(R.id.passImage) ImageView passImage;
    @BindView(R.id.passDaysLeft) TextView passDaysLeft;
    @BindView(R.id.passPurchase) TextView passPurchase;
    @BindView(R.id.passClaim) TextView passClaim;
    @BindView(R.id.passDescription) TextView passDescription;
    @BindView(R.id.passTab) RelativeLayout passTab;
    @BindView(R.id.vipTab) RelativeLayout vipTab;
    @BindView(R.id.bundleTab) RelativeLayout bundleTab;
    @BindView(R.id.passTabTab) TextView passTabTab;
    @BindView(R.id.vipTabTab) TextView vipTabTab;
    @BindView(R.id.bundleTabTab) TextView bundleTabTab;
    @BindView(R.id.passRewardsContainer) TableLayout passRewardsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_shop);
        prefs = getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!haveSetupTabs) {
            createPassTab();
            createVipTab();
            createBundleTab();
            updateTabs(prefs.getInt("selectedShopTabId", R.id.passTabTab));
            haveSetupTabs = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        prefs.edit().putInt("selectedShopTabId", selectedTabId).apply();
    }

    private void createPassTab() {
        int daysLeft = IapHelper.getPassDaysLeft();
        int dayOfPass = daysLeft > 0 ? (Constants.PASS_DAYS - daysLeft) : daysLeft;
        boolean canClaimPass = Statistic.get(Enums.Statistic.CurrentPassClaimedDay).getIntValue() < dayOfPass;
        int daysClaimed = Statistic.get(Enums.Statistic.CurrentPassClaimedDay).getIntValue();
        int highestDaysClaimed = Statistic.get(Enums.Statistic.HighestPassClaimedDay).getIntValue();
        int monthsLeft = Statistic.get(Enums.Statistic.ExtraPassMonths).getIntValue();

        if (daysLeft <= 0) {
            passImage.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        } else {
            passImage.getDrawable().clearColorFilter();
        }
        passDaysLeft.setText(String.format(Locale.ENGLISH, getString(R.string.pass_days_left), daysLeft + (Constants.PASS_DAYS * monthsLeft)));
        passDaysLeft.setTextColor(ContextCompat.getColor(this, daysLeft > 0 ? R.color.greenText : R.color.redText));
        passPurchase.setTag(Enums.Iap.BlacksmithPass);
        passClaim.setVisibility(canClaimPass ? View.VISIBLE : View.GONE);
        passDescription.setText(daysLeft > 0 ?
                getString(R.string.pass_desc_owned) :
                String.format(Locale.ENGLISH, getString(R.string.pass_desc_unowned), Constants.PASS_DAYS));
        passRewardsContainer.removeAllViews();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        List<List<ItemBundle>> dailyRewards = IapHelper.getPassRewards();
        for (int day = 1; day <= dailyRewards.size(); day++) {
            List<ItemBundle> dailyReward = dailyRewards.get(day - 1);

            String itemsString = DisplayHelper.bundlesToString(this, dailyReward);
            int colour = R.color.greyText;
            if (day == dayOfPass && day > daysClaimed) {
                // Ready to claim today's reward!
                colour = R.color.orangeText;
            } else if ((day == dayOfPass && day == daysClaimed) || day < dayOfPass) {
                // Already claimed today, or previously claimed day
                colour = R.color.greenText;
            } else if (day > dayOfPass && day > highestDaysClaimed){
                // Future day, we've never seen it before!
                itemsString = getString(R.string.unknown);
            }

            TableRow tableRow = (TableRow)inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);
            ((TextView)tableRow.findViewById(R.id.dataName)).setText(String.format(Locale.ENGLISH, getString(R.string.pass_days), day));
            ((TextView)tableRow.findViewById(R.id.dataValue)).setText(itemsString);
            ((TextView)tableRow.findViewById(R.id.dataValue)).setTextColor(ContextCompat.getColor(this, colour));
            passRewardsContainer.addView(tableRow);
        }
        passRewardsContainer.scrollTo(0,0);
    }

    private void createVipTab() {

    }

    private void createBundleTab() {

    }

    @OnClick(R.id.passClaim)
    public void claimPassReward() {
        int daysLeft = IapHelper.getPassDaysLeft() % Constants.PASS_DAYS;
        int dayOfPass = Constants.PASS_DAYS - daysLeft;

        if (daysLeft <= 0) {
            AlertHelper.error(this, R.string.error_claimed_without_pass, false);
        } else if (Statistic.get(Enums.Statistic.CurrentPassClaimedDay).getIntValue() >= dayOfPass) {
            AlertHelper.error(this, R.string.error_claimed_pass_already, false);
        } else {
            Statistic.add(Enums.Statistic.TotalPassDaysClaimed);
            Statistic.set(Enums.Statistic.CurrentPassClaimedDay, dayOfPass);

            Statistic highestDaysClaimed = Statistic.get(Enums.Statistic.HighestPassClaimedDay);
            if (highestDaysClaimed.getIntValue() < dayOfPass) {
                highestDaysClaimed.setIntValue(dayOfPass);
                highestDaysClaimed.save();
            }

            AlertHelper.success(this, String.format(Locale.ENGLISH, getString(R.string.alert_claimed_pass_day),
                    dayOfPass,
                    DisplayHelper.bundlesToString(this, IapHelper.getPassRewardsForDay(dayOfPass))
                    ), true);

            // If it's the last day and there's months left, use them up
            if (daysLeft == 1) {
                Statistic monthsLeft = Statistic.get(Enums.Statistic.ExtraPassMonths);
                if (monthsLeft.getIntValue() > 0) {
                    monthsLeft.setIntValue(monthsLeft.getIntValue() - 1);
                    monthsLeft.save();

                    Iap iap = Iap.get(Enums.Iap.BlacksmithPass);
                    iap.setLastPurchased(DateHelper.getYesterdayMidnight().getTimeInMillis());
                    iap.save();
                }
            }

            createPassTab();
        }
    }

    @OnClick(R.id.passPurchase)
    public void buyPass(View v) {
        Enums.Iap iapEnum = (Enums.Iap)v.getTag();
        Iap iap = Iap.get(iapEnum);
        int daysLeft = IapHelper.getPassDaysLeft();

        iap.setTimesPurchased(iap.getTimesPurchased() + 1);

        if (daysLeft == 0) {
            iap.setLastPurchased(DateHelper.getYesterdayMidnight().getTimeInMillis());
            Statistic.set(Enums.Statistic.CurrentPassClaimedDay, 0);
        } else {
            Statistic.add(Enums.Statistic.ExtraPassMonths);
        }
        iap.save();

        AlertHelper.success(this, R.string.alert_pass_purchased, true);
        createPassTab();
    }

    @OnClick({ R.id.passTabTab, R.id.vipTabTab, R.id.bundleTabTab })
    public void changeTab(View v) {
        updateTabs(v.getId());
    }

    private void updateTabs(int id) {
        selectedTabId = id;
        passTab.setVisibility(id == R.id.passTabTab ? View.VISIBLE : View.GONE);
        vipTab.setVisibility(id == R.id.vipTabTab ? View.VISIBLE : View.GONE);
        bundleTab.setVisibility(id == R.id.bundleTabTab ? View.VISIBLE : View.GONE);

        passTabTab.setAlpha(id == R.id.passTabTab ? 1f : 0.5f);
        vipTabTab.setAlpha(id == R.id.vipTabTab ? 1f : 0.5f);
        bundleTabTab.setAlpha(id == R.id.bundleTabTab ? 1f : 0.5f);
    }
}
