package uk.co.jakelee.blacksmithslots.helper;

import android.app.AlertDialog;
import android.content.DialogInterface;

import uk.co.jakelee.blacksmithslots.main.SlotActivity;

public class AlertDialogHelper {
    public static void autospin(final SlotActivity activity, final SlotHelper slotHelper) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage("How many autospins?");

        alertDialog.setPositiveButton("10", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                slotHelper.autospin(10);
            }
        });

        alertDialog.setNegativeButton("5", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                slotHelper.autospin(5);
            }
        });

        alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}
