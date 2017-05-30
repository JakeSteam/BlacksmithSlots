package uk.co.jakelee.blacksmithslots.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.FontTextView;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_data_table);
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
            if (group == Enums.SettingGroup.Internal) {
                continue;
            }

            FontTextView textView = new FontTextView(this);
            textView.setPadding(5, 20, 0, 0);
            textView.setText(TextHelper.getInstance(this).getText(DisplayHelper.getSettingGroupString(group.value)));
            textView.setTextSize(30);
            settingTable.addView(textView);

            List<Setting> settings = Setting.getByGroup(group);
            for (Setting setting : settings) {
                TableRow tableRow = createTableRow(inflater, setting);
                tableRow.setTag(setting.getSetting().value);
                ((TextView)tableRow.findViewById(R.id.settingName)).setText(setting.getName(this));
                settingTable.addView(tableRow);
            }
        }
    }

    private TableRow createTableRow(LayoutInflater inflater, Setting setting) {
        TableRow row = (TableRow)inflater.inflate(getRowLayout(setting), null).findViewById(R.id.dataRow);
        if (setting.getSetting() == Enums.Setting.SaveImported) {
            row.findViewById(R.id.importButton).setEnabled(!setting.getBooleanValue());
            row.findViewById(R.id.importButton).setBackgroundResource(setting.getBooleanValue() ? R.drawable.box_orange : R.drawable.box_green);
            row.findViewById(R.id.importButton).setAlpha(setting.getBooleanValue() ? 0.5f : 1f);
        } else if (setting.getSetting() == Enums.Setting.Language) {
            createDropdown((Spinner) row.findViewById(R.id.languagePicker), 3, 1, Enums.Setting.Language);
        } else if (setting.getDataType() == Enums.DataType.Boolean.value) {
            ((TextView)row.findViewById(R.id.settingValue)).setText(setting.getBooleanValue() ? "On" : "Off");
        } else if (setting.getDataType() == Enums.DataType.Integer.value) {
            ((TextView)row.findViewById(R.id.settingValue)).setText(setting.getIntValue() + " mins");
        }
        return row;
    }

    private int getRowLayout(Setting setting) {
        if (setting.getDataType() == Enums.DataType.Boolean.value) {
            if (setting.getSetting() == Enums.Setting.SaveImported) {
                return R.layout.custom_setting_boolean_save_import;
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
        if (StorageHelper.checkForPBSave()) {
            AlertHelper.success(this, IncomeHelper.claimMiscBonus(this), false);

            // Reward fixed amount for now, ideally dependant on prestige / premium?
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
