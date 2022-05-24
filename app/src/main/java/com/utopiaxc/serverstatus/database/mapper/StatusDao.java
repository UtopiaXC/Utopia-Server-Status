package com.utopiaxc.serverstatus.database.mapper;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;

import java.util.List;
import java.util.UUID;

/**
 * 服务器状态数据库连接
 *
 * @author UtopiaXC
 * @since 2022-05-22 23:03:53
 */
@Dao
public interface StatusDao {
    /**
     * 获取全部服务器状态
     *
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.StatusBean>
     * @author UtopiaXC
     * @since 2022-05-22 23:04:55
     */
    @Query("SELECT * FROM status")
    List<StatusBean> getAll();

    /**
     * 插入服务器状态列表
     *
     * @param status 状态列表
     * @author UtopiaXC
     * @since 2022-05-22 23:05:16
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(StatusBean... status);

    /**
     * 获取已被删除的服务器的状态
     *
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.StatusBean>
     * @author UtopiaXC
     * @since 2022-05-23 11:57:51
     */
    @Query("SELECT * FROM status WHERE server_id NOT IN (SELECT id FROM servers)")
    List<StatusBean> getDeletedStatus();

    /**
     * 获取全部过期状态
     *
     * @param exp 过期时间
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.StatusBean>
     * @author UtopiaXC
     * @since 2022-05-23 11:58:12
     */
    @Query("SELECT * FROM status WHERE server_timestamp < :exp")
    List<StatusBean> getExpiredStatus(int exp);

    /**
     * 删除状态
     *
     * @param status 状态列表
     * @author UtopiaXC
     * @since 2022-05-23 11:58:33
     */
    @Delete
    void deleteStatus(StatusBean... status);

    /**
     * 获取最新的服务器状态
     *
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.StatusBean>
     * @author UtopiaXC
     * @since 2022-05-23 15:20:43
     */
    @Query("SELECT * FROM status WHERE server_timestamp = (SELECT MAX(server_timestamp) FROM status) AND server_id IN (SELECT id FROM servers)")
    List<StatusBean> getNewestStatus();

    /**
     * 获取指定服务器最新的服务器状态
     *
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.StatusBean>
     * @author UtopiaXC
     * @since 2022-05-23 15:20:43
     */
    @Query("SELECT * FROM status WHERE server_timestamp = (SELECT MAX(server_timestamp) FROM status) AND server_id =:serverId LIMIT 1")
    StatusBean getNewestStatusByServerId(UUID serverId);

    /**
     * 获取指定服务器最近的服务器状态
     *
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.StatusBean>
     * @author UtopiaXC
     * @since 2022-05-23 15:20:43
     */
    @Query("SELECT * FROM status WHERE server_timestamp = server_id =:serverId ORDER BY server_timestamp DESC LIMIT 1000")
    List<StatusBean> getDataForChartByServerId(UUID serverId);
}
