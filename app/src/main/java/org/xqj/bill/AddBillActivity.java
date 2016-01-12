package org.xqj.bill;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xqj.bill.model.BillItem;
import org.xqj.bill.model.ConsumptionType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * @author Chaos
 *         2016/01/12.
 */
public class AddBillActivity extends AppCompatActivity implements DialogCreatable,
        DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private static final String KEY_ADDED_CONSUMPTION_TYPE_TO_DB = "added_consumption_type_to_db";

    private static final int DIALOG_DATE_PICKER = 1;
    private static final int DIALOG_ADD_CONSUMPTION_TYPE = 2;

    @Bind(R.id.date) TextView mDateTextView;
    @Bind(R.id.type_group) RadioGroup mTypeGroup;
    @Bind(R.id.consumption_type) Spinner mConsumptionTypeSpinner;
    @Bind(R.id.sum) EditText mSumEditText;
    @Bind(R.id.note) EditText mNoteEditText;
    @Bind(R.id.fab) FloatingActionButton mFab;

    private DialogFragment mDialogFragment;

    private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private List<String> mConsumptionTypes;
    private ArrayAdapter<String> mConsumptionTypeAdapter;

    private int mPreConsumptionTypeIdx = 0;

    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);

        Realm realm = Realm.getInstance(this);
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!mPreferences.getBoolean(KEY_ADDED_CONSUMPTION_TYPE_TO_DB, false)) {// 判断是否为第一次启动页面
            mPreferences.edit().putBoolean(KEY_ADDED_CONSUMPTION_TYPE_TO_DB, true).apply();
            String[] defaultTypes = getResources().getStringArray(R.array.default_consumption_type);
            realm.beginTransaction();
            // '自定义'不必加到数据库中
            for (int i = 0; i < defaultTypes.length - 2; i++) {
                ConsumptionType type = new ConsumptionType();
                type.setTypeName(defaultTypes[i]);
                realm.copyToRealm(type);
            }
            // 将默认的消费类别添加到数据库中
            realm.commitTransaction();
        }

        ButterKnife.bind(this);

        mDateTextView.setOnClickListener(this);
        mFab.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        setDateText(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        mConsumptionTypes = new ArrayList<>();
        for (ConsumptionType ct : realm.allObjects(ConsumptionType.class)) {
            mConsumptionTypes.add(ct.getTypeName());
        }
        mConsumptionTypes.add("自定义");
        mConsumptionTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mConsumptionTypes);
        mConsumptionTypeSpinner.setAdapter(mConsumptionTypeAdapter);
        mConsumptionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == mConsumptionTypes.size() - 1) {
                    showFragmentDialog(DIALOG_ADD_CONSUMPTION_TYPE);
                    mConsumptionTypeSpinner.setSelection(mPreConsumptionTypeIdx);
                } else {
                    mPreConsumptionTypeIdx = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setDateText(int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mDateTextView.setText(sDateFormat.format(calendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        if (v == mDateTextView) {
            removeFragmentDialog();
            showFragmentDialog(DIALOG_DATE_PICKER);
        } else if (v == mFab) {
            save();
        }
    }

    @SuppressLint("inflateParams")
    @Override
    public Dialog onCreateCustomDialog(int dialogId) {
        if (dialogId == DIALOG_DATE_PICKER) {
            return new DatePickerDialog(this, this, mYear, mMonth, mDay);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_consumption_type, null);
            builder.setView(dialogView);
            final EditText newTypeEditText = (EditText) dialogView.findViewById(R.id.consumption_type);
            builder.setTitle(R.string.input_consumption_type);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newType = newTypeEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(newType)) {
                        Toast.makeText(getApplicationContext(), "类别不能为空", Toast.LENGTH_SHORT).show();
                    } else if (mConsumptionTypes.contains(newType)) {
                        Toast.makeText(getApplicationContext(), "已存在该类别", Toast.LENGTH_SHORT).show();
                    } else {
                        int lastIdx = mConsumptionTypes.size() - 1;
                        mConsumptionTypes.add(lastIdx, newType);
                        mConsumptionTypeAdapter.notifyDataSetChanged();
                        mConsumptionTypeSpinner.setSelection(lastIdx);
                        saveConsumptionType(newType);
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, null);
            return builder.create();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        setDateText(year, monthOfYear, dayOfMonth);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeFragmentDialog();
    }

    private void removeFragmentDialog() {
        if (mDialogFragment != null) {
            mDialogFragment.dismiss();
            mDialogFragment = null;
        }
    }

    private void showFragmentDialog(int dialogId) {
        mDialogFragment = new BillDialogFragment(this, dialogId);
        mDialogFragment.show(getFragmentManager(), "DatePicker");
    }

    private void save() {
        String sumStr = mSumEditText.getText().toString();
        sumStr = TextUtils.isEmpty(sumStr) ? "0" : sumStr;
        float sum = Float.parseFloat(sumStr);
        if (sum != 0) {
            Realm realm = Realm.getInstance(this);
            BillItem item = new BillItem();
            Calendar calendar = Calendar.getInstance();
            calendar.set(mYear, mMonth, mDay);
            item.setDateTime(calendar.getTimeInMillis());
            item.setIncome(mTypeGroup.getCheckedRadioButtonId() == R.id.income);
            item.setSum(Float.parseFloat(sumStr));
            item.setNote(mNoteEditText.getText().toString().trim());
            item.setConsumptionType(realm.where(ConsumptionType.class).equalTo("typeName", mConsumptionTypes.get(mConsumptionTypeSpinner.getSelectedItemPosition())).findFirst());
            realm.beginTransaction();
            realm.copyToRealm(item);
            realm.commitTransaction();
            finish();
        } else {
            Toast.makeText(this, "金额不能为零", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveConsumptionType(String newType) {
        ConsumptionType type = new ConsumptionType();
        type.setTypeName(newType);
        Realm realm = Realm.getInstance(this);
        realm.beginTransaction();
        realm.copyToRealm(type);
        realm.commitTransaction();
    }
}
