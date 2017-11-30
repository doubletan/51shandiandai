package org.xqj.shandiandai;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;
import java.util.Date;

import static org.xqj.shandiandai.PreferenceKeys.KEY_CHECK_FOR_UPDATE;
import static org.xqj.shandiandai.PreferenceKeys.KEY_ENABLE_REMIND_ADD_BILL;
import static org.xqj.shandiandai.PreferenceKeys.KEY_ENABLE_REMIND_EXCEEDING;
import static org.xqj.shandiandai.PreferenceKeys.KEY_HELP_AND_FEEDBACK;
import static org.xqj.shandiandai.PreferenceKeys.KEY_REMIND_ADD_BILL;
import static org.xqj.shandiandai.PreferenceKeys.KEY_REMIND_EXCEEDING;
import static org.xqj.shandiandai.PreferenceKeys.KEY_VIEW_MODE;
import static org.xqj.shandiandai.PreferenceKeys.KEY_DEFAULT_THEME;

/**
 * 设置页面
 *
 * @author Chaos
 *         2016/01/10.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if ("粉红".equals(PreferenceManager.getDefaultSharedPreferences(this).getString("default_theme", "粉红"))) {
            setTheme(R.style.AppTheme_Pink);
        }

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

        private Preference mCheckForUpdatePref;
        private SwitchPreference mEnableRemindAddBillPref;
        private Preference mRemindAddBillPref;
        private SwitchPreference mEnableRemindExceedingPref;
        private EditTextPreference mRemindExceedingPref;
        private Preference mHelpAndFeedbackPref;
        private ListPreference mDefaultThemePref;
        private ListPreference mViewModePref;
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
            mViewModePref = (ListPreference) findPreference(KEY_VIEW_MODE);
            mDefaultThemePref = (ListPreference) findPreference(KEY_DEFAULT_THEME);

            mCheckForUpdatePref.setSummary(
                    String.format(getString(R.string.version_name), BuildConfig.VERSION_NAME));

            mRemindAddBillPref.setEnabled(
                    getPreferenceManager().getSharedPreferences().getBoolean(KEY_ENABLE_REMIND_ADD_BILL, false));
            Calendar hourCalendar = Calendar.getInstance();
            hourCalendar.clear();
            hourCalendar.set(Calendar.HOUR_OF_DAY, 10);
            mRemindAddBillPref.setSummary(DateFormat.getTimeFormat(getActivity()).format(new Date(
                    getPreferenceManager().getSharedPreferences().getLong(KEY_REMIND_ADD_BILL, hourCalendar.getTimeInMillis()))));

            mRemindExceedingPref.setEnabled(
                    getPreferenceManager().getSharedPreferences().getBoolean(KEY_ENABLE_REMIND_EXCEEDING, false));
            mRemindExceedingPref.setSummary(
                    getPreferenceManager().getSharedPreferences().getString(
                            KEY_REMIND_EXCEEDING, getString(R.string.remind_exceeding_default_summary)));

            mViewModePref.setSummary(
                    getPreferenceManager().getSharedPreferences().getString(KEY_VIEW_MODE, "月"));
            mDefaultThemePref.setSummary(
                    getPreferenceManager().getSharedPreferences().getString(KEY_DEFAULT_THEME, "粉红"));

            mCheckForUpdatePref.setOnPreferenceClickListener(this);
            mHelpAndFeedbackPref.setOnPreferenceClickListener(this);
            mRemindAddBillPref.setOnPreferenceClickListener(this);

            mEnableRemindAddBillPref.setOnPreferenceChangeListener(this);
            mEnableRemindExceedingPref.setOnPreferenceChangeListener(this);
            mViewModePref.setOnPreferenceChangeListener(this);
            mDefaultThemePref.setOnPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
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
                        getPreferenceManager().getSharedPreferences().getString(
                                KEY_REMIND_EXCEEDING, getString(R.string.remind_exceeding_default_summary)));
            } else if (KEY_VIEW_MODE.equals(key)) {
                mViewModePref.setSummary(newValue.toString());
            } else if (KEY_DEFAULT_THEME.equals(key)) {
                mDefaultThemePref.setSummary(newValue.toString());
                getActivity().recreate();
            }
            return true;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (KEY_REMIND_EXCEEDING.equals(key)) {
                String value = mRemindExceedingPref.getText();
                if (TextUtils.isEmpty(value) || Float.parseFloat(value) == 0) {
                    value = getString(R.string.remind_exceeding_default_summary);
                    mRemindExceedingPref.setText(value);
                    getPreferenceManager().getSharedPreferences().edit().putString(
                            KEY_REMIND_EXCEEDING, value).apply();
                    Toast.makeText(getActivity(), R.string.remind_exceeding_empty_tips, Toast.LENGTH_SHORT).show();
                }
                mRemindExceedingPref.setSummary(value);
            } else if (KEY_ENABLE_REMIND_ADD_BILL.equals(key)) {
                getActivity().sendBroadcast(new Intent("org.xqj.bill.action.NOTIFY_TIME_CHANGED"));
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
            calendar.clear();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            Date when = calendar.getTime();
            mRemindAddBillPref.setSummary(DateFormat.getTimeFormat(getActivity()).format(when));
            getPreferenceManager().getSharedPreferences().edit().putLong(
                    KEY_REMIND_ADD_BILL, when.getTime()).apply();
            getActivity().sendBroadcast(new Intent("org.xqj.bill.action.NOTIFY_TIME_CHANGED"));
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
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
