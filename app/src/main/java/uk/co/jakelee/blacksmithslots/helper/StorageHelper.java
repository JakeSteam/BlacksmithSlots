package uk.co.jakelee.blacksmithslots.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.model.Inventory;

public class StorageHelper {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void confirmStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static Pair<Integer, Integer> getSave(boolean isPbSave) {
        final String saveName = isPbSave ? "PixelBlacksmith" : "BlacksmithSlots";
        File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/" + saveName);

        // Get a list of all files
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().startsWith(saveName.toLowerCase());
            }
        });

        if (files != null && files.length > 0) {
            Arrays.sort(files, Collections.reverseOrder());
            String backupText = SupportCodeHelper.decode(getStringFromFile(files[0]));


            if (isPbSave) {
                if (!backupText.equals("")) {
                    return GooglePlayHelper.getPrestigeAndXpFromPBSave(backupText.getBytes());
                }
            }
        }
        return null;
    }

    public static String loadLocalSave(Activity activity, boolean checkIsImprovement) {
        File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/BlacksmithSlots");

        // Get a list of all files
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().startsWith("blacksmithslots");
            }
        });

        if (files != null && files.length > 0) {
            Arrays.sort(files, Collections.reverseOrder());
            String backupText = SupportCodeHelper.decode(getStringFromFile(files[0]));

            if (!backupText.equals("")) {
                Pair<Integer, Integer> cloudData = GooglePlayHelper.getXpAndItemsFromSave(backupText.getBytes());

                if (!checkIsImprovement || GooglePlayHelper.newSaveIsBetter(cloudData)) {
                    GooglePlayHelper.applyBackup(backupText);
                } else {
                    AlertDialogHelper.confirmLocalLoad(activity,
                            LevelHelper.getXp(),
                            Inventory.getUniqueItemCount(),
                            cloudData.first,
                            cloudData.second);
                    return "";
                }

                return files[0].getName();
            }
        }
        return activity.getString(R.string.error_no_save_found);
    }

    public static String saveLocalSave(Activity activity) {
        confirmStoragePermissions(activity);

        try {
            String filename = getFilename(activity);
            byte[] backup = SupportCodeHelper.encode(GooglePlayHelper.createBackup());

            File path = new File(Environment.getExternalStorageDirectory() + "/BlacksmithSlots");
            boolean success = path.mkdirs();

            File file = new File(Environment.getExternalStorageDirectory() + "/BlacksmithSlots", filename);

            FileOutputStream outputStream;
            outputStream = new FileOutputStream(file);
            outputStream.write(backup);
            outputStream.close();
            return filename;
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    public static String getFilename(Context context) {
        String date = new SimpleDateFormat("yyyyMMdd_kkmmss").format(new Date());
        return String.format(context.getString(R.string.saveNameFormat), date);
    }

    private static String getStringFromFile(File file) {
        String extractedText = "";
        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            boolean done = false;
            while (!done) {
                final String line = reader.readLine();
                done = (line == null);

                if (line != null) {
                    stringBuilder.append(line);
                }
            }

            reader.close();
            inputStream.close();
            extractedText = stringBuilder.toString();
        } catch (IOException e) {
            Log.d("Error", "Error loading");
        }
        return extractedText;
    }
}
