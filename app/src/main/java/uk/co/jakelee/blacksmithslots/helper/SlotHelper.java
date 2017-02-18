package uk.co.jakelee.blacksmithslots.helper;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.constructs.SlotResult;
import uk.co.jakelee.blacksmithslots.main.SlotActivity;
import uk.co.jakelee.blacksmithslots.model.Reward;
import uk.co.jakelee.blacksmithslots.model.Slot;

public class SlotHelper {
    private int stillSpinningSlots = 0;
    private boolean buttonPressed = false;
    private SlotActivity activity;
    private int numSlots;
    private List<WheelView> slots = new ArrayList<>();
    private List<Reward> items;

    public SlotHelper(SlotActivity activity, Slot slot) {
        this.activity = activity;
        this.numSlots = slot.getSlots();
        this.items = slot.getRewards();
    }

    public void createWheel() {
        LinearLayout container = (LinearLayout)activity.findViewById(R.id.slotContainer);
        for (int i = 0; i < numSlots; i++) {
            WheelView wheel = new WheelView(activity);
            wheel.setViewAdapter(new SlotAdapter(wheel.getContext(), items));
            wheel.setCurrentItem(0);

            wheel.addScrollingListener(new OnWheelScrollListener() {
                @Override
                public void onScrollingStarted(WheelView wheel) {
                    stillSpinningSlots = numSlots;
                }

                @Override
                public void onScrollingFinished(WheelView wheel) {
                    if (stillSpinningSlots <= 1) {
                        buttonPressed = false;
                        Log.d("Slot", "Calling finished");
                        updateStatus();
                    } else {
                        stillSpinningSlots--;
                    }
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
        List<SlotResult> results = new ArrayList<>();
        for (WheelView wheel : slots) {
            results.add(new SlotResult(items.get(wheel.getCurrentItem()).getResourceId(), items.get(wheel.getCurrentItem()).getQuantity()));
            /*Log.d("Slot", "Selected: " + wheel.getViewAdapter().getItem(wheel.getCurrentItem(), null, (ViewGroup)wheel.getParent()).toString());
            //Log.d("Slot", "Selected: " + items.get(wheel.getCurrentItem()).getQuantity() + "x " + Resource.getName(activity, items.get(wheel.getCurrentItem()).getResourceId()));
            if (targetedValue == 0) {
                targetedValue = wheel.getCurrentItem();
            } else {
                if (targetedValue != wheel.getCurrentItem()) {
                    return false;
                }
            }*/
        }
        return true;
    }

    public void mixWheel() {
        if (!buttonPressed) {
            buttonPressed = true;
            for (WheelView wheel : slots) {
                wheel.scroll(-350 + (int) (Math.random() * 50), 2000);
            }
        }
    }

}
