package com.utopiaxc.serverstatus.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author UtopiaXC
 * @date 2022-05-22 19:57
 */
public class UpdateStatus implements Runnable {
    Context context;

    public UpdateStatus(Context context) {
        this.context = context;
    }

    @SuppressWarnings({"BusyWait"})
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                int interval = Integer.parseInt(sharedPreferences.getString("interval", "5000"));
                String address = sharedPreferences.getString("address", "");
                Connection conn = Jsoup.connect(address).header("Accept", "*/*")
                        .header("Content-Type", "application/xml;charset=UTF-8")
                        .ignoreContentType(true);
                Document document = conn.get();
                JSONArray servers = JSONArray.parseArray(JSONObject.parseObject(document.body().text()).getString("servers"));
                for (Object server : servers) {
                    System.out.println(server.toString());
                }
                Intent broadcast = new Intent("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED");
                context.sendBroadcast(broadcast);
                Thread.sleep(interval);
            } catch (InterruptedException ie) {
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
