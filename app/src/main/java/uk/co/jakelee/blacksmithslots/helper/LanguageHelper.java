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

    public static void changeLanguage(Context context, int language) {
        updateLanguage(context, getLocaleById(language));
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

    private static String getLocaleById(int id) {
        switch (id) {
            case 1: return "en";
            case 2: return "zh";
            case 3: return "nl";
            case 4: return "fr";
            case 5: return "de";
            case 6: return "ko";
            case 7: return "pt";
            case 8: return "ru";
            case 9: return "es";
            default: return "";
        }
    }

    public static String getLanguageById(Context context, int id) {
        return TextHelper.getInstance(context).getText("language_" + id);
    }

    public static String getFlagById(int id) {
        switch (id) {
            case 1: return new String(Character.toChars(0x1F1EC)) + new String(Character.toChars(0x1F1E7));
            case 2: return new String(Character.toChars(0x1F1E8)) + new String(Character.toChars(0x1F1F3));
            case 3: return new String(Character.toChars(0x1F1F3)) + new String(Character.toChars(0x1F1F1));
            case 4: return new String(Character.toChars(0x1F1EB)) + new String(Character.toChars(0x1F1F7));
            case 5: return new String(Character.toChars(0x1F1E9)) + new String(Character.toChars(0x1F1EA));
            case 6: return new String(Character.toChars(0x1F1F0)) + new String(Character.toChars(0x1F1F7));
            case 7: return new String(Character.toChars(0x1F1E7)) + new String(Character.toChars(0x1F1F7));
            case 8: return new String(Character.toChars(0x1F1F7)) + new String(Character.toChars(0x1F1FA));
            case 9: return new String(Character.toChars(0x1F1EA)) + new String(Character.toChars(0x1F1F8));
            default: return "";
        }
    }
}
