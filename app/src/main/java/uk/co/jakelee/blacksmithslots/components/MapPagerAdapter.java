package uk.co.jakelee.blacksmithslots.components;

import android.app.Activity;
import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;
import java.util.Locale;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.TaskHelper;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class MapPagerAdapter extends PagerAdapter {
    private LayoutInflater mLayoutInflater;
    public final static int[] townLayouts = {R.layout.custom_map_1, R.layout.custom_map_2, R.layout.custom_map_3,
            R.layout.custom_map_4, R.layout.custom_map_5, R.layout.custom_map_6,
            R.layout.custom_map_7, R.layout.custom_map_8, R.layout.custom_map_9,
            R.layout.custom_map_10, R.layout.custom_map_11, R.layout.custom_map_12,
            R.layout.custom_map_13, R.layout.custom_map_14, R.layout.custom_map_15,
            R.layout.custom_map_16, R.layout.custom_map_17, R.layout.custom_map_18,
            R.layout.custom_map_19, R.layout.custom_map_20};

    public MapPagerAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return townLayouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = mLayoutInflater.inflate(townLayouts[position], container, false);

        // The lowest required slot for this map
        Slot firstSlotUnlocked = Select.from(Slot.class).where(Condition.prop("m").eq(position + 1)).orderBy("k ASC").first();
        boolean isUnlocked = Constants.DEBUG_UNLOCK_ALL ? true : (position == 0 || firstSlotUnlocked == null || !TaskHelper.isSlotLocked(firstSlotUnlocked.getRequiredSlot()));
        itemView.findViewById(R.id.lockedMapBlocker).setVisibility(isUnlocked ? View.GONE : View.VISIBLE);
        if (!isUnlocked) {
            Slot requiredSlot = Slot.get(firstSlotUnlocked.getRequiredSlot());
            if (requiredSlot != null) {
                ((TextView) itemView.findViewById(R.id.lockedMapText)).setText(String.format(Locale.ENGLISH,
                        itemView.getContext().getString(R.string.alert_map_locked),
                        TextHelper.getInstance(container.getContext()).getText(DisplayHelper.getMapString(position + 1)),
                        requiredSlot.getName(container.getContext())));
            }
        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((PercentRelativeLayout) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public void populateItemContainer(int id, List<ItemBundle> items, Activity activity) {
        LinearLayout rewardContainer = (LinearLayout) activity.findViewById(id);
        rewardContainer.removeAllViews();
        for (ItemBundle itemBundle : items) {
            rewardContainer.addView(DisplayHelper.createImageView(activity,
                    DisplayHelper.getItemImageFile(itemBundle.getTier().value, itemBundle.getType().value, itemBundle.getQuantity()),
                    30,
                    30,
                    itemBundle.getQuantity() + "x " + Inventory.getName(activity, itemBundle.getTier(), itemBundle.getType())));
        }
    }
}
