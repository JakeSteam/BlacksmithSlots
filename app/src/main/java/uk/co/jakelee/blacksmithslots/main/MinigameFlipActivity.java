package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.CalculationHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class MinigameFlipActivity extends MinigameActivity {
    private List<ItemBundle> resources;
    private int multiplier = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame_flip);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int slotId = intent.getIntExtra("slot", 0);
        if (slotId == 0) {
            confirmClose();
        }

        Slot slot = Slot.get(slotId);
        if (slot != null) {
            resources = slot.getResources();

            updateDisplay();
            LinearLayout itemImages = findViewById(R.id.itemImageContainer);
            for (ItemBundle resource : resources) {
                itemImages.addView(DisplayHelper.createImageView(this, DisplayHelper.getItemImageFile(resource.getTier().value, resource.getType().value), 120, 120));
            }
        }
    }

    @OnClick(R.id.gambleButton)
    public void gamble(View v) {
        if (CalculationHelper.randomBoolean()) {
            multiplier = multiplier * 2;
            AlertHelper.success(this, String.format(Locale.ENGLISH, getString(R.string.minigame_flip_success), multiplier), false);
            updateDisplay();
        } else {
            multiplier = 0;
            AlertHelper.info(this, getString(R.string.minigame_flip_failure), false);
            stick(null);
        }
    }

    private void updateDisplay() {
        ((TextView)findViewById(R.id.currentStake)).setText(DisplayHelper.bundlesToString(this, resources, multiplier));
    }

    @OnClick(R.id.stickButton)
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
