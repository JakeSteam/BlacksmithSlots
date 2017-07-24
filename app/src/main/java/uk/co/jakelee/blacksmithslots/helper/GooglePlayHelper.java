package uk.co.jakelee.blacksmithslots.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.quest.QuestBuffer;
import com.google.android.gms.games.quest.Quests;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.gson.Gson;
import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.jakelee.blacksmithslots.BuildConfig;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.MapActivity;
import uk.co.jakelee.blacksmithslots.model.Achievement;
import uk.co.jakelee.blacksmithslots.model.Farm;
import uk.co.jakelee.blacksmithslots.model.Iap;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Message;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Statistic;
import uk.co.jakelee.blacksmithslots.model.SupportCode;
import uk.co.jakelee.blacksmithslots.model.Task;
import uk.co.jakelee.blacksmithslots.model.Upgrade;

import static android.content.Context.MODE_PRIVATE;

public class GooglePlayHelper implements com.google.android.gms.common.api.ResultCallback {
    public static final int RC_ACHIEVEMENTS = 9002;
    public static final int RC_LEADERBOARDS = 9003;
    public static final int RC_SAVED_GAMES = 9004;
    private static final int RESULT_OK = -1;
    private static final int RC_SIGN_IN = 9001;
    private static final String SAVE_DELIMITER = "UNIQUEDELIMITINGSTRING";
    private static final String mCurrentSaveName = "blacksmithslotsCloudSave";
    public static GoogleApiClient mGoogleApiClient;
    private static boolean mResolvingConnectionFailure = false;
    private static byte[] cloudSaveData;
    private static Context callingContext;
    private static Activity callingActivity;
    private static Snapshot loadedSnapshot;

    public static void ConnectionFailed(Activity activity, ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            return;
        }

        mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(activity,
                mGoogleApiClient, connectionResult,
                RC_SIGN_IN, "Failed to connect");
    }

    public static void ActivityResult(Activity activity, int requestCode, int resultCode) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                Setting signIn = Setting.get(Enums.Setting.AttemptLogin);
                signIn.setBooleanValue(false);
                signIn.save();
            }
        }
    }

    /*public static void addEvent(String eventString, int quantity) {
        if (!IsConnected() || quantity <= 0) {
            return;
        }

        Games.Events.increment(mGoogleApiClient, eventString, quantity);
    }*/

    public static void updateLeaderboards(String leaderboardID, int value) {
        if (!IsConnected()) {
            return;
        }

        Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardID, value);
    }

    public static void UpdateAchievements() {
        if (!IsConnected()) {
            return;
        }

        List<Statistic> statistics = Select.from(Statistic.class).where(
                Condition.prop("j").notEq(Constants.STATISTIC_NOT_TRACKED)).list();

        for (Statistic statistic : statistics) {
            int currentValue = statistic.getIntValue();
            int lastSentValue = statistic.getLastSentValue();
            List<Achievement> achievements = Select.from(Achievement.class).where(
                    Condition.prop("c").eq(statistic.getStatistic().value)).orderBy("b ASC").list();

            for (Achievement achievement : achievements) {
                UpdateAchievement(achievement, currentValue, lastSentValue);
            }

            UpdateStatistic(statistic, currentValue, lastSentValue);
        }
    }

    private static void UpdateAchievement(uk.co.jakelee.blacksmithslots.model.Achievement achievement, int currentValue, int lastSentValue) {
        boolean hasChanged = (currentValue > lastSentValue);
        boolean isAchieved = (achievement.getMaximumValue() <= lastSentValue);
        if (hasChanged && !isAchieved && mGoogleApiClient.isConnected()) {
            int difference = currentValue - lastSentValue;
            if (achievement.getMaximumValue() == 1) {
                Games.Achievements.unlock(mGoogleApiClient, achievement.getRemoteID());
            } else {
                Games.Achievements.increment(mGoogleApiClient, achievement.getRemoteID(), difference);
            }
        }
    }

    public static void UnlockAchievement(String achievementID) {
        if (mGoogleApiClient.isConnected()) {
            Games.Achievements.unlock(mGoogleApiClient, achievementID);
        }
    }

    private static void UpdateStatistic(Statistic statistic, int currentValue, int lastSentValue) {
        if (currentValue > lastSentValue && mGoogleApiClient.isConnected()) {
            statistic.setLastSentValue(currentValue);
            statistic.save();
        }
    }

    public static void SavedGamesIntent(final Context context, final Activity activity, final Intent intent) {
        if (intent == null || !mGoogleApiClient.isConnected()) {
            return;
        }
        callingContext = context;
        callingActivity = activity;

        AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                Snapshots.OpenSnapshotResult result = Games.Snapshots.open(mGoogleApiClient, mCurrentSaveName, true).await();

                // Conflict! Let's fix it
                while (!result.getStatus().isSuccess()) {
                    callingActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertHelper.error(callingActivity, R.string.google_cloud_save_conflict, true);
                        }
                    });

                    if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
                        Snapshot snapshot = result.getSnapshot();
                        Snapshot conflictSnapshot = result.getConflictingSnapshot();
                        Snapshot mResolvedSnapshot = snapshot;

                        if (snapshot.getMetadata().getLastModifiedTimestamp() < conflictSnapshot.getMetadata().getLastModifiedTimestamp()) {
                            mResolvedSnapshot = conflictSnapshot;
                        }

                        result = Games.Snapshots.resolveConflict(mGoogleApiClient, result.getConflictId(), mResolvedSnapshot).await();
                    }
                }

                Snapshot snapshot = result.getSnapshot();
                try {
                    if (intent.hasExtra(Snapshots.EXTRA_SNAPSHOT_METADATA)) {
                        cloudSaveData = snapshot.getSnapshotContents().readFully();
                        loadFromCloud(true);
                    } else if (intent.hasExtra(Snapshots.EXTRA_SNAPSHOT_NEW)) {
                        loadedSnapshot = snapshot;
                        saveToCloud();
                    }
                } catch (final IOException e) {
                    callingActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertHelper.error(callingActivity, R.string.google_cloud_save_error, true);
                        }
                    });
                }
                return result.getStatus().getStatusCode();
            }
        };

        task.execute();
    }

    public static void getInfoFromPBSave() {
        Pair<Integer, Integer> cloudData = getXpAndItemsFromSave(cloudSaveData);
    }

    private static void loadFromCloud(final boolean checkIsImprovement) {
        if (!IsConnected() || callingContext == null || callingActivity == null || cloudSaveData == null) {
            return;
        }

        callingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!checkIsImprovement) {
                    AlertHelper.info(callingActivity, R.string.google_cloud_save_loading, false);
                }
            }
        });

        Pair<Integer, Integer> cloudData = getXpAndItemsFromSave(cloudSaveData);

        if (!checkIsImprovement || newSaveIsBetter(cloudData)) {
            applyBackup(new String(cloudSaveData));
        } else {
            AlertDialogHelper.confirmCloudLoad(callingActivity,
                    LevelHelper.getXp(),
                    Inventory.getUniqueItemCount(),
                    cloudData.first,
                    cloudData.second);
        }
    }

    public static void forceLoadFromCloud() {
        new Thread(new Runnable() {
            public void run() {
                loadFromCloud(false);
            }
        }).start();
    }

    public static void forceSaveToCloud() {
        callingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertHelper.info(callingActivity, R.string.google_cloud_save_saving, false);
            }
        });

        new Thread(new Runnable() {
            public void run() {
                byte[] data = createBackup();
                String desc = String.format(Locale.ENGLISH, callingActivity.getString(R.string.save_description),
                        BuildConfig.VERSION_NAME,
                        LevelHelper.getLevel(),
                        LevelHelper.getXp(),
                        Inventory.getUniqueItemCount());
                Bitmap cover = BitmapFactory.decodeResource(callingContext.getResources(), R.drawable.title);

                loadedSnapshot.getSnapshotContents().writeBytes(data);

                SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                        .setDescription(desc)
                        .setCoverImage(cover)
                        .build();

                // Commit the operation
                Games.Snapshots.commitAndClose(mGoogleApiClient, loadedSnapshot, metadataChange);

                callingActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertHelper.success(callingActivity, R.string.google_cloud_save_saved, true);
                    }
                });
            }
        }).start();
    }

    public static void autosave(final Activity activity) {
        new Thread(new Runnable() {
            public void run() {
                if (!IsConnected()) {
                    return;
                }

                Snapshots.OpenSnapshotResult result = Games.Snapshots.open(mGoogleApiClient, "autoSave", true).await();
                byte[] data = createBackup();
                String desc = String.format(Locale.ENGLISH, "(Auto) " + activity.getString(R.string.save_description),
                        BuildConfig.VERSION_NAME,
                        LevelHelper.getLevel(),
                        LevelHelper.getXp(),
                        Inventory.getUniqueItemCount());
                Bitmap cover = BitmapFactory.decodeResource(activity.getResources(), R.drawable.title);

                // Check the result of the open operation
                if (result.getStatus().isSuccess()) {
                    Snapshot snapshot = result.getSnapshot();
                    snapshot.getSnapshotContents().writeBytes(data);

                    // Create the change operation
                    SnapshotMetadataChange metadataChange = new
                            SnapshotMetadataChange.Builder()
                            .setCoverImage(cover)
                            .setDescription(desc)
                            .build();

                    try {
                        Games.Snapshots.commitAndClose(mGoogleApiClient, snapshot, metadataChange);


                        Statistic lastAutosave = Statistic.get(Enums.Statistic.LastAutosave);
                        if (lastAutosave != null) {
                            lastAutosave.setLongValue(System.currentTimeMillis());
                            lastAutosave.save();
                        }
                    } catch (IllegalStateException e) {
                        Log.d("Autosave", "Autosave failed!");
                    }
                }
            }
        }).start();
    }

    public static boolean shouldAutosave() {
        if (GooglePlayHelper.IsConnected() && Setting.getBoolean(Enums.Setting.Autosave)) {
            Statistic lastAutosave = Statistic.get(Enums.Statistic.LastAutosave);

            long nextAutosave = lastAutosave.getLongValue() + Constants.TIME_BETWEEN_AUTOSAVES;
            Log.d("Autosave", "Next autosave is at " + DateHelper.timestampToDateTime(nextAutosave));
            return nextAutosave <= System.currentTimeMillis();
        }
        return false;
    }

    private static void saveToCloud() {
        if (!IsConnected() || callingContext == null || callingActivity == null || loadedSnapshot == null) {
            return;
        }

        if (loadedSnapshot.getMetadata().getDeviceName() == null) {
            forceSaveToCloud();
        } else {
            AlertDialogHelper.confirmCloudSave(callingActivity,
                    LevelHelper.getXp(),
                    Inventory.getUniqueItemCount(),
                    loadedSnapshot.getMetadata().getDescription(),
                    loadedSnapshot.getMetadata().getLastModifiedTimestamp(),
                    loadedSnapshot.getMetadata().getDeviceName());
        }

    }

    private static boolean AreGooglePlayServicesInstalled(Activity activity) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(activity);
        return code == ConnectionResult.SUCCESS;
    }

    public static boolean IsConnected() {
        return GooglePlayHelper.mGoogleApiClient != null && GooglePlayHelper.mGoogleApiClient.isConnected();
    }

    public static void disconnect() {
        if (!GooglePlayHelper.mGoogleApiClient.isConnected()) {
            return;
        }

        Games.signOut(GooglePlayHelper.mGoogleApiClient);
        GooglePlayHelper.mGoogleApiClient.disconnect();

        Setting signIn = Setting.get(Enums.Setting.AttemptLogin);
        signIn.setBooleanValue(false);
        signIn.save();
    }


    @SuppressWarnings("unchecked")
    private static final Class<? extends SugarRecord>[] backupClasses = new Class[] {
            Iap.class,
            Inventory.class,
            Message.class,
            Setting.class,
            Statistic.class,
            SupportCode.class,
            Task.class,
            Upgrade.class,
            ItemBundle.class,
            Farm.class
    };

    public static byte[] createBackup() {
        Gson gson = new Gson();

        StringBuilder backupString = new StringBuilder(DatabaseHelper.LATEST_PATCH + GooglePlayHelper.SAVE_DELIMITER);
        backupString.append(LevelHelper.getXp()).append(GooglePlayHelper.SAVE_DELIMITER);
        backupString.append(Inventory.getUniqueItemCount()).append(GooglePlayHelper.SAVE_DELIMITER);

        for (Class<? extends SugarRecord> backupClass : backupClasses) {
            backupString.append(gson.toJson(SugarRecord.listAll(backupClass))).append(GooglePlayHelper.SAVE_DELIMITER);
        }

        return backupString.toString().getBytes();
    }

    public static void applyBackup(String backupData) {
        Gson gson = new Gson();
        String[] splitData = splitBackupData(backupData);

        if (backupData.length() == 0 || splitData.length <= 3) {
            return;
        }

        if (MapActivity.prefs != null) {
            MapActivity.prefs.edit().putInt("databaseVersion", Integer.parseInt(splitData[0])).apply();
        }

        // 0 is db version, 1 & 2 are xp & item count
        int backupPosition = 3;
        for (Class<? extends SugarRecord> backupClass : backupClasses) {
            if (splitData.length > backupPosition) {
                SugarRecord.deleteAll(backupClass);
                SugarRecord.saveInTx(fromJsonList(gson, splitData[backupPosition++], backupClass));
            }
        }

        new DatabaseHelper(callingActivity).execute();

        if (callingActivity != null) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertHelper.success(callingActivity, R.string.google_cloud_save_loaded, true);
                }
            });
        }
    }

    private static <T extends SugarRecord> List<T> fromJsonList(Gson gson, String json, Class<T> className) {
        Object[] array = (Object[])java.lang.reflect.Array.newInstance(className, 1);
        array = gson.fromJson(json, array.getClass());
        List<T> list = new ArrayList<>();
        for (Object item : array) {
            list.add((T)item);
        }
        return list;
    }

    public static Pair<Integer, Integer> getXpAndItemsFromSave(byte[] saveBytes) {
        int xp = 0;
        int items = 0;

        String[] splitData = splitBackupData(new String(saveBytes));
        if (splitData.length > 2) {
            xp = Integer.parseInt(splitData[1]);
            items = Integer.parseInt(splitData[2]);
        }

        return new Pair<>(xp, items);
    }

    public static Pair<Integer, Integer> getPrestigeAndXpFromPBSave(byte[] saveBytes) {
        int prestige = 0;
        int xp = 0;

        String splitData = new String(saveBytes);

        String prestigeString = getStringBetweenStrings(splitData, ",\"name\":\"Premium\",\"id\":16},{\"intValue\":1,\"lastSentValue\":", ",\"name\":\"Prestige\",\"id\":17}");
        String xpString = getStringBetweenStrings(splitData, "}]UNIQUEDELIMITINGSTRING[{\"intValue\":", ",\"lastSentValue\":-1,\"name\":\"XP\",\"id\":1}");

        try {
            xp = Integer.parseInt(xpString);
            prestige = Integer.parseInt(prestigeString);
        } catch (Exception e) {
            Log.d("Parse", "Couldn't parse " + xpString + " and " + prestigeString);
        }

        return new Pair<>(prestige, xp);
    }

    private static String getStringBetweenStrings(String aString, String aPattern1, String aPattern2) {
        int pos1,pos2;

        pos1 = aString.indexOf(aPattern1) + aPattern1.length();
        pos2 = aString.indexOf(aPattern2);

        if ((pos1>0) && (pos2>0) && (pos2 > pos1)) {
            return aString.substring(pos1, pos2);
        }

        return null;
    }

    public static boolean newSaveIsBetter(Pair<Integer, Integer> newValues) {
        return !(newValues.first <= LevelHelper.getXp() && newValues.second <= Inventory.getUniqueItemCount());
    }

    private static String[] splitBackupData(String backupData) {
        String[] splitData = backupData.split(GooglePlayHelper.SAVE_DELIMITER);
        for (int i = 0; i < splitData.length; i++) {
            splitData[i] = splitData[i].replace(GooglePlayHelper.SAVE_DELIMITER, "");
        }

        return splitData;
    }

    public static void tryGoogleLogin(Activity activity, boolean forceAttempt) {
        // If we've got all we need, and we need to sign in, or it is first run.
        boolean a = AreGooglePlayServicesInstalled(activity);
        boolean b = !IsConnected();
        boolean c = !mGoogleApiClient.isConnecting();
        boolean d = forceAttempt;
        boolean e = Setting.getBoolean(Enums.Setting.AttemptLogin);
        boolean f = activity.getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE).getInt("databaseVersion", DatabaseHelper.NO_DATABASE) <= DatabaseHelper.NO_DATABASE;
        if (a && b && c && (d || e || f)) {
            mGoogleApiClient.connect();
        }
    }

    public void onResult(@NonNull com.google.android.gms.common.api.Result result) {
        Quests.LoadQuestsResult r = (Quests.LoadQuestsResult) result;
        QuestBuffer qb = r.getQuests();
        qb.close();
    }
}
