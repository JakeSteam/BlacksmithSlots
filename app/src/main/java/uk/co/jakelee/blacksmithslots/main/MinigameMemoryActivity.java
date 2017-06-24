package uk.co.jakelee.blacksmithslots.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.helper.MinigameHelper;
import uk.co.jakelee.blacksmithslots.model.Inventory;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;

public class MinigameMemoryActivity extends MinigameActivity {
    private List<ItemBundle> winnings = new ArrayList<>();
    private ItemBundle openedBox;
    private ImageView openedBoxView;
    private List<ItemBundle> items = new ArrayList<>();
    private int chancesLeft = Constants.MEMORY_GAME_LIVES;
    private int boxesOpened = 0;
    private int totalPossibleMatches = 8;
    private boolean justFailedCooldown = false;
    private Handler handler = new Handler();

    @BindView(R.id.boxGrid) LinearLayout boxTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame_memory);
        ButterKnife.bind(this);

        MinigameHelper.useCharge();

        ((TextView)findViewById(R.id.minigameDesc)).setText(String.format(Locale.ENGLISH, getString(R.string.minigame_memory_desc), MinigameHelper.getMinsToNextCharge()));
        createRewards();
        populateBoxList();
    }

    private void createRewards() {
        items.add(new ItemBundle(1, 1, 10));
        items.add(new ItemBundle(1, 1, 10));
        items.add(new ItemBundle(1, 19, 10));
        items.add(new ItemBundle(1, 19, 10));

        items.add(new ItemBundle(2, 1, 10));
        items.add(new ItemBundle(2, 1, 10));
        items.add(new ItemBundle(2, 19, 10));
        items.add(new ItemBundle(2, 19, 10));

        items.add(new ItemBundle(3, 1, 10));
        items.add(new ItemBundle(3, 1, 10));
        items.add(new ItemBundle(3, 19, 10));
        items.add(new ItemBundle(3, 19, 10));

        items.add(new ItemBundle(4, 1, 10));
        items.add(new ItemBundle(4, 1, 10));
        items.add(new ItemBundle(4, 19, 10));
        items.add(new ItemBundle(4, 19, 10));
        Collections.shuffle(items);
    }

    private void populateBoxList() {
        final Activity activity = this;
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1);
        LinearLayout boxRow = new LinearLayout(this);
        for (int i = 0; i < 16; i++) {
            if (i % 4 == 0) {
                boxRow = inflater.inflate(R.layout.custom_memory_row, null).findViewById(R.id.row);
            }
            final ImageView imageView = (ImageView)boxRow.getChildAt(i % 4);
            imageView.setImageResource(R.drawable.item_999_997);
            imageView.setTag(i);
            imageView.setOnClickListener(boxClickListener(activity));

            if (i % 4 == 3) {
                boxTable.addView(boxRow, layoutParams);
            }
        }
    }

    @NonNull
    private View.OnClickListener boxClickListener(final Activity activity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (boxesOpened < 2 && chancesLeft > 0 && !justFailedCooldown) {
                    boxesOpened++;

                    ItemBundle boxItem = items.get((int) view.getTag());
                    int itemImage = getResources().getIdentifier(DisplayHelper.getItemImageFile(boxItem), "drawable", getPackageName());
                    ((ImageView)view).setImageResource(itemImage);

                    if (boxesOpened == 1) {
                        openedBox = boxItem;
                        openedBoxView = (ImageView)view;
                    } else if (boxesOpened == 2) {
                        if (matchesExisting(boxItem)) {
                            totalPossibleMatches--;
                            winnings.add(boxItem);
                            AlertHelper.success(activity, String.format(Locale.ENGLISH, getString(R.string.minigame_memory_winnings_added), boxItem.toString(activity)), false);

                            openedBoxView.setOnClickListener(null);
                            view.setOnClickListener(null);
                        } else {
                            chancesLeft--;
                            AlertHelper.error(activity, String.format(Locale.ENGLISH, getString(R.string.minigame_memory_no_match), chancesLeft), false);
                            justFailedCooldown = true;
                            handler.postDelayed(boxNoMatch((ImageView) view), 1000);
                        }

                        if (chancesLeft <= 0 || totalPossibleMatches <= 0) {
                            displayGameOverScreen();
                        }
                        boxesOpened = 0;
                    }
                }
            }
        };
    }

    private void displayGameOverScreen() {
        findViewById(R.id.boxGrid).setAlpha(0.3f);
        findViewById(R.id.gameOverScreen).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.minigameRewards)).setText(String.format(Locale.ENGLISH, getString(R.string.minigame_memory_game_over),
                winnings.size() == 0 ? getString(R.string.none) : DisplayHelper.bundlesToString(this, winnings)));
    }

    @NonNull
    private Runnable boxNoMatch(final ImageView view) {
        return new Runnable() {
            @Override
            public void run() {
                justFailedCooldown = false;
                openedBoxView.setImageResource(R.drawable.item_999_997);
                view.setImageResource(R.drawable.item_999_997);
            }
        };
    }

    private boolean matchesExisting(ItemBundle boxItem) {
        return openedBox.getTier() == boxItem.getTier()
                && openedBox.getType() == boxItem.getType()
                && openedBox.getQuantity() == boxItem.getQuantity();
    }


    public void forceClose(View v) {
        confirmClose();
    }

    @Override
    @OnClick(R.id.claimRewards)
    public void confirmClose() {
        if (winnings.size() > 0) {
            Inventory.addInventory(winnings);
            setResult(Constants.MINIGAME_MEMORY, new Intent().putExtra("winningsString", DisplayHelper.bundlesToString(this, winnings)));
        }
        finish();
    }
}
