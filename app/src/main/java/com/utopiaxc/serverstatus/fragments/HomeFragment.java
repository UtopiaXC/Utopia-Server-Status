package com.utopiaxc.serverstatus.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.utopiaxc.serverstatus.Beans.HomePageBean;
import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.adapters.HomePageAdapter;
import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;
import com.utopiaxc.serverstatus.databinding.FragmentHomeBinding;
import com.utopiaxc.serverstatus.services.ServerStatusUpdateService;
import com.utopiaxc.serverstatus.utils.Variables;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

/**
 * 主页Fragment
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:52:18
 */
public class HomeFragment extends Fragment {

    protected FragmentHomeBinding binding;
    protected ServerUpdatedReceiver serverUpdatedReceiver;
    protected Context context;
    protected List<HomePageBean> homePageBeans;
    private final int messageUpdateFlag = 21315124;
    private HomeFragmentHandler homeFragmentHandler;
    private HomePageAdapter homePageAdapter;
    private int normal = 0;
    private int offline = 0;
    private int sysOverload = 0;
    private int cpuOverload = 0;
    private int memoryOverload = 0;
    private int diskOverload = 0;

    private enum CardFlag {NORMAL, OFFLINE, OVERLOAD, CPU_OVERLOAD, MEMORY_OVERLOAD, DISK_OVERLOAD}

    /**
     * 主页Fragment视图渲染
     *
     * @param inflater           参数
     * @param container          参数
     * @param savedInstanceState 参数
     * @return android.view.View
     * @author UtopiaXC
     * @since 2022-05-22 22:52:29
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        context = requireContext();
        binding.listHome.setLayoutManager(new LinearLayoutManager(requireContext()));
        homeFragmentHandler = new HomeFragmentHandler(context.getMainLooper());
        //设置显示列表适配器
        homePageBeans = new ArrayList<>();
        homePageBeans.add(CardFlag.NORMAL.ordinal(), new HomePageBean(CardFlag.NORMAL.ordinal(), getString(R.string.home_normal), normal, R.drawable.server_normal));
        homePageBeans.add(CardFlag.OFFLINE.ordinal(), new HomePageBean(CardFlag.OFFLINE.ordinal(), getString(R.string.home_offline), offline, R.drawable.server_offline));
        homePageBeans.add(CardFlag.OVERLOAD.ordinal(), new HomePageBean(CardFlag.OVERLOAD.ordinal(), getString(R.string.home_system_overload), sysOverload, R.drawable.server_overload));
        homePageBeans.add(CardFlag.CPU_OVERLOAD.ordinal(), new HomePageBean(CardFlag.CPU_OVERLOAD.ordinal(), getString(R.string.home_cpu_overload), cpuOverload, R.drawable.cpu));
        homePageBeans.add(CardFlag.MEMORY_OVERLOAD.ordinal(), new HomePageBean(CardFlag.MEMORY_OVERLOAD.ordinal(), getString(R.string.home_memory_overload), memoryOverload, R.drawable.memory));
        homePageBeans.add(CardFlag.DISK_OVERLOAD.ordinal(), new HomePageBean(CardFlag.DISK_OVERLOAD.ordinal(), getString(R.string.home_disk_overload), diskOverload, R.drawable.disk));
        homePageAdapter = new HomePageAdapter(homePageBeans);
        binding.listHome.setAdapter(homePageAdapter);

        //注册后台服务进程异常广播
        serverUpdatedReceiver = new ServerUpdatedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED");
        context.registerReceiver(serverUpdatedReceiver, intentFilter, "com.utopiaxc.receiver.receivebroadcast", null);
        new Thread(new GetServerUpdatedStatus()).start();
        return binding.getRoot();
    }

    /**
     * 主页Fragment视图销毁
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:52:53
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        context.unregisterReceiver(serverUpdatedReceiver);
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
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            List<StatusBean> statusBeans = Variables.database.statusDao().getNewestStatus();
            normal = 0;
            offline = 0;
            sysOverload = 0;
            cpuOverload = 0;
            memoryOverload = 0;
            diskOverload = 0;
            double systemLoad = Double.parseDouble(sharedPreferences.getString("system_load_threshold", "1.0"));
            double cpuLoad = Double.parseDouble(sharedPreferences.getString("cpu_alert_threshold", "0.75"));
            double memoryLoad = Double.parseDouble(sharedPreferences.getString("memory_alert_threshold", "0.75"));
            double diskLoad = Double.parseDouble(sharedPreferences.getString("disk_alert_threshold", "0.75"));

            for (StatusBean statusBean : statusBeans) {
                try {
                    if (statusBean.getServerIpv4Status() || statusBean.getServerIpv6Status()) {
                        boolean isNormal = true;
                        if (statusBean.getServerLoad() != null && statusBean.getServerLoad() > systemLoad) {
                            isNormal = false;
                            sysOverload++;
                        }
                        if (statusBean.getServerCpuPercent() != null && statusBean.getServerCpuPercent() > cpuLoad) {
                            isNormal = false;
                            cpuOverload++;
                        }
                        if (statusBean.getServerMemoryPercent() != null && statusBean.getServerMemoryPercent() > memoryLoad) {
                            isNormal = false;
                            memoryOverload++;
                        }
                        if (statusBean.getServerDiskPercent() != null && statusBean.getServerDiskPercent() > diskLoad) {
                            isNormal = false;
                            diskOverload++;
                        }
                        if (isNormal) {
                            normal++;
                        }
                    } else {
                        offline++;
                    }
                    Message msg = new Message();
                    msg.what = messageUpdateFlag;
                    homeFragmentHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class HomeFragmentHandler extends Handler {

        public HomeFragmentHandler(@NonNull Looper looper) {
            super(looper);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == messageUpdateFlag) {
                homePageBeans.get(CardFlag.NORMAL.ordinal()).setSubTitle(normal);
                homePageBeans.get(CardFlag.OFFLINE.ordinal()).setSubTitle(offline);
                homePageBeans.get(CardFlag.OVERLOAD.ordinal()).setSubTitle(sysOverload);
                homePageBeans.get(CardFlag.CPU_OVERLOAD.ordinal()).setSubTitle(cpuOverload);
                homePageBeans.get(CardFlag.MEMORY_OVERLOAD.ordinal()).setSubTitle(memoryOverload);
                homePageBeans.get(CardFlag.DISK_OVERLOAD.ordinal()).setSubTitle(diskOverload);
                homePageAdapter.notifyDataSetChanged();
            }
        }
    }
}