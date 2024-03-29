package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.helper.SoundHelper;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class MinigameChestActivity extends MinigameActivity {
    private Handler handler = new Handler();
    private boolean selected = false;
    private final List<Pair<Integer, ItemBundle>> potentialRewards = new ArrayList<>();
    private ItemBundle winnings;
    private boolean isAutospin = false;
    private ImageView autospinView;
    private final static int[] chestDrawables = {R.drawable.chest_1, R.drawable.chest_2, R.drawable.chest_3, R.drawable.chest_4, R.drawable.chest_5, R.drawable.chest_6};

    @BindView(R.id.chestContainer) LinearLayout chestContainer;
    @BindView(R.id.description) TextView description;
    @BindView(R.id.close) TextView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame_chest);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        isAutospin = intent.getBooleanExtra("autospin", false);
        int slotId = intent.getIntExtra("slot", 0);
        if (slotId == 0) {
            confirmClose();
        }

        Slot slot = Slot.get(slotId);
        if (slot != null) {
            List<ItemBundle> uniqueRewards = getUniqueRewards(slotId);
            if (uniqueRewards == null) {
                Toast.makeText(this, "Failed to find enough rewards to populate minigame chests! Please contact support.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                populatePotentialRewards(uniqueRewards);
                populateChests();
            }
        }

        if (isAutospin) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    selectChest(autospinView);
                }
            }, 1000);
        }
    }

    private void populatePotentialRewards(List<ItemBundle> rewards) {
        List<ItemBundle> rewardBundles = new ArrayList<>();
        ItemBundle rewardOne = rewards.get(0);
        ItemBundle rewardTwo = rewards.get(1);

        rewardBundles.add(new ItemBundle(rewardOne.getTier(), rewardOne.getType(), 0));
        rewardBundles.add(new ItemBundle(rewardOne.getTier(), rewardOne.getType(), 1));
        rewardBundles.add(new ItemBundle(rewardOne.getTier(), rewardOne.getType(), 5));
        rewardBundles.add(new ItemBundle(rewardOne.getTier(), rewardOne.getType(), 25));
        rewardBundles.add(new ItemBundle(rewardTwo.getTier(), rewardTwo.getType(), 1));
        rewardBundles.add(new ItemBundle(rewardTwo.getTier(), rewardTwo.getType(), 5));
        Collections.shuffle(rewardBundles);

        for (int i = 0; i < 6; i++) {
            potentialRewards.add(new Pair<>(chestDrawables[i], rewardBundles.get(i)));
        }

        Collections.shuffle(potentialRewards);
    }

    private void populateChests() {
        int index = 0;
        for (int i = 0; i <= 1; i++) {
            LinearLayout row = (LinearLayout) chestContainer.getChildAt(i);
            for (int j = 0; j <= 2; j++) {
                ImageView chest = (ImageView) row.getChildAt(j);
                try {
                    chest.setImageResource(potentialRewards.get(index).first);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
                chest.setTag(index++);

                if (isAutospin && autospinView == null) {
                    autospinView = chest;
                }
            }
        }
    }

    public void selectChest(View v) {
        if (!selected) {
            SoundHelper.playSound(this, SoundHelper.chestSounds);
            selected = true;
            Pair<Integer, ItemBundle> rewardPair = potentialRewards.get((int) v.getTag());
            winnings = rewardPair.second;

            description.setText(getWinText(winnings));
            ((ImageView)v).setImageResource(getWinImage(winnings));

            close.setVisibility(View.VISIBLE);
        }

        if (isAutospin) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    confirmClose();
                }
            }, 1000);
        }
    }

    private int getWinImage(ItemBundle reward) {
        if (reward.getQuantity() > 0) {
            return getResources().getIdentifier(DisplayHelper.getItemImageFile(reward.getTier().value, reward.getType().value), "drawable", getPackageName());
        } else {
            return R.drawable.item_999_999;
        }
    }

    private String getWinText(ItemBundle reward) {
        switch (reward.getQuantity()) {
            case 0: return getString(R.string.minigame_chest_win_0);
            case 1: return String.format(Locale.ENGLISH, getString(R.string.minigame_chest_win_1), reward.toString(this));
            case 5: return String.format(Locale.ENGLISH, getString(R.string.minigame_chest_win_5), reward.toString(this));
            case 25: return String.format(Locale.ENGLISH, getString(R.string.minigame_chest_win_25), reward.toString(this));
            default: return "";
        }
    }

    private List<ItemBundle> getUniqueRewards(int slotId) {
        List<ItemBundle> rewards = ItemBundle.find(ItemBundle.class,
                "f = " + Enums.ItemBundleType.SlotReward.value + " AND " +
                "a = " + slotId + " AND " +
                "b <> " + Enums.Tier.Internal.value +
                " GROUP BY b, c");
        return rewards.size() < 2 ? null : rewards;
    }

    public void forceClose(View v) {
        confirmClose();
    }

    @Override
    public void confirmClose() {
        if (winnings != null) {
            setResult(1, new Intent()
                    .putExtra("tier", winnings.getTier().value)
                    .putExtra("type", winnings.getType().value)
                    .putExtra("quantity", winnings.getQuantity()));
        }
        finish();
    }
}
