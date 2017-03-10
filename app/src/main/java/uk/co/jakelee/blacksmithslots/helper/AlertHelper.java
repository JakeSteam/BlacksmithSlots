package uk.co.jakelee.blacksmithslots.helper;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.model.Message;

public class AlertHelper {
    public static void success(Activity activity, int textId, boolean log) {
        success(activity, activity.getString(textId), log);
    }

    public static void success(Activity activity, String text, boolean log) {
        display(activity, text, R.layout.custom_crouton_success, log);
    }

    public static void info(Activity activity, int textId, boolean log) {
        info(activity, activity.getString(textId), log);
    }

    public static void info(Activity activity, String text, boolean log) {
        display(activity, text, R.layout.custom_crouton_info, log);
    }

    public static void error(Activity activity, int textId, boolean log) {
        error(activity, activity.getString(textId), log);
    }

    public static void error(Activity activity, String text, boolean log) {
        display(activity, text, R.layout.custom_crouton_error, log);
    }

    private static void display(Activity activity, String text, int layoutId, boolean log) {
        Crouton.cancelAllCroutons();
        View layout = activity.getLayoutInflater().inflate(layoutId, null);
        ((TextView) layout.findViewById(R.id.text)).setText(text);
        Crouton.make(activity, layout, (ViewGroup) activity.findViewById(R.id.croutonview)).show();

        if(log) {
            Message.log(text);
        }
    }
}
