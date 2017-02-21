package uk.co.jakelee.blacksmithslots.helper;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private int activeRows = 9;

    private int stillSpinningSlots = 0;
    private SlotActivity activity;
    private int numSlots;
    private int resourceUsed;
    private List<WheelView> slots = new ArrayList<>();
    private List<SlotResult> items;
    private List<List<Integer>> highlightedRoutes;

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
            wheel.setVisibleItems(Constants.ROWS);

            container.addView(wheel, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            slots.add(wheel);
        }

        ((TextView)activity.findViewById(R.id.rowsActive)).setText(activeRows + " active rows");
        ((TextView)activity.findViewById(R.id.amountGambled)).setText(amountGambled + " ores gambled per row");
    }

    private void highlightTile(int row, int column, boolean applyEffect) {
        Log.d("Highlight", "Row: " + row + " Col: " + column);
        ((WheelView)((LinearLayout) activity.findViewById(R.id.slotContainer)).getChildAt(row)).itemsLayout.getChildAt(column).setAlpha(applyEffect ? 0.5f : 1.0f);
    }

    private void updateStatus() {
        TextView text = (TextView) activity.findViewById(R.id.slotResult);
        List<List<SlotResult>> results = getResults();
        List<SlotResult> wonItems = getWinnings(results);
        if (wonItems.size() > 0) {
            text.setText(applyWinnings(wonItems, amountGambled));
            updateResourceCount();
        } else {
            text.setText("No match!");
        }
    }

    private List<SlotResult> getWinnings(List<List<SlotResult>> rows) {
        List<SlotResult> winningResults = new ArrayList<>();
        List<List<Integer>> winningRoutes = new ArrayList<>();

        // Loop through possible win paths
        List<List<Integer>> routes = MatchHelper.getRoutes(rows.get(0).size(), activeRows);
        for (List<Integer> route : routes) {
            List<SlotResult> results = new ArrayList<>();

            // Loop through positions in win paths
            for (int i = 0; i < route.size(); i++) {
                results.add(rows.get(route.get(i)).get(i));
            }

            if (isAMatch(results)) {
                winningRoutes.add(route);
                winningResults.add(results.get(0));
            }
        }

        this.highlightedRoutes = winningRoutes;
        highlightResults(true);

        return winningResults;
    }

    private String applyWinnings(List<SlotResult> unmergedWinnings, int amountGambled) {
        LinkedHashMap<Integer, Integer> dataStore = new LinkedHashMap<>();
        for (SlotResult winning : unmergedWinnings) {
            Integer temp;
            if (dataStore.containsKey(winning.getResourceId())) {
                temp = dataStore.get(winning.getResourceId()) + winning.getResourceMultiplier();
                dataStore.put(winning.getResourceId(), temp);
            } else {
                dataStore.put(winning.getResourceId(), winning.getResourceMultiplier());
            }
        }

        StringBuilder winningsText = new StringBuilder().append("Won: ");
        for (Map.Entry<Integer, Integer> winning : dataStore.entrySet()) {
            Resource resource = Resource.get(winning.getKey());
            if (resource != null) {
                int quantity = winning.getValue() * amountGambled;
                Inventory.addInventory(resource.getResourceId(), quantity);
                winningsText.append(String.format(Locale.ENGLISH, "%dx %s, ", quantity, resource.getName(activity)));
            }
        }
        return winningsText.substring(0, winningsText.length() - 2);
    }

    private boolean isAMatch(List<SlotResult> routeTiles) {
        SlotResult checkedResult = new SlotResult();
        for (SlotResult routeTile : routeTiles) {
            if (checkedResult.getResourceId() == 0) {
                checkedResult = routeTile;
            } else {
                if (routeTile.getResourceId() != checkedResult.getResourceId() || routeTile.getResourceMultiplier() != checkedResult.getResourceMultiplier()) {
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

    private List<List<SlotResult>> getResults() {
        int numRows = 5;
        int topRowPosition = -2;

        // Setup data holder
        List<List<SlotResult>> rows = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            rows.add(new ArrayList<SlotResult>());
        }

        // Add data
        for (WheelView wheel : slots) {
            for (int i = 0; i < numRows; i++) {
                int position = (wheel.getCurrentItem() + (i + topRowPosition) + items.size()) % items.size();
                rows.get(i).add(items.get(position));
            }
        }

        return rows;
    }

    public void spin() {
        if (stillSpinningSlots <= 0) {
            if (highlightedRoutes != null) {
                highlightResults(false);
            }
            stillSpinningSlots = numSlots;
            Inventory inventory = Inventory.getInventory(resourceUsed);

            int spinCost = amountGambled * activeRows;
            if (inventory.getQuantity() >= spinCost) {
                inventory.setQuantity(inventory.getQuantity() - spinCost);
                inventory.save();

                for (WheelView wheel : slots) {
                    wheel.invalidateWheel(false);
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

    public void highlightResults(boolean applyEffect) {
        LinearLayout container = (LinearLayout)activity.findViewById(R.id.routesContainer);
        container.removeAllViews();

        for (List<Integer> route : highlightedRoutes) {
            String winningRoute = "";
            for (int i = 0; i < route.size(); i++) {
                winningRoute += route.get(i) + ", ";
                highlightTile(i, route.get(i), applyEffect);
            }
            TextView textView = new TextView(activity);
            textView.setText(winningRoute);
            container.addView(textView);
        }
    }

}
