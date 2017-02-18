package uk.co.jakelee.blacksmithslots.helper;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.SlotActivity;

public class SlotHelper {
    private boolean currentlySpinning  = false;
    private SlotActivity activity;
    private int numSlots;
    private ArrayList<WheelView> slots = new ArrayList<>();
    private int[] items;

    public SlotHelper(SlotActivity activity, int numSlots, int[] items) {
        this.activity = activity;
        this.numSlots = numSlots;
        this.items = items;
    }

    public void createWheel() {
        LinearLayout container = (LinearLayout)activity.findViewById(R.id.slotContainer);
        for (int i = 0; i < numSlots; i++) {
            WheelView wheel = new WheelView(activity);
            wheel.setViewAdapter(new SlotAdapter(wheel.getContext(), items));
            wheel.setCurrentItem((int) (Math.random() * 10));

            wheel.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    if (!currentlySpinning) {
                        updateStatus();
                    }
                }
            });
            wheel.addScrollingListener(new OnWheelScrollListener() {
                @Override
                public void onScrollingStarted(WheelView wheel) {
                    currentlySpinning = true;
                }

                @Override
                public void onScrollingFinished(WheelView wheel) {
                    currentlySpinning = false;
                    updateStatus();
                }
            });
            wheel.setCyclic(true);
            wheel.setEnabled(false);

            container.addView(wheel, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            slots.add(wheel);

        }
    }

    private void updateStatus() {
        TextView text = (TextView) activity.findViewById(R.id.pwd_status);
        if (test()) {
            text.setText("Match!");
        } else {
            text.setText("No match!");
        }
    }

    private boolean test() {
        int targetedValue = 0;
        for (WheelView wheel : slots) {
            if (targetedValue == 0) {
                targetedValue = wheel.getCurrentItem();
            } else {
                if (targetedValue != wheel.getCurrentItem()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void mixWheel() {
        for (WheelView wheel : slots) {
            wheel.scroll(-350 + (int) (Math.random() * 50), 2000);
        }
    }

}
