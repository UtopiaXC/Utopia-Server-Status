package com.utopiaxc.serverstatus.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.preference.PreferenceManager;

import com.utopiaxc.serverstatus.MainActivity;
import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.utils.UpdateStatus;

/**
 * 服务器状态更新服务
 * <p>后台服务，用于对服务器状态数据进行更新
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:24:31
 */
public class ServerStatusUpdateService extends Service {
    private ServerStatusUpdateBinder binder;
    private Context context;
    private NotificationManager notificationManager;
    private final String NOTIFICATION_CHANNEL_ID = "USS_BACKGROUND_NOTIFICATION";
    private final int NOTIFICATION_ID = 154651133;
    private Thread updateThread;

    /**
     * 服务入口
     * <p>当服务启动时运行
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:25:27
     */
    @Override
    public void onCreate() {
        super.onCreate();
        //创建进程间通信
        binder = new ServerStatusUpdateBinder();
        context = this;

        //后台常驻通知
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_NAME = "USS_BACKGROUND_NOTIFICATION";
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, getNotification());

        //当允许后台服务时启动更新线程
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean("backgroundService", true)) {
            updateThread = new Thread(new UpdateStatus(context));
            updateThread.start();
        }
    }

    /**
     * 获取后他常驻通知
     *
     * @return android.app.Notification
     * @author UtopiaXC
     * @since 2022-05-22 22:27:06
     */
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

    /**
     * 获取进程间通信
     * <p>重载函数，用于获取Binder
     *
     * @param intent 默认传递对象
     * @return android.os.IBinder
     * @author UtopiaXC
     * @since 2022-05-22 22:27:33
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * 自定义服务器状态更新线程通信
     * <p>用于返回当前服务
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:28:17
     */
    public class ServerStatusUpdateBinder extends Binder {
        /**
         * 返回服务器状态更新服务
         *
         * @return com.utopiaxc.serverstatus.services.ServerStatusUpdateService
         * @author UtopiaXC
         * @since 2022-05-22 22:28:49
         */
        public ServerStatusUpdateService getService() {
            return ServerStatusUpdateService.this;
        }
    }

    /**
     * 服务销毁
     * <p>销毁服务器数据更新后台服务并回收资源
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:29:09
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        //回收更新线程
        updateThread.interrupt();
        //回收后台通知
        notificationManager.cancel(NOTIFICATION_ID);
    }
}