package com.utopiaxc.serverstatus.database.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

/**
 * 服务器状态数据模型
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:57:16
 */
@Entity(tableName = "status")
public class StatusBean {
    /**
     * 状态ID
     */
    @PrimaryKey
    @NonNull
    private UUID id = UUID.randomUUID();

    /**
     * 服务器ID
     */
    @ColumnInfo(name = "server_id")
    private UUID serverId;

    /**
     * 服务器类型
     */
    @ColumnInfo(name = "server_type")
    private String serverType;

    /**
     * 服务器位置
     */
    @ColumnInfo(name = "server_location")
    private String serverLocation;

    /**
     * 服务器地区
     */
    @ColumnInfo(name = "server_region")
    private String serverRegion;

    /**
     * IPV4网络状态
     */
    @ColumnInfo(name = "server_ipv4_status")
    private Boolean serverIpv4Status;

    /**
     * IPV6网络状态
     */
    @ColumnInfo(name = "server_ipv6_status")
    private Boolean serverIpv6Status;

    /**
     * 服务器运行时间
     */
    @Nullable
    @ColumnInfo(name = "server_uptime")
    private Integer serverUptime;

    /**
     * 服务器负载
     */
    @Nullable
    @ColumnInfo(name = "server_load")
    private Double serverLoad;

    /**
     * 服务器实时下载网速
     */
    @Nullable
    @ColumnInfo(name = "server_network_realtime_download_speed")
    private Integer serverNetworkRealtimeDownloadSpeed;

    /**
     * 服务器实时上传网速
     */
    @Nullable
    @ColumnInfo(name = "server_network_realtime_upload_speed")
    private Integer serverNetworkRealtimeUploadSpeed;

    /**
     * 服务器下载流量
     */
    @Nullable
    @ColumnInfo(name = "server_network_in")
    private Long serverNetworkIn;

    /**
     * 服务器上传流量
     */
    @Nullable
    @ColumnInfo(name = "server_network_out")
    private Long serverNetworkOut;

    /**
     * 服务器CPU负载
     */
    @Nullable
    @ColumnInfo(name = "server_cpu_percent")
    private Double serverCpuPercent;

    /**
     * 服务器内存总量
     */
    @Nullable
    @ColumnInfo(name = "server_memory_total")
    private Integer serverMemoryTotal;

    /**
     * 服务器内存占用
     */
    @Nullable
    @ColumnInfo(name = "server_memory_used")
    private Integer serverMemoryUsed;

    /**
     * 服务器内存负载
     */
    @Nullable
    @ColumnInfo(name = "server_memory_percent")
    private Double serverMemoryPercent;

    /**
     * 服务器交换总量
     */
    @Nullable
    @ColumnInfo(name = "server_swap_total")
    private Integer serverSwapTotal;

    /**
     * 服务器交换占用
     */
    @Nullable
    @ColumnInfo(name = "server_swap_used")
    private Integer serverSwapUsed;

    /**
     * 服务器交换负载
     */
    @Nullable
    @ColumnInfo(name = "server_swap_percent")
    private Double serverSwapPercent;

    /**
     * 服务器硬盘总量
     */
    @Nullable
    @ColumnInfo(name = "server_disk_total")
    private Integer serverDiskTotal;

    /**
     * 服务器硬盘占用
     */
    @Nullable
    @ColumnInfo(name = "server_disk_used")
    private Integer serverDiskUsed;

    /**
     * 服务器硬盘负载
     */
    @Nullable
    @ColumnInfo(name = "server_disk_percent")
    private Double serverDiskPercent;

    /**
     * 服务器状态更新时间
     */
    @Nullable
    @ColumnInfo(name = "server_timestamp")
    private Integer serverTimestamp;


    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    public UUID getServerId() {
        return serverId;
    }

