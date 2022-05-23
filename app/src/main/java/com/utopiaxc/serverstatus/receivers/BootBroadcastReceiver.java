package com.utopiaxc.serverstatus.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.utopiaxc.serverstatus.services.ServerStatusUpdateService;

/**
 * 开机自启广播
 * <p>如果用户选择开机自启则监听开机广播
 *
 * @author UtopiaXC
 * @since 2022-05-23 12:06
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean background = sharedPreferences.getBoolean("backgroundService", true);
        boolean startWithBoot = sharedPreferences.getBoolean("start_with_boot", false);
        if (background && startWithBoot) {
            Intent service = new Intent(context, ServerStatusUpdateService.class);
            context.startService(service);
        }
    }
}
