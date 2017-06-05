package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
import android.content.Intent;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.adview.AppLovinInterstitialAd;
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
    public AppLovinIncentivizedInterstitial advert;
    private MapActivity activity;
    private Context context;
    private boolean verified;
    private static AdvertHelper dhInstance = null;

    public AdvertHelper(Context context) {
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

    public void showAdvert(MapActivity activity) {
        this.activity = activity;
        verified = false;

        if (advert.isAdReadyToDisplay()) {
            advert.show(activity, this, this, this);
        } else if (AppLovinInterstitialAd.isAdReadyToDisplay(activity)) {
            AppLovinInterstitialAd.show(activity);
        } else {
            activity.startActivityForResult(new Intent(activity, InterstitialActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
                    Constants.ADVERT_WATCH);
        }
    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        if (verified) {
            activity.advertHelper.rewardAdvertItems(activity);
        } else {
            AlertHelper.error(activity, activity.getString(R.string.error_advert_unverified), false);
        }

        advert.preload(null);
    }

    @Override
    public void userRewardVerified(AppLovinAd appLovinAd, Map map) {
        verified = true;
    }

    @Override public void videoPlaybackBegan(AppLovinAd appLovinAd) {}
    @Override public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {}
    @Override public void adDisplayed(AppLovinAd appLovinAd) {}
    @Override public void userOverQuota(AppLovinAd appLovinAd, Map map) {}
    @Override public void userRewardRejected(AppLovinAd appLovinAd, Map map) {}
    @Override public void validationRequestFailed(AppLovinAd appLovinAd, int i) {}
    @Override public void userDeclinedToViewAd(AppLovinAd appLovinAd) {}

    public void rewardAdvertItems(MapActivity mapActivity) {
        AlertHelper.success(mapActivity, context.getString(R.string.advert_watch_verified) + IncomeHelper.claimAdvertBonus(mapActivity), true);
        Statistic.add(Enums.Statistic.AdvertsWatched);
        mapActivity.setAdvertUnclaimable();
    }
}