    public void setServerId(UUID serverId) {
        this.serverId = serverId;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getServerLocation() {
        return serverLocation;
    }

    public void setServerLocation(String serverLocation) {
        this.serverLocation = serverLocation;
    }

    public String getServerRegion() {
        return serverRegion;
    }

    public void setServerRegion(String serverRegion) {
        this.serverRegion = serverRegion;
    }

    public Boolean getServerIpv4Status() {
        return serverIpv4Status;
    }

    public void setServerIpv4Status(Boolean serverIpv4Status) {
        this.serverIpv4Status = serverIpv4Status;
    }

    public Boolean getServerIpv6Status() {
        return serverIpv6Status;
    }

    public void setServerIpv6Status(Boolean serverIpv6Status) {
        this.serverIpv6Status = serverIpv6Status;
    }

    @Nullable
    public Integer getServerUptime() {
        return serverUptime;
    }

    public void setServerUptime(@Nullable Integer serverUptime) {
        this.serverUptime = serverUptime;
    }

    @Nullable
    public Double getServerLoad() {
        return serverLoad;
    }

    public void setServerLoad(@Nullable Double serverLoad) {
        this.serverLoad = serverLoad;
    }

    @Nullable
    public Integer getServerNetworkRealtimeDownloadSpeed() {
        return serverNetworkRealtimeDownloadSpeed;
    }

    public void setServerNetworkRealtimeDownloadSpeed(@Nullable Integer serverNetworkRealtimeDownloadSpeed) {
        this.serverNetworkRealtimeDownloadSpeed = serverNetworkRealtimeDownloadSpeed;
    }

    @Nullable
    public Integer getServerNetworkRealtimeUploadSpeed() {
        return serverNetworkRealtimeUploadSpeed;
    }

    public void setServerNetworkRealtimeUploadSpeed(@Nullable Integer serverNetworkRealtimeUploadSpeed) {
        this.serverNetworkRealtimeUploadSpeed = serverNetworkRealtimeUploadSpeed;
    }

    @Nullable
    public Long getServerNetworkIn() {
        return serverNetworkIn;
    }

    public void setServerNetworkIn(@Nullable Long serverNetworkIn) {
        this.serverNetworkIn = serverNetworkIn;
    }

    @Nullable
    public Long getServerNetworkOut() {
        return serverNetworkOut;
    }

    public void setServerNetworkOut(@Nullable Long serverNetworkOut) {
        this.serverNetworkOut = serverNetworkOut;
    }

    @Nullable
    public Double getServerCpuPercent() {
        return serverCpuPercent;
    }

    public void setServerCpuPercent(@Nullable Double serverCpuPercent) {
        this.serverCpuPercent = serverCpuPercent;
    }

    @Nullable
    public Integer getServerMemoryTotal() {
        return serverMemoryTotal;
    }

    public void setServerMemoryTotal(@Nullable Integer serverMemoryTotal) {
        this.serverMemoryTotal = serverMemoryTotal;
    }

    @Nullable
    public Integer getServerMemoryUsed() {
        return serverMemoryUsed;
    }

    public void setServerMemoryUsed(@Nullable Integer serverMemoryUsed) {
        this.serverMemoryUsed = serverMemoryUsed;
    }

    @Nullable
    public Double getServerMemoryPercent() {
        return serverMemoryPercent;
    }

    public void setServerMemoryPercent(@Nullable Double serverMemoryPercent) {
        this.serverMemoryPercent = serverMemoryPercent;
    }

    @Nullable
    public Integer getServerSwapTotal() {
        return serverSwapTotal;
    }

    public void setServerSwapTotal(@Nullable Integer serverSwapTotal) {
        this.serverSwapTotal = serverSwapTotal;
    }

    @Nullable
    public Integer getServerSwapUsed() {
        return serverSwapUsed;
    }

    public void setServerSwapUsed(@Nullable Integer serverSwapUsed) {
        this.serverSwapUsed = serverSwapUsed;
    }

    @Nullable
    public Double getServerSwapPercent() {
        return serverSwapPercent;
    }

    public void setServerSwapPercent(@Nullable Double serverSwapPercent) {
        this.serverSwapPercent = serverSwapPercent;
    }

    @Nullable
    public Integer getServerDiskTotal() {
        return serverDiskTotal;
    }

    public void setServerDiskTotal(@Nullable Integer serverDiskTotal) {
        this.serverDiskTotal = serverDiskTotal;
    }

    @Nullable
    public Integer getServerDiskUsed() {
        return serverDiskUsed;
    }

    public void setServerDiskUsed(@Nullable Integer serverDiskUsed) {
        this.serverDiskUsed = serverDiskUsed;
    }

    @Nullable
    public Double getServerDiskPercent() {
        return serverDiskPercent;
    }

    public void setServerDiskPercent(@Nullable Double serverDiskPercent) {
        this.serverDiskPercent = serverDiskPercent;
    }

    @Nullable
    public Integer getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(@Nullable Integer serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

    @NonNull
    @Override
    public String toString() {
        return "StatusBean{" +
                "id=" + id +
                ", serverId=" + serverId +
                ", serverType='" + serverType + '\'' +
                ", serverLocation='" + serverLocation + '\'' +
                ", serverRegion='" + serverRegion + '\'' +
                ", serverIpv4Status=" + serverIpv4Status +
                ", serverIpv6Status=" + serverIpv6Status +
                ", serverUptime=" + serverUptime +
                ", serverLoad=" + serverLoad +
                ", serverNetworkRealtimeDownloadSpeed=" + serverNetworkRealtimeDownloadSpeed +
                ", serverNetworkRealtimeUploadSpeed=" + serverNetworkRealtimeUploadSpeed +
                ", serverNetworkIn=" + serverNetworkIn +
                ", serverNetworkOut=" + serverNetworkOut +
                ", serverCpuPercent=" + serverCpuPercent +
                ", serverMemoryTotal=" + serverMemoryTotal +
                ", serverMemoryUsed=" + serverMemoryUsed +
                ", serverMemoryPercent=" + serverMemoryPercent +
                ", serverSwapTotal=" + serverSwapTotal +
                ", serverSwapUsed=" + serverSwapUsed +
                ", serverSwapPercent=" + serverSwapPercent +
                ", serverDiskTotal=" + serverDiskTotal +
                ", serverDiskUsed=" + serverDiskUsed +
                ", serverDiskPercent=" + serverDiskPercent +
                ", serverTimestamp=" + serverTimestamp +
                '}';
    }
}
