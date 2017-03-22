package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.CalculationHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.model.Item;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class MinigameFlipActivity extends MinigameActivity {
    private int slotId;
    private List<ItemBundle> resources;
    private int multiplier = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_minigame_flip);

        Intent intent = getIntent();
        slotId = intent.getIntExtra("slot", 0);
        if (slotId == 0) {
            confirmClose();
        }

        resources = Slot.get(slotId).getResources();

        updateDisplay();
        for (ItemBundle resource : resources) {
            ((TextView) findViewById(R.id.itemName)).setText(Item.getName(this, resource.getTier(), resource.getType()));
            ((ImageView) findViewById(R.id.itemImage)).setImageResource(getResources().getIdentifier(DisplayHelper.getItemImageFile(resource.getTier().value, resource.getType().value), "drawable", getPackageName()));
        }
    }

    public void gamble(View v) {
        if (CalculationHelper.randomBoolean()) {
            multiplier = multiplier * 2;
            AlertHelper.success(this, "Success! Doubled to " + multiplier + "!", false);
            updateDisplay();
        } else {
            multiplier = 0;
            AlertHelper.info(this, "Unlucky, lost it all!", false);
            stick(null);
        }
    }

    private void updateDisplay() {
        ((TextView)findViewById(R.id.currentStake)).setText(multiplier + "x ");
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
        setResult(multiplier, new Intent());
        finish();
    }
}
