package uk.co.jakelee.blacksmithslots.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.games.quest.QuestBuffer;
import com.google.android.gms.games.quest.Quests;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.gson.Gson;
import com.orm.SugarRecord;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.MapActivity;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Statistic;
import uk.co.jakelee.blacksmithslots.model.Task;

public class GooglePlayHelper implements com.google.android.gms.common.api.ResultCallback {
    public static final int RC_ACHIEVEMENTS = 9002;
    public static final int RC_LEADERBOARDS = 9003;
    public static final int RC_SAVED_GAMES = 9004;
    public static final int RC_QUESTS = 9005;
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

    public static String CompleteQuest(Quest quest) {
        Games.Quests.claim(mGoogleApiClient, quest.getQuestId(),
                quest.getCurrentMilestone().getMilestoneId());

        String questName = quest.getName();
        String questDifficulty = new String(quest.getCurrentMilestone().getCompletionRewardData(), Charset.forName("UTF-8"));
        int questCoins = getQuestReward(questDifficulty);

        Statistic.add(Enums.Statistic.QuestsCompleted);
        return "Completed"; /*String.format(Locale.ENGLISH, Text.get("QUEST_COMPLETED_TEXT"),
                questDifficulty,
                questName,
                questCoins);*/
    }

    public static void addEvent(String eventString, int quantity) {
        if (!IsConnected() || quantity <= 0) {
            return;
        }

        Games.Events.increment(mGoogleApiClient, eventString, quantity);
    }

    private static int getQuestReward(String questDifficulty) {
        /*switch (questDifficulty) {
            case "Easy":
                return Constants.CURRENCY_QUEST_EASY;
            case "Medium":
                return Constants.CURRENCY_QUEST_MEDIUM;
            case "Hard":
                return Constants.CURRENCY_QUEST_HARD;
            case "Elite":
                return Constants.CURRENCY_QUEST_ELITE;
        }*/
        return 999;
    }

