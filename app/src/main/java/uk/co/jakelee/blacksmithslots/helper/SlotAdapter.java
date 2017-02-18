package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.adapters.AbstractWheelAdapter;
import uk.co.jakelee.blacksmithslots.constructs.SlotResult;

public class SlotAdapter extends AbstractWheelAdapter {
    // Image size
    final int IMAGE_WIDTH = 160;
    final int IMAGE_HEIGHT = 160;

    // Slot machine symbols
    private final List<SlotResult> rewards;

    // Cached images
    private List<SoftReference<Bitmap>> images;

    // Layout inflater
    private Context context;

    /**
     * Constructor
     */
    public SlotAdapter(Context context, List<SlotResult> rewards) {
        this.context = context;
        this.rewards = rewards;
        images = new ArrayList<>(rewards.size());
        for (SlotResult reward : rewards) {
            images.add(new SoftReference<>(loadImage(reward.getResourceId())));
        }
    }

    private Bitmap loadImage(int itemId) {
        Resources resources = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, resources.getIdentifier("item_" + itemId, "drawable", context.getPackageName()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
        bitmap.recycle();
        return scaled;
    }

    @Override
    public int getItemsCount() {
        return rewards.size();
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        ImageView img;
        if (cachedView != null) {
            img = (ImageView) cachedView;
        } else {
            img = new ImageView(context);
        }
        img.setLayoutParams(new ViewGroup.LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT));
        SoftReference<Bitmap> bitmapRef = images.get(index);
        Bitmap bitmap = bitmapRef.get();
        if (bitmap == null) {
            bitmap = loadImage(rewards.get(index).getResourceId());
            images.set(index, new SoftReference<>(bitmap));
        }
        img.setImageBitmap(bitmap);

        return img;
    }
}
