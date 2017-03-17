package uk.co.jakelee.blacksmithslots.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.MinigameActivity;
import uk.co.jakelee.blacksmithslots.main.SlotActivity;

public class AlertDialogHelper {
    public static void confirmCloseMinigame(final MinigameActivity activity) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.Theme_AlertDialog);
        alertDialog.setMessage("Are you sure you want to leave the minigame with no reward?");

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.confirmClose();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void autospin(final SlotActivity activity, final SlotHelper slotHelper) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage("How many autospins?");

        final int maxAutospins = LevelHelper.getAutospinsByVip(LevelHelper.getVipLevel());
        final int halfAutospins = LevelHelper.getAutospinsByVip(LevelHelper.getVipLevel()) / 2;
        alertDialog.setPositiveButton(Integer.toString(maxAutospins), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                slotHelper.autospin(maxAutospins);
            }
        });

        alertDialog.setNegativeButton(Integer.toString(halfAutospins), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                slotHelper.autospin(halfAutospins);
            }
        });

        alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmCloudLoad(final Activity activity, int localStars, int localCurrency, int cloudStars, int cloudCurrency) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.Theme_AlertDialog);
        /*alertDialog.setMessage(String.format(Locale.ENGLISH, Text.get("DIALOG_CLOUD_LOAD_CONFIRM"),
                localStars,
                localCurrency,
                cloudStars,
                cloudCurrency));*/

        alertDialog.setPositiveButton("Load", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GooglePlayHelper.forceLoadFromCloud();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
    }

    public static void confirmCloudSave(final Activity activity, int localStars, int localCurrency, String desc, long saveTime, String deviceName) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.Theme_AlertDialog);
        /*alertDialog.setMessage(String.format(Locale.ENGLISH, Text.get("DIALOG_CLOUD_SAVE_CONFIRM"),
                desc,
                DateHelper.displayTime(saveTime, DateHelper.datetime),
                deviceName,
                localStars,
                localCurrency,
                BuildConfig.VERSION_NAME));*/

        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GooglePlayHelper.forceSaveToCloud();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.show();
            }
        });
    }
}
