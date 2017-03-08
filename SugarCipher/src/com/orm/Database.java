package com.orm;

import android.content.Context;
import android.provider.Settings;

import net.sqlcipher.database.SQLiteDatabase;


public class Database {
    private final String databasePassword;
    private SugarDb sugarDb;
    private SQLiteDatabase sqLiteDatabase;

    public Database(Context context){
        SQLiteDatabase.loadLibs(context);
        databasePassword = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        this.sugarDb  = new SugarDb(context);
    }


    public synchronized SQLiteDatabase getDB() {
        if (this.sqLiteDatabase == null) {
            this.sqLiteDatabase = this.sugarDb.getWritableDatabase(databasePassword);
        }

        return this.sqLiteDatabase;
    }

}
