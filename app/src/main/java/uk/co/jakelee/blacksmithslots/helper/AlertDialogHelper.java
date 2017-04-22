package uk.co.jakelee.blacksmithslots.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.constructs.DialogAction;
import uk.co.jakelee.blacksmithslots.main.MinigameActivity;
import uk.co.jakelee.blacksmithslots.main.SlotActivity;

public class AlertDialogHelper {
    private static void displayAlertDialog(Context context, String title, String body, DialogAction... actions) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        final View inflatedLayout = inflater.inflate(R.layout.custom_alert_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setView(inflatedLayout);

        ((TextView)inflatedLayout.findViewById(R.id.title)).setText(title);
        ((TextView)inflatedLayout.findViewById(R.id.body)).setText(body);
        final LinearLayout buttonContainer = (LinearLayout)inflatedLayout.findViewById(R.id.buttonContainer);

        for (final DialogAction action : actions) {
            TextView button = (TextView)inflater.inflate(R.layout.custom_alert_dialog_button, null);
            button.setText(action.getText());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    action.getRunnable().run();
                    dialog.dismiss();
                }
            });
            buttonContainer.addView(button, params);
        }

        dialog.show();
    }

    public static void confirmCloseMinigame(final MinigameActivity activity) {
        displayAlertDialog(activity, activity.getString(R.string.alert_minigame), activity.getString(R.string.alert_minigame_body),
                new DialogAction(activity.getString(R.string.yes), new Runnable() {
                    @Override
                    public void run() {
                        activity.confirmClose();
                    }
                }),
                new DialogAction(activity.getString(R.string.no), new Runnable() {
                    @Override
                    public void run() {
                    }
                }));
    }

    public static void autospin(final SlotActivity activity, final SlotHelper slotHelper) {
        final int maxAutospins = LevelHelper.getAutospinsByVip(LevelHelper.getVipLevel());
        final int halfAutospins = LevelHelper.getAutospinsByVip(LevelHelper.getVipLevel()) / 2;

        displayAlertDialog(activity, activity.getString(R.string.alert_autospin), activity.getString(R.string.alert_autospin_body),
                new DialogAction(Integer.toString(maxAutospins), new Runnable() {
                    @Override
                    public void run() {
                        slotHelper.autospin(maxAutospins);
                    }
                }),
                new DialogAction(Integer.toString(halfAutospins), new Runnable() {
                    @Override
                    public void run() {
                        slotHelper.autospin(halfAutospins);
                    }
                }),
                new DialogAction(activity.getString(R.string.cancel), new Runnable() {
                    @Override
                    public void run() {
                    }
                }));
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
