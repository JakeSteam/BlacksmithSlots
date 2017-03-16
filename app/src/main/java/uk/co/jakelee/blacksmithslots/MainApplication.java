package uk.co.jakelee.blacksmithslots;

import android.app.Application;

import com.orm.SugarContext;

import uk.co.jakelee.blacksmithslots.helper.NotificationHelper;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
        NotificationHelper.clearNotifications(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}