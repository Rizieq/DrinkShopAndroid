package com.rizieq.drinkshop.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rizieq.drinkshop.R;
import com.rizieq.drinkshop.Retrofit.IDrinkShopAPI;
import com.rizieq.drinkshop.ShowOrderActivity;
import com.rizieq.drinkshop.Utils.Common;
import com.rizieq.drinkshop.Utils.NotificationHelper;

import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        if (Common.currentUser != null)
            updateTokenToFirebase(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData() != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                sendNotificationAPI26(remoteMessage);
            else
                sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        // GET Information from Message

        Intent intent = new Intent(getBaseContext(), ShowOrderActivity.class);
        PendingIntent contextIntent = PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Map<String,String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(contextIntent)
                .setSound(defaultSoundUri);

        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(new Random().nextInt(),builder.build());
    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {

        // GET Information from Message
        Map<String,String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");
        // FROM API level 26 , we need implement Notification Channel
        NotificationHelper helper;
        Notification.Builder builder;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        helper = new NotificationHelper(this);
        builder = helper.getDrinkShopNotification(title,message,defaultSoundUri);

        helper.getManager().notify(new Random().nextInt(),builder.build());

    }

    private void updateTokenToFirebase(String token) {
        IDrinkShopAPI mService = Common.getAPI();
        mService.updateToken(Common.currentUser.getPhone(),
                token,
                "0")
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("DEBUG",response.toString());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("DEBUG",t.getMessage());
                    }
                });
    }
}
