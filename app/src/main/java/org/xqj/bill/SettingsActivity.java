package org.xqj.bill;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * 设置页面
 *
 * @author Chaos
 *         2016/01/10.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
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

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || PreferenceFragment.class.getName().equals(fragmentName)
                || super.isValidFragment(fragmentName);
    }

    /**
     * 主设置页面
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener,
            SharedPreferences.OnSharedPreferenceChangeListener,
            TimePickerDialog.OnTimeSetListener, DialogCreatable {

        private static final String KEY_CHECK_FOR_UPDATE = "check_for_update";
        private static final String KEY_HELP_AND_FEEDBACK = "help_and_feedback";
        private static final String KEY_ENABLE_REMIND_ADD_BILL = "enable_remind_add_bill";
        private static final String KEY_REMIND_ADD_BILL = "remind_add_bill";
        private static final String KEY_ENABLE_REMIND_EXCEEDING = "enable_remind_exceeding";
        private static final String KEY_REMIND_EXCEEDING = "remind_exceeding";

        private Preference mCheckForUpdatePref;
        private SwitchPreference mEnableRemindAddBillPref;
        private Preference mRemindAddBillPref;
        private SwitchPreference mEnableRemindExceedingPref;
        private EditTextPreference mRemindExceedingPref;
        private Preference mHelpAndFeedbackPref;

        private DialogFragment mDialogFragment;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);

            mCheckForUpdatePref = findPreference(KEY_CHECK_FOR_UPDATE);
            mEnableRemindAddBillPref = (SwitchPreference) findPreference(KEY_ENABLE_REMIND_ADD_BILL);
            mRemindAddBillPref = findPreference(KEY_REMIND_ADD_BILL);
            mEnableRemindExceedingPref = (SwitchPreference) findPreference(KEY_ENABLE_REMIND_EXCEEDING);
            mRemindExceedingPref = (EditTextPreference) findPreference(KEY_REMIND_EXCEEDING);
            mHelpAndFeedbackPref = findPreference(KEY_HELP_AND_FEEDBACK);

            mCheckForUpdatePref.setSummary(String.format(getString(R.string.version_name), BuildConfig.VERSION_NAME));

            mRemindAddBillPref.setEnabled(
                    getPreferenceManager().getSharedPreferences().getBoolean(KEY_ENABLE_REMIND_ADD_BILL, false));
            mRemindAddBillPref.setSummary(DateFormat.getTimeFormat(getActivity()).format(new Date(
                    getPreferenceManager().getSharedPreferences().getLong(KEY_REMIND_ADD_BILL, 0L))));

            mRemindExceedingPref.setEnabled(
                    getPreferenceManager().getSharedPreferences().getBoolean(KEY_ENABLE_REMIND_EXCEEDING, false));
            mRemindExceedingPref.setSummary(
                    getPreferenceManager().getSharedPreferences().getString(KEY_REMIND_EXCEEDING, "1000"));

            mCheckForUpdatePref.setOnPreferenceClickListener(this);
            mHelpAndFeedbackPref.setOnPreferenceClickListener(this);
            mRemindAddBillPref.setOnPreferenceClickListener(this);

            mEnableRemindAddBillPref.setOnPreferenceChangeListener(this);
            mEnableRemindExceedingPref.setOnPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            mRemindExceedingPref.setSummary(mRemindExceedingPref.getText());
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            String key = preference.getKey();
            if (KEY_CHECK_FOR_UPDATE.equals(key)) {
                Toast.makeText(getActivity(), R.string.already_the_newest_version, Toast.LENGTH_SHORT).show();
            } else if (KEY_HELP_AND_FEEDBACK.equals(key)) {
                Toast.makeText(getActivity(), R.string.feature_is_not_yet_open, Toast.LENGTH_SHORT).show();
            } else if (KEY_REMIND_ADD_BILL.equals(key)) {
                removeTimePickerDialog();
                showTimePickerDialog();
            }
            return true;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            if (KEY_ENABLE_REMIND_ADD_BILL.equals(key)) {
                mRemindAddBillPref.setEnabled((boolean) newValue);
                mRemindAddBillPref.setSummary(DateFormat.getTimeFormat(getActivity()).format(new Date(
                        getPreferenceManager().getSharedPreferences().getLong(KEY_REMIND_ADD_BILL, 0L))));
            } else if (KEY_ENABLE_REMIND_EXCEEDING.equals(key)) {
                mRemindExceedingPref.setEnabled((boolean) newValue);
                mRemindExceedingPref.setSummary(
                        getPreferenceManager().getSharedPreferences().getString(KEY_REMIND_EXCEEDING, "1000"));
            }
            return true;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (KEY_REMIND_EXCEEDING.equals(key)) {
                mRemindExceedingPref.setSummary(mRemindExceedingPref.getText());
            }
        }

        private void removeTimePickerDialog() {
            if (mDialogFragment != null) {
                mDialogFragment.dismiss();
                mDialogFragment = null;
            }
        }

        private void showTimePickerDialog() {
            mDialogFragment = new BillDialogFragment(this, 0);
            mDialogFragment.show(getChildFragmentManager(), "TimePicker");
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            Date when = calendar.getTime();
            mRemindAddBillPref.setSummary(DateFormat.getTimeFormat(getActivity()).format(when));
            getPreferenceManager().getSharedPreferences().edit().putLong(KEY_REMIND_ADD_BILL, when.getTime()).apply();
        }

        @Override
        public Dialog onCreateCustomDialog(int dialogId) {
            Calendar calendar = Calendar.getInstance();
            return new TimePickerDialog(
                    getActivity(),
                    this,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(getActivity()));
        }
    }
}
