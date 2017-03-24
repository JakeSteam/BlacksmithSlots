package uk.co.jakelee.blacksmithslots.helper;

import java.util.concurrent.TimeUnit;

import uk.co.jakelee.blacksmithslots.model.Iap;

/**
 * Created by jakel on 24/03/2017.
 */

public class IapHelper {
    public static int getPassDaysLeft() {
        Iap pass = Iap.get(Enums.Iap.BlacksmithPass);
        if (pass.getLastPurchased() == 0 || pass.getTimesPurchased() == 0) {
            return 0;
        }

        long difference = System.currentTimeMillis() - pass.getLastPurchased();
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(difference);
        return differenceInDays > Constants.PASS_DAYS ? Constants.PASS_DAYS : (int)differenceInDays;
    }
}
