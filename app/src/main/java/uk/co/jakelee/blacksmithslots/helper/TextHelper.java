package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.content.res.Resources;

public class TextHelper {
    private final Resources resources;
    private static TextHelper thInstance = null;
    private final String packageName;

    public TextHelper(Context context) {
        this.resources = context.getResources();
        this.packageName = context.getPackageName();
    }

    public static TextHelper getInstance(Context ctx) {
        if (thInstance == null) {
            thInstance = new TextHelper(ctx.getApplicationContext());
        }
        return thInstance;
    }

    public String getText(String identifier) {
        return (String)resources.getText(resources.getIdentifier(identifier, "string", packageName));
    }
}
