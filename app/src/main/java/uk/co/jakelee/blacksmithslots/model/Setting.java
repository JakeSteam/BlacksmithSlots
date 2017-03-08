package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmithslots.helper.Enums;

public class Setting extends SugarRecord {
    private int setting;
    private boolean booleanValue;
    private int intValue;
    private String stringValue;

    public Setting() {
    }

    public Setting(Enums.Setting setting, boolean booleanValue) {
        this.setting = setting.value;
        this.booleanValue = booleanValue;
    }

    public Setting(Enums.Setting setting, int intValue) {
        this.setting = setting.value;
        this.intValue = intValue;
    }

    public Setting(Enums.Setting setting, String stringValue) {
        this.setting = setting.value;
        this.stringValue = stringValue;
    }

    public static Setting get(Enums.Setting settingId) {
        return (Setting)Select.from(Setting.class).where(
                Condition.prop("setting").eq(settingId.value)).first();
    }

    public static int getInt(Enums.Setting settingId) {
        Setting setting = (Setting)Select.from(Setting.class).where(
                Condition.prop("setting").eq(settingId.value)).first();

        if (setting != null) {
            return setting.getIntValue();
        }
        return 0;
    }

    public static String getString(Enums.Setting settingId) {
        Setting setting = (Setting)Select.from(Setting.class).where(
                Condition.prop("setting").eq(settingId.value)).first();

        if (setting != null) {
            return setting.getStringValue();
        }
        return "";
    }

    public static boolean getBoolean(Enums.Setting settingId) {
        Setting setting = Setting.get(settingId);

        return setting != null && setting.getBooleanValue();
    }

    public Enums.Setting getSetting() {
        return Enums.Setting.get(setting);
    }

    public void setSetting(Enums.Setting setting) {
        this.setting = setting.value;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
}
