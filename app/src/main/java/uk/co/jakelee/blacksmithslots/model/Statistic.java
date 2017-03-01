package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmithslots.helper.Enums;

public class Statistic extends SugarRecord {
    private Enums.Statistic statistic;
    private int intValue;
    private boolean boolValue;
    private String stringValue;

    public Statistic() {
    }

    public Statistic(Enums.Statistic statistic, int intValue) {
        this.statistic = statistic;
        this.intValue = intValue;
    }

    public Statistic(Enums.Statistic statistic, boolean boolValue) {
        this.statistic = statistic;
        this.boolValue = boolValue;
    }

    public Statistic(Enums.Statistic statistic, String stringValue) {
        this.statistic = statistic;
        this.stringValue = stringValue;
    }

    public Enums.Statistic getStatistic() {
        return statistic;
    }

    public void setStatistic(Enums.Statistic statistic) {
        this.statistic = statistic;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public boolean isBoolValue() {
        return boolValue;
    }

    public void setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

}
