package com.utopiaxc.serverstatus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.utopiaxc.serverstatus.database.helper.DatabaseHelper;
import com.utopiaxc.serverstatus.databinding.ActivityMainBinding;
import com.utopiaxc.serverstatus.intro.IntroActivity;
import com.utopiaxc.serverstatus.services.ServerStatusUpdateService;
import com.utopiaxc.serverstatus.utils.ThemeUtil;
import com.utopiaxc.serverstatus.utils.UpdateStatus;
import com.utopiaxc.serverstatus.utils.Variables;

/**
 * 入口Activity
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:12:13
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 程序入口
     * <p>软件入口，UI主题判断、后台服务判断、Fragment支持
     *
     * @param savedInstanceState Activity启动传入数据
     * @author UtopiaXC
     * @since 2022-05-22 22:04:38
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取偏好设置
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //设置UI主题
        ThemeUtil.setThemeMode(sharedPreferences.getString("theme", "auto"));
        //全局上下文与数据库设置
        Variables.context = this;
        new Thread(() -> Variables.database = Room.databaseBuilder(getApplicationContext(), DatabaseHelper.class, "uss").build()).start();
        //Activity Layout设置
        com.utopiaxc.serverstatus.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //判断是否初次启动
        if (sharedPreferences.getBoolean("first_start", true)) {
            //初次启动调用欢迎框架
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        } else {
            //非初次启动检查数据获取方式是否为后台
            if (!sharedPreferences.getBoolean("backgroundService", true)) {
                Variables.updateThread = new Thread(new UpdateStatus(this));
                Variables.updateThread.start();
            } else {
                Intent service = new Intent(this, ServerStatusUpdateService.class);
                startService(service);
            }
        }

        //Fragment管理器与导航
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_server_list, R.id.navigation_about)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}