package uk.co.jakelee.blacksmithslots.helper;

import android.content.Intent;
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
import uk.co.jakelee.blacksmithslots.constructs.ItemResult;
import uk.co.jakelee.blacksmithslots.constructs.WinRoute;
import uk.co.jakelee.blacksmithslots.main.MinigameFlipActivity;
import uk.co.jakelee.blacksmithslots.main.SlotActivity;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Item;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Message;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Statistic;

import static android.R.attr.name;

public class SlotHelper {
    private int stillSpinningSlots = 0;
    public int autospinsLeft = 0;
    private SlotActivity activity;
    private Slot slot;
    private List<WheelView> slots = new ArrayList<>();
    private List<ItemResult> baseItems;
    private List<List<ItemResult>> items = new ArrayList<>();
    private List<WinRoute> highlightedRoutes;
    private Picasso picasso;
    private LayoutInflater inflater;
    private Handler handler;
    private Enums.Type minigameToLoad = null;

    public SlotHelper(SlotActivity activity, Handler handler, Slot slot) {
        this.activity = activity;
        this.slot = slot;
        this.baseItems = convertToSlots(slot.getRewards());
        this.picasso = Picasso.with(activity);
        this.inflater = LayoutInflater.from(activity);
        this.handler = handler;
    }

    private List<ItemResult> convertToSlots(List<ItemBundle> dbItemBundles) {
        List<ItemResult> rewards = new ArrayList<>();
        for (ItemBundle dbItemBundle : dbItemBundles) {
            for (int i = 0; i < dbItemBundle.getWeighting(); i++) {
                rewards.add(new ItemResult(dbItemBundle.getTier(), dbItemBundle.getType(), dbItemBundle.getQuantity()));
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.MINIGAME_FLIP) {
            if (resultCode > 0) {
                StringBuilder itemText = new StringBuilder();
                for (ItemBundle itemBundle : slot.getResources()) {
                    if (itemBundle.getTier() != Enums.Tier.Internal) {
                        itemText.append(itemBundle.getQuantity() * resultCode);
                        itemText.append("x ");
                        itemText.append(Item.getName(activity, itemBundle.getTier(), itemBundle.getType()));
                        itemText.append(", ");
                        Inventory.addInventory(itemBundle.getTier(), itemBundle.getType(), itemBundle.getQuantity() * resultCode);
                    }
                }

                String resourceString = itemText.toString();
                resourceString = resourceString.length() > 0 ? resourceString.substring(0, resourceString.length() - 2) : "";

                AlertHelper.success(activity, "Won " + resourceString + " from flip minigame!", true);
            } else {
                AlertHelper.info(activity, "Unlucky, won nothing from flip minigame!", false);
            }
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
                    if (stillSpinningSlots <= 0) {
                        updateStatus();
                        afterSpinUpdate();
                        if (minigameToLoad != null) {
                            activity.startActivityForResult(new Intent(activity, MinigameFlipActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                    .putExtra("slot", slot.getSlotId()),
                                    Constants.MINIGAME_FLIP);
                            minigameToLoad = null;
                        } else if (autospinsLeft > 0) {
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
        ((TextView)activity.findViewById(R.id.spinButton)).setText("Spin (" + (slot.getCurrentRows() * slot.getCurrentStake()) + "x)");
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
        List<List<ItemResult>> results = retrieveSpinResult();
        List<ItemResult> wonItems = getWinnings(results);
        if (wonItems.size() > 0) {
            String winText = applyWinnings(wonItems);
            if (winText.length() > 0) {
                Message.logSpin(activity, slot.getSlotId(), winText);
                AlertHelper.success(activity, winText, false);
            }
            updateResourceCount();
        } else {
            AlertHelper.info(activity, R.string.alert_no_matches, false);
        }
    }

    private String applyWinnings(List<ItemResult> unmergedWinnings) {
        LinkedHashMap<Pair<Enums.Tier, Enums.Type>, Integer> dataStore = new LinkedHashMap<>();
        for (ItemResult winning : unmergedWinnings) {
            Integer temp;
            Pair<Enums.Tier, Enums.Type> pair = new Pair<>(winning.getResourceTier(), winning.getResourceType());
            if (dataStore.containsKey(pair)) {
                temp = dataStore.get(pair) + winning.getResourceQuantity();
                dataStore.put(pair, temp);
            } else {
                dataStore.put(pair, winning.getResourceQuantity());
            }
        }

        int totalResourcesWon = 0;
        StringBuilder winningsText = new StringBuilder().append("Won: ");
        for (Map.Entry<Pair<Enums.Tier, Enums.Type>, Integer> winning : dataStore.entrySet()) {
            Item item = Item.get(winning.getKey().first, winning.getKey().second);
            if (item != null) {
                if (item.getTier() != Enums.Tier.Internal) {
                    int quantity = winning.getValue() * slot.getCurrentStake();
                    totalResourcesWon += quantity;
                    Inventory.addInventory(item.getTier(), item.getType(), quantity);
                    winningsText.append(String.format(Locale.ENGLISH, "%dx %s, ", quantity, item.getName(activity)));
                } else {
                    // Wowsa, they got a minigame match!
                    if (item.getType() != Enums.Type.Wildcard) {
                        autospinsLeft = 0;
                        minigameToLoad = item.getType();
                    }
                }
            }
        }

        Statistic.add(Enums.Statistic.ResourcesWon, totalResourcesWon);
        return winningsText.length() > 5 ? winningsText.substring(0, winningsText.length() - 2) : "";
    }

    private List<ItemResult> getWinnings(List<List<ItemResult>> rows) {
        List<ItemResult> winningResults = new ArrayList<>();
        List<WinRoute> winningRoutes = new ArrayList<>();

        // Loop through possible win paths
        List<WinRoute> routes = MatchHelper.getRoutes(rows.get(0).size(), slot.getCurrentRows());
        for (int i = 0; i < routes.size(); i++) {
            List<ItemResult> results = new ArrayList<>();

            WinRoute route = routes.get(i);
            // Loop through positions in win paths
            for (int j = 0; j < route.size(); j++) {
                results.add(rows.get(route.get(j)).get(j));
            }

            if (isRouteAWin(results)) {
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

    private boolean isRouteAWin(List<ItemResult> routeTiles) {
        ItemResult checkedResult = new ItemResult();
        for (ItemResult routeTile : routeTiles) {
            // If there's no tile to check, set it to current
            if (checkedResult.getResourceTier() == null && checkedResult.getResourceType() == null
                    && (routeTile.getResourceType() != Enums.Type.Wildcard)) {
                checkedResult = routeTile;
            } else {
                if ((routeTile.getResourceType() != checkedResult.getResourceType() && routeTile.getResourceType() != checkedResult.getResourceType())
                        && (routeTile.getResourceType() != Enums.Type.Wildcard)) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<List<ItemResult>> retrieveSpinResult() {

        // Setup data holder
        List<List<ItemResult>> rows = new ArrayList<>();
        for (int i = 0; i < Constants.ROWS; i++) {
            rows.add(new ArrayList<ItemResult>());
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
            boolean canAfford = true;
            for (ItemBundle item : slot.getResources()) {
                Inventory inventory = Inventory.getInventory(item.getTier().value, item.getType().value);
                int spinCost = slot.getCurrentStake() * slot.getCurrentRows() * item.getQuantity();
                if (inventory.getQuantity() >= spinCost) {
                    canAfford = false;
                    break;
                }
            }

            if (!canAfford) {
                AlertHelper.error(activity, R.string.error_not_enough_resources, false);
            } else {
                activity.findViewById(R.id.slotContainer).bringToFront();
                if (highlightedRoutes != null) {
                    resetRouteColours();
                    //highlightResults(false);
                }

                stillSpinningSlots = slot.getSlots();

                int totalSpinCost = 0;
                for (ItemBundle item : slot.getResources()) {
                    Inventory inventory = Inventory.getInventory(item.getTier().value, item.getType().value);
                    int spinCost = slot.getCurrentStake() * slot.getCurrentRows() * item.getQuantity();
                    totalSpinCost += spinCost;
                    inventory.setQuantity(inventory.getQuantity() - spinCost);
                    inventory.save();
                }


                String levelUpText = LevelHelper.addXp(activity, totalSpinCost);
                if (levelUpText.length() > 0) {
                    AlertHelper.success(activity, levelUpText, true);
                }
                Statistic.add(Enums.Statistic.TotalSpins);
                Statistic.add(Enums.Statistic.ResourcesGambled, totalSpinCost);

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
        List<Inventory> items;
        boolean orderByTier = Setting.getBoolean(Enums.Setting.OrderByTier);
        boolean reverseOrder = Setting.getBoolean(Enums.Setting.OrderReversed);
        String orderBy = (orderByTier ? "a " : "c ") + (reverseOrder ? "ASC" : "DESC");

        if (Setting.getBoolean(Enums.Setting.OnlyActiveResources) && baseItems.size() > 0) {
            String where = "(a != " + resourceTier.value + " OR b != " + resourceType.value + ") AND ";
            for (ItemResult item : baseItems) {
                where += "(a = " + item.getResourceTier().value + " AND b = " + item.getResourceType().value + ") OR ";
            }
            items = Select.from(Inventory.class).where(where.substring(0, where.length() - 4)).orderBy(orderBy).list();
        } else {
            items = Select.from(Inventory.class).where(
                    Condition.prop("a").notEq(resourceTier.value)).or(
                    Condition.prop("b").notEq(resourceType.value))
                    .orderBy(orderBy).list();
        }


        Inventory inventory = Inventory.getInventory(resourceTier.value, resourceType.value);
        picasso.load(inventory.getDrawableId(activity, slot.getResourceQuantity())).into((ImageView)activity.findViewById(R.id.resourceImage));
        ((TextView)activity.findViewById(R.id.resourceInfo)).setText(inventory.getQuantity() + "x " + Item.getName(activity, resourceTier.value, resourceType.value));

        TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        boolean onlyShowStockedItems = Setting.getBoolean(Enums.Setting.OnlyShowStocked);
        TableLayout layout = (TableLayout)activity.findViewById(R.id.inventoryDisplay);
        layout.removeAllViews();
        for (Inventory item : items) {
            if (onlyShowStockedItems && inventory.getQuantity() <= 0) {
                continue;
            }

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
