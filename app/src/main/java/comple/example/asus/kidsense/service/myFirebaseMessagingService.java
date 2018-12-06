package comple.example.asus.kidsense.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import comple.example.asus.kidsense.MainActivity;
import comple.example.asus.kidsense.MapsActivity;
import comple.example.asus.kidsense.R;

public class myFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        System.out.println("New Fb Token : " + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData() != null) {
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        String content = data.get("content");
        String deviceId = data.get("deviceId");
        String latitude = data.get("latitude");
        String longitude = data.get("longitude");
        String timestamp = data.get("timestamp");

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "kidsense";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "kidsense Notification", NotificationManager.IMPORTANCE_MAX);

            notificationChannel.setDescription("Kidsense Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

//        String click_action = remoteMessage.getNotification().getClickAction();
//        Intent intent = new Intent(click_action);
        int uniqueId = (int) (System.currentTimeMillis() & 0xfffffff);

        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("content", content);
        intent.putExtra("deviceId", deviceId);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.putExtra("timestamp", timestamp);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(content);
        inboxStyle.addLine("Lat : " + latitude);
        inboxStyle.addLine("Lng : " + longitude);
        inboxStyle.addLine("When : " + timestamp);
        inboxStyle.setSummaryText(deviceId);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_kidsense_logo)
                .setTicker("New Incoming Location")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentTitle(content)
                .setContentText(timestamp)
                .setStyle(inboxStyle)
                .setContentInfo("Location")
                .setContentIntent(pendingIntent);

        notificationManager.notify(uniqueId,notificationBuilder.build());

    }
}
