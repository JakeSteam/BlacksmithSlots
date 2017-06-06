package uk.co.jakelee.blacksmithslots.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.components.TrophyGridAdapter;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.Enums;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.Trophy;

public class TrophyActivity extends BaseActivity {
    @BindView(R.id.trophyGrid) GridView trophyGrid;
    @BindView(R.id.itemImage) ImageView itemImage;
    @BindView(R.id.itemName) TextView itemName;
    @BindView(R.id.itemInfo) TextView itemInfo;

    private int currentTrophy = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trophy);
        ButterKnife.bind(this);

        ((TextView)findViewById(R.id.activityTitle)).setText(getTrophyProgressString());
        populateTrophyList();
    }

    private void populateTrophyList() {
        trophyGrid.setAdapter(new TrophyGridAdapter(this));
        trophyGrid.setOnItemClickListener(getTrophyClickListener());
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

    private LinearLayout populateTrophyTile(LayoutInflater inflater, Trophy trophy) {
        LinearLayout trophyTile = (LinearLayout) inflater.inflate(R.layout.custom_trophy_tile, null).findViewById(R.id.trophyTile);
        trophyTile.setTag(trophy.getId());

        ImageView itemImage = (ImageView)trophyTile.findViewById(R.id.itemImage);
        itemImage.setImageResource(getTrophyResource(trophy));
        if (trophy.isAchieved()) {
            itemImage.getDrawable().clearColorFilter();
        } else {
            itemImage.getDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }

        ((TextView) trophyTile.findViewById(R.id.itemName)).setText(getTrophyName(trophy));

        return trophyTile;
    }

    private String getTrophyProgressString() {
        int achievedTrophies = (int)Select.from(Trophy.class).where(Condition.prop("e").gt(0)).count();
        int totalTrophies = (int)Trophy.count(Trophy.class);
        int progress = (int)Math.ceil((achievedTrophies * 100d) / totalTrophies);
        return String.format(Locale.ENGLISH, getString(R.string.trophies_progress),
                achievedTrophies,
                totalTrophies,
                progress);
    }

    private void populateSidebar() {
        Trophy trophy = Trophy.findById(Trophy.class, currentTrophy);
        itemImage.setImageResource(getTrophyResource(trophy));
        itemName.setText(getTrophyName(trophy));
        itemInfo.setText(trophy.isAchieved() ? "Achieved!" : (trophy.getItemsHandedIn() + "/" + trophy.getItemsRequired()));
    }

    @OnClick(R.id.handInButton)
    public void handInItems() {
        Trophy trophy = Trophy.findById(Trophy.class, currentTrophy);
        Inventory inventory = Inventory.getInventory(trophy.getItemTier(), trophy.getItemType());
        if (trophy.isAchieved()) {
            AlertHelper.error(this, getString(R.string.error_trophy_already_unlocked), false);
        } else if (inventory.getQuantity() <= 0) {
            AlertHelper.error(this, getString(R.string.error_trophy_no_items), false);
        } else {
            AlertDialogHelper.trophyHandIn(this, trophy, inventory);
        }
    }

    public void handInItems(int quantity) {
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
            populateTrophyList();
        } else {
            populateSidebar();
        }
    }
}
