package uk.co.jakelee.blacksmithslots.helper;

import android.graphics.PorterDuff;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import uk.co.jakelee.blacksmithslots.model.Item;
import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class SlotHelper {
    private int stillSpinningSlots = 0;
    private int autospinsLeft = 0;
    private SlotActivity activity;
    private Enums.Tier resourceTier;
    private Enums.Type resourceType;
    private Slot slot;
    private List<WheelView> slots = new ArrayList<>();
    private List<SlotResult> baseItems;
    private List<List<SlotResult>> items = new ArrayList<>();
    private List<WinRoute> highlightedRoutes;
    private Picasso picasso;
    private LayoutInflater inflater;
    private Handler handler = new Handler();
    private int wildcardId = (int)(long) Item.get(Enums.Tier.Internal, Enums.Type.Wildcard).getId();

    public SlotHelper(SlotActivity activity, Slot slot) {
        this.activity = activity;
        this.slot = slot;
        this.baseItems = convertToSlots(slot.getRewards());
        this.resourceTier = slot.getResourceTier();
        this.resourceType = slot.getResourceType();
        this.picasso = Picasso.with(activity);
        this.inflater = LayoutInflater.from(activity);
    }

    private List<SlotResult> convertToSlots(List<Reward> dbRewards) {
        List<SlotResult> rewards = new ArrayList<>();
        for (Reward dbReward : dbRewards) {
            for (int i = 0; i < dbReward.getWeighting(); i++) {
                rewards.add(new SlotResult(dbReward.getTier(), dbReward.getType(), dbReward.getQuantityMultiplier()));
            }
        }
        return rewards;
    }

    public void pause() {
        autospinsLeft = 0;
        handler.removeCallbacksAndMessages(null);
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
                    if (stillSpinningSlots <= 1) {
                        updateStatus();
                        afterSpinUpdate();
                        if (autospinsLeft > 0) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            spin(false);
                                        }
                                    });
                                }
                            }, 1500);
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

        afterStakeChangeUpdate();
    }

    private void afterStakeChangeUpdate() {
        ((TextView)activity.findViewById(R.id.spinButton)).setText("Spin (" + (slot.getCurrentRows() * slot.getCurrentStake()) + ")");
        ((TextView)activity.findViewById(R.id.rowsActive)).setText(Integer.toString(slot.getCurrentRows()));
        ((TextView)activity.findViewById(R.id.amountGambled)).setText(Integer.toString(slot.getCurrentStake()));
    }

    public void afterSpinUpdate() {
        ((TextView)activity.findViewById(R.id.autospinButton)).setText(autospinsLeft > 0 ? "" + autospinsLeft : "A");
        ((TextView)activity.findViewById(R.id.currentLevel)).setText("Lev " + LevelHelper.getLevel());
        int levelProgress = LevelHelper.getLevelProgress();
        Log.d("Progress", "" + levelProgress);
        ((ProgressBar)activity.findViewById(R.id.currentLevelProgress)).setProgress(levelProgress);
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
        LinkedHashMap<Pair<Enums.Tier, Enums.Type>, Integer> dataStore = new LinkedHashMap<>();
        for (SlotResult winning : unmergedWinnings) {
            Integer temp;
            Pair<Enums.Tier, Enums.Type> pair = new Pair<>(winning.getResourceTier(), winning.getResourceType());
            if (dataStore.containsKey(pair)) {
                temp = dataStore.get(pair) + winning.getResourceMultiplier();
                dataStore.put(pair, temp);
            } else {
                dataStore.put(pair, winning.getResourceMultiplier());
            }
        }

        StringBuilder winningsText = new StringBuilder().append("Won: ");
        for (Map.Entry<Pair<Enums.Tier, Enums.Type>, Integer> winning : dataStore.entrySet()) {
            Item item = Item.get(winning.getKey().first, winning.getKey().second);
            if (item != null && item.getId() != wildcardId) {
                int quantity = winning.getValue() * slot.getCurrentStake();
                Inventory.addInventory(item.getTier(), item.getType(), quantity);
                winningsText.append(String.format(Locale.ENGLISH, "%dx %s, ", quantity, item.getName(activity)));
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
            if (checkedResult.getResourceTier() == null && checkedResult.getResourceType() == null
                    && (routeTile.getResourceTier() != Enums.Tier.Internal && routeTile.getResourceType() != Enums.Type.Wildcard)) {
                checkedResult = routeTile;
            } else {
                if ((routeTile.getResourceType() != checkedResult.getResourceType() && routeTile.getResourceType() != checkedResult.getResourceType())
                        && (routeTile.getResourceTier() != Enums.Tier.Internal && routeTile.getResourceType() != Enums.Type.Wildcard)) {
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

    public void spin(boolean checkNotAutospinning) {
        if (stillSpinningSlots <= 1 && (!checkNotAutospinning || autospinsLeft <= 0)) {
            activity.findViewById(R.id.slotContainer).bringToFront();
            if (highlightedRoutes != null) {
                resetRouteColours();
                //highlightResults(false);
            }
            stillSpinningSlots = slot.getSlots();
            Inventory inventory = Inventory.getInventory(slot.getResourceTier(), slot.getResourceType());

            int spinCost = slot.getCurrentStake() * slot.getCurrentRows();
            if (inventory.getQuantity() >= spinCost) {
                inventory.setQuantity(inventory.getQuantity() - spinCost);
                inventory.save();

                LevelHelper.addXp(spinCost);
                Statistic.add(Enums.Statistic.TotalSpins);

                for (WheelView wheel : slots) {
                    wheel.scroll(-350 + (int) (Math.random() * 150), 2250);
                }
            }
            updateResourceCount();

            if (autospinsLeft > 0) {
                autospinsLeft--;
            }
        }
        afterSpinUpdate();
    }

    public void updateResourceCount() {
        List<Inventory> items = Select.from(Inventory.class).where(
                Condition.prop("tier").notEq(resourceTier.name())).or(
                Condition.prop("type").notEq(resourceType.name())
        ).orderBy("quantity DESC").list();

        Inventory inventory = Inventory.getInventory(resourceTier, resourceType);
        picasso.load(inventory.getDrawableId(activity)).into((ImageView)activity.findViewById(R.id.resourceImage));
        ((TextView)activity.findViewById(R.id.resourceInfo)).setText(inventory.getQuantity() + "x " + Item.getName(activity, resourceTier, resourceType));

        TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TableLayout layout = (TableLayout)activity.findViewById(R.id.inventoryDisplay);
        layout.removeAllViews();
        for (Inventory item : items) {
            View inflatedView = inflater.inflate(R.layout.custom_resource_info, null);
            TableRow itemRow = (TableRow) inflatedView.findViewById(R.id.itemRow);

            picasso.load(item.getDrawableId(activity)).into((ImageView)itemRow.findViewById(R.id.itemImage));
            ((TextView)itemRow.findViewById(R.id.itemInfo)).setText(item.getQuantity() + "x " + item.getName(activity));

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
            afterStakeChangeUpdate();
            resetRouteColours();
        }
    }

    public void decreaseStake() {
        if (slot.getCurrentStake() > slot.getMinimumStake() && stillSpinningSlots == 0) {
            slot.setCurrentStake(slot.getCurrentStake() - 1);
            slot.save();
            afterStakeChangeUpdate();
            resetRouteColours();
        }
    }

    public void increaseRows() {
        if (slot.getCurrentRows() < slot.getMaximumRows() && stillSpinningSlots == 0) {
            slot.setCurrentRows(slot.getCurrentRows() + 1);
            slot.save();
            afterStakeChangeUpdate();
            resetRouteColours();
        }
    }

    public void decreaseRows() {
        if (slot.getCurrentRows() > slot.getMinimumRows() && stillSpinningSlots == 0) {
            slot.setCurrentRows(slot.getCurrentRows() - 1);
            slot.save();
            afterStakeChangeUpdate();
            resetRouteColours();
        }
    }

    public void autospin(int spins) {
        if (stillSpinningSlots > 0) {
            return;
        }

        autospinsLeft = spins;
        spin(false);
    }

}
