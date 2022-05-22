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
import com.utopiaxc.serverstatus.utils.Constants;
import com.utopiaxc.serverstatus.utils.ThemeUtil;
import com.utopiaxc.serverstatus.utils.UpdateStatus;
import com.utopiaxc.serverstatus.utils.Variables;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ThemeUtil.setThemeMode(sharedPreferences.getString("theme", "auto"));
        Variables.context = this;
        new Thread(() -> {
            Variables.database = Room.databaseBuilder(getApplicationContext(), DatabaseHelper.class, "uss").build();
        }).start();
        com.utopiaxc.serverstatus.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (sharedPreferences.getBoolean("first_start", true)) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (!sharedPreferences.getBoolean("backgroundService", true)) {
                Variables.updateThread = new Thread(new UpdateStatus(this));
                Variables.updateThread.start();
            } else {
                Intent service = new Intent(this, ServerStatusUpdateService.class);
                startService(service);
            }
        }
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_server_list, R.id.navigation_about)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }
}