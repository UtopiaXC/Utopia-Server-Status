package com.utopiaxc.serverstatus.database.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

/**
 * 消息提醒历史
 *
 * @author UtopiaXC
 * @since 2022-05-24 9:09
 */
@Entity(tableName = "notifications")
public class NotificationBean {
    /**
     * 消息ID
     */
    @PrimaryKey
    @NonNull
    private UUID id = UUID.randomUUID();
    /**
     * 提醒ID
     */
    @ColumnInfo(name = "notification_id")
    private int notificationId;
    /**
     * 服务器ID
     */
    @ColumnInfo(name = "server_id")
    private UUID serverId;
    /**
     * 服务器名
     */
    @ColumnInfo(name = "server_name")
    private String serverName;
    /**
     * 状态ID
     */
    @ColumnInfo(name = "status_id")
    private UUID statusId;
    /**
     * 提醒类型
     */
    @ColumnInfo(name = "alert_type")
    private int alertType;
    /**
     * 提醒时间
     */
    @ColumnInfo(name = "alert_timestamp")
    private int alertTimestamp;

    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public UUID getServerId() {
        return serverId;
    }

    public void setServerId(UUID serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public UUID getStatusId() {
        return statusId;
    }

    public void setStatusId(UUID statusId) {
        this.statusId = statusId;
    }

    public int getAlertType() {
        return alertType;
    }

    public void setAlertType(int alertType) {
        this.alertType = alertType;
    }

    public int getAlertTimestamp() {
        return alertTimestamp;
    }

    public void setAlertTimestamp(int alertTimestamp) {
        this.alertTimestamp = alertTimestamp;
    }
}
