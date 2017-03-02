package uk.co.jakelee.blacksmithslots.helper;

import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmithslots.model.TaskRequirement;

public class TaskHelper {
    public static boolean isSlotUnlocked(int slot) {
        return Select.from(TaskRequirement.class).where(Condition.prop("completed").eq(0)).count() > 0;
    }
}
