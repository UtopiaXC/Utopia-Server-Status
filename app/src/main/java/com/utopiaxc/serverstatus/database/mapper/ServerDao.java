package com.utopiaxc.serverstatus.database.mapper;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.utopiaxc.serverstatus.database.model.ServerBean;

import java.util.List;

/**
 * @author UtopiaXC
 * @date 2022-05-22 14:55
 */
@SuppressWarnings("AndroidUnresolvedRoomSqlReference")
@Dao
public interface ServerDao {
    @Query("SELECT id,server_name FROM servers")
    List<ServerBean> getAll();

    @Query("SELECT id,server_name FROM servers WHERE id IN (:userIds)")
    List<ServerBean> loadAllByIds(List<String> userIds);

    @Query("SELECT id,server_name FROM servers WHERE id=:id")
    ServerBean getById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ServerBean... servers);
}
