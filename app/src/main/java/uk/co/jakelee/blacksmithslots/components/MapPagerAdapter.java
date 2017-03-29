package uk.co.jakelee.blacksmithslots.components;

import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.jakelee.blacksmithslots.R;

public class MapPagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    private int[] townLayouts = {R.layout.custom_map_1, R.layout.custom_map_2, R.layout.custom_map_3};

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
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((PercentRelativeLayout) object);
    }
}
