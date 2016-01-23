package com.rbsoftware.pfm.personalfinancemanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by burzakovskiy on 1/5/2016.
 */
public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager mNM = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder dailyReminder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_wallet_white_24dp)
                .setContentTitle(context.getString(R.string.daily_reminder_title))
                .setContentText(context.getString(R.string.daily_reminder_text))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            dailyReminder.setContentText(context.getString(R.string.weekly_reminder_text));
        }
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, LoginActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LoginActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        dailyReminder.setContentIntent(resultPendingIntent);
        mNM.notify(1, dailyReminder.build());


    }
}
