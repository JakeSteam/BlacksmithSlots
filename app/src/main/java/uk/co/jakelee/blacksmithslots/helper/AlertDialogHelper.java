package uk.co.jakelee.blacksmithslots.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import uk.co.jakelee.blacksmithslots.BuildConfig;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.constructs.DialogAction;
import uk.co.jakelee.blacksmithslots.main.MinigameActivity;
import uk.co.jakelee.blacksmithslots.main.ShopActivity;
import uk.co.jakelee.blacksmithslots.main.SlotActivity;
import uk.co.jakelee.blacksmithslots.main.TrophyActivity;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.SupportCode;
import uk.co.jakelee.blacksmithslots.model.Trophy;

public class AlertDialogHelper {

    private static void displayAlertDialog(final Activity activity, final String title, final String body, final DialogAction... actions) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(activity);
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
                final View inflatedLayout = inflater.inflate(R.layout.custom_alert_dialog, null);
                final AlertDialog dialog = new AlertDialog.Builder(activity).create();
                dialog.setView(inflatedLayout);

                ((TextView) inflatedLayout.findViewById(R.id.title)).setText(title);
                ((TextView) inflatedLayout.findViewById(R.id.body)).setText(body);
                final LinearLayout buttonContainer = (LinearLayout) inflatedLayout.findViewById(R.id.buttonContainer);

                for (final DialogAction action : actions) {
                    TextView button = (TextView) inflater.inflate(R.layout.custom_alert_dialog_button, null);
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

                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialog.show();
                dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
        });
    }

    public static void enterSupportCode(final Context context, final Activity activity) {
        final EditText supportCodeBox = new EditText(context);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage(R.string.alert_enter_support_code);
        alertDialog.setView(supportCodeBox);

        alertDialog.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //String supportCode = SupportCodeHelper.encode((System.currentTimeMillis() + 259200000) + "|UPDATE b SET c = 999");
                String supportCode = supportCodeBox.getText().toString().trim();

                Log.d("Code", supportCode);
                if (SupportCode.alreadyApplied(supportCode)) {
                    AlertHelper.error(activity, activity.getString(R.string.alert_support_code_successful), true);
                } else if (SupportCodeHelper.applyCode(supportCode)) {
                    AlertHelper.success(activity, activity.getString(R.string.alert_support_code_successful), true);
                } else {
                    AlertHelper.error(activity, activity.getString(R.string.alert_support_code_failed), true);
                }
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void trophyHandIn(final TrophyActivity activity, Trophy trophy, Inventory inventory) {
        String itemName = inventory.getName(activity);
        final int maximumPossible = trophy.getItemsRemaining() > inventory.getQuantity() ? inventory.getQuantity() : trophy.getItemsRemaining();
        final int halfMax = (int)Math.ceil(maximumPossible / 2d);

        displayAlertDialog(activity, activity.getString(R.string.hand_in_items),
                String.format(Locale.ENGLISH, activity.getString(R.string.hand_in_items_long),
                    itemName,
                    trophy.getItemsRequired() - trophy.getItemsHandedIn()),
                new DialogAction(activity.getString(R.string.cancel), new Runnable() {
                    @Override
                    public void run() {

                    }
                }),
                new DialogAction(maximumPossible + "", new Runnable() {
                    @Override
                    public void run() {
                        activity.handInItems(maximumPossible);
                    }
                }),
                new DialogAction(activity.getString(R.string.shop), new Runnable() {
                    @Override
                    public void run() {
                        activity.handInItems(halfMax);
                    }
                }));
    }

    public static void outOfItems(final Activity activity, final int itemTier, final int itemType) {
        displayAlertDialog(activity, activity.getString(R.string.outOfItems), activity.getString(R.string.outOfItemsLong),
                new DialogAction(activity.getString(R.string.lowerBet), new Runnable() {
                    @Override
                    public void run() {

                    }
                }),
                new DialogAction(activity.getString(R.string.exit), new Runnable() {
                    @Override
                    public void run() {
                        activity.finish();
                    }
                }),
                new DialogAction(activity.getString(R.string.shop), new Runnable() {
                    @Override
                    public void run() {
                        activity.startActivity(new Intent(activity, ShopActivity.class)
                                .putExtra("tier", itemTier)
                                .putExtra("type", itemType)
                                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                    }
                }));
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

    public static void confirmCloudLoad(final Activity activity, int localXp, int localItems, int cloudXp, int cloudItems) {
        displayAlertDialog(activity, activity.getString(R.string.cloud_load), String.format(Locale.ENGLISH, activity.getString(R.string.google_cloud_load_confirm),
                LevelHelper.convertXpToLevel(localXp),
                localXp,
                localItems,
                LevelHelper.convertXpToLevel(cloudXp),
                cloudXp,
                cloudItems),
                new DialogAction(activity.getString(R.string.load), new Runnable() {
                    @Override
                    public void run() {
                        GooglePlayHelper.forceLoadFromCloud();
                    }
                }),
                new DialogAction(activity.getString(R.string.cancel), new Runnable() {
                    @Override
                    public void run() {

                    }
                }));
    }

    public static void confirmCloudSave(final Activity activity, int localXp, int localItems, String desc, long saveTime, String deviceName) {
        displayAlertDialog(activity, activity.getString(R.string.cloud_save), String.format(Locale.ENGLISH, activity.getString(R.string.google_cloud_save_confirm),
                desc,
                DateHelper.timestampToDateTime(saveTime),
                deviceName,
                BuildConfig.VERSION_NAME,
                LevelHelper.convertXpToLevel(localXp),
                localXp,
                localItems),
                new DialogAction(activity.getString(R.string.save), new Runnable() {
                    @Override
                    public void run() {
                        GooglePlayHelper.forceSaveToCloud();
                    }
                }),
                new DialogAction(activity.getString(R.string.cancel), new Runnable() {
                    @Override
                    public void run() {

                    }
                }));
    }
}
