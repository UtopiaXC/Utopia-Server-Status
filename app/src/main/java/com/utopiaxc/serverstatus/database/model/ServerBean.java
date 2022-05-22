package com.utopiaxc.serverstatus.database.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

/**
 * @author UtopiaXC
 * @date 2022-05-22 14:53
 */
@Entity(tableName = "servers")
public class ServerBean {
    @PrimaryKey
    @NonNull
    private UUID id = UUID.randomUUID();
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
