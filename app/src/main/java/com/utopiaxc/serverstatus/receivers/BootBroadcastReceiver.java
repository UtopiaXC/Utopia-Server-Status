package com.utopiaxc.serverstatus.receivers;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.utopiaxc.serverstatus.database.helper.DatabaseHelper;
import com.utopiaxc.serverstatus.services.ServerStatusUpdateService;
import com.utopiaxc.serverstatus.utils.Variables;

/**
 * 开机自启广播
 * <p>如果用户选择开机自启则监听开机广播
 *
 * @author UtopiaXC
 * @since 2022-05-23 12:06
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    /**
     * 开机广播监听
     * <p>接收到开机广播后执行
     *
     * @param context 上下文
     * @param intent  入参
     * @author UtopiaXC
     * @since 2022-05-23 12:11:11
     */
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Boot Over", Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean background = sharedPreferences.getBoolean("backgroundService", true);
        boolean startWithBoot = sharedPreferences.getBoolean("start_with_boot", false);
        if (background && startWithBoot) {
            Variables.context = context;
            Variables.database = Room.databaseBuilder(context, DatabaseHelper.class, "uss").build();
            Intent service = new Intent(context, ServerStatusUpdateService.class);
            context.startService(service);
        }
    }
}
