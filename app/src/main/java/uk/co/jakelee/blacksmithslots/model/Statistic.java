package uk.co.jakelee.blacksmithslots.model;

import android.util.Log;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

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

    public static void add(Enums.Statistic stat) {
        add(stat, 1);
    }

    public static void add(Enums.Statistic stat, int amount) {
        Statistic statistic = Select.from(Statistic.class).where(Condition.prop("statistic").eq(stat)).first();

        if (statistic == null) {
            return;
        }

        statistic.setIntValue(statistic.getIntValue() + amount);
        statistic.save();

        List<TaskRequirement> taskRequirements = Select.from(TaskRequirement.class).where(
                Condition.prop("statistic").eq(stat),
                Condition.prop("started").gt(0),
                Condition.prop("completed").eq(0)).list();

        for (TaskRequirement task : taskRequirements) {
            Log.d("Task", "Remaining: " + task.getRemaining() + ", removing " + amount);
            if (task.getRemaining() <= amount) {
                task.setRemaining(0);
                task.setCompleted(System.currentTimeMillis());
                Log.d("Task", "Completed!");
            } else {
                task.setRemaining(task.getRemaining() - amount);
            }
        }

        if (taskRequirements.size() > 0) {
            TaskRequirement.saveInTx(taskRequirements);
        }
    }

}
