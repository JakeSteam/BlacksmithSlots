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
import java.util.Locale;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.FontTextView;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmithslots.helper.IncomeHelper;
import uk.co.jakelee.blacksmithslots.helper.LanguageHelper;
import uk.co.jakelee.blacksmithslots.helper.PrestigeHelper;
import uk.co.jakelee.blacksmithslots.helper.StorageHelper;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Statistic;

import static uk.co.jakelee.blacksmithslots.model.Setting.get;

public class SettingsActivity extends BaseActivity {
    private int spinnersInitialised = 0;
    private final int totalSpinners = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_table);

        ((TextView)findViewById(R.id.activityTitle)).setText(R.string.settings);
        ((TextView)findViewById(R.id.activitySubtitle)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.activitySubtitle)).setText(R.string.settings_desc);
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
            if (settingEnum == Enums.Setting.Language) {
                envAdapter.add(LanguageHelper.getFlagById(i) + " " + LanguageHelper.getLanguageById(this, i));
            } else if (settingEnum == Enums.Setting.Orientation && i != Constants.ORIENTATION_INBETWEEN) {
                String orientationName = TextHelper.getInstance(this).getText(DisplayHelper.getOrientationString(i));
                envAdapter.add(orientationName);
            }
        }

        spinner.setAdapter(envAdapter);
        spinner.setSelection(getSpinnerPosition(setting));
        spinner.setOnItemSelectedListener(getListener(setting));
    }

    private int getSpinnerPosition(Setting setting) {
        if (setting.getSetting() == Enums.Setting.Orientation) {
            if (setting.getIntValue() == Constants.ORIENTATION_AUTO) {
                return setting.getIntValue() - 4;
            }
            return setting.getIntValue() - 5;
        }
        return setting.getIntValue() - 1;
    }

    private int getOrientationValue(int position) {
        switch (position) {
            case 1: return Constants.ORIENTATION_LANDSCAPE;
            case 2: return Constants.ORIENTATION_PORTRAIT;
            default: return Constants.ORIENTATION_AUTO;
        }
    }

    private AdapterView.OnItemSelectedListener getListener(final Setting setting) {
        final Activity activity = this;
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spinnersInitialised < totalSpinners) {
                    spinnersInitialised++;
                } else {
                    if (setting.getSetting() == Enums.Setting.Language) {
                        setting.setIntValue(position + 1);
                        setting.save();

                        LanguageHelper.changeLanguage(activity, position + 1);
                        AlertHelper.success(activity, getString(R.string.setting_changed_language) + parentView.getSelectedItem().toString(), true);
                    } else if (setting.getSetting() == Enums.Setting.Orientation) {
                        setting.setIntValue(getOrientationValue(position));
                        setting.save();

                        //noinspection ResourceType
                        setRequestedOrientation(setting.getIntValue());
                        AlertHelper.success(activity, getString(R.string.updated_orientation), true);
                    }
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
        TableLayout settingTable = findViewById(R.id.dataTable);
        settingTable.removeAllViews();
        for (Enums.SettingGroup group : Enums.SettingGroup.values()) {
            displaySettingGroup(inflater, settingTable, group);
        }

        LinearLayout socialRow = inflater.inflate(R.layout.custom_row_social, null).findViewById(R.id.socialRow);
        settingTable.addView(socialRow);

        LinearLayout supportRow = inflater.inflate(R.layout.custom_row_misc, null).findViewById(R.id.supportRow);
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

    private void displaySetting(LayoutInflater inflater, TableLayout settingTable, final Setting setting) {
        final Activity activity = this;
        TableRow tableRow = createTableRow(inflater, setting);
        tableRow.setTag(setting.getSetting().value);
        ((TextView)tableRow.findViewById(R.id.settingName)).setText(setting.getName(this));
        ((TextView)tableRow.findViewById(R.id.settingName)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String settingDesc = TextHelper.getInstance(activity).getText(DisplayHelper.getSettingDescString(setting.getSetting().value));
                AlertHelper.info(activity, settingDesc, false);
            }
        });
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
        TableRow row = inflater.inflate(getRowLayout(setting), null).findViewById(R.id.dataRow);
        if (setting.getSetting() == Enums.Setting.SaveImported) {
            row.findViewById(R.id.importButton).setEnabled(!setting.getBooleanValue());
            row.findViewById(R.id.importButton).setBackgroundResource(setting.getBooleanValue() ? R.drawable.box_orange : R.drawable.box_green);
            row.findViewById(R.id.importButton).setAlpha(setting.getBooleanValue() ? 0.5f : 1f);
        } else if (setting.getSetting() == Enums.Setting.Prestige) {
            boolean canPrestige = PrestigeHelper.canPrestige();
            row.findViewById(R.id.prestigeButton).setBackgroundResource(canPrestige ? R.drawable.box_green : R.drawable.box_orange);
            row.findViewById(R.id.prestigeButton).setAlpha(canPrestige ? 1f : 0.5f);
        } else if (setting.getSetting() == Enums.Setting.PlayLogout) {
            row.findViewById(R.id.logoutButton).setEnabled(GooglePlayHelper.IsConnected());
            row.findViewById(R.id.logoutButton).setBackgroundResource(GooglePlayHelper.IsConnected() ? R.drawable.box_orange : R.drawable.box_green);
            row.findViewById(R.id.logoutButton).setAlpha(GooglePlayHelper.IsConnected() ? 1f : 0.5f);
        } else if (setting.getSetting() == Enums.Setting.Language) {
            createDropdown((Spinner) row.findViewById(R.id.settingPicker), 9, 1, Enums.Setting.Language);
        } else if (setting.getSetting() == Enums.Setting.Orientation) {
            createDropdown((Spinner) row.findViewById(R.id.settingPicker), Constants.ORIENTATION_PORTRAIT, Constants.ORIENTATION_AUTO, Enums.Setting.Orientation);
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
            } else if (setting.getSetting() == Enums.Setting.Prestige) {
                return R.layout.custom_setting_boolean_prestige;
            } else if (setting.getSetting() == Enums.Setting.PlayLogout) {
                return R.layout.custom_setting_boolean_logout;
            }
            return R.layout.custom_setting_boolean;
        } else if (setting.getDataType() == Enums.DataType.Integer.value) {
            if (setting.getSetting() == Enums.Setting.Language || setting.getSetting() == Enums.Setting.Orientation) {
                return R.layout.custom_setting_integer_spinner;
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

        AlertHelper.success(this, String.format(Locale.ENGLISH, getString(setting.getBooleanValue() ? R.string.setting_turned_on : R.string.setting_turned_off), setting.getName(this)), true);
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

    public void prestigeGame(View v) {
        if (PrestigeHelper.canPrestige()) {
            AlertDialogHelper.confirmPrestige(this);
        } else {
            AlertHelper.error(this, getString(R.string.error_prestige_locked), true);
        }
    }

}
