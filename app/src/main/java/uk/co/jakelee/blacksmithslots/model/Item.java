package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;

@Table(name = "b")
public class Item extends SugarRecord {
    @Column(name = "a")
    private int tier;

    @Column(name = "b")
    private int type;

    public Item() {
    }

    public Item(Enums.Tier tier, Enums.Type type) {
        this.tier = tier.value;
        this.type = type.value;
    }

    public static Item get(Enums.Tier tier, Enums.Type type) {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("a").eq(tier.value),
                Condition.prop("b").eq(type.value)
        ).list();
        return items.size() > 0 ? items.get(0) : null;
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

    public String getName(Context context) {
        return getName(context, tier, type);
    }

    public static String getName(Context context, Enums.Tier tier, Enums.Type type) {
        return getName(context, tier.value, type.value);
    }

    public static String getName(Context context, int tier, int type) {
        return TextHelper.getInstance(context).getText(DisplayHelper.getItemTierString(tier)) +
                " " +
                TextHelper.getInstance(context).getText(DisplayHelper.getItemTypeString(type));;
    }
}
