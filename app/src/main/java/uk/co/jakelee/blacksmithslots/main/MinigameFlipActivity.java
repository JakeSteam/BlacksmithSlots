package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.CalculationHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
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
        stakeQuantity = intent.getIntExtra("quantity", 0) * 5;

        if (stakeTier == 0 || stakeType == 0 || stakeQuantity == 0) {
            confirmClose();
        }

        updateDisplay();
        ((TextView)findViewById(R.id.itemName)).setText(Item.getName(this, stakeTier, stakeType));
        ((ImageView)findViewById(R.id.itemImage)).setImageResource(getResources().getIdentifier(DisplayHelper.getItemImageFile(stakeTier, stakeType), "drawable", getPackageName()));
    }

    public void gamble(View v) {
        if (CalculationHelper.randomBoolean()) {
            stakeQuantity = stakeQuantity * 2;
            AlertHelper.success(this, "Success! Doubled to " + stakeQuantity + "!", false);
            updateDisplay();
        } else {
            stakeQuantity = 0;
            AlertHelper.info(this, "Unlucky, lost it all!", false);
            stick(null);
        }
    }

    private void updateDisplay() {
        ((TextView)findViewById(R.id.currentStake)).setText(stakeQuantity + "x ");
    }

    public void stick(View v) {
        if (v == null) {
            updateDisplay();
            findViewById(R.id.actionButtons).setVisibility(View.GONE);
            findViewById(R.id.close).setVisibility(View.VISIBLE);
        } else {
            confirmClose();
        }
    }

    public void forceClose(View v) {
        confirmClose();
    }

    @Override
    public void confirmClose() {
        setResult(stakeQuantity, new Intent());
        finish();
    }
}
