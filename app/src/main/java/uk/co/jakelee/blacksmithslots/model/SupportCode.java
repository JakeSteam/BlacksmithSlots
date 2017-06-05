package uk.co.jakelee.blacksmithslots.model;

import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmithslots.helper.SupportCodeHelper;

@Table(name = "k")
public class SupportCode extends SugarRecord {
    @Column(name = "a")
    private String supportCode;

    @Column(name = "b")
    private long timeUsed;

    public SupportCode() {
    }

    public SupportCode(String supportCode) {
        this.supportCode = SupportCodeHelper.encode(supportCode);
        this.timeUsed = System.currentTimeMillis();
    }

    public static boolean alreadyApplied(String supportCode) {
        return Select.from(SupportCode.class).where(
                Condition.prop("a").eq(SupportCodeHelper.encode(supportCode))).count() > 0;
    }

    public String getSupportCode() {
        return SupportCodeHelper.decode(supportCode);
    }

    public void setSupportCode(String supportCode) {
        this.supportCode = SupportCodeHelper.encode(supportCode);
        this.timeUsed = System.currentTimeMillis();
    }

    public long getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(long timeUsed) {
        this.timeUsed = timeUsed;
    }
}
