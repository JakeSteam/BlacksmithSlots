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
import uk.co.jakelee.blacksmithslots.model.Upgrade;

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
        TrophyGridAdapter adapter = new TrophyGridAdapter(this, Select.from(Upgrade.class).list());
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
        Upgrade upgrade = Upgrade.findById(Upgrade.class, currentTrophy);
        upgrade.setBoostEnabled(!upgrade.isBoostEnabled());
        upgrade.save();
        boostToggle.setBackgroundResource(upgrade.isBoostEnabled() ? R.drawable.box_green : R.drawable.box_orange);
        AlertHelper.info(this, String.format(Locale.ENGLISH, "Upgrade's boost is now %s!",
                getString(upgrade.isBoostEnabled() ? R.string.enabled : R.string.not_enabled)), true);
    }

    private String getTrophyProgressString() {
        int achievedTrophies = (int)Select.from(Upgrade.class).where(Condition.prop("e").gt(0)).count();
        int totalTrophies = (int) Upgrade.count(Upgrade.class);
        double boost = achievedTrophies * Constants.TROPHY_XP_MODIFIER;
        return String.format(Locale.ENGLISH, getString(R.string.trophies_progress),
                achievedTrophies,
                totalTrophies,
                boost);
    }

    private void populateSidebar() {
        Upgrade upgrade = Upgrade.findById(Upgrade.class, currentTrophy);
        itemImage.setImageResource(TrophyGridAdapter.getTrophyResource(this, upgrade));
        itemName.setText(TrophyGridAdapter.getTrophyName(this, upgrade));
        boostToggle.setBackgroundResource(upgrade.isBoostEnabled() ? R.drawable.box_green : R.drawable.box_orange);
        trophyHandInButton.setBackgroundResource(upgrade.isAchieved() ? R.drawable.box_green : R.drawable.box_orange);
        this.boostHandInButton.setText(String.format(Locale.ENGLISH, getString(R.string.boost_level), upgrade.getBoostTier()));
        if (upgrade.isAchieved()) {
            this.trophyHandInButton.setText(R.string.trophy_progress_achieved);
        } else {
            this.trophyHandInButton.setText(String.format(Locale.ENGLISH, getString(R.string.trophy_progress_unachieved),
                    upgrade.getItemsHandedIn(),
                    upgrade.getItemsRequired()));
        }
    }

    @OnClick(R.id.trophyHandInButton)
    public void handInTrophyItems() {
        Upgrade upgrade = Upgrade.findById(Upgrade.class, currentTrophy);
        Inventory inventory = Inventory.getInventory(upgrade.getItemTier(), upgrade.getItemType());

        if (!upgrade.isAchieved()) {
            if (inventory.getQuantity() <= 0) {
                AlertHelper.error(this, getString(R.string.error_trophy_no_items), false);
            } else {
                AlertDialogHelper.trophyHandIn(this, upgrade, inventory);
            }
        }
    }

    public void handInTrophyItems(int quantity) {
        Upgrade upgrade = Upgrade.findById(Upgrade.class, currentTrophy);
        Inventory inventory = Inventory.getInventory(upgrade.getItemTier(), upgrade.getItemType());

        inventory.setQuantity(inventory.getQuantity() - quantity);
        upgrade.setItemsHandedIn(upgrade.getItemsHandedIn() + quantity);

        inventory.save();
        upgrade.save();

        // If we've just unlocked a upgrade, update the main upgrade list
        if (upgrade.getItemsRemaining() <= 0) {
            upgrade.setAchieved();
            upgrade.save();
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
        Upgrade upgrade = Upgrade.findById(Upgrade.class, currentTrophy);
        Inventory inventory = Inventory.getInventory(upgrade.getItemTier(), upgrade.getItemType());

        if (inventory.getQuantity() <= 0) {
            AlertHelper.error(this, getString(R.string.error_trophy_no_items), false);
        } else {
            AlertDialogHelper.boostHandIn(this, upgrade, inventory);
        }
    }

    public void upgradeBoostTier() {
        Upgrade upgrade = Upgrade.findById(Upgrade.class, currentTrophy);
        Inventory inventory = Inventory.getInventory(upgrade.getItemTier(), upgrade.getItemType());

        int quantityNeeded = upgrade.getBoostTierUpgradeCost();

        if (inventory.getQuantity() < quantityNeeded) {
            AlertHelper.error(this, getString(R.string.error_trophy_no_items), false);
        } else {
            inventory.setQuantity(inventory.getQuantity() - quantityNeeded);
            inventory.save();

            upgrade.setBoostTier(upgrade.getBoostTier() + 1);
            upgrade.save();

            populateSidebar();
        }
    }
}
