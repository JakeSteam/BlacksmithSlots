package uk.co.jakelee.blacksmithslots.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;
import android.widget.TableRow;

import java.util.Locale;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.SettingsActivity;
import uk.co.jakelee.blacksmithslots.main.SplashScreenActivity;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Statistic;

import static uk.co.jakelee.blacksmithslots.model.Setting.get;

public class Listeners {

    @NonNull
    public static View.OnClickListener openLinkListener(final SettingsActivity settingsActivity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = (String)view.getTag();
                settingsActivity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url)));
            }
        };
    }

    @NonNull
    public static View.OnClickListener logoutButton(final Context context, final SettingsActivity settingsActivity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GooglePlayHelper.IsConnected()) {
                    GooglePlayHelper.disconnect();
                    settingsActivity.populateSettings();
                    AlertHelper.success(settingsActivity, context.getString(R.string.alert_logged_out), true);
                }
            }
        };
    }

    @NonNull
    public static View.OnClickListener splashScreenListener(final SettingsActivity settingsActivity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsActivity.startActivity(new Intent(view.getContext(), SplashScreenActivity.class)
                        .putExtra("replayingIntro", true)
                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            }
        };
    }

    @NonNull
    public static View.OnClickListener supportCodeListener(final Activity activity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialogHelper.enterSupportCode(activity, activity);
            }
        };
    }

    @NonNull
    public static View.OnClickListener emailListener(final SettingsActivity settingsActivity, final Activity activity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","blacksmithslots@jakelee.co.uk", null));
                settingsActivity.startActivity(Intent.createChooser(emailIntent, settingsActivity.getString(R.string.send_an_email)));
                AlertHelper.info(activity, settingsActivity.getString(R.string.alert_email_fallback), true);
            }
        };
    }

    @NonNull
    public static View.OnClickListener importSave(final SettingsActivity settingsActivity, final Activity activity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = StorageHelper.loadLocalSave(activity, true);
                if (result.startsWith("BlacksmithSlots")) {
                    AlertHelper.success(activity, R.string.local_save_loaded, true);
                } else if (!result.equals("")) {
                    AlertHelper.error(activity, String.format(Locale.ENGLISH, settingsActivity.getString(R.string.error_failed_import), result), false);
                }
            }
        };
    }

    @NonNull
    public static View.OnClickListener exportSave(final SettingsActivity settingsActivity, final Activity activity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = StorageHelper.saveLocalSave(activity);
                if (result.startsWith("BlacksmithSlots")) {
                    AlertHelper.success(activity, String.format(Locale.ENGLISH, settingsActivity.getString(R.string.local_save_saved), result), true);
                } else {
                    AlertHelper.error(activity, String.format(Locale.ENGLISH, settingsActivity.getString(R.string.error_failed_export), result), false);
                }
            }
        };
    }

    @NonNull
    public static View.OnClickListener toggleBoolean(final SettingsActivity settingsActivity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Setting setting = get((Integer)((TableRow)view.getParent()).getTag());
                setting.setBooleanValue(!setting.getBooleanValue());
                setting.save();
                settingsActivity.onResume();

                AlertHelper.success(settingsActivity, String.format(Locale.ENGLISH, settingsActivity.getString(setting.getBooleanValue() ? R.string.setting_turned_on : R.string.setting_turned_off), setting.getName(settingsActivity)), true);
            }
        };
    }

    @NonNull
    public static View.OnClickListener importPbSave(final SettingsActivity settingsActivity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageHelper.confirmStoragePermissions(settingsActivity);
                Pair<Integer, Integer> pbData = StorageHelper.getSave(true);
                if (pbData != null) {

                    AlertHelper.success(settingsActivity, IncomeHelper.claimImportBonus(settingsActivity, pbData.first), false);

                    Setting haveImported = Setting.get(Enums.Setting.SaveImported);
                    haveImported.setBooleanValue(true);
                    haveImported.save();

                    Statistic haveImportedStat = Statistic.get(Enums.Statistic.SaveImported);
                    haveImportedStat.setBoolValue(true);
                    haveImportedStat.save();

                    Statistic.add(Enums.Statistic.SaveImported);

                    settingsActivity.populateSettings();
                } else {
                    AlertHelper.error(settingsActivity, R.string.error_failed_pb_import, false);
                }
            }
        };
    }

    @NonNull
    public static View.OnClickListener prestigeAccount(final SettingsActivity settingsActivity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PrestigeHelper.canPrestige()) {
                    AlertDialogHelper.confirmPrestige(settingsActivity);
                } else {
                    AlertHelper.error(settingsActivity, settingsActivity.getString(R.string.error_prestige_locked), true);
                }
            }
        };
    }

    @NonNull
    public static View.OnClickListener displaySettingName(final SettingsActivity activity, final Setting setting) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String settingDesc = TextHelper.getInstance(activity).getText(DisplayHelper.getSettingDescString(setting.getSetting().value));
                AlertHelper.info(activity, settingDesc, false);
            }
        };
    }
}
