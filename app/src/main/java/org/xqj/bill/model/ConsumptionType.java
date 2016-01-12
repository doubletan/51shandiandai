package org.xqj.bill.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Chaos
 *         2016/01/12.
 */
public class ConsumptionType extends RealmObject {

    @PrimaryKey
    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
