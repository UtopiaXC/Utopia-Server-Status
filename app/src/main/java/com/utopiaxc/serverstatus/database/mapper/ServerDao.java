package com.utopiaxc.serverstatus.database.mapper;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.utopiaxc.serverstatus.database.model.ServerBean;

import java.util.List;

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
     * @author UtopiaXC
     * @since 2022-05-22 23:06:13
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.ServerBean>
     */
    @Query("SELECT id,server_name FROM servers")
    List<ServerBean> getAll();

    /**
     * 通过ID列表获取服务器
     *
     * @author UtopiaXC
     * @since 2022-05-22 23:06:47
     * @param userIds 服务器ID列表
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.ServerBean>
     */
    @Query("SELECT id,server_name FROM servers WHERE id IN (:userIds)")
    List<ServerBean> loadAllByIds(List<String> userIds);

    /**
     * 通过服务器ID获取服务器
     *
     * @author UtopiaXC
     * @since 2022-05-22 23:07:20
     * @param id 服务器ID
     * @return com.utopiaxc.serverstatus.database.model.ServerBean
     */
    @Query("SELECT id,server_name FROM servers WHERE id=:id")
    ServerBean getById(String id);

    /**
     * 插入服务器列表
     *
     * @author UtopiaXC
     * @since 2022-05-22 23:07:43
     * @param servers 服务器列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ServerBean... servers);
}
