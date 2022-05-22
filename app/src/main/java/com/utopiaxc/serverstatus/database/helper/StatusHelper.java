package com.utopiaxc.serverstatus.database.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * @author UtopiaXC
 * @date 2022-05-22 12:09
 */
public class StatusHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "uss.db";
    public static final String TABLE_NAME = "Status";

    public StatusHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table if not exists " + TABLE_NAME + " (" +
                "id text primary key, " +
                "server_id text, " +
                "server_ipv4_status text, " +
                "server_ipv6_status text, " +
                "server_uptime text, " +
                "server_load text, " +
                "server_network_realtime_download_speed text, " +
                "server_network_realtime_upload_speed text, " +
                "server_network_in text, " +
                "server_network_out text, " +
                "server_cpu_percent text, " +
                "server_memory_total text, " +
                "server_memory_used text, " +
                "server_memory_percent text, " +
                "server_swap_total text, " +
                "server_swap_used text, " +
                "server_swap_percent text, " +
                "server_disk_total text, " +
                "server_disk_used text, " +
                "server_disk_percent text, " +
                "server_timestamp datatime" +
                ")";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }
}