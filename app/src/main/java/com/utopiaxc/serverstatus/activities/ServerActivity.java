package com.utopiaxc.serverstatus.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.utopiaxc.serverstatus.databinding.ActivityServerBinding;

import java.util.Objects;

/**
 * 服务器详情Activity
 *
 * @author UtopiaXC
 * @since 2022-05-23 23:30:38
 */
public class ServerActivity extends AppCompatActivity {

    /**
     * Activity创建方法
     *
     * @param savedInstanceState 入参
     * @author UtopiaXC
     * @since 2022-05-23 23:30:52
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityServerBinding binding = ActivityServerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    /**
     * 返回键
     *
     * @param item 点击对象
     * @return boolean
     * @author UtopiaXC
     * @since 2022-05-23 23:31:12
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