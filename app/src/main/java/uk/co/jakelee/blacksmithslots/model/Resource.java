package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;

public class Resource extends SugarRecord {
    private int resourceId;

    public Resource() {
    }

    public Resource(int resourceId) {
        this.resourceId = resourceId;
    }
}
