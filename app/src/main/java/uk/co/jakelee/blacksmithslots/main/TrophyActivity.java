package uk.co.jakelee.blacksmithslots.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.TrophyGridAdapter;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Trophy;

public class TrophyActivity extends BaseActivity {
    @BindView(R.id.trophyGrid) GridView trophyGrid;
    @BindView(R.id.itemImage) ImageView itemImage;
    @BindView(R.id.itemName) TextView itemName;
    @BindView(R.id.trophyHandInButton) TextView trophyHandInButton;
    @BindView(R.id.boostHandInButton) TextView boostHandInButton;
    @BindView(R.id.boostToggle) TextView boostToggle;

    private int currentTrophy = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy);
        ButterKnife.bind(this);

        populateTrophyList();
    }

    private void populateTrophyList() {
        ((TextView)findViewById(R.id.trophyTitle)).setText(getTrophyProgressString());
        TrophyGridAdapter adapter = new TrophyGridAdapter(this, Select.from(Trophy.class).list());
        trophyGrid.setAdapter(adapter);
        trophyGrid.setOnItemClickListener(getTrophyClickListener());
        populateSidebar();
    }

    @NonNull
    private AdapterView.OnItemClickListener getTrophyClickListener() {
        return new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                currentTrophy = (int)v.getTag();
                populateSidebar();
            }
        };
    }

    @OnClick(R.id.trophyInfoButton)
    public void displayTrophyInfo() {
        AlertHelper.info(this, getString(R.string.trophy_info), false);
    }

    @OnClick(R.id.boostInfoButton)
    public void displayBoostInfo() {
        AlertHelper.info(this, getString(R.string.boost_info), false);
    }

    @OnClick(R.id.boostToggle)
    public void toggleBoost() {
        Trophy trophy = Trophy.findById(Trophy.class, currentTrophy);
        trophy.setBoostEnabled(!trophy.isBoostEnabled());
        trophy.save();
        boostToggle.setBackgroundResource(trophy.isBoostEnabled() ? R.drawable.box_green : R.drawable.box_orange);
        AlertHelper.info(this, String.format(Locale.ENGLISH, "Trophy's boost is now %s!",
                getString(trophy.isBoostEnabled() ? R.string.enabled : R.string.not_enabled)), true);
    }

    private String getTrophyProgressString() {
        int achievedTrophies = (int)Select.from(Trophy.class).where(Condition.prop("e").gt(0)).count();
        int totalTrophies = (int)Trophy.count(Trophy.class);
        double boost = achievedTrophies * Constants.TROPHY_XP_MODIFIER;
        return String.format(Locale.ENGLISH, getString(R.string.trophies_progress),
                achievedTrophies,
                totalTrophies,
                boost);
    }

    private void populateSidebar() {
        Trophy trophy = Trophy.findById(Trophy.class, currentTrophy);
        itemImage.setImageResource(TrophyGridAdapter.getTrophyResource(this, trophy));
        itemName.setText(TrophyGridAdapter.getTrophyName(this, trophy));
        boostToggle.setBackgroundResource(trophy.isBoostEnabled() ? R.drawable.box_green : R.drawable.box_orange);
        trophyHandInButton.setBackgroundResource(trophy.isAchieved() ? R.drawable.box_green : R.drawable.box_orange);
        this.boostHandInButton.setText(String.format(Locale.ENGLISH, getString(R.string.boost_level), trophy.getBoostTier()));
        if (trophy.isAchieved()) {
            this.trophyHandInButton.setText(R.string.trophy_progress_achieved);
        } else {
            this.trophyHandInButton.setText(String.format(Locale.ENGLISH, getString(R.string.trophy_progress_unachieved),
                    trophy.getItemsHandedIn(),
                    trophy.getItemsRequired()));
        }
    }

    @OnClick(R.id.trophyHandInButton)
    public void handInTrophyItems() {
        Trophy trophy = Trophy.findById(Trophy.class, currentTrophy);
        Inventory inventory = Inventory.getInventory(trophy.getItemTier(), trophy.getItemType());

        if (!trophy.isAchieved()) {
            if (inventory.getQuantity() <= 0) {
                AlertHelper.error(this, getString(R.string.error_trophy_no_items), false);
            } else {
                AlertDialogHelper.trophyHandIn(this, trophy, inventory);
            }
        }
    }

    public void handInTrophyItems(int quantity) {
        Trophy trophy = Trophy.findById(Trophy.class, currentTrophy);
        Inventory inventory = Inventory.getInventory(trophy.getItemTier(), trophy.getItemType());

        inventory.setQuantity(inventory.getQuantity() - quantity);
        trophy.setItemsHandedIn(trophy.getItemsHandedIn() + quantity);

        inventory.save();
        trophy.save();

        // If we've just unlocked a trophy, update the main trophy list
        if (trophy.getItemsRemaining() <= 0) {
            trophy.setAchieved();
            trophy.save();
            ((TextView)findViewById(R.id.trophyTitle)).setText(getTrophyProgressString());
            AlertHelper.success(this, getString(R.string.trophy_unlocked), true);
            populateTrophyList();
        } else {
            AlertHelper.success(this, getString(R.string.trophy_items_incomplete), true);
            populateSidebar();
        }
    }

    @OnClick(R.id.boostHandInButton)
    public void handInBoostItems() {
        Trophy trophy = Trophy.findById(Trophy.class, currentTrophy);
        Inventory inventory = Inventory.getInventory(trophy.getItemTier(), trophy.getItemType());

        if (inventory.getQuantity() <= 0) {
            AlertHelper.error(this, getString(R.string.error_trophy_no_items), false);
        } else {
            AlertDialogHelper.boostHandIn(this, trophy, inventory);
        }
    }

    public void upgradeBoostTier() {
        Trophy trophy = Trophy.findById(Trophy.class, currentTrophy);
        Inventory inventory = Inventory.getInventory(trophy.getItemTier(), trophy.getItemType());

        int quantityNeeded = trophy.getBoostTierUpgradeCost();

        if (inventory.getQuantity() < quantityNeeded) {
            AlertHelper.error(this, getString(R.string.error_trophy_no_items), false);
        } else {
            inventory.setQuantity(inventory.getQuantity() - quantityNeeded);
            inventory.save();

            trophy.setBoostTier(trophy.getBoostTier() + 1);
            trophy.save();

            populateSidebar();
        }
    }
}
