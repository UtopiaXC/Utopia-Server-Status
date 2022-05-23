package com.utopiaxc.serverstatus.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.utopiaxc.serverstatus.Beans.LicensesBean;
import com.utopiaxc.serverstatus.Beans.ServerCardBean;
import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.activities.ServerActivity;
import com.utopiaxc.serverstatus.adapters.ServerCardAdapter;
import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;
import com.utopiaxc.serverstatus.databinding.FragmentServerListBinding;
import com.utopiaxc.serverstatus.utils.Constants;
import com.utopiaxc.serverstatus.utils.Variables;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务器列表Fragment
 * <p>用于显示服务器列表
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:50:32
 */
public class ServerListFragment extends Fragment {

    private FragmentServerListBinding binding;
    protected Context context;
    protected List<ServerCardBean> serverCardBeans;
    private final int messageUpdateFlag = 213465124;
    protected ServerCardAdapter serverCardAdapter;
    protected ServerUpdatedReceiver serverUpdatedReceiver;
    protected ServerListFragmentHandler serverListFragmentHandler;

    /**
     * 服务器列表Fragment视图创建
     *
     * @param inflater           参数
     * @param container          参数
     * @param savedInstanceState 参数
     * @return android.view.View
     * @author UtopiaXC
     * @since 2022-05-22 22:51:05
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentServerListBinding.inflate(inflater, container, false);
        context = requireContext();
        serverListFragmentHandler = new ServerListFragmentHandler(context.getMainLooper());
        binding.listServerList.setLayoutManager(new LinearLayoutManager(requireContext()));
        serverCardBeans = new ArrayList<>();
        serverCardAdapter = new ServerCardAdapter(serverCardBeans);
        serverCardAdapter.registerItemClickID();
        serverCardAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.cardServer) {
                ServerCardBean serverCardBean = (ServerCardBean) adapter.getItem(position);
                Intent intent = new Intent(context, ServerActivity.class);
                intent.putExtra("serverId", serverCardBean.getServerId());
                startActivity(intent);
            }
        });
        binding.listServerList.setAdapter(serverCardAdapter);

        //注册后台服务进程异常广播
        serverUpdatedReceiver = new ServerUpdatedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED");
        context.registerReceiver(serverUpdatedReceiver, intentFilter, "com.utopiaxc.receiver.RECEIVE_INTERNAL_BROADCAST", null);
        new Thread(new GetServerUpdatedStatus()).start();
        return binding.getRoot();
    }

    /**
     * 服务器列表Fragment视图销毁
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:51:50
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
            //查询全部服务器的最新状态
            List<StatusBean> statusBeans = Variables.database.statusDao().getNewestStatus();
            List<ServerBean> serverBeans = Variables.database.serverDao().getAll();
            serverCardBeans.clear();
            for (StatusBean statusBean : statusBeans) {
                //将服务器名进行映射
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
                serverCardBeans.add(serverCardBean);
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
            //当收到数据更新消息时调用适配器更新
            if (msg.what == messageUpdateFlag) {
                serverCardAdapter.notifyDataSetChanged();
                if (serverCardBeans.size() == 0) {
                    binding.noServerAlert.setVisibility(View.VISIBLE);
                } else {
                    binding.noServerAlert.setVisibility(View.GONE);
                }
            }
        }
    }
}