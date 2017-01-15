package org.xqj.youqianhua.model.serializable;

import java.io.Serializable;

/**
 * @author Chaos
 *         2016/01/14.
 */
public class SerializableBillItem implements Serializable {

    private int id;

    private boolean income;// true 为收入

    private String consumptionType;// 消费类型

    private float sum;// 金额

    private long dateTime;// 隶属于哪天

    private String note;// 备注

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getConsumptionType() {
        return this.consumptionType;
    }

    public void setConsumptionType(String consumptionType) {
        this.consumptionType = consumptionType;
    }

    public float getSum() {
        return this.sum;
    }

    public void setSum(float sum) {
        this.sum = sum;
    }

    public long getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isIncome() {
        return income;
    }

    public void setIncome(boolean income) {
        this.income = income;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
