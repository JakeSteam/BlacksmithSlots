package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmithslots.helper.Enums;

public class Task extends SugarRecord {
    private int slotId;
    private Enums.Statistic statistic;
    private Enums.Tier tier;
    private Enums.Type type;
    private int remaining;
    private long started;
    private long completed;

    public Task() {
    }

    public Task(int slotId, Enums.Statistic statistic, int remaining) {
        this.slotId = slotId;
        this.statistic = statistic;
        this.remaining = remaining;
        this.started = 0;
        this.completed = 0;
    }

    public Task(int slotId, Enums.Tier tier, Enums.Type type, int remaining) {
        this.slotId = slotId;
        this.tier = tier;
        this.type = type;
        this.remaining = remaining;
        this.started = 0;
        this.completed = 0;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public Enums.Statistic getStatistic() {
        return statistic;
    }

    public void setStatistic(Enums.Statistic statistic) {
        this.statistic = statistic;
    }

    public Enums.Tier getTier() {
        return tier;
    }

    public void setTier(Enums.Tier tier) {
        this.tier = tier;
    }

    public Enums.Type getType() {
        return type;
    }

    public void setType(Enums.Type type) {
        this.type = type;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public long getStarted() {
        return started;
    }

    public void setStarted(long started) {
        this.started = started;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }
}
