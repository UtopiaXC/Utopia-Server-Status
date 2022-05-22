package com.utopiaxc.serverstatus.database.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

/**
 * 服务器数据模型
 *
 * @author UtopiaXC
 * @since 2022-05-22 23:03:00
 */
@Entity(tableName = "servers")
public class ServerBean {
    /**
     * 服务器ID
     */
    @PrimaryKey
    @NonNull
    private UUID id = UUID.randomUUID();

    /**
     * 服务器名
     */
    @ColumnInfo(name = "server_name")
    private String serverName;


    @NonNull
    public UUID getId() {
        return id;
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    @NonNull
    @Override
    public String toString() {
        return "Server{" +
                "id='" + id + '\'' +
                ", serverName='" + serverName + '\'' +
                '}';
    }
}
