package com.rbsoftware.pfm.personalfinancemanager;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Sender reminder message to user
 *
 * @author Roman Burzakovskiy
 */
public class NotificationReceiver extends BroadcastReceiver {
    private final static String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        WakefulIntentService.acquireStaticLock(context); //acquire a partial WakeLock
        NotificationManager mNM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder dailyReminder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_wallet_white_24dp)
                .setContentTitle(context.getString(R.string.daily_reminder_title))
                .setContentText(context.getString(R.string.daily_reminder_text))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            dailyReminder.setContentText(context.getString(R.string.weekly_reminder_text));
        }
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, LoginActivity.class);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        dailyReminder.setContentIntent(resultPendingIntent);
        mNM.notify(1, dailyReminder.build());
    }

}
