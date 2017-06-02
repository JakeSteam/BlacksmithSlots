package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.games.Games;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.DateHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class CloudSaveActivity extends BaseActivity {
    @BindView(R.id.autosaveStatus) TextView autosaveStatus;
    @BindView(R.id.lastSaveInfo) TextView lastSaveInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_save);
        ButterKnife.bind(this);

        populateLayout();
    }

    private void populateLayout() {
        autosaveStatus.setText(String.format(Locale.ENGLISH, getString(R.string.google_play_autosave_status),
                GooglePlayHelper.IsConnected() ? getString(R.string.enabled) : getString(R.string.not_enabled),
                Setting.getBoolean(Enums.Setting.Autosave) ? getString(R.string.enabled) : getString(R.string.not_enabled)));
        lastSaveInfo.setText(String.format(Locale.ENGLISH, getString(R.string.google_play_last_autosave),
                (!Setting.getBoolean(Enums.Setting.Autosave) || Statistic.get(Enums.Statistic.LastAutosave).getLongValue() == 0) ?
                "N/A" :
                DateHelper.timestampToDateTime(Statistic.get(Enums.Statistic.LastAutosave).getLongValue())));
    }

    @OnClick(R.id.manageSaves)
    public void manageCloudSaves() {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            Intent savedGamesIntent = Games.Snapshots.getSelectSnapshotIntent(GooglePlayHelper.mGoogleApiClient,
                    getString(R.string.cloud_saves), true, true, 2);
            startActivityForResult(savedGamesIntent, GooglePlayHelper.RC_SAVED_GAMES);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null) {
            AlertHelper.info(this, R.string.google_cloud_comparing, true);
            GooglePlayHelper.SavedGamesIntent(getApplicationContext(), this, intent);
        }
    }
}
