package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;

public class Statistic extends SugarRecord {
    private int statistic;
    private String eventId;
    private String leaderboardId;
    private int intValue;
    private long longValue;
    private boolean boolValue;
    private String stringValue;

    public Statistic() {
    }

    public Statistic(Enums.Statistic statistic, String eventId, String leaderboardId, int intValue) {
        this.statistic = statistic.value;
        this.eventId = eventId;
        this.leaderboardId = leaderboardId;
        this.intValue = intValue;
    }

    public Statistic(Enums.Statistic statistic, String eventId, String leaderboardId, long longValue) {
        this.statistic = statistic.value;
        this.eventId = eventId;
        this.leaderboardId = leaderboardId;
        this.longValue = longValue;
    }

    public Statistic(Enums.Statistic statistic, String eventId, String leaderboardId, boolean boolValue) {
        this.statistic = statistic.value;
        this.eventId = eventId;
        this.leaderboardId = leaderboardId;
        this.boolValue = boolValue;
    }

    public Statistic(Enums.Statistic statistic, String eventId, String leaderboardId, String stringValue) {
        this.statistic = statistic.value;
        this.eventId = eventId;
        this.leaderboardId = leaderboardId;
        this.stringValue = stringValue;
    }

    public static Statistic get(Enums.Statistic statistic) {
        List<Statistic> statisticList = Select.from(Statistic.class).where(
                Condition.prop("statistic").eq(statistic)
        ).list();
        return statisticList.size() > 0 ? statisticList.get(0) : null;
    }

    public Enums.Statistic getStatistic() {
        return Enums.Statistic.get(statistic);
    }

    public void setStatistic(Enums.Statistic statistic) {
        this.statistic = statistic.value;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getLeaderboardId() {
        return leaderboardId;
    }

    public void setLeaderboardId(String leaderboardId) {
        this.leaderboardId = leaderboardId;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
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
        Statistic statistic = Select.from(Statistic.class).where(Condition.prop("statistic").eq(stat.value)).first();

        if (statistic == null) {
            return;
        }

        statistic.setIntValue(statistic.getIntValue() + amount);
        statistic.save();

        List<Task> tasks = getTasksForUpdating(stat);
        if (tasks.size() > 0) {
            for (Task task : tasks) {
                if (task.getRemaining() <= amount) {
                    task.setRemaining(0);
                    Log.d("Task", "Completed: " + task.toString());
                } else {
                    task.setRemaining(task.getRemaining() - amount);
                }
            }
            Task.saveInTx(tasks);
        }

        if (!statistic.getEventId().equals("")) {
            Log.d("Event", "Adding " + amount + " to " + statistic.getEventId());
            GooglePlayHelper.addEvent(statistic.getEventId(), amount);
        }
        if (!statistic.getLeaderboardId().equals("") && statistic.getIntValue() > 0) {
            Log.d("Leaderboard", "Adding " + amount + " to " + statistic.getLeaderboardId());
            GooglePlayHelper.updateLeaderboards(statistic.getLeaderboardId(), statistic.getIntValue());
        }
    }

    private static List<Task> getTasksForUpdating(Enums.Statistic stat) {
        List<Task> tasks = Select.from(Task.class).where(
                Condition.prop("statistic").eq(stat), // Tasks of this stat
                Condition.prop("statistic").notEq(Enums.Statistic.Level), // Except levels
                Condition.prop("started").gt(0), // That have been started
                Condition.prop("completed").eq(0), // And not completed
                Condition.prop("remaining").gt(0) // And not waiting to be handed in
        ).list();

        // Level tasks are updated independently
        if (stat == Enums.Statistic.Level) {
            tasks.addAll(Select.from(Task.class).where(
                    Condition.prop("statistic").eq(Enums.Statistic.Level),
                    Condition.prop("completed").eq(0),
                    Condition.prop("remaining").gt(0)).list());
        }

        return tasks;
    }

    public static String getName(Context context, Enums.Statistic statistic) {
        return TextHelper.getInstance(context).getText("statistic_" + statistic.value + "_name");
    }

}
