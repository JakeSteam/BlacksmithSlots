package uk.co.jakelee.blacksmithslots.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.adapters.AbstractWheelAdapter;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;

class SlotAdapter extends AbstractWheelAdapter {
    // Image size
    private int IMAGE_WIDTH = 120;
    private int IMAGE_HEIGHT = 120;

    // Slot machine symbols
    private final List<ItemBundle> rewards;

    // Cached images
    private final List<SoftReference<Bitmap>> images;

    // Layout inflater
    private final Context context;

    /**
     * Constructor
     */
    public SlotAdapter(Context context, DisplayMetrics displayMetrics, List<ItemBundle> rewards) {
        this.context = context;
        this.rewards = rewards;
        this.IMAGE_WIDTH = (int)Math.ceil(displayMetrics.heightPixels / 7.5);
        this.IMAGE_HEIGHT = (int)Math.ceil(displayMetrics.heightPixels / 7.5);
        images = new ArrayList<>(rewards.size());
        for (ItemBundle reward : rewards) {
            images.add(new SoftReference<>(loadImage(reward)));
        }
    }

    private Bitmap loadImage(ItemBundle reward) {
        try {
            Resources resources = context.getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(resources, resources.getIdentifier(DisplayHelper.getItemImageFile(reward), "drawable", context.getPackageName()));
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(resources, resources.getIdentifier(DisplayHelper.getItemImageFile(reward, true), "drawable", context.getPackageName()));
            }
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
            bitmap.recycle();
            return scaled;
        } catch (OutOfMemoryError e) {
            AlertHelper.error((Activity)context, "No memory available to resize image! If this happens repeatedly please contact support.", false);
            e.printStackTrace();
            return null;
        }
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
        img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        SoftReference<Bitmap> bitmapRef = images.get(index);
        Bitmap bitmap = bitmapRef.get();
        if (bitmap == null) {
            bitmap = loadImage(rewards.get(index));
            images.set(index, new SoftReference<>(bitmap));
        }
        img.setImageBitmap(bitmap);

        return img;
    }
}
