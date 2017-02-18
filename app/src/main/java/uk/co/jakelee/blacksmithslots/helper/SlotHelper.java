package uk.co.jakelee.blacksmithslots.helper;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.constructs.SlotResult;
import uk.co.jakelee.blacksmithslots.main.SlotActivity;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Resource;
import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class SlotHelper {
    private int stillSpinningSlots = 0;
    private boolean buttonPressed = false;
    private SlotActivity activity;
    private int numSlots;
    private int resourceUsed;
    private List<WheelView> slots = new ArrayList<>();
    private List<SlotResult> items;

    public SlotHelper(SlotActivity activity, Slot slot) {
        this.activity = activity;
        this.numSlots = slot.getSlots();
        this.items = convertToSlots(slot.getRewards());
        this.resourceUsed = slot.getResourceNeeded();
    }

    public void createWheel() {
        LinearLayout container = (LinearLayout)activity.findViewById(R.id.slotContainer);
        for (int i = 0; i < numSlots; i++) {
            WheelView wheel = new WheelView(activity);
            wheel.setViewAdapter(new SlotAdapter(wheel.getContext(), items));
            wheel.setCurrentItem(0);

            wheel.addScrollingListener(new OnWheelScrollListener() {
                @Override
                public void onScrollingStarted(WheelView wheel) {
                    stillSpinningSlots = numSlots;
                }

                @Override
                public void onScrollingFinished(WheelView wheel) {
                    if (stillSpinningSlots <= 1) {
                        buttonPressed = false;
                        updateStatus();
                    } else {
                        stillSpinningSlots--;
                    }
                }
            });
            wheel.setCyclic(true);
            wheel.setEnabled(false);

            container.addView(wheel, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            slots.add(wheel);

        }
    }

    private void updateStatus() {
        TextView text = (TextView) activity.findViewById(R.id.slotResult);
        List<SlotResult> results = getResults();
        if (doResultsMatch(results)) {
            text.setText("You win (" + results.get(0).getResourceMultiplier() + "x1) " + Resource.getName(activity, results.get(0).getResourceId()));
            Inventory.addInventory(results.get(0).getResourceId(), results.get(0).getResourceMultiplier() * 1);
            updateResourceCount();
        } else {
            text.setText("No match!");
        }
    }

    private boolean doResultsMatch(List<SlotResult> results) {
        SlotResult checkedResult = new SlotResult();
        for (SlotResult result : results) {
            if (checkedResult.getResourceId() == 0) {
                checkedResult = result;
            } else {
                if (result.getResourceId() != checkedResult.getResourceId() || result.getResourceMultiplier() != checkedResult.getResourceMultiplier()) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<SlotResult> convertToSlots(List<Reward> dbRewards) {
        List<SlotResult> rewards = new ArrayList<>();
        for (Reward dbReward : dbRewards) {
            for (int i = 0; i < dbReward.getWeighting(); i++) {
                rewards.add(new SlotResult(dbReward.getResourceId(), dbReward.getQuantityMultiplier()));
            }
        }
        return rewards;
    }

    private List<SlotResult> getResults() {
        List<SlotResult> results = new ArrayList<>();
        for (WheelView wheel : slots) {
            results.add(items.get(wheel.getCurrentItem()));
        }
        return results;
    }

    public void mixWheel() {
        if (!buttonPressed) {
            Inventory inventory = Inventory.getInventory(resourceUsed);
            if (inventory.getQuantity() > 0) {
                inventory.setQuantity(inventory.getQuantity() - 1);
                inventory.save();
                buttonPressed = true;
                for (WheelView wheel : slots) {
                    wheel.scroll(-350 + (int) (Math.random() * 50), 2000);
                }
            }
            updateResourceCount();
        }
    }

    public void updateResourceCount() {
        ((TextView)activity.findViewById(R.id.resourceCount)).setText(Inventory.getInventory(resourceUsed).getQuantity() + "x");
        ((TextView)activity.findViewById(R.id.barCount)).setText(Inventory.getInventory(Constants.BRONZE_BAR).getQuantity() + "x");
    }

}
