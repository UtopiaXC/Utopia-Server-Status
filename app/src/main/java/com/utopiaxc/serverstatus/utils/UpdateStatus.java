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
 * 服务器数据更新线程
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:12:51
 */
public class UpdateStatus implements Runnable {
    Context context;

    /**
     * 数据更新线程构造函数
     * <p> 初始化上下文
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:16:04
     * @param context 上下文
     */
    public UpdateStatus(Context context) {
        this.context = context;
    }

    /**
     * 重载线程方法
     * <p> 用于获取服务器数据并保存数据库
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:17:00
     */
    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        //当线程不被打断时一直运行
        while (!Thread.currentThread().isInterrupted()) {
            try {
                //从偏好中取得更新间隔与接口地址
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                int interval = Integer.parseInt(sharedPreferences.getString("interval", "5000"));
                String address = sharedPreferences.getString("address", "");

                //请求服务器接口
                Connection conn = Jsoup.connect(address).header("Accept", "*/*")
                        .header("Content-Type", "application/xml;charset=UTF-8")
                        .ignoreContentType(true);
                Document document = conn.get();

                //处理JSON
                JSONArray servers = JSONArray.parseArray(JSONObject.parseObject(document.body().text()).getString("servers"));
                for (Object server : servers) {
                    System.out.println(server.toString());
                }

                //完成服务器数据更新后发送广播
                Intent broadcast = new Intent("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED");
                context.sendBroadcast(broadcast);

                //休眠间隔
                Thread.sleep(interval);
            } catch (InterruptedException ie) {
                //当线程被打断时结束
                return;
            } catch (Exception e) {
                //处理其他异常
                e.printStackTrace();
            }
        }
    }
}
