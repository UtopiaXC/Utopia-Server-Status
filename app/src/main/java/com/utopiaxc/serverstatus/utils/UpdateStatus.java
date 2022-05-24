package com.utopiaxc.serverstatus.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
     * @param context 上下文
     * @author UtopiaXC
     * @since 2022-05-22 22:16:04
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
            //从偏好中取得更新间隔与接口地址
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            int interval = Integer.parseInt(sharedPreferences.getString("interval", "5000"));
            String address = sharedPreferences.getString("address", "");
            try {
                //请求服务器接口
                Connection conn = Jsoup.connect(address).header("Accept", "*/*")
                        .header("Content-Type", "application/xml;charset=UTF-8")
                        .ignoreContentType(true);
                Document document = conn.get();

                //处理JSON
                JSONArray serversJsonArray = JSONArray.parseArray(JSONObject.parseObject(document.body().text()).getString("servers"));
                int timestamp = JSONObject.parseObject(document.body().text()).getInteger("updated");
                List<JSONObject> servers = new ArrayList<>();
                for (Object serverObject : serversJsonArray) {
                    servers.add((JSONObject) serverObject);
                }


                //检索数据库是否拥有该服务器，如没有则添加
                List<String> names = new ArrayList<>();
                for (JSONObject jsonObject : servers) {
                    names.add(jsonObject.getString("name"));
                }
                List<ServerBean> serverBeans = Variables.database.serverDao().getByNames(names);
                for (String serverName : names) {
                    boolean isIn = false;
                    for (ServerBean serverBean : serverBeans) {
                        if (serverBean.getServerName().equals(serverName)) {
                            isIn = true;
                            break;
                        }
                    }
                    if (!isIn) {
                        ServerBean serverBean = new ServerBean();
                        serverBean.setServerName(serverName);
                        Variables.database.serverDao().insertAll(serverBean);
                    }
                }

                //检索列表外的服务器并执行删除
                serverBeans = Variables.database.serverDao().getOutsideByNames(names);
                Variables.database.serverDao().deleteServer(serverBeans.toArray(new ServerBean[0]));

                //重新检索映射并向状态库添加数据
                serverBeans = Variables.database.serverDao().getByNames(names);
                List<StatusBean> statusBeans = new ArrayList<>();
                for (JSONObject jsonObject : servers) {
                    UUID server_id = null;
                    String serverName = jsonObject.getString("name");
                    for (ServerBean serverBean : serverBeans) {
                        if (serverBean.getServerName().equals(serverName)) {
                            server_id = serverBean.getId();
                            break;
                        }
                    }
                    if (server_id == null) {
                        continue;
                    }
                    String serverType = jsonObject.getString("type");
                    String serverLocation = jsonObject.getString("location");
                    boolean serverIpv4 = jsonObject.getBoolean("online4");
                    boolean serverIpv6 = jsonObject.getBoolean("online6");
                    String serverRegion = jsonObject.getString("region");
                    StatusBean statusBean = new StatusBean();
                    statusBean.setServerId(server_id);
                    statusBean.setServerIpv4Status(serverIpv4);
                    statusBean.setServerIpv6Status(serverIpv6);
                    statusBean.setServerType(serverType);
                    statusBean.setServerLocation(serverLocation);
                    statusBean.setServerTimestamp(timestamp);
                    statusBean.setServerRegion(serverRegion);
                    if (!serverIpv4 && !serverIpv6) {
                        statusBeans.add(statusBean);
                        continue;
                    }
                    statusBean.setServerUptime(jsonObject.getDouble("uptime").toString());
                    double serverLoad = jsonObject.getDouble("load");
                    int serverDownloadSpeed = jsonObject.getInteger("network_rx");
                    int serverUploadSpeed = jsonObject.getInteger("network_tx");
                    long serverNetworkIn = jsonObject.getLong("network_in");
                    long serverNetworkOut = jsonObject.getLong("network_out");
                    double serverCpuLoad = (jsonObject.getInteger("cpu")) * 1.0 / 100.0;
                    int serverMemoryTotal = jsonObject.getInteger("memory_total");
                    int serverMemoryUsed = jsonObject.getInteger("memory_used");
                    double serverMemoryLoad = (serverMemoryUsed * 1.0) / (serverMemoryTotal * 1.0);
                    int serverSwapTotal = jsonObject.getInteger("swap_total");
                    int serverSwapUsed = jsonObject.getInteger("swap_used");
                    double serverSwapLoad = (serverSwapUsed * 1.0) / (serverSwapTotal * 1.0);
                    int serverDiskTotal = jsonObject.getInteger("hdd_total");
                    int serverDiskUsed = jsonObject.getInteger("hdd_used");
                    double serverDiskLoad = (serverDiskUsed * 1.0) / (serverDiskTotal * 1.0);
                    statusBean.setServerLoad(serverLoad);
                    statusBean.setServerNetworkRealtimeDownloadSpeed(serverDownloadSpeed);
                    statusBean.setServerNetworkRealtimeUploadSpeed(serverUploadSpeed);
                    statusBean.setServerNetworkIn(serverNetworkIn);
                    statusBean.setServerNetworkOut(serverNetworkOut);
                    statusBean.setServerCpuPercent(serverCpuLoad);
                    statusBean.setServerMemoryTotal(serverMemoryTotal);
                    statusBean.setServerMemoryUsed(serverMemoryUsed);
                    statusBean.setServerMemoryPercent(serverMemoryLoad);
                    statusBean.setServerSwapTotal(serverSwapTotal);
                    statusBean.setServerSwapUsed(serverSwapUsed);
                    statusBean.setServerSwapPercent(serverSwapLoad);
                    statusBean.setServerDiskTotal(serverDiskTotal);
                    statusBean.setServerDiskUsed(serverDiskUsed);
                    statusBean.setServerDiskPercent(serverDiskLoad);
                    statusBeans.add(statusBean);
                }
                Variables.database.statusDao().insertAll(statusBeans.toArray(new StatusBean[0]));

                //删除已被删除的服务器的状态历史中不存在的状态
                List<String> serverIds = new ArrayList<>();
                List<StatusBean> statusBeansToDelete = Variables.database.statusDao().getDeletedStatus();
                Variables.database.statusDao().deleteStatus(statusBeansToDelete.toArray(new StatusBean[0]));


                //清理过期数据
                int exp = Integer.parseInt(sharedPreferences.getString("archive_time", "5")) * 24 * 60 * 60;
                if (exp != 0) {
                    exp = (int) (new Date().getTime() / 1000) - exp;
                    statusBeansToDelete = Variables.database.statusDao().getExpiredStatus(exp);
                    Variables.database.statusDao().deleteStatus(statusBeansToDelete.toArray(new StatusBean[0]));
                }

                //完成服务器数据更新后发送广播
                Intent broadcast = new Intent("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED");
                broadcast.putExtra("timestamp", timestamp);
                context.sendBroadcast(broadcast);
                //休眠间隔
                Thread.sleep(interval);
            } catch (InterruptedException ie) {
                //当线程被打断时结束
                return;
            } catch (Exception e) {
                //发生其他异常时发送广播
                e.printStackTrace();
                Intent broadcast = new Intent("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATE_ERROR");
                context.sendBroadcast(broadcast);
                return;
            }
        }
    }
}
