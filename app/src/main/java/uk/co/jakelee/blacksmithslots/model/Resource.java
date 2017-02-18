package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.TextHelper;

public class Resource extends SugarRecord {
    private int resourceId;

    public Resource() {
    }

    public Resource(int resourceId) {
        this.resourceId = resourceId;
    }

    public static Resource get(int resourceId) {
        List<Resource> resources = Select.from(Resource.class).where(
                Condition.prop("resource_id").eq(resourceId)).list();
        return resources.size() > 0 ? resources.get(0) : null;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("resource_" + resourceId);
    }

    public static String getName(Context context, int resourceId) {
        return TextHelper.getInstance(context).getText("resource_" + resourceId);
    }
}
