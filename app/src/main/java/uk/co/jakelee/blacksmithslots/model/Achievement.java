package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

import uk.co.jakelee.blacksmithslots.helper.Enums;

@Table(name = "j")
public class Achievement extends SugarRecord {
    @Column(name = "b")
    private int maximumValue;

    @Column(name = "c")
    private long statistic;

    @Column(name = "d")
    private String remoteID;

    public Achievement() {
    }

    public Achievement(int maximumValue, Enums.Statistic statistic, String remoteID) {
        this.maximumValue = maximumValue;
        this.statistic = statistic.value;
        this.remoteID = remoteID;
    }

    public int getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(int maximumValue) {
        this.maximumValue = maximumValue;
    }

    public long getStatistic() {
        return statistic;
    }

    public void setStatistic(long statistic) {
        this.statistic = statistic;
    }

    public String getRemoteID() {
        return remoteID;
    }

    public void setRemoteID(String remoteID) {
        this.remoteID = remoteID;
    }
}