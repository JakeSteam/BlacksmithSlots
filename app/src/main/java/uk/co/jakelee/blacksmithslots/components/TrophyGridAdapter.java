package uk.co.jakelee.blacksmithslots.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Upgrade;

public class TrophyGridAdapter extends BaseAdapter {
    private final Activity activity;
    private final LayoutInflater layoutInflater;
    private final List<Upgrade> trophies;

    public TrophyGridAdapter(Activity activity, List<Upgrade> trophies) {
        this.activity = activity;
        layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.trophies = trophies;
    }

    public int getCount() {
        return trophies.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View trophyTile, ViewGroup parent) {
        Upgrade upgrade = trophies.get(position);
        if (trophyTile == null) {
            trophyTile = layoutInflater.inflate(R.layout.custom_trophy_tile, null).findViewById(R.id.trophyTile);
        }

        trophyTile.setTag((int)(long) upgrade.getId());

        ImageView itemImage = trophyTile.findViewById(R.id.itemImage);
        itemImage.setImageResource(getTrophyResource(activity, upgrade));
        if (upgrade.isAchieved()) {
            itemImage.getDrawable().clearColorFilter();
        } else {
            itemImage.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }

        ((TextView) trophyTile.findViewById(R.id.itemName)).setText(getTrophyName(activity, upgrade));

        return trophyTile;
    }

    public static int getTrophyResource(Activity activity, Upgrade upgrade) {
        String itemFile = DisplayHelper.getItemImageFile(upgrade.getItemTier().value, upgrade.getItemType().value);
        return DisplayHelper.getDrawableId(activity, itemFile);
    }

    public static String getTrophyName(Activity activity, Upgrade upgrade) {
        return Inventory.getName(activity, upgrade.getItemTier(), upgrade.getItemType());
    }
}
