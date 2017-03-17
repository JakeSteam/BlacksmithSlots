package uk.co.jakelee.blacksmithslots.main;

import android.view.View;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.helper.AlertDialogHelper;

public class MinigameActivity extends BaseActivity {
    @Override
    public void close(View v) {
        AlertDialogHelper.confirmCloseMinigame(this);
    }

    public void confirmClose() {
        finish();
    }
}
