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
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;

public class SplashScreenActivity extends BaseActivity {
    Handler handler = new Handler();

    @BindView(R.id.globe) ImageView globeImage;
    @BindView(R.id.topBar) TextView topBar;
    @BindView(R.id.progressText) TextView progressText;
    @BindView(R.id.startButton) TextView startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        ButterKnife.bind(this);

        new DatabaseHelper(this).execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    public void startIntro() {
        changeButton(false);

        handler.post(stage1);
        handler.postDelayed(stage2, 5000);
        handler.postDelayed(stage3, 10000);
        handler.postDelayed(stage4, 11000);
    }

    private Runnable stage1 = new Runnable() {
        @Override
        public void run() {
            globeImage.setVisibility(View.VISIBLE);
            globeImage.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
            setTopText("First everything was fine :D");
        }
    };

    private Runnable stage2 = new Runnable() {
        @Override
        public void run() {
            globeImage.setImageResource(R.drawable.globe_purple);
            setTopText("Then things went badly, when the Purple came :(");
        }
    };

    private Runnable stage3 = new Runnable() {
        @Override
        public void run() {
            setTopText("Make it right again, thanks!");
        }
    };

    private Runnable stage4 = new Runnable() {
        @Override
        public void run() {
            changeButton(true);
        }
    };

    public void setTopText(String string) {
        topBar.setText(string);
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
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
    }
}
