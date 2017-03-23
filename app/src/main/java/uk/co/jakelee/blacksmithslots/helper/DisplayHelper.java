package uk.co.jakelee.blacksmithslots.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Item;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;

public class DisplayHelper {
    public static String getItemTierString(int tier) {
        return "tier_" + tier;
    }

    public static String getSettingGroupString(int group) {
        return "settinggroup_" + group;
    }

    public static String getSettingString(int setting) {
        return "setting_" + setting;
    }

    public static String getItemTypeString(int type) {
        return "type_" + type;
    }

    public static String getItemImageFile(ItemBundle result, boolean useDefault) {
        return getItemImageFile(result.getTier().value, result.getType().value, 1);
    }

    public static String getItemImageFile(ItemBundle result) {
        return getItemImageFile(result.getTier().value, result.getType().value, result.getQuantity());
    }

    public static String getPersonImageFile(int person) {
        return "person_" + person;
    }

    public static String getItemImageFile(int tier, int type) {
        return getItemImageFile(tier, type, 1);
    }

    public static String getItemImageFile(int tier, int type, int quantity) {
        return "item_" + tier + "_" + type + (quantity > 1 ? "_" + quantity : "");
    }

    public static ImageView createImageView(Activity context, String idName, int width, int height) {
        return createImageView(context, idName, width, height, null);
    }

    public static ImageView createImageView(final Activity context, String idName, int width, int height, final String textOnClick) {
        ImageView image = new ImageView(context);
        int drawableId = context.getResources().getIdentifier(idName, "drawable", context.getPackageName());
        int adjustedWidth = convertDpToPixel(context, width);
        int adjustedHeight = convertDpToPixel(context, height);

        try {
            Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(), drawableId);
            Bitmap resizedImage = Bitmap.createScaledBitmap(rawImage, adjustedWidth, adjustedHeight, false);
            Drawable imageResource = new BitmapDrawable(context.getResources(), resizedImage);

            image.setImageDrawable(imageResource);
            if (textOnClick != null) {
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertHelper.info(context, textOnClick, false);
                    }
                });
            }
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

    public static String bundlesToString(Context context, List<ItemBundle> bundles) {
        return bundlesToString(context, bundles, 1, true);
    }

    public static String bundlesToString(Context context, List<ItemBundle> bundles, int multiplier) {
        return bundlesToString(context, bundles, multiplier, true);
    }

    public static String bundlesToString(Context context, List<ItemBundle> bundles, int multiplier, boolean includeQuantity) {
        StringBuilder itemText = new StringBuilder();
        for (ItemBundle itemBundle : bundles) {
            if (includeQuantity) {
                itemText.append(itemBundle.getQuantity() * multiplier);
                itemText.append("x ");
            }
            itemText.append(Item.getName(context, itemBundle.getTier(), itemBundle.getType()));
            itemText.append(", ");
        }

        String itemString = itemText.toString();
        return itemString.length() > 0 ? itemString.substring(0, itemString.length() - 2) : "";
    }
}
