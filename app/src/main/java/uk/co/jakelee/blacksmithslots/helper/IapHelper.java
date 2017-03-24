package uk.co.jakelee.blacksmithslots.helper;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.jakelee.blacksmithslots.model.Iap;
import uk.co.jakelee.blacksmithslots.model.ItemBundle;

public class IapHelper {
    public static int getPassDaysLeft() {
        Iap pass = Iap.get(Enums.Iap.BlacksmithPass);
        if (pass.getLastPurchased() == 0 || pass.getTimesPurchased() == 0) {
            return 0;
        }

        long difference = System.currentTimeMillis() - pass.getLastPurchased();
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(difference);
        return Constants.PASS_DAYS - (differenceInDays > Constants.PASS_DAYS ? Constants.PASS_DAYS : (int)differenceInDays);
    }

    public static List<List<ItemBundle>> getPassRewards() {
        List<List<ItemBundle>> rewards = new ArrayList<>();
        for (int i = 1; i <= Constants.PASS_DAYS; i++) {
            List<ItemBundle> dailyRewards = getPassRewardsForDay(i);
            if (dailyRewards != null) {
                rewards.add(dailyRewards);
            } else {
                rewards.add(new ArrayList<ItemBundle>());
            }
        }
        return rewards;
    }

    public static List<ItemBundle> getPassRewardsForDay(int day) {
        return Select.from(ItemBundle.class).where(
                Condition.prop("f").eq(Enums.ItemBundleType.PassReward.value),
                Condition.prop("a").eq(day)).list();
    }
}
