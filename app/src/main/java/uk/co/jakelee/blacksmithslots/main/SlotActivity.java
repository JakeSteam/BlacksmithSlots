package uk.co.jakelee.blacksmithslots.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import kankan.wheel.widget.WheelView;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;
import uk.co.jakelee.blacksmithslots.helper.SlotHelper;

public class SlotActivity extends AppCompatActivity {
    private int[] slots = new int[] {R.id.slot_1, R.id.slot_2, R.id.slot_3};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);
        onWindowFocusChanged(true);

        SharedPreferences prefs = getSharedPreferences("uk.co.jakelee.blacksmithslots", MODE_PRIVATE);
        if (prefs.getBoolean("firstRun", true)) {
            DatabaseHelper.testSetup();
            prefs.edit().putBoolean("firstRun", false).apply();
        }

        final SlotHelper slotHelper = new SlotHelper(this);

        for (int slot : slots) {
            slotHelper.initWheel((WheelView)findViewById(slot));
        }

        Button mix = (Button)findViewById(R.id.btn_mix);
        mix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int slot : slots) {
                    slotHelper.mixWheel(slot);
                }

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus) {
            findViewById(R.id.parent).setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
