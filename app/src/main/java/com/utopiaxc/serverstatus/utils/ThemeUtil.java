package com.utopiaxc.serverstatus.utils;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * 主题工具
 * <p> 用于提供对主题操作的方法
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:19:58
 */
public class ThemeUtil {
    /**
     * 设置主题
     * <p>类方法，用于设置APP主题
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:20:19
     * @param mode 主题模式
     */
    public static void setThemeMode(String mode) {
        //通过主题枚举设置主题
        if (mode.equals(Constants.ThemeModeEnum.AUTO_MODE.getMode())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (mode.equals(Constants.ThemeModeEnum.NIGHT_MODE.getMode())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (mode.equals(Constants.ThemeModeEnum.DAY_MODE.getMode())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static boolean isNightMode(Context context){
        int mode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }
}
