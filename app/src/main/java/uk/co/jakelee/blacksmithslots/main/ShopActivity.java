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
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.IapHelper;
import uk.co.jakelee.blacksmithslots.model.Iap;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class ShopActivity extends BaseActivity {
    private boolean haveSetupTabs = false;
    private int selectedTabId = R.id.tab_vip;
    SharedPreferences prefs;

    @BindView(R.id.passImage) ImageView passImage;
    @BindView(R.id.passDaysLeft) TextView passDaysLeft;
    @BindView(R.id.passPurchase) TextView passPurchase;
    @BindView(R.id.passClaim) TextView passClaim;
    @BindView(R.id.passDescription) TextView passDescription;
    @BindView(R.id.passTab) RelativeLayout passTab;
    @BindView(R.id.vipTab) RelativeLayout vipTab;
    @BindView(R.id.bundleTab) RelativeLayout bundleTab;
    @BindView(R.id.passTabTab) RelativeLayout passTabTab;
    @BindView(R.id.vipTabTab) RelativeLayout vipTabTab;
    @BindView(R.id.bundleTabTab) RelativeLayout bundleTabTab;
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

        passImage.getDrawable().setColorFilter(daysLeft > 0 ? Color.TRANSPARENT : Color.BLACK, PorterDuff.Mode.MULTIPLY);
        passDaysLeft.setText(String.format(Locale.ENGLISH, getString(R.string.pass_days_left), daysLeft));
        passDaysLeft.setTextColor(daysLeft > 0 ? Color.GREEN : Color.RED);
        passPurchase.setTag(Enums.Iap.BlacksmithPass);
        passClaim.setVisibility(daysLeft > 0 ? View.VISIBLE : View.GONE);
        passDescription.setText(daysLeft > 0 ? R.string.pass_desc_owned : R.string.pass_desc_unowned);

        int dayOfPass = Constants.PASS_DAYS - daysLeft;
        int daysClaimed = Statistic.get(Enums.Statistic.CurrentPassClaimedDay).getIntValue();
        int highestDaysClaimed = Statistic.get(Enums.Statistic.HighestPassClaimedDay).getIntValue();

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
            } else if (day > dayOfPass && day <= highestDaysClaimed){
                // Future day, we've never seen it before!
                itemsString = getString(R.string.unknown);
            }

            TableRow tableRow = (TableRow)inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);
            ((TextView)tableRow.findViewById(R.id.dataName)).setText(String.format(Locale.ENGLISH, getString(R.string.pass_days), day));
            ((TextView)tableRow.findViewById(R.id.dataValue)).setText(itemsString);
            ((TextView)tableRow.findViewById(R.id.dataValue)).setTextColor(ContextCompat.getColor(this, colour));
            passRewardsContainer.addView(tableRow);
        }
    }

    private void createVipTab() {

    }

    private void createBundleTab() {

    }

    @OnClick(R.id.passPurchase)
    public void buyIap(View v) {
        if (v.getTag() != null) {
            Enums.Iap iapEnum = (Enums.Iap)v.getTag();
            // Fire purchase popup
            // purchase(iapEnum.name());

            // Temporary for testing
            Iap iap = Iap.get(iapEnum);
            iap.setLastPurchased(System.currentTimeMillis());
            iap.setTimesPurchased(iap.getTimesPurchased() + 1);

            Statistic statistic = Statistic.get(Enums.Statistic.HighestPassClaimedDay);
            statistic.setIntValue(0);
            statistic.save();

            createPassTab();
        }
    }

    @OnClick({ R.id.passTabTab, R.id.vipTabTab, R.id.bundleTabTab })
    public void changeTab(View v) {
        updateTabs(v.getId());
    }

    private void updateTabs(int id) {
        selectedTabId = id;
        passTab.setVisibility(id == R.id.tab_pass ? View.VISIBLE : View.GONE);
        vipTab.setVisibility(id == R.id.tab_vip ? View.VISIBLE : View.GONE);
        bundleTab.setVisibility(id == R.id.tab_bundle? View.VISIBLE : View.GONE);

        passTabTab.setAlpha(id == R.id.tab_pass ? 1f : 0.5f);
        vipTabTab.setAlpha(id == R.id.tab_vip ? 1f : 0.5f);
        bundleTabTab.setAlpha(id == R.id.tab_bundle ? 1f : 0.5f);
    }
}
