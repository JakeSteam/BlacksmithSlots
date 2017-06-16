package uk.co.jakelee.blacksmithslots.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.TransitionDrawable;
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.MusicHelper;
import uk.co.jakelee.blacksmithslots.model.Setting;

public class SplashScreenActivity extends Activity {
    private final Handler handler = new Handler();
    private TransitionDrawable transitionDrawable;
    public boolean isFirstInstall = false;

    @BindView(R.id.globe) ImageView globeImage;
    @BindView(R.id.textBar) TextView textBar;
    @BindView(R.id.progressText) TextView progressText;
    @BindView(R.id.startButton) TextView startButton;
    @BindView(R.id.muteButton) TextView muteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("replayingIntro", false)) {
            MusicHelper.getInstance(this).playIfPossible(R.raw.time_passes);
            startIntro();
            enableStartButton();
            changeButton(true);
        } else {
            new DatabaseHelper(this).execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicHelper.getInstance(this).setMovingInApp(false);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!MusicHelper.getInstance(this).isMovingInApp()) {
            MusicHelper.getInstance(this).stopMusic();
        }
    }

    @OnClick(R.id.muteButton)
    public void mute() {
        boolean mutingMusic = MusicHelper.getInstance(this).isMusicServiceIsStarted();
        if (mutingMusic) {
            MusicHelper.getInstance(this).stopMusic();
        } else {
            MusicHelper.getInstance(this).playIfPossible(R.raw.time_passes);
        }

        muteButton.setText(mutingMusic ? R.string.icon_mute : R.string.icon_volume);
        muteButton.setBackgroundResource(mutingMusic ? R.drawable.box_orange : R.drawable.box_green);
        Setting setting = Setting.get(Enums.Setting.Music);
        if (setting != null) {
            setting.setBooleanValue(!mutingMusic);
            setting.save();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    public void startIntro() {
        changeButton(false);

        transitionDrawable = new TransitionDrawable(
                ContextCompat.getDrawable(this, R.drawable.globe_1),
                ContextCompat.getDrawable(this, R.drawable.globe_2),
                ContextCompat.getDrawable(this, R.drawable.globe_3),
                ContextCompat.getDrawable(this, R.drawable.globe_4));
        globeImage.setImageDrawable(transitionDrawable);

        handler.post(stage1);
        handler.postDelayed(stage2, 6000);
        handler.postDelayed(stage3, 12000);
        handler.postDelayed(stage4, 18000);
        handler.postDelayed(stage5, 24000);
    }

    private final Runnable stage1 = new Runnable() {
        @Override
        public void run() {
            globeImage.setVisibility(View.VISIBLE);
            globeImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
            setStoryText(getString(R.string.stage_1));
        }
    };

    private final Runnable stage2 = new Runnable() {
        @Override
        public void run() {
            transitionDrawable.startTransition(4000);
            setStoryText(getString(R.string.stage_2));
        }
    };

    private final Runnable stage3 = new Runnable() {
        @Override
        public void run() {
            transitionDrawable.startTransition(4000);
            setStoryText(getString(R.string.stage_3));
        }
    };

    private final Runnable stage4 = new Runnable() {
        @Override
        public void run() {
            transitionDrawable.startTransition(4000);
            setStoryText(getString(R.string.stage_4));
        }
    };

    private final Runnable stage5 = new Runnable() {
        @Override
        public void run() {
            setStoryText(getString(R.string.stage_5));
            changeButton(true);
        }
    };

    private void setStoryText(String string) {
        textBar.setText(string);
    }

    public void enableStartButton() {
        startButton.setVisibility(View.VISIBLE);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
    }

    private void changeButton(boolean toStart) {
        startButton.setText(toStart ? R.string.start : R.string.skip);
        startButton.setBackgroundResource(toStart ? R.drawable.box_green : R.drawable.box_orange);
    }

    public void startGame() {
        MusicHelper.getInstance(this).setMovingInApp(true);
        if (Build.VERSION.SDK_INT >= 16) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(globeImage, 0, 0, globeImage.getWidth(), globeImage.getHeight());
            ActivityCompat.startActivity(SplashScreenActivity.this, new Intent(this, MapActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    .putExtra("isFirstInstall", isFirstInstall), options.toBundle());
        } else {
            startActivity(new Intent(this, MapActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    .putExtra("isFirstInstall", isFirstInstall));
        }
        finish();
    }
}
