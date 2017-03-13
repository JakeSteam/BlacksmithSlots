package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.MainActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.FontTextView;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;
import uk.co.jakelee.blacksmithslots.model.Setting;

public class SettingsActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableLayout settingTable = (TableLayout)findViewById(R.id.settingsTable);
        for (Enums.SettingGroup group : Enums.SettingGroup.values()) {
            if (group == Enums.SettingGroup.Internal) {
                continue;
            }

            FontTextView textView = new FontTextView(this);
            textView.setText(TextHelper.getInstance(this).getText(DisplayHelper.getSettingGroupString(group.value)));
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
        TableRow row = (TableRow)inflater.inflate(getRowLayout(setting.getDataType()), null).findViewById(R.id.dataRow);
        if (setting.getDataType() == Enums.DataType.Boolean.value) {
            ((TextView)row.findViewById(R.id.settingValue)).setText(setting.getBooleanValue() ? "On" : "Off");
        } else if (setting.getDataType() == Enums.DataType.Integer.value) {
            ((TextView)row.findViewById(R.id.settingValue)).setText(setting.getIntValue() + " mins");
        } else {
            ((TextView)row.findViewById(R.id.settingValue)).setText("string");
        }
        return row;
    }

    private int getRowLayout(int dataType) {
        if (dataType == Enums.DataType.Boolean.value) {
            return R.layout.custom_setting_boolean;
        } else if (dataType == Enums.DataType.Integer.value) {
            return R.layout.custom_setting_integer;
        } else {
            return R.layout.custom_setting_string;
        }
    }

    public void changeBoolean(View v) {
        AlertHelper.info(this, "Toggle boolean for " + ((TableRow)v.getParent()).getTag(), true);
    }

    public void changeString(View v) {
        AlertHelper.info(this, "Toggle string for " + ((TableRow)v.getParent()).getTag(), true);
    }

    public void changeInteger(View v) {
        AlertHelper.info(this, "Toggle integer for " + ((TableRow)v.getParent()).getTag(), true);
    }

    public void close(View v) {
        finish();
    }

    public void suppress(View v) {

    }
}
