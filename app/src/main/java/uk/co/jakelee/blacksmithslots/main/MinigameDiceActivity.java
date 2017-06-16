package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.CalculationHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.SoundHelper;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class MinigameDiceActivity extends MinigameActivity {
    private List<ItemBundle> resources;
    private int multiplier = 1;
    private int diceRolled = 0;
    private final Handler handler = new Handler();
    private final int[] diceDrawables = {R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3, R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6};

    @BindView(R.id.diceContainer) LinearLayout diceContainer;
    @BindView(R.id.description) TextView description;
    @BindView(R.id.roll) TextView roll;
    @BindView(R.id.close) TextView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame_dice);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int slotId = intent.getIntExtra("slot", 0);
        if (slotId == 0) {
            confirmClose();
        }

        Slot slot = Slot.get(slotId);
        if (slot != null) {
            resources = slot.getResources();
        }
    }

    @OnClick(R.id.roll)
    public void roll(View v) {
        SoundHelper.playSound(this, SoundHelper.diceSounds);
        roll.setVisibility(View.INVISIBLE);

        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < diceContainer.getChildCount(); i++) {
            final ImageView dice = (ImageView)diceContainer.getChildAt(i);
            final int millisecondsToRollFor = CalculationHelper.randomNumber(1000, 5000);
            handler.post(getDiceRollRunnable(startTime, dice, millisecondsToRollFor));

        }
    }

    @NonNull
    private Runnable getDiceRollRunnable(final long startTime, final ImageView dice, final int millisecondsToRollFor) {
        return new Runnable() {
            @Override
            public void run() {
                int diceRoll = CalculationHelper.randomNumber(1, 6);
                dice.setImageResource(diceDrawables[diceRoll - 1]);
                dice.setTag(diceRoll);
                if (System.currentTimeMillis() - startTime < millisecondsToRollFor) {
                    handler.postDelayed(this, 100);
                } else {
                    rollFinished();
                }
            }
        };
    }

    private void rollFinished() {
        diceRolled++;
        if (diceRolled >= diceContainer.getChildCount()) {
            roll.setVisibility(View.GONE);
            close.setVisibility(View.VISIBLE);

            multiplier = 0;
            for (int i = 0; i < diceContainer.getChildCount(); i++) {
                ImageView dice = (ImageView) diceContainer.getChildAt(i);
                multiplier += (int) dice.getTag();
            }

            description.setText(String.format(Locale.ENGLISH, getString(R.string.dice_total_multiplier),
                    multiplier,
                    DisplayHelper.bundlesToString(this, resources, multiplier)));
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
