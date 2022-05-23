package com.utopiaxc.serverstatus.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;

import com.utopiaxc.serverstatus.Beans.ServerCardBean;
import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.adapters.ServerCardAdapter;
import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;
import com.utopiaxc.serverstatus.databinding.ActivityConditionServerListBinding;
import com.utopiaxc.serverstatus.utils.Constants;
import com.utopiaxc.serverstatus.utils.Variables;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 条件筛选的服务器列表Activity
 *
 * @author UtopiaXC
 * @since 2022-05-23 23:34:20
 */
public class ConditionServerListActivity extends AppCompatActivity {
    private ActivityConditionServerListBinding binding;
    protected Context context;
    protected List<ServerCardBean> serverCardBeans;
    private final int messageUpdateFlag = 165161414;
    protected ServerCardAdapter serverCardAdapter;
    protected ServerUpdatedReceiver serverUpdatedReceiver;
    protected ServerListFragmentHandler serverListFragmentHandler;
    private int type;
    private SharedPreferences sharedPreferences;

    /**
     * Activity创建
     *
     * @param savedInstanceState 入参
     * @author UtopiaXC
     * @since 2022-05-23 23:34:36
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //UI与参数
        binding = ActivityConditionServerListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //根据筛选类型显示标题
        Intent intent = getIntent();
        type = (int) intent.getExtras().get("type");
        if (type == Constants.CardFlag.NORMAL.ordinal()) {
            setTitle(R.string.home_normal);
        } else if (type == Constants.CardFlag.OFFLINE.ordinal()) {
            setTitle(R.string.home_offline);
        } else if (type == Constants.CardFlag.OVERLOAD.ordinal()) {
            setTitle(R.string.home_system_overload);
        } else if (type == Constants.CardFlag.CPU_OVERLOAD.ordinal()) {
            setTitle(R.string.home_cpu_overload);
        } else if (type == Constants.CardFlag.MEMORY_OVERLOAD.ordinal()) {
            setTitle(R.string.home_memory_overload);
        } else if (type == Constants.CardFlag.DISK_OVERLOAD.ordinal()) {
            setTitle(R.string.home_disk_overload);
        }
        //设置适配器与消息句柄等
        serverListFragmentHandler = new ServerListFragmentHandler(context.getMainLooper());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        binding.listConditionServerList.setLayoutManager(new LinearLayoutManager(this));
        serverCardBeans = new ArrayList<>();
        serverCardAdapter = new ServerCardAdapter(serverCardBeans);
        serverCardAdapter.registerItemClickID();
        serverCardAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.cardServer) {
                ServerCardBean serverCardBean = (ServerCardBean) adapter.getItem(position);
                Intent serverActivityIntent = new Intent(context, ServerActivity.class);
                serverActivityIntent.putExtra("serverId", serverCardBean.getServerId());
                startActivity(serverActivityIntent);
            }
        });
        binding.listConditionServerList.setAdapter(serverCardAdapter);

        //注册后台服务进程异常广播
        serverUpdatedReceiver = new ServerUpdatedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED");
        context.registerReceiver(serverUpdatedReceiver, intentFilter, "com.utopiaxc.receiver.RECEIVE_INTERNAL_BROADCAST", null);
        new Thread(new GetServerUpdatedStatus()).start();
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
     * 更新页面服务
     *
     * @author UtopiaXC
     * @since 2022-05-23 16:42:19
     */
    class GetServerUpdatedStatus implements Runnable {

