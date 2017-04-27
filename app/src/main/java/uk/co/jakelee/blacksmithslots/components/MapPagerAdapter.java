package uk.co.jakelee.blacksmithslots.components;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Locale;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.TaskHelper;
import uk.co.jakelee.blacksmithslots.helper.TextHelper;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class MapPagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    public final static int[] townLayouts = {R.layout.custom_map_1, R.layout.custom_map_2, R.layout.custom_map_3};

    public MapPagerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        boolean isUnlocked = firstSlotUnlocked == null || firstSlotUnlocked.getRequiredSlot() == 1 || !TaskHelper.isSlotLocked(firstSlotUnlocked.getSlotId());
        itemView.findViewById(R.id.lockedMapBlocker).setVisibility(isUnlocked ? View.GONE : View.VISIBLE);
        if (!isUnlocked) {
            Slot requiredSlot = Slot.get(firstSlotUnlocked.getRequiredSlot());
            ((TextView)itemView.findViewById(R.id.lockedMapText)).setText(String.format(Locale.ENGLISH,
                    itemView.getContext().getString(R.string.alert_map_locked),
                    TextHelper.getInstance(container.getContext()).getText(DisplayHelper.getMapString(position + 1)),
                    requiredSlot.getName(container.getContext())));
        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((PercentRelativeLayout) object);
    }
}
