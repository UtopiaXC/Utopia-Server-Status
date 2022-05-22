package com.utopiaxc.serverstatus.utils;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * @author UtopiaXC
 * @date 2022-05-22 21:53
 */
public class ThemeUtil {
    public static void setThemeMode(String mode) {
        if (mode.equals(Constants.ThemeModeEnum.AUTO_MODE.getMode())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (mode.equals(Constants.ThemeModeEnum.NIGHT_MODE.getMode())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (mode.equals(Constants.ThemeModeEnum.DAY_MODE.getMode())) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
