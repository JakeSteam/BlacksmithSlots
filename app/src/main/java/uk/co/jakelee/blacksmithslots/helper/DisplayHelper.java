package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import uk.co.jakelee.blacksmithslots.constructs.SlotResult;

public class DisplayHelper {
    public static String getItemNameLookupString(Enums.Tier tier, Enums.Type type) {
        return "item_" + tier.value + "_" + type.value;
    }

    public static String getItemImageFile(SlotResult result, boolean useDefault) {
        return getItemImageFile(result.getResourceTier(), result.getResourceType(), 1);
    }

    public static String getItemImageFile(SlotResult result) {
        return getItemImageFile(result.getResourceTier(), result.getResourceType(), result.getResourceMultiplier());
    }

    public static String getItemImageFile(Enums.Tier tier, Enums.Type type) {
        return getItemImageFile(tier, type, 1);
    }

    public static String getItemImageFile(Enums.Tier tier, Enums.Type type, int quantity) {
        return "item_" + tier.value + "_" + type.value + (quantity > 1 ? "_" + quantity : "");
    }

    public static ImageView createImageView(Context context, String idName, int width, int height) {
        ImageView image = new ImageView(context);
        int drawableId = context.getResources().getIdentifier(idName, "drawable", context.getPackageName());
        int adjustedWidth = convertDpToPixel(context, width);
        int adjustedHeight = convertDpToPixel(context, height);

        try {
            Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(), drawableId);
            Bitmap resizedImage = Bitmap.createScaledBitmap(rawImage, adjustedWidth, adjustedHeight, false);
            Drawable imageResource = new BitmapDrawable(context.getResources(), resizedImage);

            image.setImageDrawable(imageResource);
        } catch (OutOfMemoryError e) {
        }

        return image;
    }

    public static int convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
}
