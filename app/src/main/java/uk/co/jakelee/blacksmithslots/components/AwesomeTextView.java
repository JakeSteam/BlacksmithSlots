package uk.co.jakelee.blacksmithslots.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class AwesomeTextView extends AppCompatTextView {
    private static Typeface mTypeface;

    public AwesomeTextView(final Context context) {
        this(context, null);
    }

    public AwesomeTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AwesomeTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/fontello.ttf");
        }
        setTypeface(mTypeface);
    }

}
