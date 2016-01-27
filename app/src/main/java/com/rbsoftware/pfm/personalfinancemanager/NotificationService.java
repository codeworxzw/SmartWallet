package com.rbsoftware.pfm.personalfinancemanager;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Background services that fires notification at scheduled time
 *
 * @author Roman Burzakovskiy
 */
public class NotificationService extends WakefulIntentService {
    private final static String TAG = "NotificationService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationService() {

        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {


        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        Log.d(TAG, "onHandleIntent called. Notification will be fired on " + calendar.getTimeInMillis());
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
        super.onHandleIntent(intent);


    }
}
