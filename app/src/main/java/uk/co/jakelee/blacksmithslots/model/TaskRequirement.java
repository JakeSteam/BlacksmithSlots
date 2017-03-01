package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmithslots.helper.Enums;

public class TaskRequirement extends SugarRecord {
    private int taskId;
    private Enums.RequirementType type;
    private int typeId;
    private long completed;

    public TaskRequirement() {
    }

    public TaskRequirement(int taskId, Enums.RequirementType type, int typeId, long completed) {
        this.taskId = taskId;
        this.type = type;
        this.typeId = typeId;
        this.completed = completed;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Enums.RequirementType getType() {
        return type;
    }

    public void setType(Enums.RequirementType type) {
        this.type = type;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }
}
