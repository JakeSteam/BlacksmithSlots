package uk.co.jakelee.blacksmithslots.components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Trophy;

public class TrophyGridAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<Trophy> trophies;

    public TrophyGridAdapter(Activity activity) {
        activity = activity;
        layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        trophies = Trophy.listAll(Trophy.class);
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

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View trophyTile, ViewGroup parent) {
        Trophy trophy = trophies.get(position);
        if (trophyTile == null) {
            trophyTile = layoutInflater.inflate(R.layout.custom_trophy_tile, null).findViewById(R.id.trophyTile);
        }

        trophyTile.setTag(trophy.getId());

        ImageView itemImage = (ImageView)trophyTile.findViewById(R.id.itemImage);
        itemImage.setImageResource(getTrophyResource(trophy));
        if (trophy.isAchieved()) {
            itemImage.getDrawable().clearColorFilter();
        } else {
            itemImage.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }

        ((TextView) trophyTile.findViewById(R.id.itemName)).setText(getTrophyName(trophy));

        return trophyTile;
    }

    private int getTrophyResource(Trophy trophy) {
        String itemFile = DisplayHelper.getItemImageFile(trophy.getItemTier().value, trophy.getItemType().value);
        return DisplayHelper.getDrawableId(activity, itemFile);
    }

    private String getTrophyName(Trophy trophy) {
        return Inventory.getName(activity, trophy.getItemTier(), trophy.getItemType());
    }
}
