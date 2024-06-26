package uk.co.jakelee.blacksmithslots.helper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class NotificationHelper extends BroadcastReceiver {
    private static final String NOTIFICATION_TYPE = "uk.co.jakelee.notification_type";
    private static boolean useSounds = false;

    public static void addFarmNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        long nextClaimTime = IncomeHelper.getNextFarmMaxTime();
        if (System.currentTimeMillis() < nextClaimTime) {
            NotificationHelper.addNotification(context, nextClaimTime, Constants.NOTIFICATION_FARM_BONUS);
        }
    }

    public static void addBonusNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        long nextClaimTime = IncomeHelper.getNextPeriodicClaimTime();
        if (System.currentTimeMillis() < nextClaimTime) {
            NotificationHelper.addNotification(context, nextClaimTime, Constants.NOTIFICATION_PERIODIC_BONUS);
        }
    }

    public static void addBlacksmithPassNotification(Context context, boolean useSoundsSetting) {
        useSounds = useSoundsSetting;

        int daysLeft = IapHelper.getPassDaysLeft();
        int dayOfPass = Constants.PASS_DAYS - daysLeft;
        int lastClaimedDay = Statistic.get(Enums.Statistic.CurrentPassClaimedDay).getIntValue();
        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (hourOfDay > 13 && daysLeft > 1) {
            calendar.add(Calendar.DATE, 1);
            NotificationHelper.addNotification(context, calendar.getTimeInMillis(), Constants.NOTIFICATION_PASS_BONUS);
        } else if (hourOfDay <= 13 && daysLeft > 0 && lastClaimedDay < dayOfPass) {
            NotificationHelper.addNotification(context, calendar.getTimeInMillis(), Constants.NOTIFICATION_PASS_BONUS);
        }
    }

    private static void addNotification(Context context, long notificationTime, int notificationType) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra(NOTIFICATION_TYPE, notificationType);

        PendingIntent broadcast = PendingIntent.getBroadcast(context, notificationType, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, broadcast);
    }

    public static void clearNotifications(final Context context) {
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent = new Intent(context, BaseActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(BaseActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        int notificationType = intent.getExtras().getInt(NOTIFICATION_TYPE);
        String notificationText = "";
        if (notificationType == Constants.NOTIFICATION_PERIODIC_BONUS) {
            notificationText = context.getString(R.string.notification_periodic_bonus);
        } else if (notificationType == Constants.NOTIFICATION_PASS_BONUS) {
            notificationText = context.getString(R.string.notification_pass_bonus);
        } else if (notificationType == Constants.NOTIFICATION_FARM_BONUS) {
            notificationText = context.getString(R.string.notification_farm_bonus);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText(notificationText)
                .setSmallIcon(R.drawable.arrow_orange)
                .setContentIntent(pendingIntent)
                .build();

        if (useSounds) {
            notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
