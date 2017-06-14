package uk.co.jakelee.blacksmithslots.helper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.model.Inventory;
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

    public static String getMapString(int map) {
        return "map_" + map;
    }

    public static String getHintString(int hint) {
        return "hint_" + hint;
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

    public static String getOrientationString(int orientation) {
        return "orientation_" + orientation;
    }

    public static String getMapBackgroundImageFile(int map) {
        return "background_" + map;
    }

    public static ImageView createImageView(Activity context, String idName, int width, int height) {
        return createImageView(context, idName, width, height, null);
    }

    public static ImageView createImageView(final Activity context, String idName, int width, int height, final String textOnClick) {
        ImageView image = new ImageView(context);
        int drawableId = getDrawableId(context, idName);
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
        } catch (OutOfMemoryError | NullPointerException e) {
            Log.d("LOG", e.toString());
        }

        return image;
    }

    public static int getDrawableId(Activity context, String idName) {
        int drawableId = context.getResources().getIdentifier(idName, "drawable", context.getPackageName());
        if (drawableId == 0) {
            drawableId = context.getResources().getIdentifier(idName.substring(0, idName.lastIndexOf("_")), "drawable", context.getPackageName());
        }
        return drawableId;
    }

    public static int convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public static String bundlesToString(Context context, List<ItemBundle> bundles) {
        return bundlesToString(context, bundles, 1, true, "", ", ");
    }

    public static String bundlesToString(Context context, List<ItemBundle> bundles, int multiplier) {
        return bundlesToString(context, bundles, multiplier, true, "", ", ");
    }

    public static String bundlesToString(Context context, List<ItemBundle> bundles, int multiplier, boolean includeQuantity) {
        return bundlesToString(context, bundles, multiplier, includeQuantity, "", ", ");
    }

    public static String bundlesToString(Context context, List<ItemBundle> bundles, int multiplier, boolean includeQuantity, String itemPrefix, String itemDivider) {
        StringBuilder itemText = new StringBuilder();
        for (ItemBundle itemBundle : bundles) {
            if (includeQuantity) {
                itemText.append(itemPrefix);
                itemText.append(itemBundle.getQuantity() * multiplier);
                itemText.append("x ");
            }
            itemText.append(Inventory.getName(context, itemBundle.getTier(), itemBundle.getType()));
            itemText.append(itemDivider);
        }

        String itemString = itemText.toString();
        return itemString.length() > 0 ? itemDivider.equals(", ") ? itemString.substring(0, itemString.length() - 2) : itemString : "";
    }

    public static void populateItemRows(Activity activity, int id, LayoutInflater inflater, Picasso picasso, ViewGroup.LayoutParams params, List<Inventory> items, boolean showOutOfStock) {
        TableLayout layout = (TableLayout)activity.findViewById(id);
        layout.removeAllViews();
        for (Inventory item : items) {
            if (!showOutOfStock && item.getQuantity() <= 0) {
                continue;
            }

            View inflatedView = inflater.inflate(R.layout.custom_resource_info, null);
            TableRow itemRow = (TableRow) inflatedView.findViewById(R.id.itemRow);

            picasso.load(item.getDrawableId(activity)).into((ImageView)itemRow.findViewById(R.id.itemImage));
            ((TextView)itemRow.findViewById(R.id.itemInfo)).setText(item.getQuantity() + "x " + item.getName(activity));

            layout.addView(itemRow, params);
        }
    }

    public static String centsToDollars(int cents) {
        return "$" + (cents / 100d);
    }

    public static int getMinTypeForTier(int tier) {
        switch (tier) {
            case 0: return 20;
            case 1: return 1;
            case 2: return 1;
            case 3: return 1;
            case 4: return 1;
            case 5: return 1;
            case 6: return 35;
            case 7: return 35;
            case 10: return 21;
        }
        return 1;
    }

    public static int getMaxTypeForTier(int tier) {
        switch (tier) {
            case 0: return 53;
            case 1: return 19;
            case 2: return 19;
            case 3: return 19;
            case 4: return 19;
            case 5: return 2;
            case 6: return 39;
            case 7: return 40;
            case 10: return 33;
        }
        return 1;
    }

    public static TableRow getTableRow(LayoutInflater inflater, int name, int value) {
        return getTableRow(inflater, inflater.getContext().getString(name), inflater.getContext().getString(value), 0);
    }

    public static TableRow getTableRow(LayoutInflater inflater, String name, String value) {
        return getTableRow(inflater, name, value, 0);
    }

    public static TableRow getTableRow(LayoutInflater inflater, String name, String value, int valueColour) {
        TableRow tableRow = (TableRow) inflater.inflate(R.layout.custom_data_row, null).findViewById(R.id.dataRow);
        ((TextView) tableRow.findViewById(R.id.dataName)).setText(name);
        ((TextView) tableRow.findViewById(R.id.dataValue)).setText(value);
        if (valueColour != 0) {
            ((TextView) tableRow.findViewById(R.id.dataValue)).setTextColor(valueColour);
        }
        return tableRow;
    }
}
