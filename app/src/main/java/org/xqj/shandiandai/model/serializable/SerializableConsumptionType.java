package org.xqj.shandiandai.model.serializable;

import java.io.Serializable;

/**
 * @author Chaos
 *         2016/01/14.
 */
public class SerializableConsumptionType implements Serializable {

    private String typeName;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
