package uk.co.jakelee.blacksmithslots.main;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.IapHelper;
import uk.co.jakelee.blacksmithslots.model.Iap;

public class ShopActivity extends BaseActivity {
    private boolean haveSetupTabs = false;
    private int selectedTabId = R.id.tab_vip;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_shop);

        prefs = getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!haveSetupTabs) {
            createPassTab();
            createVipTab();
            createBundleTab();
            updateTabs(prefs.getInt("selectedShopTabId", 1));
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

        ((ImageView)findViewById(R.id.passImage)).getDrawable().setColorFilter(daysLeft > 0 ? Color.TRANSPARENT : Color.BLACK, PorterDuff.Mode.MULTIPLY);
        ((TextView)findViewById(R.id.passDaysLeft)).setText(String.format(Locale.ENGLISH, getString(R.string.pass_days_left), daysLeft));
        ((TextView)findViewById(R.id.passDaysLeft)).setTextColor(daysLeft > 0 ? Color.GREEN : Color.RED);
        findViewById(R.id.passPurchase).setTag(Enums.Iap.BlacksmithPass);
        findViewById(R.id.passClaim).setVisibility(daysLeft > 0 ? View.VISIBLE : View.GONE);
        ((TextView)findViewById(R.id.passDescription)).setText(daysLeft > 0 ? R.string.pass_desc_owned : R.string.pass_desc_unowned);

        // Display daily bonuses
    }

    private void createVipTab() {

    }

    private void createBundleTab() {

    }

    public void buyIap(View v) {
        if (v.getTag() != null) {
            Enums.Iap iapEnum = (Enums.Iap)v.getTag();
            // Fire purchase popup
            // purchase(iapEnum.name());

            // Temporary for testing
            Iap iap = Iap.get(iapEnum);
            iap.setLastPurchased(System.currentTimeMillis());
            iap.setTimesPurchased(iap.getTimesPurchased() + 1);
            createPassTab();
        }
    }

    public void changeTab(View v) {
        updateTabs(v.getId());
    }

    private void updateTabs(int id) {
        selectedTabId = id;
        findViewById(R.id.passTab).setVisibility(id == R.id.tab_pass ? View.VISIBLE : View.GONE);
        findViewById(R.id.vipTab).setVisibility(id == R.id.tab_vip ? View.VISIBLE : View.GONE);
        findViewById(R.id.bundleTab).setVisibility(id == R.id.tab_bundle? View.VISIBLE : View.GONE);

        findViewById(R.id.tab_pass).setAlpha(id == R.id.tab_pass ? 1f : 0.5f);
        findViewById(R.id.tab_vip).setAlpha(id == R.id.tab_vip ? 1f : 0.5f);
        findViewById(R.id.tab_bundle).setAlpha(id == R.id.tab_bundle ? 1f : 0.5f);
    }
}
