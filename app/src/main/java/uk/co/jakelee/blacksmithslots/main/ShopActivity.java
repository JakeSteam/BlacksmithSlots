package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.CalculationHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DateHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.IapHelper;
import uk.co.jakelee.blacksmithslots.helper.LevelHelper;
import uk.co.jakelee.blacksmithslots.model.Iap;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class ShopActivity extends BaseActivity implements BillingProcessor.IBillingHandler {
    private boolean haveSetupTabs = false;
    private int selectedTabId = R.id.vipTabTab;
    SharedPreferences prefs;
    private List<Pair<Integer, Integer>> dropdownItems = new ArrayList<>();
    int preloadingTier = 0;
    int preloadingType = 0;
    boolean preloadingVip = false;

    boolean canBuyIAPs = false;
    private BillingProcessor bp;

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

    @BindView(R.id.vipIntro) TextView vipIntro;
    @BindView(R.id.vipBonuses) TextView vipBonuses;
    @BindView(R.id.vipUpgrade) TextView vipUpgrade;

    @BindView(R.id.bundleItemDropdown) Spinner bundleItemDropdown;
    @BindView(R.id.bundleItemContainer) LinearLayout bundleItemContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        prefs = getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        preloadingTier = intent.getIntExtra("tier", 0);
        preloadingType = intent.getIntExtra("type", 0);
        preloadingVip = intent.getBooleanExtra("vip", false);
        if (preloadingTier > 0 && preloadingType > 0) {
            prefs.edit().putInt("selectedShopTabId", R.id.bundleTabTab).apply();
        } else if (preloadingVip) {
            prefs.edit().putInt("selectedShopTabId", R.id.vipTabTab).apply();
        }

        canBuyIAPs = BillingProcessor.isIabServiceAvailable(this);
        if (canBuyIAPs) {
            bp = new BillingProcessor(this, getPublicKey(), this);
        }

        SkuDetails skuDetails = bp.getPurchaseListingDetails("ironore10000");
        if (skuDetails != null) {
            Log.d("IAP Debug1", "Currency: " + skuDetails.currency + " priceValue: " + (skuDetails.priceValue) + " priceText: " + skuDetails.priceText);
        } else {
            Log.d("IAP Debug1", "It didn't load...");
        }
    }

    private String getPublicKey() {
        String[] keyArray = new String[]{
                "MIIBIjANBgkqhk", "iG9w0BAQEFAAOC", "AQ8AMIIBCgKCAQ", "EAhz8/eEVdISSu", "M2ChjVVNIpn7Hz", "5SlaR72FHoP7Mv", "W4KjnQNQbA6VrN", "vn84HTQQ2l4izu", "rWU6+7g82ZQh3E", "jEBwfOdJdFdydN", "ZdX/YSnprmRsnT", "ucFiU6jCWj5yZ9", "AQnLj71woL1cVH", "D8Ht5XUuYk7YRo", "CacxFHYMlxUfS3", "0r7tvztYHZFIGq", "pJjIkfXoxlVoUg", "SJ+WFTchpBGtT9", "daQmheU7ZxkwbB", "yNoFGQKJoMAdDj", "H6mXBUwMrXzfmq", "jXRjMCR+Rdz0zS", "ORoj+Ps4ZSTr3v", "NhEjoH4ElOlOoO", "MTHyNjiTlY2IIC", "XCuU1xHgfJ0hrd", "t6BW3qlMamsxkZ", "WTLSlTfwIDAQAB"
        };

        StringBuilder builder = new StringBuilder();
        for (String keyPart : keyArray) {
            builder.append(keyPart);
        }

        return builder.toString();
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

        int vipLevel = LevelHelper.getVipLevel();
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        List<List<ItemBundle>> dailyRewards = IapHelper.getPassRewards();
        for (int day = 1; day <= dailyRewards.size(); day++) {
            displayDayRow(dayOfPass, daysClaimed, highestDaysClaimed, inflater, dailyRewards, day, vipLevel);
        }
        passRewardsContainer.scrollTo(0,0);
    }


    private void displayDayRow(int dayOfPass, int daysClaimed, int highestDaysClaimed, LayoutInflater inflater, List<List<ItemBundle>> dailyRewards, int day, int vipLevel) {
        List<ItemBundle> dailyReward = adjustDayRewardForVip(dailyRewards.get(day - 1), vipLevel);

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

        passRewardsContainer.addView(DisplayHelper.getTableRow(inflater,
                String.format(Locale.ENGLISH, getString(R.string.pass_days), day),
                itemsString,
                ContextCompat.getColor(this, colour)));
    }

    private List<ItemBundle> adjustDayRewardForVip(List<ItemBundle> dailyReward, int vipLevel) {
        if (vipLevel > 0) {
            for (ItemBundle reward : dailyReward) {
                reward.setQuantity(CalculationHelper.increaseByPercentage(reward.getQuantity(), Constants.VIP_DAILY_BONUS_MODIFIER * vipLevel));
            }
        }
        return dailyReward;
    }

    private void createVipTab() {
        int vipLevel = LevelHelper.getVipLevel();
        if (vipLevel >= Constants.MAX_VIP_LEVEL) {
            vipIntro.setText(R.string.vip_intro_max);
            vipBonuses.setText("");
            vipUpgrade.setVisibility(View.GONE);
        } else {
            vipIntro.setText(String.format(Locale.ENGLISH, getString(R.string.vip_intro), vipLevel + 1));
            vipUpgrade.setText(String.format(Locale.ENGLISH, getString(R.string.vip_upgrade_price), DisplayHelper.centsToDollars(IapHelper.getVipPrice(vipLevel + 1))));

            String vipBonusesText = getVipBonusesText(vipLevel);

            vipBonuses.setText(vipBonusesText);
        }
    }

    @NonNull
    private String getVipBonusesText(int vipLevel) {
        String vipBonusesText = DisplayHelper.bundlesToString(this, IapHelper.getVipRewardsForLevel(vipLevel + 1), 1, true, "- ", "\n");
        vipBonusesText += "- " + getString(R.string.chest_cooldown) + " -" + String.format(Locale.ENGLISH, getString(R.string.time_hours), Constants.CHEST_COOLDOWN_VIP_REDUCTION);
        vipBonusesText += "\n- " + getString(R.string.reward_boost) + " +" + Constants.VIP_LEVEL_MODIFIER + "%";
        vipBonusesText += "\n- " + getString(R.string.advert_cooldown) + " -" + String.format(Locale.ENGLISH, getString(R.string.time_mins), (int)(60 * Constants.ADVERT_COOLDOWN_VIP_REDUCTION));
        vipBonusesText += "\n- " + getString(R.string.daily_bonus) + " +" + Constants.VIP_DAILY_BONUS_MODIFIER + "%";
        vipBonusesText += "\n- " + getString(R.string.autospins) + " +" + Constants.AUTOSPIN_INCREASE;
        vipBonusesText += "\n- " + (vipLevel + 1) + " " + getString(R.string.extra_wildcards);
        vipBonusesText += "\n- " + "Exclusive /r/BlacksmithSlots flair";
        return vipBonusesText;
    }

    private void createBundleTab() {
        ArrayAdapter<String> envAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_display);
        envAdapter.setDropDownViewResource(R.layout.custom_spinner_item);

        dropdownItems.clear();
        List<ItemBundle> items = IapHelper.getUniqueBundleItems();
        int preloadedItemPosition = 0;
        for (int i = 0; i < items.size(); i++) {
            ItemBundle item = items.get(i);
            if (preloadingTier > 0 && preloadingType > 0 && item.getTier().value == preloadingTier && item.getType().value == preloadingType) {
                preloadedItemPosition = i;
            }
            if (item.getTier() == Enums.Tier.None) {
                envAdapter.add(getString(R.string.gems));
            } else {
                envAdapter.add(Inventory.getName(this, item.getTier(), item.getType()));
            }
            dropdownItems.add(new Pair<>(item.getTier().value, item.getType().value));
        }

        bundleItemDropdown.setAdapter(envAdapter);
        bundleItemDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    populateItems(dropdownItems.get(position));
            }
            @Override public void onNothingSelected(AdapterView<?> parentView) {}
        });

        bundleItemDropdown.setSelection(preloadedItemPosition);
    }

    private void populateItems(Pair<Integer, Integer> item) {
        bundleItemContainer.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        List<ItemBundle> iaps = IapHelper.getBundlesForItem(item.first, item.second);

        for (ItemBundle iap : iaps) {
            displayIap(params, inflater, iap);
        }
    }

    private void displayIap(LinearLayout.LayoutParams params, LayoutInflater inflater, ItemBundle iap) {
        LinearLayout itemTile = (LinearLayout) inflater.inflate(R.layout.custom_iap_tile, null).findViewById(R.id.iapTile);
        int imageResource = getResources().getIdentifier(DisplayHelper.getItemImageFile(iap), "drawable", getPackageName());

        ((ImageView)itemTile.findViewById(R.id.itemImage)).setImageResource(imageResource);
        ((TextView)itemTile.findViewById(R.id.itemPrice)).setText(iap.getPrice());
        final Iap iapItem = Iap.get(iap.getIdentifier());
        if (bp != null) {
            SkuDetails iapInfo = bp.getPurchaseListingDetails(iapItem.getIapName());
            if (iapInfo != null) {
                ((TextView) itemTile.findViewById(R.id.itemPrice)).setText(iapInfo.toString());
            }
        }
        itemTile.setOnClickListener(getItemOnClick(iapItem));
        bundleItemContainer.addView(itemTile, params);
    }

    @NonNull
    private View.OnClickListener getItemOnClick(final Iap iapItem) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyIAP(iapItem.getIapName().toLowerCase());
            }
        };
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
            claimTodaysPassReward(daysLeft, dayOfPass);
        }
    }

    private void claimTodaysPassReward(int daysLeft, int dayOfPass) {
        Statistic.add(Enums.Statistic.TotalPassDaysClaimed);
        Statistic.set(Enums.Statistic.CurrentPassClaimedDay, dayOfPass);

        Statistic highestDaysClaimed = Statistic.get(Enums.Statistic.HighestPassClaimedDay);
        if (highestDaysClaimed.getIntValue() < dayOfPass) {
            highestDaysClaimed.setIntValue(dayOfPass);
            highestDaysClaimed.save();
        }

        List<ItemBundle> dailyReward = adjustDayRewardForVip(IapHelper.getPassRewardsForDay(dayOfPass), LevelHelper.getVipLevel());
        Inventory.addInventory(dailyReward);
        AlertHelper.success(this, String.format(Locale.ENGLISH, getString(R.string.alert_claimed_pass_day),
                dayOfPass,
                DisplayHelper.bundlesToString(this, dailyReward)
                ), true);

        // If it's the last day and there's months left, use them up
        if (daysLeft == 1) {
            useUpMonth();
        }

        createPassTab();
    }

    private void useUpMonth() {
        Statistic monthsLeft = Statistic.get(Enums.Statistic.ExtraPassMonths);
        if (monthsLeft.getIntValue() > 0) {
            monthsLeft.setIntValue(monthsLeft.getIntValue() - 1);
            monthsLeft.save();

            Iap iap = Iap.get(Enums.Iap.BlacksmithPass);
            iap.setLastPurchased(DateHelper.getYesterdayMidnight().getTimeInMillis());
            iap.save();
        }
    }

    @OnClick(R.id.vipCompare)
    public void compareVip() {
        startActivity(new Intent(this, VipComparisonActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));

        SkuDetails skuDetails = bp.getPurchaseListingDetails("bronzeore10000");
        if (skuDetails != null) {
            Log.d("IAP Debug2", "Currency: " + skuDetails.currency + " priceValue: " + (skuDetails.priceValue) + " priceText: " + skuDetails.priceText);
        } else {
            Log.d("IAP Debug2", "It didn't load...");
        }
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

    @Override
    public void onBillingInitialized() {
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        bp.consumePurchase(productId);

        Iap iap = Iap.get(productId);
        iap.setTimesPurchased(iap.getTimesPurchased() + 1);
        iap.save();

        List<ItemBundle> items = new ArrayList<>();
        if (iap.isVipPurchase()) {
            items = onVipPurchased();
        } else if (iap.getIapId() == Enums.Iap.BlacksmithPass.value) {
            onPassPurchased(iap);
        } else {
            items = onBundlePurchased(iap);
        }

        for (ItemBundle item : items) {
            Inventory.addInventory(item);
        }
    }

    private List<ItemBundle> onBundlePurchased(Iap iap) {
        List<ItemBundle> items;
        items = IapHelper.getBundleRewards(iap.getIapId());
        Statistic.add(Enums.Statistic.PacksPurchased);
        AlertHelper.success(this, String.format(Locale.ENGLISH, getString(R.string.iap_bundle_purchased), DisplayHelper.bundlesToString(this, items)), true);
        return items;
    }

    private void onPassPurchased(Iap iap) {
        int daysLeft = IapHelper.getPassDaysLeft();

        if (daysLeft == 0) {
            iap.setLastPurchased(DateHelper.getYesterdayMidnight().getTimeInMillis());
            Statistic.set(Enums.Statistic.CurrentPassClaimedDay, 0);
        } else {
            Statistic.add(Enums.Statistic.ExtraPassMonths);
        }
        iap.setTimesPurchased(iap.getTimesPurchased()+1);
        iap.save();

        Statistic vipLevel = Statistic.get(Enums.Statistic.VipLevel);
        if (vipLevel.getIntValue() == 0) {
            vipLevel.setIntValue(1);
            vipLevel.save();
            AlertHelper.success(this, R.string.alert_pass_purchased_bonus_vip, true);
        } else {
            AlertHelper.success(this, R.string.alert_pass_purchased, true);
        }

        createPassTab();
    }

    private List<ItemBundle> onVipPurchased() {
        List<ItemBundle> items;Statistic vipLevel = Statistic.get(Enums.Statistic.VipLevel);
        if (vipLevel.getIntValue() < Constants.MAX_VIP_LEVEL) {
            vipLevel.setIntValue(vipLevel.getIntValue() + 1);
            vipLevel.save();
            createVipTab();
        }

        items = IapHelper.getVipRewardsForLevel(vipLevel.getIntValue());
        AlertHelper.success(this, String.format(Locale.ENGLISH, getString(R.string.vip_level_increased),
                vipLevel.getIntValue(), DisplayHelper.
                        bundlesToString(this, items)), true);
        return items;
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        if (errorCode != com.anjlab.android.iab.v3.Constants.BILLING_RESPONSE_RESULT_USER_CANCELED) {
            AlertHelper.error(this, getString(R.string.error_iap_failed), false);
        }
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void buyIAP(String iapId) {
        if (canBuyIAPs) {
            bp.purchase(this, iapId);
        } else {
            AlertHelper.error(this, getString(R.string.error_iap_failed), false);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) { bp.release(); }
        super.onDestroy();
    }

    @OnClick(R.id.vipUpgrade)
    public void upgradeVip() {
        int vipLevel = LevelHelper.getVipLevel();
        if (vipLevel < Constants.MAX_VIP_LEVEL) {
            buyIAP(("VipLevel" + (vipLevel + 1)));
        }
    }

    @OnClick(R.id.passPurchase)
    public void buyPass() {
        buyIAP("BlacksmithPass");
    }
}
