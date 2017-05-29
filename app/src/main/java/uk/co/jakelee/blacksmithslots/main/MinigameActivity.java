package uk.co.jakelee.blacksmithslots.main;

import android.view.View;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmithslots.helper.Constants;
import uk.co.jakelee.blacksmithslots.helper.Enums;

public class MinigameActivity extends BaseActivity {

    @Override
    public void close(View v) {
        AlertDialogHelper.confirmCloseMinigame(this);
    }

    public void confirmClose() {
        finish();
    }

    public static Class getClassToLoad(Enums.Type type) {
        switch(type) {
            case MinigameFlip: return MinigameFlipActivity.class;
            case MinigameDice: return MinigameDiceActivity.class;
            case MinigameChest: return MinigameChestActivity.class;
        }
        return null;
    }

    public static int getRequestCode(Enums.Type type) {
        switch(type) {
            case MinigameFlip: return Constants.MINIGAME_FLIP;
            case MinigameDice: return Constants.MINIGAME_DICE;
            case MinigameChest: return Constants.MINIGAME_CHEST;
        }
        return 0;
    }

    public static Enums.Statistic getStatistic(Enums.Type type) {
        switch(type) {
            case MinigameFlip: return Enums.Statistic.MinigameFlip;
            case MinigameDice: return Enums.Statistic.MinigameDice;
            case MinigameChest: return Enums.Statistic.MinigameChest;
        }
        return Enums.Statistic.Xp;
    }
}
