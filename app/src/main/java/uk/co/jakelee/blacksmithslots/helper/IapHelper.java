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

    public static List<ItemBundle> getVipRewardsForLevel(int level) {
        return Select.from(ItemBundle.class).where(
                Condition.prop("f").eq(Enums.ItemBundleType.IapReward.value),
                Condition.prop("a").eq(level + 1)).list();
    }

    public static List<ItemBundle> getPassRewardsForDay(int day) {
        return Select.from(ItemBundle.class).where(
                Condition.prop("f").eq(Enums.ItemBundleType.PassReward.value),
                Condition.prop("a").eq(day)).list();
    }

    public static List<ItemBundle> getBundleRewards(int bundleId) {
        return Select.from(ItemBundle.class).where(
                Condition.prop("f").eq(Enums.ItemBundleType.IapReward.value),
                Condition.prop("a").eq(bundleId)).list();
    }

    public static List<ItemBundle> getUniqueBundleItems() {
        List<ItemBundle> bundles =  Select.from(ItemBundle.class).where("a > " + Enums.Iap.VipLevel6.value + " " +
                "AND f = " + Enums.ItemBundleType.IapReward.value + " " +
                "AND b <> " + Enums.Tier.None.value + " " +
                "GROUP BY b, c ORDER BY b, c").list();
        bundles.add(new ItemBundle(0, 0, 0));return bundles;
    }

    public static List<ItemBundle> getBundlesForItem(int tier, int type) {
        if (tier == Enums.Tier.None.value) {
            return Select.from(ItemBundle.class).where("a > " + Enums.Iap.VipLevel6.value + " AND f = " + Enums.ItemBundleType.IapReward.value + " AND b = " + tier).list();
        } else {
            return Select.from(ItemBundle.class).where("a > " + Enums.Iap.VipLevel6.value + " AND f = " + Enums.ItemBundleType.IapReward.value + " AND b = " + tier + " AND c = " + type).list();
        }
    }

    public static int getVipPrice(int level) {
        switch (level) {
            case 1: return 249;
            case 2: return 399;
            case 3: return 499;
            case 4: return 749;
            case 5: return 1099;
            case 6: return 1599;
        }
        return 0;
    }
}
