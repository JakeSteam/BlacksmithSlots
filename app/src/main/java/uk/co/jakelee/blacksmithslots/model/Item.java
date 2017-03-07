package uk.co.jakelee.blacksmithslots.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;

public class Item extends SugarRecord {
    private int tier;
    private int type;

    public Item() {
    }

    public Item(Enums.Tier tier, Enums.Type type) {
        this.tier = tier.value;
        this.type = type.value;
    }

    public static Item get(Enums.Tier tier, Enums.Type type) {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("tier").eq(tier.value),
                Condition.prop("type").eq(type.value)
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
        return TextHelper.getInstance(context).getText(DisplayHelper.getItemNameLookupString(tier, type));
    }

    public static String getName(Context context, int tier, int type) {
        return TextHelper.getInstance(context).getText(DisplayHelper.getItemNameLookupString(tier, type));
    }
}
