package com.utopiaxc.serverstatus.Beans;

import java.util.UUID;

/**
 * 服务器卡片数据
 *
 * @author UtopiaXC
 * @since 2022-05-23 17:45
 */
public class ServerCardBean {
    UUID serverId;
    Integer regionFlag;
    String serverName;
    String serverType;
    Double serverLoad;
    Double serverLoadProcess;
    String serverUptime;

    public ServerCardBean(UUID serverId, Integer regionFlag, String serverName, String serverType, Double serverLoad, Double serverLoadProcess,String serverUptime) {
        this.serverId = serverId;
        this.regionFlag = regionFlag;
        this.serverName = serverName;
        this.serverType = serverType;
        this.serverLoad = serverLoad;
        this.serverUptime=serverUptime;

        this.serverLoadProcess = serverLoadProcess;
    }

    public ServerCardBean() {
    }

    public UUID getServerId() {
        return serverId;
    }

    public void setServerId(UUID serverId) {
        this.serverId = serverId;
    }

    public Integer getRegionFlag() {
        return regionFlag;
    }

    public void setRegionFlag(Integer regionFlag) {
        this.regionFlag = regionFlag;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public Double getServerLoad() {
        return serverLoad;
    }

    public void setServerLoad(Double serverLoad) {
        this.serverLoad = serverLoad;
    }

    public Double getServerLoadProcess() {
        return serverLoadProcess;
    }

    public void setServerLoadProcess(Double serverLoadProcess) {
        this.serverLoadProcess = serverLoadProcess;
    }

    public String getServerUptime() {
        return serverUptime;
    }

    public void setServerUptime(String serverUptime) {
        this.serverUptime = serverUptime;
    }
}
