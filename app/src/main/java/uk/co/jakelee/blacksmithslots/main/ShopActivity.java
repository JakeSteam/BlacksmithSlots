package uk.co.jakelee.blacksmithslots.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;

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

    }

    private void createVipTab() {

    }

    private void createBundleTab() {

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
