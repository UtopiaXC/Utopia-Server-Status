package com.utopiaxc.serverstatus.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

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
    private Context mContext;
    private NotificationManager notificationManager;
    private final String NOTIFICATION_CHANNEL_ID = "USS_BACKGROUND_NOTIFICATION";
    private final int NOTIFICATION_ID = 154651133;
    private Thread updateThread;
    private ServerUpdateErrorReceiver serverUpdateErrorReceiver;

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
        mContext = this;

        //后台常驻通知
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String NOTIFICATION_CHANNEL_NAME = "USS_BACKGROUND_NOTIFICATION";
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, getNotification());

        //当允许后台服务时启动更新线程
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (sharedPreferences.getBoolean("backgroundService", true)) {
            updateThread = new Thread(new UpdateStatus(mContext));
            updateThread.start();
        }

        //注册后台服务进程异常广播
        serverUpdateErrorReceiver=new ServerUpdateErrorReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATE_ERROR");
        mContext.registerReceiver(serverUpdateErrorReceiver,intentFilter,"com.utopiaxc.receiver.RECEIVE_INTERNAL_BROADCAST",null);
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
                .setContentTitle(mContext.getString(R.string.app_name))
                .setOngoing(true)
                .setContentText(mContext.getString(R.string.background_notification));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }
        builder.setSound(null);
        builder.setVibrate(null);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE);
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
        //回收广播监听器
        mContext.unregisterReceiver(serverUpdateErrorReceiver);
    }

    /**
     * 后台进程异常终止处理
     *
     * @author UtopiaXC
     * @since 2022-05-23 10:24:22
     */
    class ServerUpdateErrorReceiver extends BroadcastReceiver {

        /**
         * 后台进程异常广播处理
         * <p>终止后台服务
         *
         * @author UtopiaXC
         * @since 2022-05-23 10:27:15
         * @param context 上下文
         * @param intent 参数
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, R.string.background_service_error, Toast.LENGTH_SHORT).show();
            intent = new Intent(context, ServerStatusUpdateService.class);
            mContext.stopService(intent);
        }
    }
}