package uk.co.jakelee.blacksmithslots.helper;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.model.Task;

public class TaskHelper {
    public static boolean isSlotLocked(int slot) {
        if (slot == 1) {
            return false;
        }
        List<Task> tasks = Select.from(Task.class).where(
                Condition.prop("i").eq(0),
                Condition.prop("a").eq(slot)).list();
        return tasks.size() > 0;
    }

    public static Task getCurrentTask(List<Task> tasks) {
        for (Task task : tasks) {
            if(task.getCompleted() == 0) {
                return task;
            }
        }
        return new Task();
    }
}
