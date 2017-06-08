package uk.co.jakelee.blacksmithslots.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.FontTextView;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmithslots.helper.IncomeHelper;
import uk.co.jakelee.blacksmithslots.helper.LanguageHelper;
import uk.co.jakelee.blacksmithslots.helper.StorageHelper;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Statistic;

import static uk.co.jakelee.blacksmithslots.model.Setting.get;

public class SettingsActivity extends BaseActivity {
    private int spinnersInitialised = 0;
    private int totalSpinners = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_table);
        ((TextView)findViewById(R.id.activityTitle)).setText(R.string.settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        spinnersInitialised = 0;
        populateSettings();
    }

    private void createDropdown(Spinner spinner, int max, int min, Enums.Setting settingEnum) {
        Setting setting = get(settingEnum);
        ArrayAdapter<String> envAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_display);
        envAdapter.setDropDownViewResource(R.layout.custom_spinner_item);

        for (int i = min; i <= max; i++) {
            String text = LanguageHelper.getFlagById(i) + " " + LanguageHelper.getLanguageById(this, i);
            envAdapter.add(text);
        }

        spinner.setAdapter(envAdapter);
        spinner.setSelection(setting.getIntValue() - 1);
        spinner.setOnItemSelectedListener(getListener(setting));
    }

    private AdapterView.OnItemSelectedListener getListener(final Setting setting) {
        final Activity activity = this;
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spinnersInitialised < totalSpinners) {
                    spinnersInitialised++;
                } else {
                    setting.setIntValue(position + 1);
                    setting.save();

                    LanguageHelper.changeLanguage(activity, position + 1);
                    AlertHelper.success(activity, "Set language to " + parentView.getSelectedItem().toString(), true);
                    onResume();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        };
    }

    private void populateSettings() {
        spinnersInitialised = 0;
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout settingTable = (TableLayout)findViewById(R.id.dataTable);
        settingTable.removeAllViews();
        for (Enums.SettingGroup group : Enums.SettingGroup.values()) {
            displaySettingGroup(inflater, settingTable, group);
        }

        LinearLayout socialRow = (LinearLayout)inflater.inflate(R.layout.custom_row_social, null).findViewById(R.id.socialRow);
        settingTable.addView(socialRow);

        LinearLayout supportRow = (LinearLayout) inflater.inflate(R.layout.custom_row_misc, null).findViewById(R.id.supportRow);
        settingTable.addView(supportRow);


    }

    private void displaySettingGroup(LayoutInflater inflater, TableLayout settingTable, Enums.SettingGroup group) {
        if (group == Enums.SettingGroup.Internal) {
            return;
        }

        FontTextView textView = new FontTextView(this);
        textView.setPadding(5, 20, 0, 0);
        textView.setText(TextHelper.getInstance(this).getText(DisplayHelper.getSettingGroupString(group.value)));
        textView.setTextSize(30);
        settingTable.addView(textView);

        List<Setting> settings = Setting.getByGroup(group);
        for (Setting setting : settings) {
            displaySetting(inflater, settingTable, setting);
        }
    }

    private void displaySetting(LayoutInflater inflater, TableLayout settingTable, Setting setting) {
        TableRow tableRow = createTableRow(inflater, setting);
        tableRow.setTag(setting.getSetting().value);
        ((TextView)tableRow.findViewById(R.id.settingName)).setText(setting.getName(this));
        settingTable.addView(tableRow);
    }

    public void openLink(View v) {
        String url = (String)v.getTag();
        startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
    }

    public void replayIntro(View v) {
        startActivity(new Intent(this, SplashScreenActivity.class)
                .putExtra("replayingIntro", true)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void openSupportCode(View v) {
        AlertDialogHelper.enterSupportCode(this, this);
    }

    public void openEmail(View v) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","blacksmithslots@jakelee.co.uk", null));
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_an_email)));
        AlertHelper.info(this, getString(R.string.alert_email_fallback), true);
    }

    private TableRow createTableRow(LayoutInflater inflater, Setting setting) {
        TableRow row = (TableRow)inflater.inflate(getRowLayout(setting), null).findViewById(R.id.dataRow);
        if (setting.getSetting() == Enums.Setting.SaveImported) {
            row.findViewById(R.id.importButton).setEnabled(!setting.getBooleanValue());
            row.findViewById(R.id.importButton).setBackgroundResource(setting.getBooleanValue() ? R.drawable.box_orange : R.drawable.box_green);
            row.findViewById(R.id.importButton).setAlpha(setting.getBooleanValue() ? 0.5f : 1f);
        } else if (setting.getSetting() == Enums.Setting.PlayLogout) {
            row.findViewById(R.id.logoutButton).setEnabled(GooglePlayHelper.IsConnected());
            row.findViewById(R.id.logoutButton).setBackgroundResource(GooglePlayHelper.IsConnected() ? R.drawable.box_orange : R.drawable.box_green);
            row.findViewById(R.id.logoutButton).setAlpha(GooglePlayHelper.IsConnected() ? 1f : 0.5f);
        } else if (setting.getSetting() == Enums.Setting.Language) {
            createDropdown((Spinner) row.findViewById(R.id.languagePicker), 3, 1, Enums.Setting.Language);
        } else if (setting.getDataType() == Enums.DataType.Boolean.value) {
            ((TextView)row.findViewById(R.id.settingValue)).setText(setting.getBooleanValue() ? getString(R.string.on) : getString(R.string.off));
        } else if (setting.getDataType() == Enums.DataType.Integer.value) {
            ((TextView)row.findViewById(R.id.settingValue)).setText(setting.getIntValue() + " mins");
        }
        return row;
    }

    public void logout(View v) {
        if (GooglePlayHelper.IsConnected()) {
            GooglePlayHelper.disconnect();
            populateSettings();
            AlertHelper.success(this, getString(R.string.alert_logged_out), true);
        }
    }

    private int getRowLayout(Setting setting) {
        if (setting.getDataType() == Enums.DataType.Boolean.value) {
            if (setting.getSetting() == Enums.Setting.SaveImported) {
                return R.layout.custom_setting_boolean_save_import;
            } else if (setting.getSetting() == Enums.Setting.PlayLogout) {
                return R.layout.custom_setting_boolean_logout;
            }
            return R.layout.custom_setting_boolean;
        } else if (setting.getDataType() == Enums.DataType.Integer.value) {
            if (setting.getSetting() == Enums.Setting.Language) {
                return R.layout.custom_setting_integer_language;
            }
            return R.layout.custom_setting_integer;
        } else {
            return R.layout.custom_setting_string;
        }
    }

    public void changeBoolean(View v) {
        Setting setting = get((Integer)((TableRow)v.getParent()).getTag());
        setting.setBooleanValue(!setting.getBooleanValue());
        setting.save();
        onResume();

        AlertHelper.success(this, "Turned " + setting.getName(this) + (setting.getBooleanValue() ? " on" : " off") + "!", true);
    }

    public void changeString(View v) {
        AlertHelper.info(this, "Toggle string for " + ((TableRow)v.getParent()).getTag(), true);
    }

    public void changeInteger(View v) {
        AlertHelper.info(this, "This should change the value for setting #" + ((TableRow)v.getParent()).getTag(), true);
    }

    public void importSave(View v) {
        StorageHelper.confirmStoragePermissions(this);
        Pair<Integer, Integer> pbData = StorageHelper.getPBSave();
        if (pbData != null) {

            AlertHelper.success(this, IncomeHelper.claimImportBonus(this, pbData.first), false);

            Setting haveImported = Setting.get(Enums.Setting.SaveImported);
            haveImported.setBooleanValue(true);
            haveImported.save();

            Statistic haveImportedStat = Statistic.get(Enums.Statistic.SaveImported);
            haveImportedStat.setBoolValue(true);
            haveImportedStat.save();

            Statistic.add(Enums.Statistic.SaveImported);

            populateSettings();
        } else {
            AlertHelper.error(this, R.string.error_failed_pb_import, false);
        }
    }

}
