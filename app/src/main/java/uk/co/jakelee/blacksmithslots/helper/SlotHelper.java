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
    private int amountGambled = 1;
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
        List<List<SlotResult>> results = getResults();
        List<SlotResult> wonItems = getWinnings(results);
        if (wonItems.size() > 0) {
            text.setText("You win (" + wonItems.get(0).getResourceMultiplier() + "x" + amountGambled + ") " + Resource.getName(activity, wonItems.get(0).getResourceId()));
            Inventory.addInventory(wonItems.get(0).getResourceId(), wonItems.get(0).getResourceMultiplier() * amountGambled);
            updateResourceCount();
        } else {
            text.setText("No match!");
        }
    }

    private List<SlotResult> getWinnings(List<List<SlotResult>> rows) {
        List<SlotResult> winningResults = new ArrayList<>();
        for (List<SlotResult> row : rows) {
            boolean matchFailure = false;
            SlotResult checkedResult = new SlotResult();
            for (SlotResult result : row) {
                if (checkedResult.getResourceId() == 0) {
                    checkedResult = result;
                } else {
                    if (result.getResourceId() != checkedResult.getResourceId() || result.getResourceMultiplier() != checkedResult.getResourceMultiplier()) {
                        matchFailure = true;
                        break;
                    }
                }
            }

            if (!matchFailure) {
                winningResults.add(checkedResult);
                Log.d("Won", checkedResult.getResourceMultiplier() + "x " + checkedResult.getResourceId());
            }
        }
        return winningResults;
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

    private List<List<SlotResult>> getResults() {
        // Setup data holder
        List<List<SlotResult>> rows = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            rows.add(new ArrayList<SlotResult>());
        }

        // Add data
        for (WheelView wheel : slots) {
            for (int i = 0; i < 5; i++) {
                Log.d("Lookup", "Curr: " + wheel.getCurrentItem() + ", i: " + i);
                int targetPosition = (wheel.getCurrentItem() + (i-2) + items.size()) % items.size();
                rows.get(i).add(items.get(targetPosition));
            }
        }

        return rows;
    }

    public void mixWheel() {
        if (stillSpinningSlots <= 0) {
            stillSpinningSlots = numSlots;
            Inventory inventory = Inventory.getInventory(resourceUsed);
            if (inventory.getQuantity() >= amountGambled) {
                inventory.setQuantity(inventory.getQuantity() - amountGambled);
                inventory.save();

                for (WheelView wheel : slots) {
                    wheel.scroll(-350 + (int) (Math.random() * 150), 2250);
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
