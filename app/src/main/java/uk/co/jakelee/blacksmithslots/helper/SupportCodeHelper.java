package uk.co.jakelee.blacksmithslots.helper;

import android.util.Log;

import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

import uk.co.jakelee.blacksmithslots.model.Statistic;
import uk.co.jakelee.blacksmithslots.model.SupportCode;

public class SupportCodeHelper {
    private static final String encryptionPwd = "it's" + "just a" + "support code!";

    public static boolean applyCode(String code) {
        boolean successful = false;
        String decodedString = decode(code);
        String[] parts = decodedString.split("\\|");
        if (validatePartsAndCode(parts)) {
            String[] queries = parts[1].split(";");
            for (String query : queries) {
                if (query.length() > 0) {
                    query = query.trim();
                    Statistic.executeQuery(query);
                }
            }
            successful = true;
        }

        if (successful) {
            (new SupportCode(code)).save();
        }
        return successful;
    }

    public static String decode(String encrypted) {
        String plaintext = "";
        try {
            plaintext = AESCrypt.decrypt(encryptionPwd, encrypted);
        } catch (GeneralSecurityException e) {
            Log.d("Blacksmith", e.toString());
        }
        return plaintext;
    }

    public static byte[] encode(byte[] plainBytes) {
        return encode(new String(plainBytes)).getBytes();
    }

    public static String encode(String plaintext) {
        String encrypted = "";
        try {
            encrypted = AESCrypt.encrypt(encryptionPwd, plaintext);
        } catch (GeneralSecurityException e) {
            Log.d("Blacksmith", e.toString());
        }
        return encrypted;
    }

    private static boolean validatePartsAndCode(String[] parts) {
        if (parts.length < 2 || parts[0].equals("") || parts[1].equals("")) {
            return false;
        }

        long codedTime;
        try {
            codedTime = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            return false;
        }

        return codedTime >= System.currentTimeMillis();
    }
}
