package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;

import java.util.Map;

import uk.co.jakelee.blacksmithslots.main.MapActivity;

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

    public void openInterstitial() {
        /*Intent intent = new Intent(context, InterstitialActivity.class);
        intent.putExtra(AdvertHelper.INTENT_ID, purpose.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);*/
        AlertHelper.error(activity, "Advert failed to load...", false);
    }

    public void showAdvert(MapActivity activity) {
        this.activity = activity;
        verified = false;

        if (advert.isAdReadyToDisplay()) {
            advert.show(activity, this, this, this);
        } else if (AppLovinInterstitialAd.isAdReadyToDisplay(activity)) {
            AppLovinInterstitialAd.show(activity);
        } else {
            openInterstitial();
        }
    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        if (verified) {
            AlertHelper.success(activity, "Advert watch verified! " + IncomeHelper.watchAdvert(context, true), false);
            activity.setAdvertUnclaimable();
        } else {
            AlertHelper.error(activity, "Something went wrong, and the ad view couldn't be verified. Sorry!", false);
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

}
