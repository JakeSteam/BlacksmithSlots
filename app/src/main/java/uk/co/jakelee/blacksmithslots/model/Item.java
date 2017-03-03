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
    private Enums.Tier tier;
    private Enums.Type type;

    public Item() {
    }

    public Item(Enums.Tier tier, Enums.Type type) {
        this.tier = tier;
        this.type = type;
    }

    public static Item get(Enums.Tier tier, Enums.Type type) {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("tier").eq(tier),
                Condition.prop("type").eq(type)
        ).list();
        return items.size() > 0 ? items.get(0) : null;
    }

    public Enums.Tier getTier() {
        return tier;
    }

    public void setTier(Enums.Tier tier) {
        this.tier = tier;
    }

    public Enums.Type getType() {
        return type;
    }

    public void setType(Enums.Type type) {
        this.type = type;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText(DisplayHelper.getItemNameLookupString(tier, type));
    }

    public static String getName(Context context, Enums.Tier tier, Enums.Type type) {
        return TextHelper.getInstance(context).getText(DisplayHelper.getItemNameLookupString(tier, type));
    }
}
