package com.utopiaxc.serverstatus.database.mapper;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.utopiaxc.serverstatus.database.model.ServerBean;

import java.util.List;
import java.util.UUID;

/**
 * 服务器数据库连接
 *
 * @author UtopiaXC
 * @since 2022-05-22 23:05:53
 */
@SuppressWarnings("AndroidUnresolvedRoomSqlReference")
@Dao
public interface ServerDao {
    /**
     * 获取全部服务器
     *
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.ServerBean>
     * @author UtopiaXC
     * @since 2022-05-22 23:06:13
     */
    @Query("SELECT id,server_name FROM servers")
    List<ServerBean> getAll();

    /**
     * 通过ID列表获取服务器
     *
     * @param serverIds 服务器ID列表
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.ServerBean>
     * @author UtopiaXC
     * @since 2022-05-22 23:06:47
     */
    @Query("SELECT id,server_name FROM servers WHERE id IN (:serverIds)")
    List<ServerBean> loadAllByIds(List<String> serverIds);

    /**
     * 通过服务器ID获取服务器
     *
     * @param id 服务器ID
     * @return com.utopiaxc.serverstatus.database.model.ServerBean
     * @author UtopiaXC
     * @since 2022-05-22 23:07:20
     */
    @Query("SELECT id,server_name FROM servers WHERE id=:id")
    ServerBean getById(UUID id);

    /**
     * 插入服务器列表
     *
     * @param servers 服务器列表
     * @author UtopiaXC
     * @since 2022-05-22 23:07:43
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ServerBean... servers);

    /**
     * 通过服务器名获取服务器
     *
     * @param serverNames 服务器名列表
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.ServerBean>
     * @author UtopiaXC
     * @since 2022-05-23 10:07:28
     */
    @Query("SELECT id,server_name FROM servers WHERE server_name IN (:serverNames)")
    List<ServerBean> getByNames(List<String> serverNames);


    /**
     * 通过服务器名获取列表外的服务器
     *
     * @param serverNames 服务器名列表
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.ServerBean>
     * @author UtopiaXC
     * @since 2022-05-23 10:07:28
     */
    @Query("SELECT id,server_name FROM servers WHERE server_name NOT IN (:serverNames)")
    List<ServerBean> getOutsideByNames(List<String> serverNames);

    /**
     * 删除对象
     *
     * @param serverBeans 服务器
     * @author UtopiaXC
     * @since 2022-05-23 10:07:28
     */
    @Delete
    void deleteServer(ServerBean... serverBeans);
}
