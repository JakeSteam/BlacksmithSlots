package uk.co.jakelee.blacksmithslots.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.AlertHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;

public class MinigameMemoryActivity extends MinigameActivity {
    private ItemBundle winnings;
    private ItemBundle openedBox;
    private ImageView openedBoxView;
    private List<ItemBundle> items = new ArrayList<>();
    private int chancesLeft = 5;
    private int boxesOpened = 0;
    private boolean justFailedCooldown = false;
    private Handler handler = new Handler();

    @BindView(R.id.boxGrid) LinearLayout boxTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minigame_memory);
        ButterKnife.bind(this);

        // Create arraylist of rewards
        createRewards();
        populateBoxList();
    }

    private void createRewards() {
        items.add(new ItemBundle(1, 1, 1));
        for (int i = 0; i < 15; i++) {
            items.add(new ItemBundle(1, i + 1, 1));
        }
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
            imageView.setOnClickListener(new View.OnClickListener() {
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
                                AlertHelper.success(activity, "You've won " + boxItem.toString(activity), false);
                            } else {
                                chancesLeft--;
                                AlertHelper.error(activity, "Unlucky! Chances left: " + chancesLeft, false);
                                justFailedCooldown = true;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        justFailedCooldown = false;
                                        openedBoxView.setImageResource(R.drawable.item_999_997);
                                        ((ImageView)view).setImageResource(R.drawable.item_999_997);
                                    }
                                }, 1000);
                            }
                            boxesOpened = 0;
                        }
                    }
                }
            });

            if (i % 4 == 3) {
                boxTable.addView(boxRow, layoutParams);
            }
        }
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
    public void confirmClose() {
        if (winnings != null) {
            setResult(1, new Intent()
                    .putExtra("tier", winnings.getTier().value)
                    .putExtra("type", winnings.getType().value)
                    .putExtra("quantity", winnings.getQuantity()));
        }
        finish();
    }
}
