package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmithslots.helper.Enums;

public class Event extends SugarRecord {
    private Enums.Event event;
    private int count;

    public Event() {
    }

    public Event(Enums.Event event) {
        this.event = event;
        this.count = 0;
    }

    public Event(Enums.Event event, int count) {
        this.event = event;
        this.count = count;
    }

    public Enums.Event getEvent() {
        return event;
    }

    public void setEvent(Enums.Event event) {
        this.event = event;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
