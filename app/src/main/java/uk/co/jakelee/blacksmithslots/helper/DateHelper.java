package uk.co.jakelee.blacksmithslots.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateHelper {
    public static long hoursToMillis(double hours) {
        return TimeUnit.MINUTES.toMillis((long)Math.ceil(hours * 60d));
    }

    public static String timestampToString(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(timestamp));
    }
}
