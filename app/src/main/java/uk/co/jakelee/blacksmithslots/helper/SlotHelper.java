package uk.co.jakelee.blacksmithslots.helper;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import uk.co.jakelee.blacksmithslots.constructs.WinRoute;
import uk.co.jakelee.blacksmithslots.main.SlotActivity;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Resource;
import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class SlotHelper {
    private int stillSpinningSlots = 0;
    private int autospinsLeft = 0;
    private SlotActivity activity;
    private int resourceUsed;
    private Slot slot;
    private List<WheelView> slots = new ArrayList<>();
    private List<SlotResult> baseItems;
    private List<List<SlotResult>> items = new ArrayList<>();
    private List<WinRoute> highlightedRoutes;
    private Picasso picasso;
    private LayoutInflater inflater;

    public SlotHelper(SlotActivity activity, Slot slot) {
        this.activity = activity;
        this.slot = slot;
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

    public void createRoutes() {
        RelativeLayout container = (RelativeLayout)activity.findViewById(R.id.slotArea);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int slotContainerId = container.findViewById(R.id.slotContainer).getId();
        params.addRule(RelativeLayout.ALIGN_TOP, slotContainerId);
        params.addRule(RelativeLayout.ALIGN_LEFT, slotContainerId);
        params.addRule(RelativeLayout.ALIGN_RIGHT, slotContainerId);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, slotContainerId);

        List<WinRoute> allRoutes = MatchHelper.getRoutes(slot.getSlots(), 0);
        for (int i = 1; i <= allRoutes.size(); i++) {
            int routeResource = activity.getResources().getIdentifier("route_" + slot.getSlots() + "_" + i, "drawable", activity.getPackageName());

            ImageView routeIndicator = (ImageView)inflater.inflate(R.layout.custom_route_indicator, null);
            routeIndicator.setId(activity.getResources().getIdentifier("route_" + i, "id", activity.getPackageName()));
            routeIndicator.setImageResource(routeResource);
            if (i <= slot.getCurrentRows()) {
                routeIndicator.setColorFilter(ContextCompat.getColor(activity, R.color.blue), PorterDuff.Mode.MULTIPLY);
            }
            container.addView(routeIndicator, params);
        }
    }

    public void createWheel() {
        LinearLayout container = (LinearLayout)activity.findViewById(R.id.slotContainer);
        for (int i = 0; i < slot.getSlots(); i++) {
            WheelView wheel = new WheelView(activity);

            // Create + store shuffled list of items
            items.add(new ArrayList(baseItems));
            Collections.shuffle(items.get(i));

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            wheel.setViewAdapter(new SlotAdapter(wheel.getContext(), displayMetrics, items.get(i)));
            wheel.setCurrentItem(0);

            wheel.addScrollingListener(new OnWheelScrollListener() {
                @Override
                public void onScrollingStarted(WheelView wheel) {
                    stillSpinningSlots = slot.getSlots();
                }

                @Override
                public void onScrollingFinished(WheelView wheel) {
                    stillSpinningSlots--;
                    Log.d("SpinFin1", "Left:" + autospinsLeft + ", Slots:" + stillSpinningSlots);
                    if (stillSpinningSlots <= 1) {
                        updateStatus();
                        Log.d("SpinFin2", "Left:" + autospinsLeft + ", Slots:" + stillSpinningSlots);
                        if (autospinsLeft > 0) {
                            spin();
                        }
                    }
                }
            });
            wheel.setCyclic(true);
            wheel.setEnabled(false);
            wheel.setVisibleItems(Constants.ROWS);

            container.addView(wheel, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
            slots.add(wheel);
        }

        updateSpinInfo();
    }

    private void updateSpinInfo() {
        ((TextView)activity.findViewById(R.id.spinButton)).setText("Spin (" + (slot.getCurrentRows() * slot.getCurrentStake()) + ")");
        ((TextView)activity.findViewById(R.id.rowsActive)).setText(Integer.toString(slot.getCurrentRows()));
        ((TextView)activity.findViewById(R.id.amountGambled)).setText(Integer.toString(slot.getCurrentStake()));
    }

    private void updateStatus() {
        List<List<SlotResult>> results = getResults();
        List<SlotResult> wonItems = getWinnings(results);
        if (wonItems.size() > 0) {
            Toast.makeText(activity, applyWinnings(wonItems), Toast.LENGTH_SHORT).show();
            updateResourceCount();
        } else {
            Toast.makeText(activity, "No matches!", Toast.LENGTH_SHORT).show();
        }
    }

    private String applyWinnings(List<SlotResult> unmergedWinnings) {
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
                int quantity = winning.getValue() * slot.getCurrentStake();
                Inventory.addInventory(resource.getResourceId(), quantity);
                winningsText.append(String.format(Locale.ENGLISH, "%dx %s, ", quantity, resource.getName(activity)));
            }
        }
        return winningsText.substring(0, winningsText.length() - 2);
    }

    private List<SlotResult> getWinnings(List<List<SlotResult>> rows) {
        List<SlotResult> winningResults = new ArrayList<>();
        List<WinRoute> winningRoutes = new ArrayList<>();

        // Loop through possible win paths
        List<WinRoute> routes = MatchHelper.getRoutes(rows.get(0).size(), slot.getCurrentRows());
        for (int i = 0; i < routes.size(); i++) {
            List<SlotResult> results = new ArrayList<>();

            WinRoute route = routes.get(i);
            // Loop through positions in win paths
            for (int j = 0; j < route.size(); j++) {
                results.add(rows.get(route.get(j)).get(j));
            }

            if (isAMatch(results)) {
                winningRoutes.add(route);
                winningResults.addAll(results);

                // Bring winning route to front. +1 due to bottom bar
                ImageView routeImage = (ImageView)activity.findViewById(activity.getResources().getIdentifier("route_" + (i + 1), "id", activity.getPackageName()));
                if (routeImage != null) {
                    routeImage.setColorFilter(ContextCompat.getColor(activity, R.color.green), PorterDuff.Mode.MULTIPLY);
                    routeImage.bringToFront();
                }
            }
        }

        this.highlightedRoutes = winningRoutes;
        //highlightResults(true);

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

        // Setup data holder
        List<List<SlotResult>> rows = new ArrayList<>();
        for (int i = 0; i < Constants.ROWS; i++) {
            rows.add(new ArrayList<SlotResult>());
        }

        // Add data
        for (int i = 0; i < slots.size(); i++) {
            for (int j = 0; j < Constants.ROWS; j++) {
                // -2 is to offset, so top row is 0 not -2.
                int position = (slots.get(i).getCurrentItem() + (j - 2) + items.get(i).size()) % items.get(i).size();
                rows.get(j).add(items.get(i).get(position));
            }
        }

        return rows;
    }

    public void spin() {
        if (stillSpinningSlots <= 1) {
            activity.findViewById(R.id.slotContainer).bringToFront();
            if (highlightedRoutes != null) {
                resetRouteColours();
                //highlightResults(false);
            }
            stillSpinningSlots = slot.getSlots();
            Inventory inventory = Inventory.getInventory(resourceUsed);

            int spinCost = slot.getCurrentStake() * slot.getCurrentRows();
            if (inventory.getQuantity() >= spinCost) {
                inventory.setQuantity(inventory.getQuantity() - spinCost);
                inventory.save();

                for (WheelView wheel : slots) {
                    wheel.scroll(-350 + (int) (Math.random() * 150), 2250);
                }
            }
            updateResourceCount();

            if (autospinsLeft > 0) {
                autospinsLeft--;
            }
            Log.d("Spin", "Left:" + autospinsLeft + ", Slots:" + stillSpinningSlots);
        }
    }

    public void updateResourceCount() {
        List<Inventory> items = Select.from(Inventory.class).where(
                Condition.prop("item_id").notEq(resourceUsed)).list();

        Inventory inventory = Inventory.getInventory(resourceUsed);
        picasso.load(R.drawable.item_1_1).into((ImageView)activity.findViewById(R.id.resourceImage));
        ((TextView)activity.findViewById(R.id.resourceInfo)).setText(inventory.getQuantity() + "x " + Resource.getName(activity, resourceUsed));

        TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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

    private void resetRouteColours() {
        for (int i = 1; i <= slot.getMaximumRows(); i++) {
            ImageView routeImage = (ImageView)activity.findViewById(activity.getResources().getIdentifier("route_" + (i), "id", activity.getPackageName()));
            if (routeImage != null) {
                if (i <= slot.getCurrentRows()) {
                    routeImage.setColorFilter(ContextCompat.getColor(activity, R.color.blue), PorterDuff.Mode.MULTIPLY);
                } else {
                    routeImage.clearColorFilter();
                }
            }
        }
    }

    private void highlightResults(boolean applyEffect) {
        StringBuilder winningRoutes = new StringBuilder();
        LinearLayout slotContainer = (LinearLayout)activity.findViewById(R.id.slotContainer);
        for (WinRoute route : highlightedRoutes) {
            for (int i = 0; i < route.size(); i++) {
                highlightTile(slotContainer, i, route.get(i), applyEffect);
            }
            winningRoutes.append(route.toString());
            winningRoutes.append("\n");
        }
        Log.d("Routes", winningRoutes.toString());
    }

    private void highlightTile(LinearLayout slotContainer, int row, int column, boolean applyEffect) {
        ((WheelView)slotContainer.getChildAt(row)).itemsLayout.getChildAt(column).setAlpha(applyEffect ? 0.5f : 1.0f);
    }

    public void increaseStake() {
        if (slot.getCurrentStake() < slot.getMaximumStake() && stillSpinningSlots == 0) {
            slot.setCurrentStake(slot.getCurrentStake() + 1);
            slot.save();
            updateSpinInfo();
            resetRouteColours();
        }
    }

    public void decreaseStake() {
        if (slot.getCurrentStake() > slot.getMinimumStake() && stillSpinningSlots == 0) {
            slot.setCurrentStake(slot.getCurrentStake() - 1);
            slot.save();
            updateSpinInfo();
            resetRouteColours();
        }
    }

    public void increaseRows() {
        if (slot.getCurrentRows() < slot.getMaximumRows() && stillSpinningSlots == 0) {
            slot.setCurrentRows(slot.getCurrentRows() + 1);
            slot.save();
            updateSpinInfo();
            resetRouteColours();
        }
    }

    public void decreaseRows() {
        if (slot.getCurrentRows() > slot.getMinimumRows() && stillSpinningSlots == 0) {
            slot.setCurrentRows(slot.getCurrentRows() - 1);
            slot.save();
            updateSpinInfo();
            resetRouteColours();
        }
    }

    public void autospin(int spins) {
        if (stillSpinningSlots > 0) {
            return;
        }

        autospinsLeft = spins;
        spin();
    }

}
