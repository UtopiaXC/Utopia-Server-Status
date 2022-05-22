package com.utopiaxc.serverstatus.database.helper;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.utopiaxc.serverstatus.database.mapper.ServerDao;
import com.utopiaxc.serverstatus.database.mapper.StatusDao;
import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;

/**
 * 数据库持久层
 *
 * @author UtopiaXC
 * @since 2022-05-22 23:08:08
 */
@Database(entities = {ServerBean.class, StatusBean.class}, version = 1, exportSchema = false)
public abstract class DatabaseHelper extends RoomDatabase {
    public abstract ServerDao serverDao();

    public abstract StatusDao statusDao();
}