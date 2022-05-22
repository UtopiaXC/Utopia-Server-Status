package com.utopiaxc.serverstatus.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.utopiaxc.serverstatus.database.helper.DatabaseHelper;

/**
 * 全局变量
 * <p> 用于保存上下文，数据库等全局变量
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:10:39
 */
@SuppressLint("StaticFieldLeak")
public class Variables {
    /**
     * 数据库持久层
     */
    public static DatabaseHelper database;
    /**
     * 全局上下文
     */
    public static Context context;
    /**
     * 数据更新线程
     */
    public static Thread updateThread;
}
