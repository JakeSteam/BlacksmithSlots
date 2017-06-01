package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.games.Games;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmithslots.model.Setting;

public class CloudSaveActivity extends BaseActivity {
    @BindView(R.id.autosaveStatus) TextView autosaveStatus;
    @BindView(R.id.lastSaveInfo) TextView lastSaveInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_cloud_save);
        ButterKnife.bind(this);

        populateLayout();
    }

    private void populateLayout() {
        autosaveStatus.setText(String.format(Locale.ENGLISH, "Google Play is currently %1$s, and automatic cloud saves are %2$s. These can be changed in the settings.",
                GooglePlayHelper.IsConnected() ? getString(R.string.enabled) : getString(R.string.not_enabled),
                Setting.getBoolean(Enums.Setting.Autosave) ? getString(R.string.enabled) : getString(R.string.not_enabled)));
        lastSaveInfo.setText("Current cloud save: " + (Setting.getBoolean(Enums.Setting.Autosave) ? "Level 20, lots of items, etc" : "N/A"));
    }

    @OnClick(R.id.manualSave)
    public void manualSave() {

    }

    @OnClick(R.id.manualLoad)
    public void manualLoad() {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            Intent savedGamesIntent = Games.Snapshots.getSelectSnapshotIntent(GooglePlayHelper.mGoogleApiClient,
                    getString(R.string.cloud_saves), false, true, 1);
            startActivityForResult(savedGamesIntent, GooglePlayHelper.RC_SAVED_GAMES);
        }
    }
}
