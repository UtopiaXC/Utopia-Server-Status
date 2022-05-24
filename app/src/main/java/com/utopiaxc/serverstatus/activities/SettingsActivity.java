package com.utopiaxc.serverstatus.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.services.ServerStatusUpdateService;
import com.utopiaxc.serverstatus.utils.ThemeUtil;
import com.utopiaxc.serverstatus.utils.UpdateStatus;
import com.utopiaxc.serverstatus.utils.Variables;

import java.util.Objects;

/**
 * 设置Activity
 *
 * @author UtopiaXC
 * @since 2022-05-22 23:08:37
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * 设置Activity创建函数
     *
     * @param savedInstanceState 参数
     * @author UtopiaXC
     * @since 2022-05-22 23:08:51
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置页面与返回键
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 偏好Fragment
     *
     * @author UtopiaXC
     * @since 2022-05-22 23:09:58
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {
        /**
         * 偏好界面创建
         *
         * @param savedInstanceState 参数
         * @param rootKey            参数
         * @author UtopiaXC
         * @since 2022-05-22 23:10:15
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            //主题设置变化监听
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            ListPreference listPreference = findPreference("theme");
            Objects.requireNonNull(listPreference).setOnPreferenceChangeListener((preference, newValue) -> {
                ThemeUtil.setThemeMode(newValue.toString());
                return true;
            });

            //后台服务变化监听
            SwitchPreferenceCompat switchPreferenceCompat = findPreference("backgroundService");
            Objects.requireNonNull(switchPreferenceCompat).setOnPreferenceChangeListener((preference, newValue) -> {
                Intent service = new Intent(Variables.context, ServerStatusUpdateService.class);
                if ((boolean) newValue) {
                    //开启后台服务时打断基于上下文的数据获取线程并启动服务
                    Variables.updateThread.interrupt();
                    Variables.context.startService(service);
                } else {
                    //关闭后台服务时关闭后台服务并启动基于上下文的数据获取线程
                    if (!(Variables.updateThread != null && Variables.updateThread.isAlive())) {
                        Variables.updateThread = new Thread(new UpdateStatus(Variables.context));
                        Variables.updateThread.start();
                    }
                    Variables.context.stopService(service);
                }
                return true;
            });
        }
    }

    /**
     * 返回键监听
     *
     * @param item 被点击的按钮
     * @return boolean
     * @author UtopiaXC
     * @since 2022-05-22 23:10:45
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}