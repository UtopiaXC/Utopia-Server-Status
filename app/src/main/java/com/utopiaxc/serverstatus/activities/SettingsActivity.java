package com.utopiaxc.serverstatus.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.services.ServerStatusUpdateService;
import com.utopiaxc.serverstatus.utils.ThemeUtil;
import com.utopiaxc.serverstatus.utils.UpdateStatus;
import com.utopiaxc.serverstatus.utils.Variables;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            ListPreference listPreference = findPreference("theme");
            Objects.requireNonNull(listPreference).setOnPreferenceChangeListener((preference, newValue) -> {
                ThemeUtil.setThemeMode(newValue.toString());
                return true;
            });

            SwitchPreferenceCompat switchPreferenceCompat = findPreference("backgroundService");
            Objects.requireNonNull(switchPreferenceCompat).setOnPreferenceChangeListener((preference, newValue) -> {
                Intent service = new Intent(Variables.context, ServerStatusUpdateService.class);
                if ((boolean) newValue) {
                    Variables.updateThread.interrupt();
                    Variables.context.startService(service);
                } else {
                    Variables.updateThread = new Thread(new UpdateStatus(Variables.context));
                    Variables.updateThread.start();
                    Variables.context.stopService(service);
                }
                return true;
            });
        }
    }
}