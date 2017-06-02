package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.FontTextView;
import uk.co.jakelee.blacksmithslots.helper.Constants;

public class InterstitialActivity extends BaseActivity {
    private boolean timerEnded = false;
    private boolean calledCallback = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        setupTimer();

        ((FontTextView)findViewById(R.id.countdownTimer)).setMovementMethod(LinkMovementMethod.getInstance());
        ((FontTextView)findViewById(R.id.interstitialText)).setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void setupTimer() {
        final TextView countdownTimer = (TextView) findViewById(R.id.countdownTimer);
        new CountDownTimer(Constants.ADVERT_TIMEOUT, 1000) {
            public void onTick(long millisUntilFinished) {
                int timeLeft = (int) Math.ceil(millisUntilFinished / 1000);
                countdownTimer.setText(String.format(getString(R.string.interstitialTimeLeft), timeLeft));
            }

            public void onFinish() {
                timerEnded = true;
                countdownTimer.setText(String.format(getString(R.string.interstitialTimeLeft), 0));
                countdownTimer.setTextColor(Color.parseColor("#267c18"));
                countdownTimer.setPaintFlags(countdownTimer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

        }.start();
    }

    @Override
    public void close(View v) {
        if (timerEnded && !calledCallback) {
            calledCallback = true;
            setResult(999, new Intent());
        }
        finish();
    }

}
