package uk.co.jakelee.blacksmithslots.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;

public class StorageHelper {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
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

    public static Pair<Integer, Integer> getPBSave(){
        File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/PixelBlacksmith");

        // Get a list of all files
        File[] files = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().startsWith("pixelblacksmith");
            }
        });

        if (files != null && files.length > 0) {
            Arrays.sort(files, Collections.reverseOrder());
            String backupText = SupportCodeHelper.decode(getStringFromFile(files[0]));

            if (!backupText.equals("")) {
                return GooglePlayHelper.getPrestigeAndXpFromPBSave(backupText.getBytes());

            }
        }
        return null;
    }

    public static String getStringFromFile(File file) {
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
