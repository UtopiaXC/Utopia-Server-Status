package com.utopiaxc.serverstatus.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;
import com.utopiaxc.serverstatus.databinding.ActivityServerBinding;
import com.utopiaxc.serverstatus.utils.Constants;
import com.utopiaxc.serverstatus.utils.StorageUtil;
import com.utopiaxc.serverstatus.utils.ThemeUtil;
import com.utopiaxc.serverstatus.utils.Variables;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
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
    private ActivityServerBinding mBinding;
    private UUID mServerId;
    private final int SERVER_UPDATED = 32161154;
    private ServerBean mServerBean;
    private StatusBean mStatusBean;
    private List<StatusBean> mStatusBeans;
    private ServerUpdatedReceiver mServerUpdatedReceiver;
    private ServerInfoMessageHandler mServerInfoMessageHandler;

    /**
     * Activity创建方法
     *
     * @param savedInstanceState 入参
     * @author UtopiaXC
     * @since 2022-05-23 23:30:52
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityServerBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        Context context = this;
        mServerInfoMessageHandler = new ServerInfoMessageHandler(getMainLooper());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mServerId = (UUID) getIntent().getExtras().get("serverId");
        new Thread(new ServerInfo()).start();

        //注册后台服务进程异常广播
        mServerUpdatedReceiver = new ServerUpdatedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED");
        context.registerReceiver(mServerUpdatedReceiver, intentFilter, "com.utopiaxc.receiver.RECEIVE_INTERNAL_BROADCAST", null);

        //初始化图表
        setChartOptions(getString(R.string.cpu_load), mBinding.cpuChart);
        setChartOptions(getString(R.string.memory_load), mBinding.memoryChart);
        setChartOptions(getString(R.string.download), mBinding.downChart);
        setChartOptions(getString(R.string.upload), mBinding.upChart);

        //保存按钮。调用系统默认选择器选择保存路径获取Uri
        mBinding.buttonExportServerData.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("**/csv");
            intent.putExtra(Intent.EXTRA_TITLE, mServerBean.getServerName() + ".csv");
            fileStorageResult.launch(intent);
        });
    }

    //文件选择回调
    ActivityResultLauncher<Intent> fileStorageResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        //启动保存线程
                        Uri fileUri = data.getData();
                        new Thread(new StatusSaveFile(fileUri)).start();
                    }
                }
            });

    /**
     * 文件导出保存线程
     *
     * @author UtopiaXC
     * @since 2022-05-25 00:04:11
     */
    class StatusSaveFile implements Runnable {
        private final Uri uri;

        /**
         * 初始化文件保存线程
         *
         * @param uri 文件URI
         * @author UtopiaXC
         * @since 2022-05-25 00:04:27
         */
        public StatusSaveFile(Uri uri) {
            this.uri = uri;
        }

        /**
         * 文件保存方法
         *
         * @author UtopiaXC
         * @since 2022-05-25 00:04:42
         */
        @Override
        public void run() {
            //通过Uri打开文件并保存
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                List<StatusBean> status = Variables.database.statusDao().getAll();
                String content = getString(R.string.export_report_header);
                outputStream.write(content.getBytes());
                //遍历全部状态并保存
                for (StatusBean statusBean : status) {
                    content = mServerBean.getServerName()
                            + "," + statusBean.getServerType()
                            + "," + statusBean.getServerLocation()
                            + "," + statusBean.getServerRegion()
                            + "," + statusBean.getServerIpv4Status()
                            + "," + statusBean.getServerIpv6Status()
                            + "," + statusBean.getServerUptime()
                            + "," + statusBean.getServerLoad()
                            + "," + statusBean.getServerNetworkRealtimeDownloadSpeed()
                            + "," + statusBean.getServerNetworkRealtimeUploadSpeed()
                            + "," + statusBean.getServerNetworkIn()
                            + "," + statusBean.getServerNetworkOut()
                            + "," + statusBean.getServerCpuPercent()
                            + "," + statusBean.getServerMemoryTotal()
                            + "," + statusBean.getServerMemoryUsed()
                            + "," + statusBean.getServerMemoryPercent()
                            + "," + statusBean.getServerSwapTotal()
                            + "," + statusBean.getServerSwapUsed()
                            + "," + statusBean.getServerSwapPercent()
                            + "," + statusBean.getServerDiskTotal()
                            + "," + statusBean.getServerDiskUsed()
                            + "," + statusBean.getServerDiskPercent()
                            + "," + statusBean.getServerTimestamp() + "\n";
                    outputStream.write(content.getBytes());
                }
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        unregisterReceiver(mServerUpdatedReceiver);
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

    /**
     * 服务器信息更新线程
     *
     * @author UtopiaXC
     * @since 2022-05-25 00:05:12
     */
    class ServerInfo implements Runnable {
        /**
         * 对服务器信息进行更新
         *
         * @author UtopiaXC
         * @since 2022-05-25 00:05:23
         */
        @Override
        public void run() {
            mServerBean = Variables.database.serverDao().getById(mServerId);
            mStatusBean = Variables.database.statusDao().getNewestStatusByServerId(mServerId);
            mStatusBeans = Variables.database.statusDao().getDataForChartByServerId(mServerId);
            Message message = new Message();
            message.what = SERVER_UPDATED;
            mServerInfoMessageHandler.sendMessage(message);
        }
    }

    /**
     * 服务器消息更新句柄
     *
     * @author UtopiaXC
     * @since 2022-05-25 00:05:43
     */
    class ServerInfoMessageHandler extends Handler {

        /**
         * 初始化句柄
         *
         * @param looper 循环
         * @author UtopiaXC
         * @since 2022-05-25 00:05:56
         */
        public ServerInfoMessageHandler(@NonNull Looper looper) {
            super(looper);
        }

        /**
         * 消息处理
         *
         * @param msg 消息
         * @author UtopiaXC
         * @since 2022-05-25 00:06:10
         */
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == SERVER_UPDATED) {
                //设置服务器基本信息
                mBinding.serverInfoServerName.setText(mServerBean.getServerName());
                mBinding.serverInfoRegionFlag.setImageResource(Constants.RegionFlagEnum.getByKey(mStatusBean.getServerRegion()).getSourceId());
                mBinding.serverInfoServerType.setText(mStatusBean.getServerType() + " " + mStatusBean.getServerLocation());
                //设置当服务器在线时的信息
                if (mStatusBean.getServerIpv4Status() || mStatusBean.getServerIpv6Status()) {
                    mBinding.serverDetailInfo.setVisibility(View.VISIBLE);
                    Double systemLoad = mStatusBean.getServerLoad();
                    Double cpuLoad = mStatusBean.getServerCpuPercent();
                    Double memoryLoad = mStatusBean.getServerMemoryPercent();
                    Double diskLoad = mStatusBean.getServerDiskPercent();
                    //设置负载图表与文字
                    if (systemLoad != null) {
                        mBinding.serverInfoServerLoad.setText(getString(R.string.server_activity_load_title) + systemLoad);
                        if (systemLoad >= 1) {
                            mBinding.serverInfoProgressLoad.setProgress(100);
                        } else {
                            mBinding.serverInfoProgressLoad.setProgress((int) (systemLoad * 100));
                        }
                    }
                    //设置CPU负载图表与文字
                    if (cpuLoad != null) {
                        mBinding.serverInfoCpuLoad.setText(getString(R.string.server_activity_cpu_load_title) + (int) (cpuLoad * 100) + "%");
                        mBinding.serverInfoProgressCpuLoad.setProgress((int) (cpuLoad * 100));
                    }
                    //设置内存负载图表与文字
                    if (memoryLoad != null) {
                        mBinding.serverInfoMemoryLoad.setText(getString(R.string.server_activity_memory_load_title) + (int) (memoryLoad * 100) + "%");
                        mBinding.serverInfoProgressMemoryLoad.setProgress((int) (memoryLoad * 100));
                        mBinding.progressMemoryUsed.setProgress(memoryLoad.floatValue());
                        if (mStatusBean.getServerMemoryTotal() != null) {
                            mBinding.memoryTotal.setText(StorageUtil.formatKbToString(mStatusBean.getServerMemoryTotal()) + " " + getString(R.string.total));
                        }
                        if (mStatusBean.getServerMemoryUsed() != null) {
                            mBinding.memoryUsed.setText(StorageUtil.formatKbToString(mStatusBean.getServerMemoryUsed()) + " " + getString(R.string.used));
                        }
                    }
                    //设置硬盘负载图表与文字
                    if (diskLoad != null) {
                        mBinding.serverInfoDiskLoad.setText(getString(R.string.server_activity_disk_load_title) + (int) (diskLoad * 100) + "%");
                        mBinding.serverInfoDiskProgressLoad.setProgress((int) (diskLoad * 100));
                        mBinding.progressDiskUsed.setProgress(diskLoad.floatValue());
                        if (mStatusBean.getServerDiskTotal() != null) {
                            mBinding.diskTotal.setText(StorageUtil.formatMbToString(mStatusBean.getServerDiskTotal()) + " " + getString(R.string.total));
                        }
                        if (mStatusBean.getServerDiskUsed() != null) {
                            mBinding.diskUsed.setText(StorageUtil.formatMbToString(mStatusBean.getServerDiskUsed()) + " " + getString(R.string.used));
                        }
                    }
                    //设置交换分区负载图表与文字
                    if (mStatusBean.getServerSwapPercent() != null) {
                        mBinding.progressSwapUsed.setProgress(mStatusBean.getServerSwapPercent().floatValue());
                    } else {
                        mBinding.progressSwapUsed.setProgress(0);
                    }
                    if (mStatusBean.getServerSwapTotal() != null) {
                        mBinding.swapTotal.setText(StorageUtil.formatKbToString(mStatusBean.getServerSwapTotal()) + " " + getString(R.string.total));
                    }
                    if (mStatusBean.getServerSwapUsed() != null) {
                        mBinding.swapUsed.setText(StorageUtil.formatKbToString(mStatusBean.getServerSwapUsed()) + " " + getString(R.string.used));
                    }
                    //设置网络卡片
                    if (mStatusBean.getServerNetworkRealtimeDownloadSpeed() != null
                            && mStatusBean.getServerNetworkIn() != null) {
                        mBinding.networkDown.setText(
                                StorageUtil.formatByteSpeedToString(mStatusBean.getServerNetworkRealtimeDownloadSpeed())
                                        + " "
                                        + getString(R.string.realtime)
                                        + " "
                                        + StorageUtil.formatByteToString(mStatusBean.getServerNetworkIn())
                                        + " "
                                        + getString(R.string.total)
                        );
                    }
                    if (mStatusBean.getServerNetworkRealtimeUploadSpeed() != null
                            && mStatusBean.getServerNetworkOut() != null) {
                        mBinding.networkUp.setText(
                                StorageUtil.formatByteSpeedToString(mStatusBean.getServerNetworkRealtimeUploadSpeed())
                                        + " "
                                        + getString(R.string.realtime)
                                        + " "
                                        + StorageUtil.formatByteToString(mStatusBean.getServerNetworkOut())
                                        + " "
                                        + getString(R.string.total)
                        );
                    }
                    //在线时间
                    if (mStatusBean.getServerUptime() != null) {
                        mBinding.serverInfoUptime.setText(getString(R.string.uptime) + " " + mStatusBean.getServerUptime());
                    }
                } else {
                    //离线状态
                    mBinding.serverInfoServerLoad.setText(getString(R.string.server_activity_load_title) + getString(R.string.server_offline));
                    mBinding.serverInfoCpuLoad.setText(getString(R.string.server_activity_cpu_load_title) + getString(R.string.server_offline));
                    mBinding.serverInfoMemoryLoad.setText(getString(R.string.server_activity_memory_load_title) + getString(R.string.server_offline));
                    mBinding.serverInfoDiskLoad.setText(getString(R.string.server_activity_disk_load_title) + getString(R.string.server_offline));
                    mBinding.serverInfoUptime.setText(getString(R.string.uptime) + " " + getString(R.string.server_offline));
                    mBinding.serverDetailInfo.setVisibility(View.GONE);
                }

                //初始化折线表数据
                List<StatusBean> statusBeanList = mStatusBeans;
                List<Entry> cpuValues = new ArrayList<>();
                List<Entry> memoryValues = new ArrayList<>();
                List<Entry> netUpValues = new ArrayList<>();
                List<Entry> netDownValues = new ArrayList<>();
                int flag = 1;
                Collections.reverse(statusBeanList);
                //设置折线表数据
                for (StatusBean statusBean : statusBeanList) {
                    if (statusBean.getServerCpuPercent() != null) {
                        cpuValues.add(new Entry(flag++, statusBean.getServerCpuPercent().floatValue()));
                    }
                    if (statusBean.getServerMemoryPercent() != null) {
                        memoryValues.add(new Entry(flag++, statusBean.getServerMemoryPercent().floatValue()));
                    }
                    if (statusBean.getServerNetworkRealtimeUploadSpeed() != null) {
                        netUpValues.add(new Entry(flag++, statusBean.getServerNetworkRealtimeUploadSpeed().floatValue()));
                        netDownValues.add(new Entry(flag++, statusBean.getServerCpuPercent().floatValue()));
                    }
                    if (statusBean.getServerNetworkRealtimeDownloadSpeed() != null) {
                        netDownValues.add(new Entry(flag++, statusBean.getServerNetworkRealtimeDownloadSpeed().floatValue()));
                    }
                }

                //设置折线表数据样式
                setChartDataSetOptions(new LineDataSet(cpuValues, null), mBinding.cpuChart);
                setChartDataSetOptions(new LineDataSet(memoryValues, null), mBinding.memoryChart);
                setChartDataSetOptions(new LineDataSet(netUpValues, null), mBinding.upChart);
                setChartDataSetOptions(new LineDataSet(netDownValues, null), mBinding.downChart);
            }
        }
    }

    /**
     * 设置表组件样式
     *
     * @param title     表标题
     * @param lineChart 表组件
     * @author UtopiaXC
     * @since 2022-05-24 19:04:07
     */
    private void setChartOptions(String title, LineChart lineChart) {
        lineChart.setData(new LineData(new LineDataSet(new ArrayList<>(), null)));
        lineChart.setTouchEnabled(false);
        lineChart.getDescription().setEnabled(true);
        lineChart.getDescription().setText(title);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        lineChart.getDescription().setPosition(screenWidth - 100, 80F);
        lineChart.getDescription().setTextColor(Color.WHITE);
        lineChart.getDescription().setTextSize(16F);
        if (ThemeUtil.isNightMode(this)) {
            lineChart.setBackgroundColor(getColor(R.color.chart_night));
        } else {
            lineChart.setBackgroundColor(getColor(R.color.chart_day));
        }

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setMaxHighlightDistance(300);

        XAxis x = lineChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setTextColor(Color.WHITE);
        x.setDrawGridLines(false);
        x.setAxisLineColor(Color.WHITE);
        YAxis y = lineChart.getAxisLeft();
        y.setLabelCount(6, true);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        lineChart.getAxisRight().setEnabled(false);
    }

    /**
     * 设置数据样式
     *
     * @param dataSet   数据集合
     * @param lineChart 表组件
     * @author UtopiaXC
     * @since 2022-05-24 19:03:34
     */
    private void setChartDataSetOptions(LineDataSet dataSet, LineChart lineChart) {
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(1.8f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(Color.WHITE);
        dataSet.setHighLightColor(Color.rgb(244, 117, 117));
        dataSet.setColor(Color.WHITE);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawValues(false);
        dataSet.setFillColor(Color.WHITE);
        dataSet.setFillAlpha(100);
        dataSet.setDrawHorizontalHighlightIndicator(false);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}