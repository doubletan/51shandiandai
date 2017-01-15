package org.xqj.youqianhua;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

/**
 * @author Chaos
 *         2016/01/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String ACTION_NOTIFY_TIME_CHANGED = "org.xqj.bill.action.NOTIFY_TIME_CHANGED";
    private static final String ACTION_NOTIFY_ADD_BILL = "org.xqj.bill.action.NOTIFY_ADD_BILL";

    private static final int NOTIFY_ADD_BILL_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isNotifyAddBillEnable = defaultPreferences.getBoolean(PreferenceKeys.KEY_ENABLE_REMIND_ADD_BILL, false);
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || ACTION_NOTIFY_TIME_CHANGED.equals(action)) {

            if (isNotifyAddBillEnable) {
                addAlarm(context);
            } else {
                cancelAlarm(context);
            }
        } else if (ACTION_NOTIFY_ADD_BILL.equals(action)) {
            if (isNotifyAddBillEnable) {
                notifyAddBill(context);
            }
        }
    }


    private void addAlarm(Context context) {
        Calendar hourCalendar = Calendar.getInstance();
        hourCalendar.clear();
        hourCalendar.set(Calendar.HOUR_OF_DAY, 10);

        Calendar currentCalendar = Calendar.getInstance();
        long currentTime = currentCalendar.getTimeInMillis();

        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.clear();
        alarmCalendar.setTimeInMillis(PreferenceManager.getDefaultSharedPreferences(context)
                .getLong(PreferenceKeys.KEY_REMIND_ADD_BILL, hourCalendar.getTimeInMillis()));
        int dayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH);
        alarmCalendar.set(
                currentCalendar.get(Calendar.YEAR),
                currentCalendar.get(Calendar.MONTH),
                dayOfMonth);
        long alarmTime = alarmCalendar.getTimeInMillis();

        if (currentTime >= alarmTime) {
            if (currentTime == alarmTime) {
                notifyAddBill(context);
            }
            alarmCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth + 1);
            alarmTime = alarmCalendar.getTimeInMillis();
        }
        setAlarmTime(context, alarmTime);
    }

    private void setAlarmTime(Context context, long timeInMillis) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION_NOTIFY_ADD_BILL);
        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar nextDayCalendar = Calendar.getInstance();
        nextDayCalendar.set(Calendar.DAY_OF_MONTH, nextDayCalendar.get(Calendar.DAY_OF_MONTH) + 1);
        am.setRepeating(AlarmManager.RTC, timeInMillis, nextDayCalendar.getTimeInMillis() - System.currentTimeMillis(), sender);
    }

    private void cancelAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION_NOTIFY_ADD_BILL);
        PendingIntent sender = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);
    }

    private void notifyAddBill(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);

        nm.cancel(NOTIFY_ADD_BILL_ID);
        nm.notify(NOTIFY_ADD_BILL_ID,
                new Notification.Builder(context)
                        .setContentTitle(context.getResources().getString(R.string.add_bill_title))
                        .setContentText(context.getResources().getString(R.string.add_bill_content))
                        .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .build());
    }

}
