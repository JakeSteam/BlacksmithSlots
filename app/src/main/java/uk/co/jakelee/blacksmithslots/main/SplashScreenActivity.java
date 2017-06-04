package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.TransitionDrawable;
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;

public class SplashScreenActivity extends BaseActivity {
    private Handler handler = new Handler();
    private TransitionDrawable transitionDrawable;
    public boolean isFirstInstall = false;

    @BindView(R.id.globe) ImageView globeImage;
    @BindView(R.id.textBar) TextView textBar;
    @BindView(R.id.progressText) TextView progressText;
    @BindView(R.id.startButton) TextView startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("replayingIntro", false)) {
            startIntro();
            enableStartButton();
            changeButton(true);
        } else {
            new DatabaseHelper(this).execute();
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
        handler.postDelayed(stage2, 5000);
        handler.postDelayed(stage3, 10000);
        handler.postDelayed(stage4, 15000);
        handler.postDelayed(stage5, 20000);
    }

    private Runnable stage1 = new Runnable() {
        @Override
        public void run() {
            globeImage.setVisibility(View.VISIBLE);
            globeImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
            setStoryText(getString(R.string.stage_1));
        }
    };

    private Runnable stage2 = new Runnable() {
        @Override
        public void run() {
            transitionDrawable.startTransition(4000);
            setStoryText(getString(R.string.stage_2));
        }
    };

    private Runnable stage3 = new Runnable() {
        @Override
        public void run() {
            transitionDrawable.startTransition(4000);
            setStoryText(getString(R.string.stage_3));
        }
    };

    private Runnable stage4 = new Runnable() {
        @Override
        public void run() {
            transitionDrawable.startTransition(4000);
            setStoryText(getString(R.string.stage_4));
        }
    };

    private Runnable stage5 = new Runnable() {
        @Override
        public void run() {
            setStoryText(getString(R.string.stage_5));
            changeButton(true);
        }
    };

    public void setStoryText(String string) {
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

    public void changeButton(boolean toStart) {
        startButton.setText(toStart ? R.string.start : R.string.skip);
        startButton.setBackgroundResource(toStart ? R.drawable.box_green : R.drawable.box_orange);
    }

    public void startGame() {
        startActivity(new Intent(this, MapActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                .putExtra("isFirstInstall", isFirstInstall));
        finish();
    }
}
