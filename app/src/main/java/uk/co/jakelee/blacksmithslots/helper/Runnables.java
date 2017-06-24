package uk.co.jakelee.blacksmithslots.helper;

import android.os.Handler;
import android.widget.TextView;

import uk.co.jakelee.blacksmithslots.R;

public class Runnables {
    public static Runnable updateTimeToPeriodicBonusClaim(final Handler handler, final TextView button) {
        return new Runnable() {
            @Override
            public void run() {
                long difference = IncomeHelper.getNextPeriodicClaimTime() - System.currentTimeMillis();
                if (difference > 0) {
                    button.setText(DateHelper.timestampToTime(difference));
                    handler.postDelayed(this, 1000);
                } else {
                    button.setText(R.string.claim_bonus);
                    button.setBackgroundResource(R.drawable.box_green);
                }
            }
        };
    }

    public static Runnable updateTimeToWatchAdvert(final Handler handler, final TextView button) {
        return new Runnable() {
            @Override
            public void run() {
                long difference = IncomeHelper.getNextAdvertWatchTime() - System.currentTimeMillis();
                if (difference > 0) {
                    button.setText(DateHelper.timestampToTime(difference));
                    handler.postDelayed(this, 1000);
                } else {
                    button.setText(R.string.watch_advert);
                    button.setBackgroundResource(R.drawable.box_green);
                }
            }
        };
    }
}
