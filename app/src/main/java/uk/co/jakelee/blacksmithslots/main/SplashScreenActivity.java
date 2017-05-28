package uk.co.jakelee.blacksmithslots.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import uk.co.jakelee.blacksmithslots.BaseActivity;
import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.helper.CalculationHelper;
import uk.co.jakelee.blacksmithslots.helper.DatabaseHelper;
import uk.co.jakelee.blacksmithslots.helper.DisplayHelper;

public class SplashScreenActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);

        final Picasso picasso = new Picasso.Builder(this).build();
        final ImageView imageView = (ImageView)findViewById(R.id.image);
        final Handler h = new Handler();
        h.post(new Runnable(){
            public void run(){
                String resource = getRandomItemOrSlotDrawable();
                int resourceId = getResources().getIdentifier(resource, "drawable", getPackageName());
                if (resourceId == 0) {
                    resourceId = getResources().getIdentifier(DisplayHelper.getItemImageFile(999, 999), "drawable", getPackageName());
                }
                picasso.load(resourceId)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(imageView);
                h.postDelayed(this, 700);
            }
        });

        new DatabaseHelper(this, true).execute();
    }

    private String getRandomItemOrSlotDrawable() {
        if (CalculationHelper.randomBoolean()) {
            int tier = CalculationHelper.randomNumber(1, 10);
            if (tier == 8 || tier == 9) {
                tier = 0;
            }
            int minType = DisplayHelper.getMinTypeForTier(tier);
            int maxType = DisplayHelper.getMaxTypeForTier(tier);
            return DisplayHelper.getItemImageFile(tier, CalculationHelper.randomNumber(minType, maxType));
        } else {
            return DisplayHelper.getPersonImageFile(CalculationHelper.randomNumber(1, 42));
        }
    }

    public void startGame() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        finish();
    }
}
