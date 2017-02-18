package uk.co.jakelee.blacksmithslots.helper;

import android.widget.TextView;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.SlotActivity;

public class SlotHelper {
    private boolean wheelScrolled = false;
    private SlotActivity activity;

    public SlotHelper(SlotActivity activity) {
        this.activity = activity;
    }

    public void initWheel(WheelView wheel) {
        wheel.setViewAdapter(new SlotAdapter(wheel.getContext()));
        wheel.setCurrentItem((int)(Math.random() * 10));

        wheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (!wheelScrolled) {
                    updateStatus();
                }
            }
        });
        wheel.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {
                wheelScrolled = true;
            }
            @Override
            public void onScrollingFinished(WheelView wheel) {
                wheelScrolled = false;
                updateStatus();
            }
        });
        wheel.setCyclic(true);
        wheel.setEnabled(false);
    }

    private void updateStatus() {
        TextView text = (TextView) activity.findViewById(R.id.pwd_status);
        if (test()) {
            text.setText("Congratulation!");
        } else {
            text.setText("");
        }
    }

    private boolean test() {
        int value = getWheel(R.id.slot_1).getCurrentItem();
        return testWheelValue(R.id.slot_2, value) && testWheelValue(R.id.slot_3, value);
    }

    /**
     * Tests wheel value
     * @param id the wheel Id
     * @param value the value to test
     * @return true if wheel value is equal to passed value
     */
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
