package uk.co.jakelee.blacksmithslots.helper;

import android.util.Log;
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
    private int amountGambled = 2;
    private int stillSpinningSlots = 0;
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
                }

                @Override
                public void onScrollingFinished(WheelView wheel) {
                    stillSpinningSlots--;
                    if (stillSpinningSlots <= 0) {
                        updateStatus();
                    }
                }
            });
            wheel.setCyclic(true);
            wheel.setEnabled(false);
            wheel.setVisibleItems(5);

            container.addView(wheel, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            slots.add(wheel);

        }
    }

    private void updateStatus() {
        TextView text = (TextView) activity.findViewById(R.id.slotResult);
        List<SlotResult> results = getResults();
        if (doResultsMatch(results)) {
            text.setText("You win (" + results.get(0).getResourceMultiplier() + "x" + amountGambled + ") " + Resource.getName(activity, results.get(0).getResourceId()));
            Inventory.addInventory(results.get(0).getResourceId(), results.get(0).getResourceMultiplier() * amountGambled);
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
        if (stillSpinningSlots <= 0) {
            stillSpinningSlots = numSlots;
            Inventory inventory = Inventory.getInventory(resourceUsed);
            if (inventory.getQuantity() >= amountGambled) {
                inventory.setQuantity(inventory.getQuantity() - amountGambled);
                inventory.save();

                for (WheelView wheel : slots) {
                    int scrollItems = -350 + (int) (Math.random() * 150);
                    Log.d("Scroll", scrollItems + "");
                    wheel.scroll(scrollItems, 2250);
                }
            }
            updateResourceCount();
        }
    }

    public void updateResourceCount() {
        List<Inventory> items = Inventory.listAll(Inventory.class);
        LinearLayout container = (LinearLayout)activity.findViewById(R.id.inventoryContainer);
        container.removeAllViews();

        for (Inventory inventory : items) {
            String name = Resource.getName(activity, inventory.getItemId());
            TextView textView = new TextView(activity);
            textView.setText(inventory.getQuantity() + "x " + name);
            container.addView(textView);
        }
    }

}
