package com.utopiaxc.serverstatus.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;
import com.utopiaxc.serverstatus.databinding.ActivityServerBinding;
import com.utopiaxc.serverstatus.utils.Constants;
import com.utopiaxc.serverstatus.utils.Variables;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 服务器详情Activity
 *
 * @author UtopiaXC
 * @since 2022-05-23 23:30:38
 */
public class ServerActivity extends AppCompatActivity {
    private ActivityServerBinding binding;
    private UUID serverId;
    private final int SERVER_UPDATED = 32161154;
    private ServerBean serverBean;
    private StatusBean statusBean;
    private List<StatusBean> statusBeans;
    private ServerUpdatedReceiver serverUpdatedReceiver;
    private Context context;
    private ServerInfoMessageHandler serverInfoMessageHandler;

    /**
     * Activity创建方法
     *
     * @param savedInstanceState 入参
     * @author UtopiaXC
     * @since 2022-05-23 23:30:52
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        serverInfoMessageHandler = new ServerInfoMessageHandler(getMainLooper());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        serverId = (UUID) getIntent().getExtras().get("serverId");
        new Thread(new ServerInfo()).start();
        //注册后台服务进程异常广播
        serverUpdatedReceiver = new ServerUpdatedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED");
        context.registerReceiver(serverUpdatedReceiver, intentFilter, "com.utopiaxc.receiver.RECEIVE_INTERNAL_BROADCAST", null);
    }

    /**
     * 返回键
     *
     * @param item 点击对象
     * @return boolean
     * @author UtopiaXC
     * @since 2022-05-23 23:31:12
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(serverUpdatedReceiver);
    }

    /**
     * 服务器状态更新广播处理
     *
     * @author UtopiaXC
     * @since 2022-05-23 10:24:22
     */
    class ServerUpdatedReceiver extends BroadcastReceiver {

        /**
         * 后台进程数据更新
         *
         * @param context 上下文
         * @param intent  参数
         * @author UtopiaXC
         * @since 2022-05-23 10:27:15
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            new Thread(new ServerInfo()).start();
        }
    }

    class ServerInfo implements Runnable {
        @Override
        public void run() {
            serverBean = Variables.database.serverDao().getById(serverId);
            statusBean = Variables.database.statusDao().getNewestStatusByServerId(serverId);
            statusBeans = Variables.database.statusDao().getDataForChartByServerId(serverId);
            Message message = new Message();
            message.what = SERVER_UPDATED;
            serverInfoMessageHandler.sendMessage(message);
        }
    }

    class ServerInfoMessageHandler extends Handler {

        public ServerInfoMessageHandler(@NonNull Looper looper) {
            super(looper);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == SERVER_UPDATED) {
                binding.serverInfoServerName.setText(serverBean.getServerName());
                binding.serverInfoRegionFlag.setImageResource(Constants.RegionFlagEnum.getByKey(statusBean.getServerRegion()).getSourceId());
                binding.serverInfoServerType.setText(statusBean.getServerType());
                if (statusBean.getServerIpv4Status() || statusBean.getServerIpv6Status()) {
                    Double systemLoad = statusBean.getServerLoad();
                    Double cpuLoad = statusBean.getServerCpuPercent();
                    Double memoryLoad = statusBean.getServerMemoryPercent();
                    Double diskLoad = statusBean.getServerDiskPercent();
                    if (systemLoad != null) {
                        binding.serverInfoServerLoad.setText(getString(R.string.server_activity_load_title) + systemLoad);
                        if (systemLoad >= 1) {
                            binding.serverInfoProgressLoad.setProgress(100);
                        } else {
                            binding.serverInfoProgressLoad.setProgress((int) (systemLoad * 100));
                        }
                    }
                    if (cpuLoad != null) {
                        binding.serverInfoCpuLoad.setText(getString(R.string.server_activity_cpu_load_title) + (int) (cpuLoad * 100) + "%");
                        binding.serverInfoProgressCpuLoad.setProgress((int) (cpuLoad * 100));
                    }
                    if (memoryLoad != null) {
                        binding.serverInfoMemoryLoad.setText(getString(R.string.server_activity_memory_load_title) + (int) (memoryLoad * 100) + "%");
                        binding.serverInfoProgressMemoryLoad.setProgress((int) (memoryLoad * 100));
                    }
                    if (diskLoad != null) {
                        binding.serverInfoDiskLoad.setText(getString(R.string.server_activity_disk_load_title) + (int) (diskLoad * 100) + "%");
                        binding.serverInfoDiskProgressLoad.setProgress((int) (diskLoad * 100));
                    }
                } else {
                    binding.serverInfoServerLoad.setText(getString(R.string.server_activity_load_title) + getString(R.string.server_offline));
                    binding.serverInfoCpuLoad.setText(getString(R.string.server_activity_cpu_load_title) + getString(R.string.server_offline));
                    binding.serverInfoMemoryLoad.setText(getString(R.string.server_activity_memory_load_title) + getString(R.string.server_offline));
                    binding.serverInfoDiskLoad.setText(getString(R.string.server_activity_disk_load_title) + getString(R.string.server_offline));
                }
            }
        }
    }
}