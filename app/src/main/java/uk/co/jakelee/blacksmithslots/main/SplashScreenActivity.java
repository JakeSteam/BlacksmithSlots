package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
                getResources().getDrawable(R.drawable.globe_1),
                getResources().getDrawable(R.drawable.globe_2),
                getResources().getDrawable(R.drawable.globe_3),
                getResources().getDrawable(R.drawable.globe_4));
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
            setStoryText("This is the world. Pretty nice right?");
        }
    };

    private Runnable stage2 = new Runnable() {
        @Override
        public void run() {
            //picasso.load(R.drawable.globe_2).into(globeImage);
            transitionDrawable.startTransition(4000);
            setStoryText("Well, it was.. Until the Purple appeared one day...");
        }
    };

    private Runnable stage3 = new Runnable() {
        @Override
        public void run() {
            //picasso.load(R.drawable.globe_3).into(globeImage);
            transitionDrawable.startTransition(4000);
            setStoryText("It spread quickly, causing chaos and destruction everywhere it touched.");
        }
    };

    private Runnable stage4 = new Runnable() {
        @Override
        public void run() {
            //picasso.load(R.drawable.globe_4).into(globeImage);
            transitionDrawable.startTransition(4000);
            setStoryText("Can you help us stop it? Talk to people, help them out, and find the Purple's source.");
        }
    };

    private Runnable stage5 = new Runnable() {
        @Override
        public void run() {
            setStoryText("Thank you!");
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
                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        finish();
    }
}
