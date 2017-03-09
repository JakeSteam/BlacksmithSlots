package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;

@Table(name = "g")
public class Task extends SugarRecord {
    @Column(name = "a")
    private int slotId;

    @Column(name = "b")
    private int position;

    @Column(name = "c")
    private int statistic;

    @Column(name = "d")
    private int tier;

    @Column(name = "e")
    private int type;

    @Column(name = "f")
    private int target;

    @Column(name = "g")
    private int remaining;

    @Column(name = "h")
    private long started;

    @Column(name = "i")
    private long completed;

    public Task() {
    }

    public Task(int slotId, int position, Enums.Statistic statistic, int target) {
        this.slotId = slotId;
        this.position = position;
        this.statistic = statistic.value;
        this.target = target;
        this.remaining = target;
        this.started = 0;
        this.completed = 0;
    }

    public Task(int slotId, int position, Enums.Tier tier, Enums.Type type, int target) {
        this.slotId = slotId;
        this.position = position;
        this.tier = tier.value;
        this.type = type.value;
        this.target = target;
        this.remaining = target;
        this.started = 0;
        this.completed = 0;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Enums.Statistic getStatistic() {
        return Enums.Statistic.get(statistic);
    }

    public void setStatistic(Enums.Statistic statistic) {
        this.statistic = statistic.value;
    }

    public Enums.Tier getTier() {
        return Enums.Tier.get(tier);
    }

    public void setTier(Enums.Tier tier) {
        this.tier = tier.value;
    }

    public Enums.Type getType() {
        return Enums.Type.get(type);
    }

    public void setType(Enums.Type type) {
        this.type = type.value;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public long getStarted() {
        return started;
    }

    public void setStarted(long started) {
        this.started = started;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }

    public String getText(Context context) {
        return TextHelper.getInstance(context).getText("task_" + slotId + "_" + position + "_text");
    }

    public String toString(Context context) {
        if (statistic > 0) {
            return Statistic.getName(context, statistic) + ": " + (target-remaining) + "/" + target;
        } else {
            return Item.getName(context, tier, type) + ": " + (target-remaining) + "/" + target;
        }
    }

    public boolean isCompleteable() {
        return remaining == 0 && completed == 0;
    }

    public boolean itemsCanBeSubmitted() {
        return statistic == 0
            && remaining > 0
            && Inventory.getInventory(tier, type).getQuantity() > 0;
    }

    public void submitItems() {
        Inventory inventory = Inventory.getInventory(tier, type);

        if (remaining > inventory.getQuantity()) {
            remaining = remaining - inventory.getQuantity();
            inventory.setQuantity(0);
        } else {
            inventory.setQuantity(inventory.getQuantity() - remaining);
            remaining = 0;
        }
        inventory.save();
        save();
    }
}
