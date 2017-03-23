package uk.co.jakelee.blacksmithslots.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateHelper {
    public static long hoursToMillis(double hours) {
        return TimeUnit.MINUTES.toMillis((long)Math.ceil(hours * 60d));
    }

    public static long minsToMillis(long mins) {
        return TimeUnit.MINUTES.toMillis(mins);
    }

    public static String timestampToDateTime(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(timestamp));
    }

    public static String timestampToTime(long timestamp) {
        return new SimpleDateFormat("h'hr' mm'min'").format(new Date(timestamp));
    }

    public static String timestampToDetailedTime(long timestamp) {
        return new SimpleDateFormat("h'hr' mm'min' ss'sec'").format(new Date(timestamp));
    }
}
