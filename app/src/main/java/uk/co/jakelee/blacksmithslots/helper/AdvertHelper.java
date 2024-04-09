package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;

import java.util.Map;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.InterstitialActivity;
import uk.co.jakelee.blacksmithslots.main.MapActivity;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class AdvertHelper implements AppLovinAdRewardListener, AppLovinAdDisplayListener, AppLovinAdVideoPlaybackListener {
    private AppLovinIncentivizedInterstitial advert;
    private MapActivity activity;
    private final Context context;
    private boolean verified;
    private boolean tryingToLoad;
    private static AdvertHelper dhInstance = null;

    private AdvertHelper(Context context) {
        this.context = context;

        AppLovinSdk.initializeSdk(context);
        advert = AppLovinIncentivizedInterstitial.create(context);
        advert.preload(null);
    }

    public static AdvertHelper getInstance(Context ctx) {
        if (dhInstance == null) {
            dhInstance = new AdvertHelper(ctx.getApplicationContext());
        }
        return dhInstance;
    }

    public void showAdvert(final MapActivity activity) {
        this.activity = activity;
        verified = false;
        tryingToLoad = true;

        if (advert.isAdReadyToDisplay()) {
            Log.d("Advert", "1");
            advert.show(activity, this, this, this);
        } else {
            Log.d("Advert", "3");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!verified && tryingToLoad) {
                        activity.startActivityForResult(new Intent(activity, InterstitialActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
                                Constants.ADVERT_WATCH);
                    }
                }
            }, 10000);
        }
    }

    private void tryReward() {
        if (verified) {
            rewardAdvertItems(activity);
        } else {
            AlertHelper.error(activity, activity.getString(R.string.error_advert_unverified), false);
        }
    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        tryReward();
        advert.preload(null);
    }

    public void rewardAdvertItems(final MapActivity mapActivity) {
        Statistic.add(Enums.Statistic.AdvertsWatched);
        mapActivity.displayAdvertSuccess();
        mapActivity.setAdvertUnclaimable();
    }

    @Override
    public void userRewardVerified(AppLovinAd appLovinAd, Map map) {
        verified = true;
    }

    @Override public void videoPlaybackBegan(AppLovinAd appLovinAd) {}
    @Override public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {}
    @Override public void adDisplayed(AppLovinAd appLovinAd) {
        tryingToLoad = false;
    }
    @Override public void userOverQuota(AppLovinAd appLovinAd, Map map) {}
    @Override public void userRewardRejected(AppLovinAd appLovinAd, Map map) {}
    @Override public void validationRequestFailed(AppLovinAd appLovinAd, int i) {}
    @Override public void userDeclinedToViewAd(AppLovinAd appLovinAd) {}

}
