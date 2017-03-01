package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;

public class Town extends SugarRecord {
    private int townId;
    private int unlockedBy;
    private long unlockedOn;

    public Town() {
    }

    public Town(int townId, int unlockedBy, long unlockedOn) {
        this.townId = townId;
        this.unlockedBy = unlockedBy;
        this.unlockedOn = unlockedOn;
    }

    public int getTownId() {
        return townId;
    }

    public void setTownId(int townId) {
        this.townId = townId;
    }

    public int getUnlockedBy() {
        return unlockedBy;
    }

    public void setUnlockedBy(int unlockedBy) {
        this.unlockedBy = unlockedBy;
    }

    public long getUnlockedOn() {
        return unlockedOn;
    }

    public void setUnlockedOn(long unlockedOn) {
        this.unlockedOn = unlockedOn;
    }
}
