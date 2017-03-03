package uk.co.jakelee.blacksmithslots.helper;

import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmithslots.model.Task;

public class TaskHelper {
    public static boolean isSlotLocked(int slot) {
        return Select.from(Task.class).where(
                Condition.prop("completed").eq(0),
                Condition.prop("slot_id").eq(slot)).count() > 0;
    }
}
