package com.utopiaxc.serverstatus.database.mapper;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

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
     * @author UtopiaXC
     * @since 2022-05-22 23:04:55
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.StatusBean>
     */
    @Query("SELECT * FROM status")
    List<StatusBean> getAll();

    /**
     * 插入服务器状态列表
     *
     * @author UtopiaXC
     * @since 2022-05-22 23:05:16
     * @param status 状态列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(StatusBean... status);
}
