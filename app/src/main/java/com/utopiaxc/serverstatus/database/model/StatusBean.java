package com.utopiaxc.serverstatus.database.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

/**
 * @author UtopiaXC
 * @date 2022-05-22 15:50
 */
@Entity(tableName = "status")
public class StatusBean {
    @PrimaryKey
    @NonNull
    private UUID id = UUID.randomUUID();
    @ColumnInfo(name = "server_id")
    private UUID serverId;
    @ColumnInfo(name = "server_type")
    private String serverType;
    @ColumnInfo(name = "server_location")
    private String serverLocation;
    @ColumnInfo(name = "server_region")
    private String serverRegion;
    @ColumnInfo(name = "server_ipv4_status")
    private String serverIpv4Status;
    @ColumnInfo(name = "server_ipv6_status")
    private String serverIpv6Status;
    @ColumnInfo(name = "server_uptime")
    private String serverUptime;
    @ColumnInfo(name = "server_load")
    private String serverLoad;
    @ColumnInfo(name = "server_network_realtime_download_speed")
    private String serverNetworkRealtimeDownloadSpeed;
    @ColumnInfo(name = "server_network_realtime_upload_speed")
    private String serverNetworkRealtimeUploadSpeed;
    @ColumnInfo(name = "server_network_in")
    private String serverNetworkIn;
    @ColumnInfo(name = "server_network_out")
    private String serverNetworkOut;
    @ColumnInfo(name = "server_cpu_percent")
    private String serverCpuPercent;
    @ColumnInfo(name = "server_memory_total")
    private String serverMemoryTotal;
    @ColumnInfo(name = "server_memory_used")
    private String serverMemoryUsed;
    @ColumnInfo(name = "server_memory_percent")
    private String serverMemoryPercent;
    @ColumnInfo(name = "server_swap_total")
    private String serverSwapTotal;
    @ColumnInfo(name = "server_swap_used")
    private String serverSwapUsed;
    @ColumnInfo(name = "server_swap_percent")
    private String serverSwapPercent;
    @ColumnInfo(name = "server_disk_total")
    private String serverDiskTotal;
    @ColumnInfo(name = "server_disk_used")
    private String serverDiskUsed;
    @ColumnInfo(name = "server_disk_percent")
    private String serverDiskPercent;
    @ColumnInfo(name = "server_timestamp")
    private String serverTimestamp;

    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    public UUID getServerId() {
        return this.serverId;
    }

    public void setServerId(UUID serverName) {
        this.serverId = serverName;
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

    public String getServerIpv4Status() {
        return serverIpv4Status;
    }

    public void setServerIpv4Status(String serverIpv4Status) {
        this.serverIpv4Status = serverIpv4Status;
    }

    public String getServerIpv6Status() {
        return serverIpv6Status;
    }

    public void setServerIpv6Status(String serverIpv6Status) {
        this.serverIpv6Status = serverIpv6Status;
    }

    public String getServerUptime() {
        return serverUptime;
    }

    public void setServerUptime(String serverUptime) {
        this.serverUptime = serverUptime;
    }

    public String getServerLoad() {
        return serverLoad;
    }

    public void setServerLoad(String serverLoad) {
        this.serverLoad = serverLoad;
    }

    public String getServerNetworkRealtimeDownloadSpeed() {
        return serverNetworkRealtimeDownloadSpeed;
    }

    public void setServerNetworkRealtimeDownloadSpeed(String serverNetworkRealtimeDownloadSpeed) {
        this.serverNetworkRealtimeDownloadSpeed = serverNetworkRealtimeDownloadSpeed;
    }

    public String getServerNetworkRealtimeUploadSpeed() {
        return serverNetworkRealtimeUploadSpeed;
    }

    public void setServerNetworkRealtimeUploadSpeed(String serverNetworkRealtimeUploadSpeed) {
        this.serverNetworkRealtimeUploadSpeed = serverNetworkRealtimeUploadSpeed;
    }

    public String getServerNetworkIn() {
        return serverNetworkIn;
    }

    public void setServerNetworkIn(String serverNetworkIn) {
        this.serverNetworkIn = serverNetworkIn;
    }

    public String getServerNetworkOut() {
        return serverNetworkOut;
    }

    public void setServerNetworkOut(String serverNetworkOut) {
        this.serverNetworkOut = serverNetworkOut;
    }

    public String getServerCpuPercent() {
        return serverCpuPercent;
    }

    public void setServerCpuPercent(String serverCpuPercent) {
        this.serverCpuPercent = serverCpuPercent;
    }

    public String getServerMemoryTotal() {
        return serverMemoryTotal;
    }

    public void setServerMemoryTotal(String serverMemoryTotal) {
        this.serverMemoryTotal = serverMemoryTotal;
    }

    public String getServerMemoryUsed() {
        return serverMemoryUsed;
    }

    public void setServerMemoryUsed(String serverMemoryUsed) {
        this.serverMemoryUsed = serverMemoryUsed;
    }

    public String getServerMemoryPercent() {
        return serverMemoryPercent;
    }

    public void setServerMemoryPercent(String serverMemoryPercent) {
        this.serverMemoryPercent = serverMemoryPercent;
    }

    public String getServerSwapTotal() {
        return serverSwapTotal;
    }

    public void setServerSwapTotal(String serverSwapTotal) {
        this.serverSwapTotal = serverSwapTotal;
    }

    public String getServerSwapUsed() {
        return serverSwapUsed;
    }

    public void setServerSwapUsed(String serverSwapUsed) {
        this.serverSwapUsed = serverSwapUsed;
    }

    public String getServerSwapPercent() {
        return serverSwapPercent;
    }

    public void setServerSwapPercent(String serverSwapPercent) {
        this.serverSwapPercent = serverSwapPercent;
    }

    public String getServerDiskTotal() {
        return serverDiskTotal;
    }

    public void setServerDiskTotal(String serverDiskTotal) {
        this.serverDiskTotal = serverDiskTotal;
    }

    public String getServerDiskUsed() {
        return serverDiskUsed;
    }

    public void setServerDiskUsed(String serverDiskUsed) {
        this.serverDiskUsed = serverDiskUsed;
    }

    public String getServerDiskPercent() {
        return serverDiskPercent;
    }

    public void setServerDiskPercent(String serverDiskPercent) {
        this.serverDiskPercent = serverDiskPercent;
    }

    public String getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(String serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

}
