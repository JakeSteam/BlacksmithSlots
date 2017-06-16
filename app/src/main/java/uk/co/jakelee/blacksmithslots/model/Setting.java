package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;

@Table(name = "f")
public class Setting extends SugarRecord {
    @Column(name = "a")
    private int setting;

    @Column(name = "b")
    private int settingGroup;

    @Column(name = "c")
    private int dataType;

    @Column(name = "d")
    private boolean booleanValue;

    @Column(name = "e")
    private int intValue;

    @Column(name = "f")
    private String stringValue;

    public Setting() {
    }

    public Setting(Enums.SettingGroup group, Enums.Setting setting, boolean booleanValue) {
        this.settingGroup = group.value;
        this.setting = setting.value;
        this.dataType = Enums.DataType.Boolean.value;
        this.booleanValue = booleanValue;
    }

    public Setting(Enums.SettingGroup group, Enums.Setting setting, int intValue) {
        this.settingGroup = group.value;
        this.setting = setting.value;
        this.dataType = Enums.DataType.Integer.value;
        this.intValue = intValue;
    }

    public Setting(Enums.SettingGroup group, Enums.Setting setting, String stringValue) {
        this.settingGroup = group.value;
        this.setting = setting.value;
        this.dataType = Enums.DataType.String.value;
        this.stringValue = stringValue;
    }

    public static Setting get(Enums.Setting setting) {
        return get(setting.value);
    }

    public static Setting get(int settingId) {
        return Select.from(Setting.class).where(
                Condition.prop("a").eq(settingId)).first();
    }

    public static int getInt(Enums.Setting settingId) {
        Setting setting = Select.from(Setting.class).where(
                Condition.prop("a").eq(settingId.value)).first();

        if (setting != null) {
            return setting.getIntValue();
        }
        return 0;
    }

    public static String getString(Enums.Setting settingId) {
        Setting setting = Select.from(Setting.class).where(
                Condition.prop("a").eq(settingId.value)).first();

        if (setting != null) {
            return setting.getStringValue();
        }
        return "";
    }

    public static List<Setting> getByGroup(Enums.SettingGroup group) {
        return Select.from(Setting.class).where(Condition.prop("b").eq(group.value)).list();
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

    public int getSettingGroup() {
        return settingGroup;
    }

    public void setSettingGroup(Enums.SettingGroup settingGroup) {
        this.settingGroup = settingGroup.value;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
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

    private String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText(DisplayHelper.getSettingString(setting));
    }
}
