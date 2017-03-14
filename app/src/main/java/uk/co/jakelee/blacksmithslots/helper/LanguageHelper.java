package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Locale;

public class LanguageHelper {
    public static void updateLanguage(Context ctx)
    {
        String lang = PreferenceManager.getDefaultSharedPreferences(ctx).getString("locale", "");
        updateLanguage(ctx, lang);
    }

    public static void changeLanguage(Context context, Enums.Language language) {
        if (language == Enums.Language.English) {
            updateLanguage(context, "en");
        } else if (language == Enums.Language.French) {
            updateLanguage(context, "fr");
        }
    }

    private static void updateLanguage(Context ctx, String lang)
    {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString("locale", lang).apply();
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang)) {
            cfg.locale = new Locale(lang);
        } else {
            cfg.locale = Locale.getDefault();
        }

        ctx.getResources().updateConfiguration(cfg, null);
    }
}
