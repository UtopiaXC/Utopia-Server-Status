package com.utopiaxc.serverstatus.database.mapper;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.utopiaxc.serverstatus.database.model.NotificationBean;
import com.utopiaxc.serverstatus.database.model.ServerBean;

import java.util.List;
import java.util.UUID;

/**
 * 消息提醒数据库访问对象
 *
 * @author UtopiaXC
 * @since 2022-05-24 9:12
 */
@Dao
public interface NotificationDao {
    /**
     * 获取全部消息提醒
     *
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.NotificationBean>
     * @author UtopiaXC
     * @since 2022-05-22 23:06:13
     */
    @Query("SELECT * FROM notifications")
    List<NotificationBean> getAll();

    /**
     * 获取某时间戳后的消息提醒
     *
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.NotificationBean>
     * @author UtopiaXC
     * @since 2022-05-22 23:06:13
     */
    @Query("SELECT * FROM notifications WHERE alert_timestamp>=:timestamp")
    List<NotificationBean> getAllSince(int timestamp);

    /**
     * 获取指定服务器的消息提醒
     *
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.NotificationBean>
     * @author UtopiaXC
     * @since 2022-05-22 23:06:13
     */
    @Query("SELECT * FROM notifications WHERE server_id=:serverId")
    List<NotificationBean> getAllFromServer(UUID serverId);

    /**
     * 获取指定服务器某时间戳后的指定消息类型提醒
     *
     * @return java.util.List<com.utopiaxc.serverstatus.database.model.NotificationBean>
     * @author UtopiaXC
     * @since 2022-05-22 23:06:13
     */
    @Query("SELECT * FROM notifications WHERE server_id=:serverId AND alert_timestamp>=:timestamp AND alert_type=:type")
    List<NotificationBean> getAllFromServerSince(UUID serverId, int timestamp, int type);

    /**
     * 插入消息列表
     *
     * @param notificationBeans 消息列表
     * @author UtopiaXC
     * @since 2022-05-22 23:07:43
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(NotificationBean... notificationBeans);

    /**
     * 删除对象
     *
     * @param notificationBeans 消息
     * @author UtopiaXC
     * @since 2022-05-23 10:07:28
     */
    @Delete
    void deleteNotifications(NotificationBean... notificationBeans);
}