    public static void updateLeaderboards(String leaderboardID, int value) {
        if (!IsConnected()) {
            return;
        }

        Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardID, value);
    }

    private static void UnlockAchievement(Enums.Achievement achievement) {
        if (IsConnected()) {
            Games.Achievements.unlock(mGoogleApiClient, achievement.value);
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
                            Toast.makeText(context, "Save game conflict!", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context, "Unknown cloud error!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return result.getStatus().getStatusCode();
            }
        };

        task.execute();
    }

    private static void loadFromCloud(final boolean checkIsImprovement) {
        if (!IsConnected() || callingContext == null || callingActivity == null || cloudSaveData == null) {
            return;
        }

        callingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!checkIsImprovement) {
                    Toast.makeText(callingActivity, "Loading!", Toast.LENGTH_SHORT).show();;
                }
            }
        });

        Pair<Integer, Integer> cloudData = getStarsAndCoinsFromSave(cloudSaveData);

        if (!checkIsImprovement || newSaveIsBetter(cloudData)) {
            applyBackup(new String(cloudSaveData));
        } else {
            AlertDialogHelper.confirmCloudLoad(callingActivity,
                    LevelHelper.getLevel(),
                    LevelHelper.getXp(),
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
                Toast.makeText(callingActivity, "Saving...", Toast.LENGTH_SHORT).show();
            }
        });

        new Thread(new Runnable() {
            public void run() {
                byte[] data = createBackup();
                String desc = "Desc"; /*String.format(Locale.ENGLISH, Text.get("CLOUD_SAVE_DESC"),
                        PuzzleHelper.getTotalStars(),
                        Statistic.getCurrency(),
                        BuildConfig.VERSION_NAME);*/
                Bitmap cover = BitmapFactory.decodeResource(callingContext.getResources(), R.drawable.arrow_orange);

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
                        Toast.makeText(callingActivity, "Saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    public static void autosave(Context context) {
        if (!IsConnected()) {
            Log.d("GPH", "Not connected..!");
            return;
        }

        Snapshots.OpenSnapshotResult result = Games.Snapshots.open(mGoogleApiClient, "autoSave", true).await();
        byte[] data = createBackup();
        String desc = "Autosave"; /*String.format(Locale.ENGLISH, Text.get("CLOUD_AUTOSAVE_DESC"),
                PuzzleHelper.getTotalStars(),
                Statistic.getCurrency(),
                BuildConfig.VERSION_NAME);*/
        Bitmap cover = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_orange);

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

            Games.Snapshots.commitAndClose(mGoogleApiClient, snapshot, metadataChange);

            Statistic lastAutosave = Statistic.get(Enums.Statistic.LastAutosave);
            if (lastAutosave != null) {
                lastAutosave.setLongValue(System.currentTimeMillis());
                lastAutosave.save();
            }
        }
    }

    public static boolean shouldAutosave() {
        Statistic lastAutosave = Statistic.get(Enums.Statistic.LastAutosave);
        int minutesBetweenSaves = Setting.getInt(Enums.Setting.AutosaveMinutes);

        if (minutesBetweenSaves == -1) {
            return false;
        }

        long nextAutosave = lastAutosave.getLongValue() + (100 * 1000);
        return nextAutosave <= System.currentTimeMillis();
    }

    private static void saveToCloud() {
        if (!IsConnected() || callingContext == null || callingActivity == null || loadedSnapshot == null) {
            return;
        }

        if (loadedSnapshot.getMetadata().getDeviceName() == null) {
            forceSaveToCloud();
        } else {
            AlertDialogHelper.confirmCloudSave(callingActivity,
                    LevelHelper.getLevel(),
                    LevelHelper.getXp(),
                    loadedSnapshot.getMetadata().getDescription(),
                    loadedSnapshot.getMetadata().getLastModifiedTimestamp(),
                    loadedSnapshot.getMetadata().getDeviceName());
        }

    }

    public static boolean AreGooglePlayServicesInstalled(Activity activity) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(activity);
        return code == ConnectionResult.SUCCESS;
    }

    public static boolean IsConnected() {
        return GooglePlayHelper.mGoogleApiClient != null && GooglePlayHelper.mGoogleApiClient.isConnected();
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends SugarRecord>[] backupClasses = new Class[] {
            Inventory.class,
            Setting.class,
            Statistic.class,
            Task.class
    };

    public static byte[] createBackup() {
        Gson gson = new Gson();

        String backupString = DatabaseHelper.LATEST_PATCH + GooglePlayHelper.SAVE_DELIMITER;
        backupString += LevelHelper.getLevel() + GooglePlayHelper.SAVE_DELIMITER;
        backupString += LevelHelper.getXp() + GooglePlayHelper.SAVE_DELIMITER;

        for (Class<? extends SugarRecord> backupClass : backupClasses) {
            backupString += gson.toJson(SugarRecord.listAll(backupClass)) + GooglePlayHelper.SAVE_DELIMITER;
        }

        return backupString.getBytes();
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

        // 0 is db version, 1 & 2 are stars & coins
        int backupPosition = 3;
        for (Class<? extends SugarRecord> backupClass : backupClasses) {
            if (splitData.length > backupPosition) {
                SugarRecord.deleteAll(backupClass);
                SugarRecord.saveInTx(fromJsonList(gson, splitData[backupPosition++], backupClass));
            }
        }

        new DatabaseHelper(callingActivity, false).execute();

        if (callingActivity != null) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(callingActivity, "Loaded cloud save!", Toast.LENGTH_SHORT).show();
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

    public static Pair<Integer, Integer> getStarsAndCoinsFromSave(byte[] saveBytes) {
        int stars = 0;
        int coins = 0;

        String[] splitData = splitBackupData(new String(saveBytes));
        if (splitData.length > 2) {
            stars = Integer.parseInt(splitData[1]);
            coins = Integer.parseInt(splitData[2]);
        }

        return new Pair<>(stars, coins);
    }

    public static boolean newSaveIsBetter(Pair<Integer, Integer> newValues) {
        return !(newValues.first <= LevelHelper.getXp() && newValues.second <= LevelHelper.getLevel());
    }

    private static String[] splitBackupData(String backupData) {
        String[] splitData = backupData.split(GooglePlayHelper.SAVE_DELIMITER);
        for (int i = 0; i < splitData.length; i++) {
            splitData[i] = splitData[i].replace(GooglePlayHelper.SAVE_DELIMITER, "");
        }

        return splitData;
    }

    public void onResult(com.google.android.gms.common.api.Result result) {
        Quests.LoadQuestsResult r = (Quests.LoadQuestsResult) result;
        QuestBuffer qb = r.getQuests();
        qb.close();
    }
}