        @Override
        public void run() {
            //获取全部服务器最新状态数据
            List<StatusBean> statusBeans = Variables.database.statusDao().getNewestStatus();
            List<ServerBean> serverBeans = Variables.database.serverDao().getAll();
            serverCardBeans.clear();
            //获取负载阈值
            double systemLoad = Double.parseDouble(sharedPreferences.getString("system_load_threshold", "1.0"));
            double cpuLoad = Double.parseDouble(sharedPreferences.getString("cpu_alert_threshold", "0.75"));
            double memoryLoad = Double.parseDouble(sharedPreferences.getString("memory_alert_threshold", "0.75"));
            double diskLoad = Double.parseDouble(sharedPreferences.getString("disk_alert_threshold", "0.75"));
            //根据不同筛选条件进行分类
            List<ServerCardBean> normalServerCardBeans = new ArrayList<>();
            List<ServerCardBean> offlineServerCardBeans = new ArrayList<>();
            List<ServerCardBean> overloadServerCardBeans = new ArrayList<>();
            List<ServerCardBean> cpuOverloadServerCardBeans = new ArrayList<>();
            List<ServerCardBean> memoryOverloadCardBeans = new ArrayList<>();
            List<ServerCardBean> diskOverloadServerCardBeans = new ArrayList<>();
            for (StatusBean statusBean : statusBeans) {
                //建立服务器信息映射
                ServerCardBean serverCardBean = new ServerCardBean();
                for (ServerBean serverBean : serverBeans) {
                    if (serverBean.getId().equals(statusBean.getServerId())) {
                        serverCardBean.setServerName(serverBean.getServerName());
                    }
                }
                serverCardBean.setServerLoad(statusBean.getServerLoad());
                serverCardBean.setServerId(statusBean.getServerId());
                serverCardBean.setServerType(statusBean.getServerType());
                if (statusBean.getServerLoad() != null && statusBean.getServerLoad() >= 1.0) {
                    serverCardBean.setServerLoadProcess(1.0);
                }
                serverCardBean.setServerLoadProcess(statusBean.getServerLoad());
                serverCardBean.setRegionFlag(Constants.RegionFlagEnum.getByKey(statusBean.getServerRegion()).getSourceId());
                if (statusBean.getServerIpv4Status() || statusBean.getServerIpv6Status()) {
                    boolean isNormal = true;
                    if (statusBean.getServerLoad() != null && statusBean.getServerLoad() > systemLoad) {
                        //系统过载警告服务器
                        isNormal = false;
                        overloadServerCardBeans.add(serverCardBean);
                    }
                    if (statusBean.getServerCpuPercent() != null && statusBean.getServerCpuPercent() > cpuLoad) {
                        //CPU负载警告服务器
                        isNormal = false;
                        cpuOverloadServerCardBeans.add(serverCardBean);
                    }
                    if (statusBean.getServerMemoryPercent() != null && statusBean.getServerMemoryPercent() > memoryLoad) {
                        //内存负载警告服务器
                        isNormal = false;
                        memoryOverloadCardBeans.add(serverCardBean);
                    }
                    if (statusBean.getServerDiskPercent() != null && statusBean.getServerDiskPercent() > diskLoad) {
                        //硬盘容量警告服务器
                        isNormal = false;
                        diskOverloadServerCardBeans.add(serverCardBean);
                    }
                    if (isNormal) {
                        //正常服务器
                        normalServerCardBeans.add(serverCardBean);
                    }
                } else {
                    //离线服务器
                    offlineServerCardBeans.add(serverCardBean);
                }

            }
            //根据筛选条件分类设置适配据数据
            if (type == Constants.CardFlag.NORMAL.ordinal()) {
                serverCardBeans.addAll(normalServerCardBeans);
            } else if (type == Constants.CardFlag.OFFLINE.ordinal()) {
                serverCardBeans.addAll(offlineServerCardBeans);
            } else if (type == Constants.CardFlag.OVERLOAD.ordinal()) {
                serverCardBeans.addAll(overloadServerCardBeans);
            } else if (type == Constants.CardFlag.CPU_OVERLOAD.ordinal()) {
                serverCardBeans.addAll(cpuOverloadServerCardBeans);
            } else if (type == Constants.CardFlag.MEMORY_OVERLOAD.ordinal()) {
                serverCardBeans.addAll(memoryOverloadCardBeans);
            } else if (type == Constants.CardFlag.DISK_OVERLOAD.ordinal()) {
                serverCardBeans.addAll(diskOverloadServerCardBeans);
            }
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
     * @since 2022-05-23 23:36:32
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
                //当收到数据更新消息时更新数据
                serverCardAdapter.notifyDataSetChanged();
                if (serverCardBeans.size()==0){
                    binding.noConditionServerAlert.setVisibility(View.VISIBLE);
                }else{
                    binding.noConditionServerAlert.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 返回按钮监听
     *
     * @param item 点击对象
     * @return boolean
     * @author UtopiaXC
     * @since 2022-05-23 23:29:54
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Activity销毁
     *
     * @author UtopiaXC
     * @since 2022-05-23 23:29:22
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除广播接收器注册
        unregisterReceiver(serverUpdatedReceiver);
    }
}