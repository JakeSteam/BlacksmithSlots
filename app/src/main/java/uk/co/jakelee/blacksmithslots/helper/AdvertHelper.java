package uk.co.jakelee.blacksmithslots.helper;

import android.content.Context;
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
import com.tapjoy.TapjoyConnectFlag;

import java.util.Hashtable;
import java.util.Map;

import uk.co.jakelee.blacksmithslots.R;
import uk.co.jakelee.blacksmithslots.main.MapActivity;
import uk.co.jakelee.blacksmithslots.model.Statistic;

public class AdvertHelper implements AppLovinAdRewardListener, AppLovinAdDisplayListener, AppLovinAdVideoPlaybackListener, TJPlacementListener {
    private AppLovinIncentivizedInterstitial advert;
    private MapActivity activity;
    private final Context context;
    private boolean verified;
    private static AdvertHelper dhInstance = null;

    private AdvertHelper(Context context) {
        this.context = context;

        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");
        Tapjoy.connect(context, "0GdMCMz9T7usON1Y31Z0vgECyFfksewNKvvG7ugayPBzgGvec4MYEtJlgvih", connectFlags);
        Tapjoy.setDebugEnabled(true);

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

    public void showAdvert(MapActivity activity, TJPlacement adPlacement) {
        this.activity = activity;
        verified = false;

        String c = adPlacement.getName();
        boolean a = adPlacement.isContentAvailable();
        boolean b = adPlacement.isContentReady();
        Log.d("TapJoy", "Name: " + c + (a ? "Yes" : "No") + (b ? "Yes" : "No"));
        if (!a || !b) {
            Log.d("Requesting", "aaa");
            adPlacement.requestContent();
        } else {
            Log.d("Showing", "aaa");
            adPlacement.showContent();
        }
        /*if (adPlacement.isContentReady()) {
            adPlacement.showContent();
        } else if (advert.isAdReadyToDisplay()) {
            advert.show(activity, this, this, this);
        } else if (AppLovinInterstitialAd.isAdReadyToDisplay(activity)) {
            AppLovinInterstitialAd.show(activity);
        } else {
            activity.startActivityForResult(new Intent(activity, InterstitialActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
                    Constants.ADVERT_WATCH);
        }*/
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

    public void onContentDismiss(TJPlacement placement) {}
    public void onPurchaseRequest(TJPlacement placement, TJActionRequest tjActionRequest, String string) {} // Called when the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
    public void onRewardRequest(TJPlacement placement, TJActionRequest tjActionRequest, String string, int number) {} // Called when the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
    public void onRequestSuccess(TJPlacement placement) {
        int i = 6;
    } // Called when the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
    public void onRequestFailure(TJPlacement placement, TJError error) {
        int i = 5;
    } // Called when there was a problem during connecting Tapjoy servers.
    public void onContentReady(TJPlacement placement) {} // Called when the content is actually available to display.
    public void onContentShow(TJPlacement placement) {} // Called when the content is showed.

    public void rewardAdvertItems(MapActivity mapActivity) {
        AlertHelper.success(mapActivity, context.getString(R.string.advert_watch_verified) + IncomeHelper.claimAdvertBonus(mapActivity), true);
        Statistic.add(Enums.Statistic.AdvertsWatched);
        mapActivity.setAdvertUnclaimable();
    }
}
