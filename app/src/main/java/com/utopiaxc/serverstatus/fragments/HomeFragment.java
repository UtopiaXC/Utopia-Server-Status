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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.utopiaxc.serverstatus.Beans.HomePageBean;
import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.activities.ConditionServerListActivity;
import com.utopiaxc.serverstatus.adapters.HomePageAdapter;
import com.utopiaxc.serverstatus.database.model.StatusBean;
import com.utopiaxc.serverstatus.databinding.FragmentHomeBinding;
import com.utopiaxc.serverstatus.utils.Constants;
import com.utopiaxc.serverstatus.utils.Variables;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页Fragment
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:52:18
 */
public class HomeFragment extends Fragment {

    protected FragmentHomeBinding mBinding;
    protected ServerUpdatedReceiver mServerUpdatedReceiver;
    protected Context mContext;
    protected List<HomePageBean> mHomePageBeans;
    private final int MESSAGE_UPDATED_FLAG = 21315124;
    private HomeFragmentHandler mHomeFragmentHandler;
    private HomePageAdapter mHomePageAdapter;
    private int mNormal = 0;
    private int mOffline = 0;
    private int mSysOverload = 0;
    private int mCpuOverload = 0;
    private int mMemoryOverload = 0;
    private int mDiskOverload = 0;


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
        mBinding = FragmentHomeBinding.inflate(inflater, container, false);
        mContext = requireContext();
        mBinding.listHome.setLayoutManager(new LinearLayoutManager(requireContext()));
        mHomeFragmentHandler = new HomeFragmentHandler(mContext.getMainLooper());
        //设置显示列表适配器
        mHomePageBeans = new ArrayList<>();
        mHomePageBeans.add(Constants.CardFlag.NORMAL.ordinal(), new HomePageBean(Constants.CardFlag.NORMAL.ordinal(), getString(R.string.home_normal), mNormal, R.drawable.server_normal));
        mHomePageBeans.add(Constants.CardFlag.OFFLINE.ordinal(), new HomePageBean(Constants.CardFlag.OFFLINE.ordinal(), getString(R.string.home_offline), mOffline, R.drawable.server_offline));
        mHomePageBeans.add(Constants.CardFlag.OVERLOAD.ordinal(), new HomePageBean(Constants.CardFlag.OVERLOAD.ordinal(), getString(R.string.home_system_overload), mSysOverload, R.drawable.server_overload));
        mHomePageBeans.add(Constants.CardFlag.CPU_OVERLOAD.ordinal(), new HomePageBean(Constants.CardFlag.CPU_OVERLOAD.ordinal(), getString(R.string.home_cpu_overload), mCpuOverload, R.drawable.cpu));
        mHomePageBeans.add(Constants.CardFlag.MEMORY_OVERLOAD.ordinal(), new HomePageBean(Constants.CardFlag.MEMORY_OVERLOAD.ordinal(), getString(R.string.home_memory_overload), mMemoryOverload, R.drawable.memory));
        mHomePageBeans.add(Constants.CardFlag.DISK_OVERLOAD.ordinal(), new HomePageBean(Constants.CardFlag.DISK_OVERLOAD.ordinal(), getString(R.string.home_disk_overload), mDiskOverload, R.drawable.disk));
        mHomePageAdapter = new HomePageAdapter(mHomePageBeans);
        mHomePageAdapter.registerItemClickID();
        mHomePageAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.cardHomePage) {
                HomePageBean homePageBean = (HomePageBean) adapter.getItem(position);
                Intent intent = new Intent(mContext, ConditionServerListActivity.class);
                intent.putExtra("type", homePageBean.getCardId());
                startActivity(intent);
            }
        });
        mBinding.listHome.setAdapter(mHomePageAdapter);

        //注册后台服务进程异常广播
        mServerUpdatedReceiver = new ServerUpdatedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED");
        mContext.registerReceiver(mServerUpdatedReceiver, intentFilter, "com.utopiaxc.receiver.RECEIVE_INTERNAL_BROADCAST", null);
        new Thread(new GetServerUpdatedStatus()).start();
        return mBinding.getRoot();
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
        mBinding = null;
        mContext.unregisterReceiver(mServerUpdatedReceiver);
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
            //获取负载阈值
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            List<StatusBean> statusBeans = Variables.database.statusDao().getNewestStatus();
            mNormal = 0;
            mOffline = 0;
            mSysOverload = 0;
            mCpuOverload = 0;
            mMemoryOverload = 0;
            mDiskOverload = 0;
            double systemLoad = Double.parseDouble(sharedPreferences.getString("system_load_threshold", "1.0"));
            double cpuLoad = Double.parseDouble(sharedPreferences.getString("cpu_alert_threshold", "0.75"));
            double memoryLoad = Double.parseDouble(sharedPreferences.getString("memory_alert_threshold", "0.75"));
            double diskLoad = Double.parseDouble(sharedPreferences.getString("disk_alert_threshold", "0.75"));

            //进行对应负载筛选
            for (StatusBean statusBean : statusBeans) {
                try {
                    if (statusBean.getServerIpv4Status() || statusBean.getServerIpv6Status()) {
                        boolean isNormal = true;
                        if (statusBean.getServerLoad() != null && statusBean.getServerLoad() > systemLoad) {
                            isNormal = false;
                            mSysOverload++;
                        }
                        if (statusBean.getServerCpuPercent() != null && statusBean.getServerCpuPercent() > cpuLoad) {
                            isNormal = false;
                            mCpuOverload++;
                        }
                        if (statusBean.getServerMemoryPercent() != null && statusBean.getServerMemoryPercent() > memoryLoad) {
                            isNormal = false;
                            mMemoryOverload++;
                        }
                        if (statusBean.getServerDiskPercent() != null && statusBean.getServerDiskPercent() > diskLoad) {
                            isNormal = false;
                            mDiskOverload++;
                        }
                        if (isNormal) {
                            mNormal++;
                        }
                    } else {
                        mOffline++;
                    }
                    //设置新的适配器数据
                    mHomePageBeans.get(Constants.CardFlag.NORMAL.ordinal()).setSubTitle(mNormal);
                    mHomePageBeans.get(Constants.CardFlag.OFFLINE.ordinal()).setSubTitle(mOffline);
                    mHomePageBeans.get(Constants.CardFlag.OVERLOAD.ordinal()).setSubTitle(mSysOverload);
                    mHomePageBeans.get(Constants.CardFlag.CPU_OVERLOAD.ordinal()).setSubTitle(mCpuOverload);
                    mHomePageBeans.get(Constants.CardFlag.MEMORY_OVERLOAD.ordinal()).setSubTitle(mMemoryOverload);
                    mHomePageBeans.get(Constants.CardFlag.DISK_OVERLOAD.ordinal()).setSubTitle(mDiskOverload);
                    //发送消息
                    Message msg = new Message();
                    msg.what = MESSAGE_UPDATED_FLAG;
                    mHomeFragmentHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 主页消息句柄
     *
     * @author UtopiaXC
     * @since 2022-05-23 23:24:41
     */
    class HomeFragmentHandler extends Handler {
        public HomeFragmentHandler(@NonNull Looper looper) {
            super(looper);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //当收到数据更新消息时更新适配器
            if (msg.what == MESSAGE_UPDATED_FLAG) {
                mHomePageAdapter.notifyDataSetChanged();
            }
        }
    }
}