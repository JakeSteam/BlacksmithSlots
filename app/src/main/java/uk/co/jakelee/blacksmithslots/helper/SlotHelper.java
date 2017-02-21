package uk.co.jakelee.blacksmithslots.helper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
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
    private int activeRows = 29;

    private int stillSpinningSlots = 0;
    private SlotActivity activity;
    private int numSlots;
    private int resourceUsed;
    private List<WheelView> slots = new ArrayList<>();
    private List<SlotResult> baseItems;
    private List<List<SlotResult>> items = new ArrayList<>();
    private List<List<Integer>> highlightedRoutes;
    private Picasso picasso;
    private LayoutInflater inflater;

    public SlotHelper(SlotActivity activity, Slot slot) {
        this.activity = activity;
        this.numSlots = slot.getSlots();
        this.baseItems = convertToSlots(slot.getRewards());
        this.resourceUsed = slot.getResourceNeeded();
        this.picasso = Picasso.with(activity);
        this.inflater = LayoutInflater.from(activity);
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

    public void createWheel() {
        LinearLayout container = (LinearLayout)activity.findViewById(R.id.slotContainer);
        for (int i = 0; i < numSlots; i++) {
            WheelView wheel = new WheelView(activity);

            // Create + store shuffled list of items
            items.add(new ArrayList(baseItems));
            Collections.shuffle(items.get(i));
            wheel.setViewAdapter(new SlotAdapter(wheel.getContext(), items.get(i)));
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

        ((TextView)activity.findViewById(R.id.rowsActive)).setText("Rows: " + activeRows);
        ((TextView)activity.findViewById(R.id.amountGambled)).setText("Stake: " + amountGambled);
    }

    private void updateStatus() {
        List<List<SlotResult>> results = getResults();
        List<SlotResult> wonItems = getWinnings(results);
        if (wonItems.size() > 0) {
            Toast.makeText(activity, applyWinnings(wonItems, amountGambled), Toast.LENGTH_SHORT).show();
            updateResourceCount();
        } else {
            Toast.makeText(activity, "No matches!", Toast.LENGTH_SHORT).show();
        }
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
            if (resource != null && resource.getResourceId() != Constants.RES_WILDCARD) {
                int quantity = winning.getValue() * amountGambled;
                Inventory.addInventory(resource.getResourceId(), quantity);
                winningsText.append(String.format(Locale.ENGLISH, "%dx %s, ", quantity, resource.getName(activity)));
            }
        }
        return winningsText.substring(0, winningsText.length() - 2);
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
                winningResults.addAll(results);
            }
        }

        this.highlightedRoutes = winningRoutes;
        highlightResults(true);

        return winningResults;
    }

    private boolean isAMatch(List<SlotResult> routeTiles) {
        SlotResult checkedResult = new SlotResult();
        for (SlotResult routeTile : routeTiles) {
            // If there's no tile to check, set it to current
            if (checkedResult.getResourceId() == 0 && routeTile.getResourceId() != Constants.RES_WILDCARD) {
                checkedResult = routeTile;
            } else {
                if (routeTile.getResourceId() != checkedResult.getResourceId() && routeTile.getResourceId() != Constants.RES_WILDCARD) {
                    return false;
                }
            }
        }
        return true;
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
        for (int i = 0; i < slots.size(); i++) {
            for (int j = 0; j < numRows; j++) {
                int position = (slots.get(i).getCurrentItem() + (j + topRowPosition) + items.get(i).size()) % items.get(i).size();
                rows.get(j).add(items.get(i).get(position));
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
        List<Inventory> items = Select.from(Inventory.class).where(
                Condition.prop("item_id").notEq(resourceUsed)).list();

        Inventory inventory = Inventory.getInventory(resourceUsed);
        picasso.load(R.drawable.item_1).into((ImageView)activity.findViewById(R.id.resourceImage));
        ((TextView)activity.findViewById(R.id.resourceInfo)).setText(inventory.getQuantity() + "x " + Resource.getName(activity, resourceUsed));

        TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 5, 0, 5);

        TableLayout layout = (TableLayout)activity.findViewById(R.id.inventoryDisplay);
        layout.removeAllViews();
        for (Inventory item : items) {
            View inflatedView = inflater.inflate(R.layout.custom_resource_info, null);
            TableRow itemRow = (TableRow) inflatedView.findViewById(R.id.itemRow);

            picasso.load(item.getDrawableId(activity)).into((ImageView)itemRow.findViewById(R.id.itemImage));
            ((TextView)itemRow.findViewById(R.id.itemInfo)).setText(item.getQuantity() + "x " + Resource.getName(activity, item.getItemId()));

            layout.addView(itemRow, params);
        }
    }

    private void highlightResults(boolean applyEffect) {
        StringBuilder winningRoutes = new StringBuilder();
        LinearLayout slotContainer = (LinearLayout)activity.findViewById(R.id.slotContainer);
        for (List<Integer> route : highlightedRoutes) {
            for (int i = 0; i < route.size(); i++) {
                winningRoutes.append(route.get(i));
                winningRoutes.append(", ");
                highlightTile(slotContainer, i, route.get(i), applyEffect);
            }
            winningRoutes.append("\n");
        }
        Log.d("Routes", winningRoutes.toString());
    }

    private void highlightTile(LinearLayout slotContainer, int row, int column, boolean applyEffect) {
        Log.d("Highlight", "Row: " + row + " Col: " + column);
        ((WheelView)slotContainer.getChildAt(row)).itemsLayout.getChildAt(column).setAlpha(applyEffect ? 0.5f : 1.0f);
    }

}
