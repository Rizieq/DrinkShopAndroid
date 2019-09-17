package com.rizieq.drinkshop.Utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.rizieq.drinkshop.R;
import com.rizieq.drinkshop.ShowOrderActivity;

public class NotificationHelper extends ContextWrapper {

    private static final String EDMT_CHANNEL_ID = "com.rizieq.drinkshop.EDMTDev";
    private static final String EDMT_CHANNEL_NAME = "Drink Shop";

    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel edmtChannel = new NotificationChannel(EDMT_CHANNEL_ID,EDMT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        edmtChannel.enableLights(false);
        edmtChannel.enableVibration(true);
        edmtChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(edmtChannel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null)
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getDrinkShopNotification(String title,
                                                         String message,
                                                         Uri soundUri)
    {
        Intent intent = new Intent(getBaseContext(), ShowOrderActivity.class);
        PendingIntent contextIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return new Notification.Builder(getApplicationContext(),EDMT_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(contextIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(soundUri)
                .setAutoCancel(true);
    }

}
