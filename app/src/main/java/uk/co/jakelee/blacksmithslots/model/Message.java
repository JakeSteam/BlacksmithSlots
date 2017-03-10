package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;
import com.orm.query.Select;

import java.util.Locale;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.Enums;

@Table(name="c")
public class Message extends SugarRecord {
    @Column(name = "a")
    private long time;

    @Column(name = "b")
    private String message;

    public Message() {
    }

    private Message(String message) {
        this.message = message;
        this.time = System.currentTimeMillis();
    }

    public static void logSpin(Context context, int slot, Enums.Type resourceType, Enums.Tier resourceTier, int resourceQuantity, String rewardString) {
        String slotName = Slot.getName(context, slot);
        String resourceName = Item.getName(context, resourceTier.value, resourceType.value);
        String logMessage = String.format(Locale.ENGLISH, context.getString(R.string.log_spin),
                resourceQuantity,
                resourceName,
                slotName,
                rewardString);

        log(logMessage);
    }

    public static void log(String string) {
        new Message(string).save();
        removeOldest();
    }

    private static void removeOldest() {
        if (Message.count(Message.class) >= Constants.MESSAGE_LOG_LIMIT) {
            Message oldestMessage = Select.from(Message.class).orderBy("a ASC").first();
            oldestMessage.delete();
        }
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
