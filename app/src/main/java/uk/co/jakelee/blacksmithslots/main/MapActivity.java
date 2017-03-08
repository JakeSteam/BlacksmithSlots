package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.games.quest.QuestUpdateListener;

import java.util.List;

import hotchemi.android.rate.AppRate;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmithslots.helper.TaskHelper;
import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Task;

public class MapActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        QuestUpdateListener {
    public static SharedPreferences prefs;
    private static int[] townLayouts = {R.layout.custom_town_1, R.layout.custom_town_2, R.layout.custom_town_3};

    private int selectedSlot = 1;
    private ViewPager mViewPager;
    private CustomPagerAdapter mCustomPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        prefs = getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);

        ratingPrompt();

        GooglePlayHelper.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                .build();
        tryGoogleLogin();


        mCustomPagerAdapter = new CustomPagerAdapter(this);

        mViewPager = (ViewPager) findViewById(R.id.townScroller);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            public void onPageSelected(int position) {
                Log.d("Position", "Selected:" + position);
            }
        });
        mViewPager.setAdapter(mCustomPagerAdapter);
    }

    public void tryGoogleLogin() {
        // If we've got all we need, and we need to sign in, or it is first run.
        boolean a = GooglePlayHelper.AreGooglePlayServicesInstalled(this);
        boolean b = !GooglePlayHelper.IsConnected();
        boolean c = !GooglePlayHelper.mGoogleApiClient.isConnecting();
        boolean d = Setting.getBoolean(Enums.Setting.AttemptLogin);
        boolean e = prefs.getInt("databaseVersion", DatabaseHelper.NO_DATABASE) <= DatabaseHelper.NO_DATABASE;

        if (a && b && c && (d || e)) {
            GooglePlayHelper.mGoogleApiClient.connect();
        }
    }

    private void ratingPrompt() {
        AppRate.with(this)
                .setInstallDays(3)
                .setLaunchTimes(3)
                .setRemindInterval(3)
                .setShowLaterButton(true)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);
    }

    public void openSlot(View v) {
        if (selectedSlot > 0 && !TaskHelper.isSlotLocked(selectedSlot)) {
            startActivity(new Intent(this, SlotActivity.class)
                    .putExtra(Constants.INTENT_SLOT, selectedSlot)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        }
    }

    public void selectSlot(View v) {
        selectedSlot = Integer.parseInt((String)v.getTag());
        populateSlotInfo();
    }

    public void openSettings(View v) {
        startActivity(new Intent(this, SettingsActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void openStatistics(View v) {
        startActivity(new Intent(this, StatisticsActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
    }

    public void loadSidebar(View v) {
        findViewById(R.id.noSlotSelected).setVisibility(View.VISIBLE);
    }

    public void handIn(View v) {
        Task task = Task.findById(Task.class, (long)v.getTag());
        if (task.getTier() != null && task.itemsCanBeSubmitted()) {
            task.submitItems();
            Toast.makeText(this, "Items submitted!", Toast.LENGTH_SHORT).show();
        } else if (task.isCompleteable()) {
            task.setCompleted(System.currentTimeMillis());
            task.save();
        } else {
            Toast.makeText(this, "Can't hand in an unfinished task!", Toast.LENGTH_SHORT).show();
        }
        populateSlotInfo();
    }

    private void populateSlotInfo() {
        if (selectedSlot > 0) {
            findViewById(R.id.noSlotSelected).setVisibility(View.GONE);
            Slot slot = Slot.get(selectedSlot);
            if (slot != null) {
                ((TextView) findViewById(R.id.slotTitle)).setText(slot.getName(this));
                String id = DisplayHelper.getPersonImageFile(slot.getPerson());
                int id2 = getResources().getIdentifier(id, "drawable", getPackageName());
                ((ImageView) findViewById(R.id.person)).setImageResource(id2);

                if (TaskHelper.isSlotLocked(selectedSlot)) {
                    List<Task> tasks = slot.getTasks();
                    Task currentTask = TaskHelper.getCurrentTask(tasks);
                    if (currentTask.getStarted() == 0) {
                        currentTask.setStarted(System.currentTimeMillis());
                        currentTask.save();
                    }

                    ((TextView) findViewById(R.id.slotDescription)).setText(slot.getLockedText(this));
                    ((TextView) findViewById(R.id.taskProgress)).setText("Task " + currentTask.getPosition() + "/" + tasks.size());
                    ((TextView) findViewById(R.id.taskText)).setText(currentTask.getText(this));
                    ((TextView) findViewById(R.id.taskRequirement)).setText(currentTask.toString(this));
                    findViewById(R.id.handInButton).setTag(currentTask.getId());

                    findViewById(R.id.lockedSlot).setVisibility(View.VISIBLE);
                    findViewById(R.id.unlockedSlot).setVisibility(View.GONE);
                } else {
                    ((TextView) findViewById(R.id.slotDescription)).setText(slot.getUnlockedText(this));

                    LinearLayout resourceContainer = (LinearLayout)findViewById(R.id.resourceContainer);
                    resourceContainer.removeAllViews();
                    resourceContainer.addView(DisplayHelper.createImageView(this, DisplayHelper.getItemImageFile(slot.getResourceTier().value, slot.getResourceType().value), 30, 30));

                    LinearLayout rewardContainer = (LinearLayout)findViewById(R.id.rewardContainer);
                    rewardContainer.removeAllViews();
                    List<Reward> rewards = slot.getRewards();
                    for (Reward reward : rewards) {
                        rewardContainer.addView(DisplayHelper.createImageView(this, DisplayHelper.getItemImageFile(reward.getTier().value, reward.getType().value, reward.getQuantityMultiplier()), 30, 30));
                    }

                    findViewById(R.id.lockedSlot).setVisibility(View.GONE);
                    findViewById(R.id.unlockedSlot).setVisibility(View.VISIBLE);

                }
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            findViewById(R.id.townScroller).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    class CustomPagerAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return townLayouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(townLayouts[position], container, false);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((PercentRelativeLayout) object);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (GooglePlayHelper.IsConnected()) {
            Games.Quests.registerQuestUpdateListener(GooglePlayHelper.mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        GooglePlayHelper.ConnectionFailed(this, connectionResult);
    }

    @Override
    public void onConnectionSuspended(int i) {
        GooglePlayHelper.mGoogleApiClient.connect();
    }

    public void onQuestCompleted(Quest quest) {
        Toast.makeText(this, "Quest completed", Toast.LENGTH_SHORT).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        GooglePlayHelper.ActivityResult(this, requestCode, resultCode);
    }
}
