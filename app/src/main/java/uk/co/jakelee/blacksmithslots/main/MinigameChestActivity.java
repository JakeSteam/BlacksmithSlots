package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class MinigameChestActivity extends MinigameActivity {
    private boolean selected = false;
    private List<Pair<Integer, ItemBundle>> potentialRewards = new ArrayList<>();
    private ItemBundle winnings;
    public final static int[] chestDrawables = {R.drawable.chest_1, R.drawable.chest_2, R.drawable.chest_3, R.drawable.chest_4, R.drawable.chest_5, R.drawable.chest_6};

    @BindView(R.id.chestContainer) LinearLayout chestContainer;
    @BindView(R.id.description) TextView description;
    @BindView(R.id.close) TextView close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame_chest);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        int slotId = intent.getIntExtra("slot", 0);
        if (slotId == 0) {
            confirmClose();
        }

        Slot slot = Slot.get(slotId);
        if (slot != null) {

            List<ItemBundle> uniqueRewards = getUniqueRewards(slotId);
            if (uniqueRewards == null) {
                finish();
            } else {
                populatePotentialRewards(uniqueRewards);
                populateChests();
            }
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
                chest.setImageResource(potentialRewards.get(index).first);
                chest.setTag(index++);
            }
        }
    }

    public void selectChest(View v) {
        if (!selected) {
            selected = true;
            Pair<Integer, ItemBundle> rewardPair = potentialRewards.get((int) v.getTag());
            winnings = rewardPair.second;

            description.setText(getWinText(winnings));
            ((ImageView)v).setImageResource(getWinImage(winnings));

            close.setVisibility(View.VISIBLE);
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
            case 0: return "Unlucky, you won nothing!";
            case 1: return "Well, it's something.. you won " + reward.toString(this) + ".";
            case 5: return "Pretty good! You won " + reward.toString(this) + "!";
            case 25: return "Wow, very nice! You've earned yourself " + reward.toString(this) + "!";
            default: return "";
        }
    }

    private List<ItemBundle> getUniqueRewards(int slotId) {
        List<ItemBundle> rewards = ItemBundle.find(ItemBundle.class,
                "f = " + Enums.ItemBundleType.SlotReward.value + " AND " +
                "a = " + slotId + " AND " +
                "b <> \"" + Enums.Tier.Internal +
                "\" GROUP BY b, c");
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
