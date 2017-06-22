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
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;

import java.util.Map;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.InterstitialActivity;
import uk.co.jakelee.blacksmithslots.main.MapActivity;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class AdvertHelper implements AppLovinAdRewardListener, AppLovinAdDisplayListener, AppLovinAdVideoPlaybackListener, TJPlacementListener {
    private AppLovinIncentivizedInterstitial advert;
    private MapActivity activity;
    private final Context context;
    private boolean verified;
    private boolean tryingToLoad;
    private static AdvertHelper dhInstance = null;

    private AdvertHelper(Context context) {
        this.context = context;

        Tapjoy.connect(context, "0GdMCMz9T7usON1Y31Z0vgECyFfksewNKvvG7ugayPBzgGvec4MYEtJlgvih", null);

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

    public void showAdvert(final MapActivity activity, TJPlacement adPlacement) {
        this.activity = activity;
        verified = false;
        tryingToLoad = true;

        if (advert.isAdReadyToDisplay()) {
            Log.d("Advert", "1");
            advert.show(activity, this, this, this);
        } else {
            Log.d("Advert", "3");
            adPlacement.requestContent();
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
        mapActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertHelper.success(mapActivity, context.getString(R.string.advert_watch_verified) + " " + IncomeHelper.claimAdvertBonus(mapActivity), true);
            }
        });
        Statistic.add(Enums.Statistic.AdvertsWatched);
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

    public void onContentReady(TJPlacement placement) {
        tryingToLoad = false;
        placement.showContent();
    }
    public void onContentDismiss(TJPlacement placement) {
        verified = true;
        tryReward();
    }
    public void onPurchaseRequest(TJPlacement placement, TJActionRequest tjActionRequest, String string) {} // Called when the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
    public void onRewardRequest(TJPlacement placement, TJActionRequest tjActionRequest, String string, int number) {} // Called when the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
    public void onRequestSuccess(TJPlacement placement) {} // Called when the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
    public void onRequestFailure(TJPlacement placement, TJError error) {} // Called when there was a problem during connecting Tapjoy servers.
    public void onContentShow(TJPlacement placement) {
    } // Called when the content is showed.
}
