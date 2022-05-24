package com.utopiaxc.serverstatus.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
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
import android.provider.DocumentsContract;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;
import com.utopiaxc.serverstatus.databinding.ActivityServerBinding;
import com.utopiaxc.serverstatus.utils.Constants;
import com.utopiaxc.serverstatus.utils.StorageUtil;
import com.utopiaxc.serverstatus.utils.ThemeUtil;
import com.utopiaxc.serverstatus.utils.Variables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
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
    private static final int CREATE_FILE = 124521054;
    private ServerBean serverBean;
    private StatusBean statusBean;
    private List<StatusBean> statusBeans;
    private ServerUpdatedReceiver serverUpdatedReceiver;
    private ServerInfoMessageHandler serverInfoMessageHandler;

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
        binding = ActivityServerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Context context = this;
        serverInfoMessageHandler = new ServerInfoMessageHandler(getMainLooper());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        serverId = (UUID) getIntent().getExtras().get("serverId");
        new Thread(new ServerInfo()).start();
        //注册后台服务进程异常广播
        serverUpdatedReceiver = new ServerUpdatedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.utopiaxc.serverstatus.SERVER_STATUS_UPDATED");
        context.registerReceiver(serverUpdatedReceiver, intentFilter, "com.utopiaxc.receiver.RECEIVE_INTERNAL_BROADCAST", null);
        setChartOptions(getString(R.string.cpu_load), binding.cpuChart);
        setChartOptions(getString(R.string.memory_load), binding.memoryChart);
        setChartOptions(getString(R.string.download), binding.downChart);
        setChartOptions(getString(R.string.upload), binding.upChart);
        //保存按钮。调用系统默认选择器选择保存路径获取Uri
        binding.buttonExportServerData.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("**/csv");
            intent.putExtra(Intent.EXTRA_TITLE, serverBean.getServerName() + ".csv");
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

    class StatusSaveFile implements Runnable {
        private final Uri uri;

        public StatusSaveFile(Uri uri) {
            this.uri = uri;
        }

        @Override
        public void run() {
            //通过Uri打开文件并保存
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                List<StatusBean> status = Variables.database.statusDao().getAll();
                String content = getString(R.string.export_report_header);
                outputStream.write(content.getBytes());
                for (StatusBean statusBean : status) {
                    content = serverBean.getServerName()
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
                binding.serverInfoServerType.setText(statusBean.getServerType() + " " + statusBean.getServerLocation());
                if (statusBean.getServerIpv4Status() || statusBean.getServerIpv6Status()) {
                    binding.serverDetailInfo.setVisibility(View.VISIBLE);
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
                        binding.progressMemoryUsed.setProgress(memoryLoad.floatValue());
                        if (statusBean.getServerMemoryTotal() != null) {
                            binding.memoryTotal.setText(StorageUtil.formatKbToString(statusBean.getServerMemoryTotal()) + " " + getString(R.string.total));
                        }
                        if (statusBean.getServerMemoryUsed() != null) {
                            binding.memoryUsed.setText(StorageUtil.formatKbToString(statusBean.getServerMemoryUsed()) + " " + getString(R.string.used));
                        }
                    }
                    if (diskLoad != null) {
                        binding.serverInfoDiskLoad.setText(getString(R.string.server_activity_disk_load_title) + (int) (diskLoad * 100) + "%");
                        binding.serverInfoDiskProgressLoad.setProgress((int) (diskLoad * 100));
                        binding.progressDiskUsed.setProgress(diskLoad.floatValue());
                        if (statusBean.getServerDiskTotal() != null) {
                            binding.diskTotal.setText(StorageUtil.formatMbToString(statusBean.getServerDiskTotal()) + " " + getString(R.string.total));
                        }
                        if (statusBean.getServerDiskUsed() != null) {
                            binding.diskUsed.setText(StorageUtil.formatMbToString(statusBean.getServerDiskUsed()) + " " + getString(R.string.used));
                        }
                    }
                    if (statusBean.getServerSwapPercent() != null) {
                        binding.progressSwapUsed.setProgress(statusBean.getServerSwapPercent().floatValue());
                    } else {
                        binding.progressSwapUsed.setProgress(0);
                    }
                    if (statusBean.getServerSwapTotal() != null) {
                        binding.swapTotal.setText(StorageUtil.formatKbToString(statusBean.getServerSwapTotal()) + " " + getString(R.string.total));
                    }
                    if (statusBean.getServerSwapUsed() != null) {
                        binding.swapUsed.setText(StorageUtil.formatKbToString(statusBean.getServerSwapUsed()) + " " + getString(R.string.used));
                    }
                    if (statusBean.getServerNetworkRealtimeDownloadSpeed() != null
                            && statusBean.getServerNetworkIn() != null) {
                        binding.networkDown.setText(
                                StorageUtil.formatByteSpeedToString(statusBean.getServerNetworkRealtimeDownloadSpeed())
                                        + " "
                                        + getString(R.string.realtime)
                                        + " "
                                        + StorageUtil.formatByteToString(statusBean.getServerNetworkIn())
                                        + " "
                                        + getString(R.string.total)
                        );
                    }
                    if (statusBean.getServerNetworkRealtimeUploadSpeed() != null
                            && statusBean.getServerNetworkOut() != null) {
                        binding.networkUp.setText(
                                StorageUtil.formatByteSpeedToString(statusBean.getServerNetworkRealtimeUploadSpeed())
                                        + " "
                                        + getString(R.string.realtime)
                                        + " "
                                        + StorageUtil.formatByteToString(statusBean.getServerNetworkOut())
                                        + " "
                                        + getString(R.string.total)
                        );
                    }
                    if (statusBean.getServerUptime()!=null){
                        binding.serverInfoUptime.setText(getString(R.string.uptime) + " " + statusBean.getServerUptime());
                    }
                } else {
                    binding.serverInfoServerLoad.setText(getString(R.string.server_activity_load_title) + getString(R.string.server_offline));
                    binding.serverInfoCpuLoad.setText(getString(R.string.server_activity_cpu_load_title) + getString(R.string.server_offline));
                    binding.serverInfoMemoryLoad.setText(getString(R.string.server_activity_memory_load_title) + getString(R.string.server_offline));
                    binding.serverInfoDiskLoad.setText(getString(R.string.server_activity_disk_load_title) + getString(R.string.server_offline));
                    binding.serverInfoUptime.setText(getString(R.string.uptime) + " " + getString(R.string.server_offline));
                    binding.serverDetailInfo.setVisibility(View.GONE);
                }

                List<StatusBean> statusBeanList = statusBeans;

                List<Entry> cpuValues = new ArrayList<>();
                List<Entry> memoryValues = new ArrayList<>();
                List<Entry> netUpValues = new ArrayList<>();
                List<Entry> netDownValues = new ArrayList<>();
                int flag = 1;
                Collections.reverse(statusBeanList);
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
                setChartDataSetOptions(new LineDataSet(cpuValues, null), binding.cpuChart);
                setChartDataSetOptions(new LineDataSet(memoryValues, null), binding.memoryChart);
                setChartDataSetOptions(new LineDataSet(netUpValues, null), binding.upChart);
                setChartDataSetOptions(new LineDataSet(netDownValues, null), binding.downChart);
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