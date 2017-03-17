package uk.co.jakelee.blacksmithslots.main;

import android.os.Bundle;
import android.view.WindowManager;

import uk.co.jakelee.blacksmithslots.R;

public class MinigameFlipActivity extends MinigameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_minigame_flip);
    }
}
