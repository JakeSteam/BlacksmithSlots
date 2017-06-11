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
import android.widget.TextView;

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
import uk.co.jakelee.blacksmithslots.constructs.WinRoute;
import uk.co.jakelee.blacksmithslots.main.MinigameActivity;
import uk.co.jakelee.blacksmithslots.main.SlotActivity;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;
import uk.co.jakelee.blacksmithslots.model.Message;
import uk.co.jakelee.blacksmithslots.model.Setting;
import uk.co.jakelee.blacksmithslots.model.Slot;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class SlotHelper {
    private int stillSpinningSlots = 0;
    public int autospinsLeft = 0;
    private SlotActivity activity;
    private Slot slot;
    private List<WheelView> slots = new ArrayList<>();
    private List<ItemBundle> slotResources;
    private List<ItemBundle> slotRewards;
    private List<List<ItemBundle>> items = new ArrayList<>();
    private List<WinRoute> highlightedRoutes;
    private List<View> highlightedItems = new ArrayList<>();
    private Picasso picasso;
    private LayoutInflater inflater;
    private Handler handler;
    private Enums.Type minigameToLoad = null;

    public SlotHelper(SlotActivity activity, Handler handler, Slot slot) {
        this.activity = activity;
        this.slot = slot;
        this.slotResources = slot.getResources();
        this.slotRewards = slot.getRewards(true, true);
        this.picasso = Picasso.with(activity);
        this.inflater = LayoutInflater.from(activity);
        this.handler = handler;
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

        List<WinRoute> allRoutes = RouteHelper.getRoutes(slot.getSlots(), 0);
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
        if (requestCode == Constants.MINIGAME_FLIP || requestCode == Constants.MINIGAME_DICE) {
            if (resultCode > 0) {
                StringBuilder itemText = new StringBuilder();
                for (ItemBundle itemBundle : slotResources) {
                    if (itemBundle.getTier() != Enums.Tier.Internal) {
                        itemText.append(itemBundle.getQuantity() * resultCode);
                        itemText.append("x ");
                        itemText.append(Inventory.getName(activity, itemBundle.getTier(), itemBundle.getType()));
                        itemText.append(", ");
                        Inventory.addInventory(itemBundle.getTier(), itemBundle.getType(), itemBundle.getQuantity() * resultCode);
                    }
                }

                String resourceString = itemText.toString();
                resourceString = resourceString.length() > 0 ? resourceString.substring(0, resourceString.length() - 2) : "";

                AlertHelper.success(activity, String.format(Locale.ENGLISH, activity.getString(R.string.minigame_won_something), resourceString), true);
            } else {
                AlertHelper.info(activity, activity.getString(R.string.minigame_won_nothing), false);
            }
        } else if (requestCode == Constants.MINIGAME_CHEST) {
            if (data.getIntExtra("quantity", 0) > 0) {
                ItemBundle winnings = new ItemBundle(
                        data.getIntExtra("tier", 0),
                        data.getIntExtra("type", 0),
                        data.getIntExtra("quantity", 0)
                );
                AlertHelper.success(activity, "Won " + winnings.toString(activity) + " from chest minigame!", true);
            } else {
                AlertHelper.info(activity, "Unlucky, won nothing from chest minigame!", false);
            }
        }
    }

    public void setBackground() {
        activity.findViewById(R.id.mainSlotArea).setBackgroundResource(activity.getResources().getIdentifier(DisplayHelper.getMapBackgroundImageFile(slot.getMapId()), "drawable", activity.getPackageName()));
    }

    public void createWheel() {
        LinearLayout container = (LinearLayout)activity.findViewById(R.id.slotContainer);
        for (int i = 0; i < slot.getSlots(); i++) {
            WheelView wheel = new WheelView(activity);

            // Create + store shuffled list of items
            items.add(new ArrayList(slotRewards));
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
                            Class classToLoad = MinigameActivity.getClassToLoad(minigameToLoad);
                            int requestCode = MinigameActivity.getRequestCode(minigameToLoad);
                            Statistic.add(MinigameActivity.getStatistic(minigameToLoad));
                            if (classToLoad != null && requestCode > 0) {
                                activity.startActivityForResult(new Intent(activity, classToLoad)
                                                .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                                                .putExtra("slot", slot.getSlotId()),
                                        requestCode);
                            }
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
        ((TextView)activity.findViewById(R.id.spinButton)).setText(R.string.spin);
        ((TextView)activity.findViewById(R.id.rowsActive)).setText(Integer.toString(slot.getCurrentRows()));
        ((TextView)activity.findViewById(R.id.amountGambled)).setText(Integer.toString(slot.getCurrentStake()));

        activity.findViewById(R.id.increaseRows).setAlpha(slot.getCurrentRows() == slot.getMaximumRows() ? 0.25f : 1);
        activity.findViewById(R.id.decreaseRows).setAlpha(slot.getCurrentRows() == slot.getMinimumRows() ? 0.25f : 1);
        activity.findViewById(R.id.increaseStake).setAlpha(slot.getCurrentStake() == slot.getMaximumStake() ? 0.25f : 1);
        activity.findViewById(R.id.decreaseStake).setAlpha(slot.getCurrentStake() == slot.getMinimumStake() ? 0.25f : 1);
    }

    public void afterSpinUpdate() {
        ((TextView)activity.findViewById(R.id.autospinButton)).setText(autospinsLeft > 0 ? "" + autospinsLeft : activity.getString(R.string.icon_autospin));
        ((TextView)activity.findViewById(R.id.currentLevel)).setText("Lev " + LevelHelper.getLevel());
        int levelProgress = LevelHelper.getLevelProgress();
        Log.d("Progress", "" + levelProgress);
        ((ProgressBar)activity.findViewById(R.id.currentLevelProgress)).setProgress(levelProgress);
    }

    private void updateStatus() {
        List<List<ItemBundle>> results = retrieveSpinResult();
        List<ItemBundle> wonItems = getWinnings(results);
        if (wonItems.size() > 0) {
            SoundHelper.playSound(activity, SoundHelper.coinSounds);
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

    private String applyWinnings(List<ItemBundle> unmergedWinnings) {
        LinkedHashMap<Pair<Enums.Tier, Enums.Type>, Integer> dataStore = new LinkedHashMap<>();
        for (ItemBundle winning : unmergedWinnings) {
            Integer temp;
            Pair<Enums.Tier, Enums.Type> pair = new Pair<>(winning.getTier(), winning.getType());
            if (dataStore.containsKey(pair)) {
                temp = dataStore.get(pair) + winning.getQuantity();
                dataStore.put(pair, temp);
            } else {
                dataStore.put(pair, winning.getQuantity());
            }
        }

        int totalResourcesWon = 0;
        StringBuilder winningsText = new StringBuilder().append("Won: ");
        for (Map.Entry<Pair<Enums.Tier, Enums.Type>, Integer> winning : dataStore.entrySet()) {
            Enums.Tier tier = winning.getKey().first;
            Enums.Type type = winning.getKey().second;
            if (tier != Enums.Tier.Internal) {
                int quantity = winning.getValue() * slot.getCurrentStake();
                totalResourcesWon += quantity;
                Inventory.addInventory(tier, type, quantity);
                winningsText.append(String.format(Locale.ENGLISH, "%dx %s, ", quantity, Inventory.getName(activity, tier, type)));
            } else {
                // Wowsa, they got a minigame match!
                if (type != Enums.Type.Wildcard) {
                    autospinsLeft = 0;
                    minigameToLoad = type;
                }
            }
        }

        Statistic.add(Enums.Statistic.ResourcesWon, totalResourcesWon);
        return winningsText.length() > 5 ? winningsText.substring(0, winningsText.length() - 2) : "";
    }

    private List<ItemBundle> getWinnings(List<List<ItemBundle>> rows) {
        List<ItemBundle> winningResults = new ArrayList<>();
        List<WinRoute> winningRoutes = new ArrayList<>();

        // Loop through possible win paths
        List<WinRoute> routes = RouteHelper.getRoutes(rows.get(0).size(), slot.getCurrentRows());
        for (int i = 0; i < routes.size(); i++) {
            List<ItemBundle> results = new ArrayList<>();

            WinRoute route = routes.get(i);
            // Loop through positions in win paths
            for (int j = 0; j < route.size(); j++) {
                results.add(rows.get(route.get(j)).get(j));
            }

            int routeResult = isRouteAWin(results);
            if (routeResult != Constants.STATUS_NO_MATCH) {
                if (routeResult == Constants.STATUS_WILDCARD) {
                    // If all tiles are wildcards, reward 10x their bet
                    List<ItemBundle> wildcardRewards = new ArrayList<>();
                    for (ItemBundle slotResource : slotResources) {
                        wildcardRewards.add(new ItemBundle(slotResource.getTier(), slotResource.getType(), slotResource.getQuantity() * 10));
                    }
                    results = wildcardRewards;
                }
                winningRoutes.add(route);
                winningResults.addAll(results);

                // Bring winning route to front. +1 due to bottom bar
                ImageView routeImage = (ImageView)activity.findViewById(activity.getResources().getIdentifier("route_" + (i + 1), "id", activity.getPackageName()));
                if (routeImage != null) {
                    routeImage.setColorFilter(ContextCompat.getColor(activity, R.color.greenText), PorterDuff.Mode.MULTIPLY);
                    routeImage.bringToFront();
                }
            }
        }

        this.highlightedRoutes = winningRoutes;
        //highlightResults();

        return winningResults;
    }

    private int isRouteAWin(List<ItemBundle> routeTiles) {
        boolean allWildcards = true;
        ItemBundle checkedResult = new ItemBundle();
        for (ItemBundle routeTile : routeTiles) {
            if (checkedResult.getTier().value == 0 && checkedResult.getType().value == 0 && (routeTile.getType() != Enums.Type.Wildcard)) {
                // If we haven't found a good tile yet, and it isn't a wildcard, set the checker to the current tile.
                checkedResult = routeTile;
            } else {
                // Tiles here must be a wildcard, or a tile other than the first.
                // If the tile doesn't match the check tile (and it's not a wildcard), it's not a match!
                if ((routeTile.getType() != checkedResult.getType() && routeTile.getType() != checkedResult.getType())
                        && (routeTile.getType() != Enums.Type.Wildcard)) {
                    return Constants.STATUS_NO_MATCH;
                }
            }

            // Keep track of whether or not we've encountered any non-wildcards
            if (routeTile.getType() != Enums.Type.Wildcard) {
                allWildcards = false;
            }
        }
        if (allWildcards) {
            return Constants.STATUS_WILDCARD;
        }
        return Constants.STATUS_MATCH;
    }

    private List<List<ItemBundle>> retrieveSpinResult() {

        // Setup data holder
        List<List<ItemBundle>> rows = new ArrayList<>();
        for (int i = 0; i < Constants.ROWS; i++) {
            rows.add(new ArrayList<ItemBundle>());
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
            Inventory failedItem = null;
            for (ItemBundle item : slot.getResources()) {
                Inventory inventory = Inventory.getInventory(item.getTier().value, item.getType().value);
                int spinCost = slot.getCurrentStake() * slot.getCurrentRows() * item.getQuantity();
                if (inventory.getQuantity() < spinCost) {
                    failedItem = inventory;
                    break;
                }
            }

            if (failedItem != null) {
                if (failedItem.getType() == Enums.Type.Bar.value) {
                    failedItem.setType(Enums.Type.Ore.value);
                }
                AlertDialogHelper.outOfItems(activity, failedItem.getTier(), failedItem.getType());
            } else {
                SoundHelper.playSound(activity, SoundHelper.spinSounds);
                activity.findViewById(R.id.slotContainer).bringToFront();
                if (highlightedRoutes != null) {
                    resetRouteColours();
                    highlight(false);
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
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        boolean orderByTier = Setting.getBoolean(Enums.Setting.OrderByTier);
        boolean reverseOrder = Setting.getBoolean(Enums.Setting.OrderReversed);

        // Resources
        List<Inventory> resourceInventories = new ArrayList<>();
        for (ItemBundle item : slotResources) {
            resourceInventories.add(Inventory.get(item));
        }
        DisplayHelper.populateItemRows(activity, R.id.resourceDisplay, inflater, picasso, params, resourceInventories, true);

        // Inventory
        String resourceString = "";
        for (ItemBundle item : slotResources) {
            resourceString += "(a != " + item.getTier().value + " OR b != " + item.getType().value + ") AND";
        }

        String rewardString = "";
        if (Setting.getBoolean(Enums.Setting.OnlyActiveResources)) {
            for (ItemBundle item : slotRewards) {
                rewardString += "(a = " + item.getTier().value + " AND b = " + item.getType().value + ") OR ";
            }
            rewardString = "(" + rewardString.substring(0, rewardString.length() - 3) + ")";
        } else {
            resourceString = resourceString.substring(0, resourceString.length() - 4);
        }

        String orderBy = (orderByTier ? "a " : "c ") + (reverseOrder ? "ASC" : "DESC");
        List<Inventory> inventoryItems = Select.from(Inventory.class).where(resourceString + rewardString).orderBy(orderBy).list();
        boolean onlyShowStockedItems = Setting.getBoolean(Enums.Setting.OnlyShowStocked);

        DisplayHelper.populateItemRows(activity, R.id.inventoryDisplay, inflater, picasso, params, inventoryItems, onlyShowStockedItems);
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

    private void highlightResults() {
        StringBuilder winningRoutes = new StringBuilder();
        Log.d("Highlight",";");
        for (WinRoute route : highlightedRoutes) {
            for (int i = 0; i < route.size(); i++) {
                int offset = route.get(i);
                int numItems = items.get(i).size();
                int currItem = slots.get(i).getCurrentItem();
                int finalValue = numItems - currItem - offset;
                String fixed = "";

                /*if (finalValue == -1) {
                    //finalValue = (numItems - finalValue) % numItems;
                } else if (finalValue == -2) {
                    finalValue = 3; // Correct!
                }

                if (finalValue == 5) {
                    finalValue = 0;
                } else if (finalValue == 6) {
                    finalValue = 4;
                }*/

                /*if (finalValue < 0) {
                    finalValue += 4;
                    fixed += "ADDED";
                } else if (finalValue > 4) {
                    finalValue -= 5;
                    fixed += "REMOVED";
                    if (finalValue == 1) {
                        finalValue = 0;
                        fixed += "2";
                    }
                }*/

                Log.d("Highlight", "Size " + slots.get(i).itemsLayout.getChildCount() + " (" + numItems +" - " + currItem + " - " + offset + ") = " + finalValue + fixed);
                View imageViewSelected = slots.get(i).itemsLayout.getChildAt(finalValue);

                if (imageViewSelected != null) {
                    highlightedItems.add(imageViewSelected);
                }
            }
            winningRoutes.append(route.toString());
            winningRoutes.append("\n");
        }
        highlight(true);
        Log.d("Routes", winningRoutes.toString());
    }

    private void highlight(boolean applying) {


        for (View highlightedItem : highlightedItems) {
            highlightedItem.setAlpha(applying ? 0.5f : 1.0f);
        }

        if (!applying) {
            highlightedItems = new ArrayList<>();
        }
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
