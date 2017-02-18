package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.adapters.AbstractWheelAdapter;

public class SlotAdapter extends AbstractWheelAdapter {
    // Image size
    final int IMAGE_WIDTH = 160;
    final int IMAGE_HEIGHT = 160;

    // Slot machine symbols
    private final int items[];

    // Cached images
    private List<SoftReference<Bitmap>> images;

    // Layout inflater
    private Context context;

    /**
     * Constructor
     */
    public SlotAdapter(Context context, int[] items) {
        this.context = context;
        this.items = items;
        images = new ArrayList<>(items.length);
        for (int id : items) {
            images.add(new SoftReference<>(loadImage(id)));
        }
    }

    private Bitmap loadImage(int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
        bitmap.recycle();
        return scaled;
    }

    @Override
    public int getItemsCount() {
        return items.length;
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
            bitmap = loadImage(items[index]);
            images.set(index, new SoftReference<>(bitmap));
        }
        img.setImageBitmap(bitmap);

        return img;
    }
}
