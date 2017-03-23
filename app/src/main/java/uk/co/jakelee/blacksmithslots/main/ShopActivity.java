package uk.co.jakelee.blacksmithslots.main;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;

public class ShopActivity extends BaseActivity {
    private boolean haveSetupTabs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_shop);
    }

    @Override
    protected void onResume() {
        if (!haveSetupTabs) {
            createPassTab();
            createVipTab();
            createBundleTab();
            haveSetupTabs = true;
        }
    }

    private void createPassTab() {

    }

    private void createVipTab() {

    }

    private void createBundleTab() {

    }

    public void changeTab(View v) {
        findViewById(R.id.passTab).setVisibility(v.getId() == R.id.tab_pass ? View.VISIBLE : View.GONE);
        findViewById(R.id.vipTab).setVisibility(v.getId() == R.id.tab_vip ? View.VISIBLE : View.GONE);
        findViewById(R.id.bundleTab).setVisibility(v.getId() == R.id.tab_bundle? View.VISIBLE : View.GONE);

        findViewById(R.id.tab_pass).setAlpha(v.getId() == R.id.tab_pass ? 1f : 0.5f);
        findViewById(R.id.tab_vip).setAlpha(v.getId() == R.id.tab_vip ? 1f : 0.5f);
        findViewById(R.id.tab_bundle).setAlpha(v.getId() == R.id.tab_bundle ? 1f : 0.5f);
    }
}
