package com.utopiaxc.serverstatus.services;

import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.utopiaxc.serverstatus.MainActivity;
import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.database.model.NotificationBean;
import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;
import com.utopiaxc.serverstatus.utils.Constants;
import com.utopiaxc.serverstatus.utils.UpdateStatus;
import com.utopiaxc.serverstatus.utils.Variables;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
    private final String NOTIFICATION_STATUS_CHANNEL_ID = "USS_SERVER_STATUS_ALERT_NOTIFICATION";
    private final int NOTIFICATION_ID = 154651133;
    private Thread updateThread;
    private ServerUpdateErrorReceiver serverUpdateErrorReceiver;

    private final int messageUpdateFlag = 56496188;
    protected ServerUpdatedReceiver serverUpdatedReceiver;
    protected ServerListFragmentHandler serverListFragmentHandler;
    private final List<NotificationBean> notificationBeans = new ArrayList<>();
    private SharedPreferences sharedPreferences;

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
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        serverListFragmentHandler = new ServerListFragmentHandler(mContext.getMainLooper());

        //后台常驻通知
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, mContext.getString(R.string.background_notification_title), NotificationManager.IMPORTANCE_MIN);
            notificationManager.createNotificationChannel(channel);
            channel = new NotificationChannel(NOTIFICATION_STATUS_CHANNEL_ID, mContext.getString(R.string.server_alert_notification), NotificationManager.IMPORTANCE_DEFAULT);
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
        serverUpdateErrorReceiver = new ServerUpdateErrorReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATE_ERROR");
        mContext.registerReceiver(serverUpdateErrorReceiver, intentFilter, "com.utopiaxc.receiver.RECEIVE_INTERNAL_BROADCAST", null);

        //注册服务器状态更新完成广播
        serverUpdatedReceiver = new ServerUpdatedReceiver();
        intentFilter.addAction("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED");
        mContext.registerReceiver(serverUpdatedReceiver, intentFilter, "com.utopiaxc.receiver.RECEIVE_INTERNAL_BROADCAST", null);
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
                .setSmallIcon(R.drawable.server)
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
        mContext.unregisterReceiver(serverUpdatedReceiver);
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
         * @param context 上下文
         * @param intent  参数
         * @author UtopiaXC
         * @since 2022-05-23 10:27:15
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, R.string.background_service_error, Toast.LENGTH_SHORT).show();
            intent = new Intent(context, ServerStatusUpdateService.class);
            mContext.stopService(intent);
        }
    }

    /**
     * 服务器状态更新广播处理
     *
     * @author UtopiaXC
     * @since 2022-05-23 10:24:22
     */
    class ServerUpdatedReceiver extends BroadcastReceiver {

        /**
         * 后台进程异常广播处理
         * <p>终止后台服务
         *
         * @param context 上下文
         * @param intent  参数
         * @author UtopiaXC
         * @since 2022-05-23 10:27:15
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            new Thread(new GetServerUpdatedStatus()).start();
        }
    }

    /**
     * 状态更新
     *
     * @author UtopiaXC
     * @since 2022-05-23 16:42:19
     */
    class GetServerUpdatedStatus implements Runnable {

        @Override
        public void run() {
            notificationBeans.clear();
            //查询全部服务器的最新状态
            List<StatusBean> statusBeans = Variables.database.statusDao().getNewestStatus();
            List<ServerBean> serverBeans = Variables.database.serverDao().getAll();
            double systemLoad = Double.parseDouble(sharedPreferences.getString("system_load_threshold", "1.0"));
            double cpuLoad = Double.parseDouble(sharedPreferences.getString("cpu_alert_threshold", "0.75"));
            double memoryLoad = Double.parseDouble(sharedPreferences.getString("memory_alert_threshold", "0.75"));
            double diskLoad = Double.parseDouble(sharedPreferences.getString("disk_alert_threshold", "0.75"));
            boolean backgroundService = sharedPreferences.getBoolean("backgroundService", true);
            boolean enableAlert = sharedPreferences.getBoolean("alert_notification", false);
            boolean enableServerOffline = sharedPreferences.getBoolean("server_offline", false);
            boolean enableServerOverload = sharedPreferences.getBoolean("system_load", false);
            boolean enableCpuOverload = sharedPreferences.getBoolean("cpu_alert", false);
            boolean enableMemoryOverload = sharedPreferences.getBoolean("memory_alert", false);
            boolean enableDiskOverload = sharedPreferences.getBoolean("disk_alert", false);
            int alertInterval = Integer.parseInt(sharedPreferences.getString("same_overload_alert_interval", "5"));
            int timestampNow = (int) (new Date().getTime() / 1000);
            int triggerTimestamp = timestampNow - (alertInterval * 60);
            if (!enableAlert || !backgroundService) {
                return;
            }
            for (StatusBean statusBean : statusBeans) {
                //将服务器名进行映射
                String serverName = "";
                for (ServerBean serverBean : serverBeans) {
                    if (serverBean.getId().equals(statusBean.getServerId())) {
                        serverName = serverBean.getServerName();
                        break;
                    }
                }
                if (statusBean.getServerIpv4Status() || statusBean.getServerIpv6Status()) {
                    if (statusBean.getServerLoad() != null
                            && statusBean.getServerLoad() > systemLoad
                            && enableServerOverload
                            && Variables.database.notificationDao().getAllFromServerSince(statusBean.getServerId(), triggerTimestamp, Constants.CardFlag.OVERLOAD.ordinal()).size() == 0) {
                        //系统过载警告服务器
                        NotificationBean notificationBean = new NotificationBean();
                        notificationBean.setServerId(statusBean.getServerId());
                        notificationBean.setNotificationId(new Random().nextInt());
                        notificationBean.setStatusId(statusBean.getId());
                        notificationBean.setServerName(serverName);
                        notificationBean.setAlertTimestamp(timestampNow);
                        notificationBean.setAlertType(Constants.CardFlag.OVERLOAD.ordinal());
                        notificationBeans.add(notificationBean);
                    }
                    if (statusBean.getServerCpuPercent() != null
                            && statusBean.getServerCpuPercent() > cpuLoad
                            && enableCpuOverload
                            && Variables.database.notificationDao().getAllFromServerSince(statusBean.getServerId(), triggerTimestamp, Constants.CardFlag.CPU_OVERLOAD.ordinal()).size() == 0) {
                        //CPU负载警告服务器
                        NotificationBean notificationBean = new NotificationBean();
                        notificationBean.setServerId(statusBean.getServerId());
                        notificationBean.setNotificationId(new Random().nextInt());
                        notificationBean.setStatusId(statusBean.getId());
                        notificationBean.setServerName(serverName);
                        notificationBean.setAlertTimestamp(timestampNow);
                        notificationBean.setAlertType(Constants.CardFlag.CPU_OVERLOAD.ordinal());
                        notificationBeans.add(notificationBean);
                    }
                    if (statusBean.getServerMemoryPercent() != null
                            && statusBean.getServerMemoryPercent() > memoryLoad
                            && enableMemoryOverload
                            && Variables.database.notificationDao().getAllFromServerSince(statusBean.getServerId(), triggerTimestamp, Constants.CardFlag.MEMORY_OVERLOAD.ordinal()).size() == 0) {
                        //内存负载警告服务器
                        NotificationBean notificationBean = new NotificationBean();
                        notificationBean.setServerId(statusBean.getServerId());
                        notificationBean.setNotificationId(new Random().nextInt());
                        notificationBean.setStatusId(statusBean.getId());
                        notificationBean.setServerName(serverName);
                        notificationBean.setAlertTimestamp(timestampNow);
                        notificationBean.setAlertType(Constants.CardFlag.MEMORY_OVERLOAD.ordinal());
                        notificationBeans.add(notificationBean);
                    }
                    if (statusBean.getServerDiskPercent() != null
                            && statusBean.getServerDiskPercent() > diskLoad
                            && enableDiskOverload
                            && Variables.database.notificationDao().getAllFromServerSince(statusBean.getServerId(), triggerTimestamp, Constants.CardFlag.DISK_OVERLOAD.ordinal()).size() == 0) {
                        //硬盘容量警告服务器
                        NotificationBean notificationBean = new NotificationBean();
                        notificationBean.setServerId(statusBean.getServerId());
                        notificationBean.setNotificationId(new Random().nextInt());
                        notificationBean.setStatusId(statusBean.getId());
                        notificationBean.setServerName(serverName);
                        notificationBean.setAlertTimestamp(timestampNow);
                        notificationBean.setAlertType(Constants.CardFlag.DISK_OVERLOAD.ordinal());
                        notificationBeans.add(notificationBean);
                    }
                } else {
                    if (enableServerOffline
                            && Variables.database.notificationDao().getAllFromServerSince(statusBean.getServerId(), triggerTimestamp, Constants.CardFlag.OFFLINE.ordinal()).size() == 0) {
                        NotificationBean notificationBean = new NotificationBean();
                        notificationBean.setServerId(statusBean.getServerId());
                        notificationBean.setNotificationId(new Random().nextInt());
                        notificationBean.setStatusId(statusBean.getId());
                        notificationBean.setServerName(serverName);
                        notificationBean.setAlertTimestamp(timestampNow);
                        notificationBean.setAlertType(Constants.CardFlag.OFFLINE.ordinal());
                        notificationBeans.add(notificationBean);
                    }
                }
            }
            Variables.database.notificationDao().insertAll(notificationBeans.toArray(new NotificationBean[0]));
            //发送消息
            Message msg = new Message();
            msg.what = messageUpdateFlag;
            serverListFragmentHandler.sendMessage(msg);
        }
    }

    /**
     * 服务器列表消息句柄
     *
     * @author UtopiaXC
     * @since 2022-05-23 23:23:00
     */
    class ServerListFragmentHandler extends Handler {
        public ServerListFragmentHandler(@NonNull Looper looper) {
            super(looper);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == messageUpdateFlag) {
                for (NotificationBean notificationBean : notificationBeans) {
                    int type = notificationBean.getAlertType();
                    int icon = R.drawable.server_offline;
                    String title = "";
                    String description = notificationBean.getServerName();
                    if (type == Constants.CardFlag.OFFLINE.ordinal()) {
                        title = getString(R.string.server_offline);
                        description += getString(R.string.server_offline);
                    } else if (type == Constants.CardFlag.OVERLOAD.ordinal()) {
                        icon = R.drawable.server_overload;
                        title = getString(R.string.home_system_overload);
                        description += getString(R.string.home_system_overload);
                    } else if (type == Constants.CardFlag.CPU_OVERLOAD.ordinal()) {
                        icon = R.drawable.cpu;
                        title = getString(R.string.home_cpu_overload);
                        description += getString(R.string.home_cpu_overload);
                    } else if (type == Constants.CardFlag.MEMORY_OVERLOAD.ordinal()) {
                        icon = R.drawable.memory;
                        title = getString(R.string.home_memory_overload);
                        description += getString(R.string.home_memory_overload);
                    } else if (type == Constants.CardFlag.DISK_OVERLOAD.ordinal()) {
                        icon = R.drawable.disk;
                        title = getString(R.string.home_disk_overload);
                        description += getString(R.string.home_disk_overload);
                    }
                    notificationManager.notify(notificationBean.getNotificationId(), getServerAlertNotification(icon, title, description));
                }
            }
        }
    }


    /**
     * 获取服务器异常消息提醒
     *
     * @return android.app.Notification
     * @author UtopiaXC
     * @since 2022-05-22 22:27:06
     */
    private Notification getServerAlertNotification(int resourceId, String title, String description) {
        Notification.Builder builder = new Notification.Builder(mContext)
                .setSmallIcon(resourceId)
                .setContentTitle(title)
                .setOngoing(false)
                .setContentText(description);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_STATUS_CHANNEL_ID);
        }
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);
        return builder.build();
    }
}