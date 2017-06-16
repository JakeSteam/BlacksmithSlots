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
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.CalculationHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class MinigameHigherActivity extends MinigameActivity {
    private List<ItemBundle> resources;
    private int multiplier = 5;
    private int currentCard = 6;
    private boolean gambleHigher = false;
    private Handler handler = new Handler();
    private int[] cardDrawables = {R.drawable.card_1, R.drawable.card_2,
            R.drawable.card_3, R.drawable.card_4, R.drawable.card_5, R.drawable.card_6,
            R.drawable.card_7, R.drawable.card_8, R.drawable.card_9, R.drawable.card_10,
            R.drawable.card_11, R.drawable.card_12, R.drawable.card_13};

    @BindView(R.id.cardImage)ImageView cardImage;
    @BindView(R.id.actionButtons)LinearLayout actionButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame_higher);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int slotId = intent.getIntExtra("slot", 0);
        if (slotId == 0) {
            confirmClose();
        }

        Slot slot = Slot.get(slotId);
        if (slot != null) {
            resources = slot.getResources();

            updateStake();
        }
    }

    @OnClick(R.id.gambleHigherButton)
    public void gambleHigher() {
        gambleHigher = true;
        gamble();
    }

    @OnClick(R.id.gambleLowerButton)
    public void gambleLower() {
        gambleHigher = false;
        gamble();
    }

    public void gamble() {
        actionButtons.setVisibility(View.INVISIBLE);

        final long startTime = System.currentTimeMillis();
        final int millisecondsToRollFor = CalculationHelper.randomNumber(1000, 5000);
        handler.post(getCardRollRunnable(startTime, millisecondsToRollFor));
    }

    @NonNull
    private Runnable getCardRollRunnable(final long startTime, final int millisecondsToRollFor) {
        return new Runnable() {
            @Override
            public void run() {
                int cardRoll = CalculationHelper.randomNumber(1, 13);
                cardImage.setImageResource(cardDrawables[cardRoll - 1]);
                cardImage.setTag(cardRoll);
                if (System.currentTimeMillis() - startTime < millisecondsToRollFor) {
                    handler.postDelayed(this, 100);
                } else {
                    rollFinished(cardRoll);
                }
            }
        };
    }

    private void rollFinished(int newCard) {
        if ((newCard > currentCard && gambleHigher) || (newCard < currentCard && !gambleHigher)) {
            currentCard = newCard;
            multiplier = multiplier * 2;
            AlertHelper.success(this, String.format(Locale.ENGLISH, getString(R.string.minigame_flip_success), multiplier), false);
            findViewById(R.id.actionButtons).setVisibility(View.VISIBLE);
        } else {
            multiplier = 0;
            AlertHelper.info(this, getString(R.string.minigame_flip_failure), false);
            stick(null);
        }
        updateStake();
    }

    private void updateStake() {
        ((TextView)findViewById(R.id.currentStake)).setText(getString(R.string.current_stake) + DisplayHelper.bundlesToString(this, resources, multiplier));
        ((TextView)findViewById(R.id.currentCard)).setText(getString(R.string.current_card) + getCardName(currentCard));
    }

    private String getCardName(int card) {
        switch (card) {
            case 1: return getString(R.string.ace);
            case 11: return getString(R.string.jack);
            case 12: return getString(R.string.queen);
            case 13: return getString(R.string.king);
            default: return Integer.toString(card);
        }
    }

    @OnClick(R.id.stickButton)
    public void stick(View v) {
        if (v == null) {
            findViewById(R.id.actionButtons).setVisibility(View.GONE);
            findViewById(R.id.close).setVisibility(View.VISIBLE);
        } else {
            confirmClose();
        }
    }

    @OnClick(R.id.close)
    public void forceClose(View v) {
        confirmClose();
    }

    @Override
    public void confirmClose() {
        setResult(multiplier, new Intent());
        finish();
    }
}
