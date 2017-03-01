package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;

public class Task extends SugarRecord {
    private int taskId;
    private int slotId;
    private long completed;

    public Task() {
    }

    public Task(int taskId, int slotId, long completed) {
        this.taskId = taskId;
        this.slotId = slotId;
        this.completed = completed;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }
}
