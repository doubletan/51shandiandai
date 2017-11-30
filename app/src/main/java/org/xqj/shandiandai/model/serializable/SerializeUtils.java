package org.xqj.shandiandai.model.serializable;

import org.xqj.shandiandai.model.BillItem;
import org.xqj.shandiandai.model.ConsumptionType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chaos
 *         2016/01/14.
 */
public class SerializeUtils {

    public static BillItem toBillItem(SerializableBillItem sBItem) {
        BillItem item = new BillItem();
        item.setId(sBItem.getId());
        item.setNote(sBItem.getNote());
        item.setSum(sBItem.getSum());
        item.setConsumptionType(sBItem.getConsumptionType());
        item.setDateTime(sBItem.getDateTime());
        item.setIncome(sBItem.isIncome());
        return item;
    }

    public static SerializableBillItem toSerializableBillItem(BillItem bItem) {
        SerializableBillItem item = new SerializableBillItem();
        item.setId(bItem.getId());
        item.setNote(bItem.getNote());
        item.setSum(bItem.getSum());
        item.setConsumptionType(bItem.getConsumptionType());
        item.setDateTime(bItem.getDateTime());
        item.setIncome(bItem.isIncome());
        return item;
    }

    public static List<BillItem> toBillItemList(List<SerializableBillItem> sBItems) {
        List<BillItem> items = new ArrayList<>(sBItems.size());
        for (SerializableBillItem item : sBItems) {
            items.add(toBillItem(item));
        }
        return items;
    }

    public static List<SerializableBillItem> toSerializableBillItemList(List<BillItem> bItems) {
        List<SerializableBillItem> items = new ArrayList<>(bItems.size());
        for (BillItem item : bItems) {
            items.add(toSerializableBillItem(item));
        }
        return items;
    }

    public static ConsumptionType toConsumptionType(SerializableConsumptionType sCType) {
        ConsumptionType type = new ConsumptionType();
        type.setTypeName(sCType.getTypeName());
        return type;
    }

    public static SerializableConsumptionType toSerializableConsumptionType(ConsumptionType cType) {
        SerializableConsumptionType type = new SerializableConsumptionType();
        type.setTypeName(cType.getTypeName());
        return type;
    }

    public static List<ConsumptionType> toConsumptionTypeList(List<SerializableConsumptionType> sCTypes) {
        List<ConsumptionType> types = new ArrayList<>(sCTypes.size());
        for (SerializableConsumptionType type : sCTypes) {
            types.add(toConsumptionType(type));
        }
        return types;
    }

    public static List<SerializableConsumptionType> toSerializableConsumptionTypeList(List<ConsumptionType> cTypes) {
        List<SerializableConsumptionType> types = new ArrayList<>(cTypes.size());
        for (ConsumptionType type : cTypes) {
            types.add(toSerializableConsumptionType(type));
        }
        return types;
    }
}