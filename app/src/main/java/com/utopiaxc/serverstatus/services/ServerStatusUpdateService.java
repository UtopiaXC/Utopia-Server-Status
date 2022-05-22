package com.utopiaxc.serverstatus.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.utopiaxc.serverstatus.MainActivity;
import com.utopiaxc.serverstatus.R;

public class ServerStatusUpdateService extends Service {
    private ServerStatusUpdateBinder binder;
    private Context context;
    private NotificationManager notificationManager;
    private final String NOTIFICATION_CHANNEL_ID = "USS_BACKGROUND_NOTIFICATION";
    private final String NOTIFICATION_CHANNEL_NAME = "USS_BACKGROUND_NOTIFICATION";
    private final int NOTIFICATION_ID = 154651133;

    public ServerStatusUpdateService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("SERVICE START");
        binder = new ServerStatusUpdateBinder();
        context = this;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, getNotification());
    }

    private Notification getNotification() {
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.app_name))
                .setOngoing(true)
                .setContentText(context.getString(R.string.background_notification));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }
        builder.setSound(null);
        builder.setVibrate(null);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class ServerStatusUpdateBinder extends Binder {
        public ServerStatusUpdateService getService() {
            return ServerStatusUpdateService.this;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(NOTIFICATION_ID);
    }
}