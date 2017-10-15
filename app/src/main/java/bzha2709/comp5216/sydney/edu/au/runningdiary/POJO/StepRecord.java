package bzha2709.comp5216.sydney.edu.au.runningdiary.POJO;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

import bzha2709.comp5216.sydney.edu.au.runningdiary.tools.DateUtil;
import org.greenrobot.greendao.annotation.Generated;


/**
 * Created by Bingqing ZHAO on 2017/10/5.
 */

@Entity
public class StepRecord {
    @Id private Long id;
    @Property private Date date;
    @Property private int steps;

    @Keep
    public StepRecord()
    {
        date= DateUtil.getThisMorning();
        steps=0;
    }
    @Keep
    public StepRecord(Date d,int steps)
    {
     this.date=d;
    }
    @Generated(hash = 904788868)
    public StepRecord(Long id, Date date, int steps) {
        this.id = id;
        this.date = date;
        this.steps = steps;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
