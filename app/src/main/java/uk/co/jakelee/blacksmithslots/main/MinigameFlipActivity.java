package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.CalculationHelper;
import uk.co.jakelee.blacksmithslots.model.Item;

public class MinigameFlipActivity extends MinigameActivity {
    private int stakeTier;
    private int stakeType;
    private int stakeQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_minigame_flip);

        Intent intent = getIntent();
        stakeTier = intent.getIntExtra("tier", 0);
        stakeType = intent.getIntExtra("type", 0);
        stakeQuantity = intent.getIntExtra("quantity", 0);

        updateDisplay();
        ((TextView)findViewById(R.id.itemName)).setText(Item.getName(this, stakeTier, stakeType));
    }

    public void gamble(View v) {
        if (CalculationHelper.randomBoolean()) {
            stakeQuantity = stakeQuantity * 2;
            updateDisplay();
        } else {
            stakeQuantity = 0;
            stick();
        }
    }

    private void updateDisplay() {
        ((TextView)findViewById(R.id.currentStake)).setText(stakeQuantity + "x");
    }

    public void stick() {
        updateDisplay();
        findViewById(R.id.actionButtons).setVisibility(View.GONE);
        findViewById(R.id.close).setVisibility(View.VISIBLE);
    }

    public void forceClose(View v) {
        finish();
    }
}
