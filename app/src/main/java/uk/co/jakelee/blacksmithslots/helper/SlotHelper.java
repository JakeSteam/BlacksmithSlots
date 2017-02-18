package uk.co.jakelee.blacksmithslots.helper;

import android.widget.TextView;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.SlotActivity;

public class SlotHelper {
    private boolean currentlySpinning  = false;
    private SlotActivity activity;
    private int[] slots;
    private int[] items;

    public SlotHelper(SlotActivity activity, int[] slots, int[] items) {
        this.activity = activity;
        this.slots = slots;
        this.items = items;
    }

    public void createWheel() {
        for (int slot : slots) {
            WheelView wheel = (WheelView) activity.findViewById(slot);
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
        int value = getWheel(R.id.slot_1).getCurrentItem();
        return testWheelValue(R.id.slot_2, value) && testWheelValue(R.id.slot_3, value);
    }

    private boolean testWheelValue(int id, int value) {
        return getWheel(id).getCurrentItem() == value;
    }

    private WheelView getWheel(int id) {
        return (WheelView) activity.findViewById(id);
    }

    public void mixWheel(int id) {
        WheelView wheel = getWheel(id);
        wheel.scroll(-350 + (int)(Math.random() * 50), 2000);
    }

}
