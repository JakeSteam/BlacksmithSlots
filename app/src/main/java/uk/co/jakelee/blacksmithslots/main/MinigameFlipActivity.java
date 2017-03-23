package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        Slot slot = Slot.get(slotId);
        if (slot != null) {
            resources = slot.getResources();

            updateDisplay();
            LinearLayout itemImages = (LinearLayout) findViewById(R.id.itemImageContainer);
            for (ItemBundle resource : resources) {
                itemImages.addView(DisplayHelper.createImageView(this, DisplayHelper.getItemImageFile(resource.getTier().value, resource.getType().value), 120, 120));
            }
        }
    }

    public void gamble(View v) {
        if (CalculationHelper.randomBoolean()) {
            multiplier = multiplier * 2;
            AlertHelper.success(this, "Success! Doubled multiplier to " + multiplier + "x!", false);
            updateDisplay();
        } else {
            multiplier = 0;
            AlertHelper.info(this, "Unlucky, lost it all!", false);
            stick(null);
        }
    }

    private void updateDisplay() {
        ((TextView)findViewById(R.id.currentStake)).setText(DisplayHelper.bundlesToString(this, resources, multiplier));
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
