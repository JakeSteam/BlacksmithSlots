package uk.co.jakelee.blacksmithslots.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontTextView extends TextView {
    private static Typeface mTypeface;

    public FontTextView(final Context context) {
        this(context, null);
    }

    public FontTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/KenPixel Mini.ttf");
        }
        setTypeface(mTypeface);
    }
}
