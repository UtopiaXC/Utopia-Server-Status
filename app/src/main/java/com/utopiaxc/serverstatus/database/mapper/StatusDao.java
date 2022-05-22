package com.utopiaxc.serverstatus.database.mapper;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.utopiaxc.serverstatus.database.model.StatusBean;

import java.util.List;

/**
 * @author UtopiaXC
 * @date 2022-05-22 15:56
 */
@Dao
public interface StatusDao {
    @Query("SELECT * FROM status")
    List<StatusBean> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(StatusBean... status);
}
