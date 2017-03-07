package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmithslots.helper.Enums;

public class Setting extends SugarRecord {
    private Enums.Setting setting;
    private boolean booleanValue;
    private int intValue;
    private String stringValue;

    public Setting() {
    }

    public Setting(Enums.Setting setting, boolean booleanValue) {
        this.setting = setting;
        this.booleanValue = booleanValue;
    }

    public Setting(Enums.Setting setting, int intValue) {
        this.setting = setting;
        this.intValue = intValue;
    }

    public Setting(Enums.Setting setting, String stringValue) {
        this.setting = setting;
        this.stringValue = stringValue;
    }

    public static Setting get(Enums.Setting settingId) {
        return Select.from(Setting.class).where(
                Condition.prop("setting").eq(settingId)).first();
    }

    public static int getInt(Enums.Setting settingId) {
        Setting setting = Select.from(Setting.class).where(
                Condition.prop("setting").eq(settingId)).first();

        if (setting != null) {
            return setting.getIntValue();
        }
        return 0;
    }

    public static String getString(Enums.Setting settingId) {
        Setting setting = Select.from(Setting.class).where(
                Condition.prop("setting").eq(settingId)).first();

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
        return setting;
    }

    public void setSetting(Enums.Setting setting) {
        this.setting = setting;
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
