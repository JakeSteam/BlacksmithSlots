package uk.co.jakelee.blacksmithslots.helper;

import android.os.Handler;
import android.widget.TextView;

public class Runnables {
    public static Runnable updateTimeToPeriodicBonusClaim(final Handler handler, final TextView button) {
        return new Runnable() {
            @Override
            public void run() {
                button.setText(DateHelper.timestampToTime(IncomeHelper.getNextPeriodicClaimTime() - System.currentTimeMillis()));
                handler.postDelayed(this, 1000);
            }
        };
    }

    public static Runnable updateTimeToWatchAdvert(final Handler handler, final TextView button) {
        return new Runnable() {
            @Override
            public void run() {
                button.setText(DateHelper.timestampToTime(IncomeHelper.getNextAdvertWatchTime() - System.currentTimeMillis()));
                handler.postDelayed(this, 1000);
            }
        };
    }
}