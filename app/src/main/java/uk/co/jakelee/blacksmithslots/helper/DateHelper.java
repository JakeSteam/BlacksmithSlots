package uk.co.jakelee.blacksmithslots.helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateHelper {
    public static final int MILLISECONDS_IN_SECOND = 1000;
    public static final int SECONDS_IN_MINUTE = 60;
    private static final int MINUTES_IN_HOUR = 60;

    public static long hoursToMillis(double hours) {
        return TimeUnit.MINUTES.toMillis((long)Math.ceil(hours * 60d));
    }

    public static long minsToMillis(long mins) {
        return TimeUnit.MINUTES.toMillis(mins);
    }

    public static String timestampToDateTime(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
    }

    public static String timestampToTime(long timestamp) {
        return new SimpleDateFormat("H'hr' mm'min'").format(new Date(timestamp));
    }

    public static String timestampToDetailedTime(long timestamp) {
        return new SimpleDateFormat("H'hr' mm'min' ss'sec'").format(new Date(timestamp));
    }

    public static Calendar getYesterdayMidnight() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}
