package com.utopiaxc.serverstatus.database.mapper;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;

import java.util.List;

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
     * @author UtopiaXC
     * @since 2022-05-23 11:57:51
     * @param serverIds 未被删除的服务器ID
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.StatusBean>
     */
    @Query("SELECT * FROM status WHERE server_id NOT IN (:serverIds)")
    List<StatusBean> getDeletedStatus(List<String> serverIds);

    /**
     * 获取全部过期状态
     *
     * @author UtopiaXC
     * @since 2022-05-23 11:58:12
     * @param exp 过期时间
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.StatusBean>
     */
    @Query("SELECT * FROM status WHERE server_timestamp < :exp")
    List<StatusBean> getExpiredStatus(int exp);

    /**
     * 删除状态
     *
     * @author UtopiaXC
     * @since 2022-05-23 11:58:33
     * @param status 状态列表
     */
    @Delete
    void deleteStatus(StatusBean... status);
}
