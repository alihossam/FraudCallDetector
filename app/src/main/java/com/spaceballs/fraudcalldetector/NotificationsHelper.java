package com.spaceballs.fraudcalldetector;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationsHelper {
    static final String CHANNEL_ID = "FraudBusterChannel";

    public static void createAndPushMainNotification(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel nc = notificationManager.getNotificationChannel(CHANNEL_ID);
            if(nc == null) {
                notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "TestChannel", NotificationManager.IMPORTANCE_DEFAULT ));
            }

        }

        // TODO change this to make it more meaningful
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Started")
                .setContentText("Application is running in the background")
                .setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();
        mBuilder.setOngoing(true);
        // default phone settings for notifications
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notificationManager.notify(10, notification);
    }

    // TODO make a card show up or something
    public static void pushNotification(Context context, String title, String content) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel nc = notificationManager.getNotificationChannel(CHANNEL_ID);
            if(nc == null) {
                notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "TestChannel", NotificationManager.IMPORTANCE_DEFAULT ));
            }

        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();
        mBuilder.setOngoing(true);
        // default phone settings for notifications
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;

        notificationManager.notify(10, notification);
    }
}
