package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
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

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(10000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        globeImage.startAnimation(rotateAnimation);

        handler.postDelayed(stage2, 5000);
        handler.postDelayed(stage3, 10000);

        new DatabaseHelper(this).execute();
    }

    private Runnable stage2 = new Runnable() {
        @Override
        public void run() {
            globeImage.setImageResource(R.drawable.globe_purple);
            topBar.setText("Then things went badly, when the Purple came :(");
        }
    };

    private Runnable stage3 = new Runnable() {
        @Override
        public void run() {
            topBar.setText("Make it right again, thanks!");
            changeSkipToStart();
        }
    };

    public void enableStartButton() {
        startButton.setVisibility(View.VISIBLE);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
    }

    public void changeSkipToStart() {
        startButton.setText(R.string.start);
    }

    public void startGame() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
    }
}
