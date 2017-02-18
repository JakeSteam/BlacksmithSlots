package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmithslots.helper.TextHelper;

public class Resource extends SugarRecord {
    private int resourceId;

    public Resource() {
    }

    public Resource(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("resource_" + resourceId);
    }

    public static String getName(Context context, int resourceId) {
        return TextHelper.getInstance(context).getText("resource_" + resourceId);
    }
}
