package org.xqj.bill.model;

import io.realm.RealmObject;

/**
 * 账单项,对应一条收入/支出记录
 *
 * @author Chaos
 *         2016/01/10.
 */
public class BillItem extends RealmObject {

    private int type;// 消费类型

    private float sum;// 金额

    private long dateTime;// 隶属于哪天

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
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
}