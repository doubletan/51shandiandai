package org.xqj.bill;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.xqj.bill.model.BillItem;
import org.xqj.bill.model.ConsumptionType;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @author Chaos
 *         2016/01/14.
 */
public abstract class DataFragment extends Fragment implements Updateable {

    private Realm mRealm;
    private SharedPreferences mDefaultPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getActivity());
        mDefaultPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    public List<BillItem> getCurrentBillData() {
        List<BillItem> items = new ArrayList<>();
        RealmResults<BillItem> results = mRealm.where(BillItem.class)
                .greaterThanOrEqualTo("dateTime", getDefaultPreferences().getLong(PreferenceKeys.KEY_GREATER_TIME, 0))
                .lessThan("dateTime", getDefaultPreferences().getLong(PreferenceKeys.KEY_LESS_TIME, 0))
                .findAll();
        results.sort("dateTime", Sort.DESCENDING);
        for (BillItem item : results) {
            items.add(item);
        }
        return items;
    }

    public List<String> getAllConsumptionType() {
        List<String> types = new ArrayList<>();
        for (ConsumptionType type : mRealm.allObjects(ConsumptionType.class)) {
            types.add(type.getTypeName());
        }
        return types;
    }

    public float getCurrentSumByTwoType(boolean isIncome, String consumptionType) {
        Number result = mRealm.where(BillItem.class)
                .greaterThanOrEqualTo("dateTime", getDefaultPreferences().getLong(PreferenceKeys.KEY_GREATER_TIME, 0))
                .lessThan("dateTime", getDefaultPreferences().getLong(PreferenceKeys.KEY_LESS_TIME, 0))
                .equalTo("consumptionType", consumptionType)
                .equalTo("income", isIncome)
                .sum("sum");
        return result != null ? result.floatValue() : 0;
    }

    public SharedPreferences getDefaultPreferences() {
        return mDefaultPreferences;
    }

    public Realm getRealm() {
        return mRealm;
    }
}
