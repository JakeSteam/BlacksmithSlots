package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmithslots.helper.Enums;

@Table(name = "a")
public class Iap extends SugarRecord {
    @Column(name = "a")
    private int iapId;

    @Column(name = "b")
    private String iapName;

    @Column(name = "c")
    private long lastPurchased;

    @Column(name = "d")
    private int timesPurchased;

    @Column(name = "e")
    private boolean isVipPurchase;

    public Iap() {
    }

    public Iap(Enums.Iap iap, long lastPurchased, int timesPurchased, boolean isVipPurchase) {
        this.iapId = iap.value;
        this.iapName = iap.name().toLowerCase();
        this.lastPurchased = lastPurchased;
        this.timesPurchased = timesPurchased;
        this.isVipPurchase = isVipPurchase;
    }

    public Iap(Enums.Iap iap, boolean isVipPurchase) {
        this.iapId = iap.value;
        this.iapName = iap.name().toLowerCase();
        this.lastPurchased = 0;
        this.timesPurchased = 0;
        this.isVipPurchase = isVipPurchase;
    }

    public static Iap get(Enums.Iap iap) {
        return Select.from(Iap.class).where(
                Condition.prop("a").eq(iap.value)
        ).first();
    }

    public static Iap get(int iap) {
        return Select.from(Iap.class).where(
                Condition.prop("a").eq(iap)
        ).first();
    }

    public static Iap get(String iap) {
        return Select.from(Iap.class).where(
                Condition.prop("b").eq(iap)
        ).first();
    }

    public int getIapId() {
        return iapId;
    }

    public void setIapId(int iapId) {
        this.iapId = iapId;
    }

    public String getIapName() {
        return iapName;
    }

    public void setIapName(String iapName) {
        this.iapName = iapName;
    }

    public long getLastPurchased() {
        return lastPurchased;
    }

    public void setLastPurchased(long lastPurchased) {
        this.lastPurchased = lastPurchased;
    }

    public int getTimesPurchased() {
        return timesPurchased;
    }

    public void setTimesPurchased(int timesPurchased) {
        this.timesPurchased = timesPurchased;
    }

    public boolean isVipPurchase() {
        return isVipPurchase;
    }

    public void setVipPurchase(boolean vipPurchase) {
        isVipPurchase = vipPurchase;
    }
}
