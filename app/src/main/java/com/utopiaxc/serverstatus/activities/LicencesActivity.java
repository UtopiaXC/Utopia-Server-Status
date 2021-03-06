package com.utopiaxc.serverstatus.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.utopiaxc.serverstatus.Beans.LicensesBean;
import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.adapters.LicenseAdapter;
import com.utopiaxc.serverstatus.databinding.ActivityLicencesBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 开源许可证Activity
 *
 * @author UtopiaXC
 * @since 2022-05-22 23:13:06
 */
public class LicencesActivity extends AppCompatActivity {

    /**
     * 开源许可证Activity创建函数
     *
     * @param savedInstanceState 参数
     * @author UtopiaXC
     * @since 2022-05-22 23:13:32
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置UI
        ActivityLicencesBinding binding = ActivityLicencesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Context context = this;
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        List<LicensesBean> list = new ArrayList<>();
        //Androidx库
        list.add(new LicensesBean(R.drawable.license_androidx,
                "Android Jetpack Androidx",
                "Google",
                "https://github.com/androidx/androidx/blob/androidx-main/LICENSE.txt"));
        //Junit单元测试
        list.add(new LicensesBean(R.drawable.license_junit,
                "JUnit",
                "junit-team",
                "https://github.com/junit-team/junit4/blob/main/LICENSE-junit.txt"));
        //AppIntro开屏引导
        list.add(new LicensesBean(R.drawable.license_appintro,
                "AppIntro",
                "AppIntro Team",
                "https://github.com/AppIntro/AppIntro/blob/main/LICENSE"));
        //MD图标
        list.add(new LicensesBean(R.drawable.license_mikepenz,
                "MaterialDrawer",
                "mikepenz",
                "https://github.com/mikepenz/Android-Iconics/blob/develop/LICENSE"));
        //Fastjson解析工具
        list.add(new LicensesBean(R.drawable.license_fastjson,
                "Fastjson",
                "alibaba",
                "https://github.com/alibaba/fastjson/blob/master/license.txt"));
        //列表适配器工具
        list.add(new LicensesBean(R.drawable.license_base_recycler_view_adapter_helper,
                "Base Recycler View Adapter Helper",
                "CymChad",
                "https://github.com/CymChad/BaseRecyclerViewAdapterHelper/blob/master/LICENSE"));
        //图表工具
        list.add(new LicensesBean(R.drawable.license_mp_android_chart,
                "MPAndroidChart",
                "PhilJay",
                "https://github.com/CymChad/BaseRecyclerViewAdapterHelper/blob/master/LICENSE"));

        LicenseAdapter licenseAdapter = new LicenseAdapter(list);
        licenseAdapter.registerItemClickID();
        //添加监听跳转到对应开源许可证地址
        licenseAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.cardLicense) {
                LicensesBean beanLicense = (LicensesBean) adapter.getItem(position);
                Uri uri = Uri.parse(beanLicense.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = binding.listLicenses;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(licenseAdapter);
    }

    /**
     * 返回键
     *
     * @param item 点击对象
     * @return boolean
     * @author UtopiaXC
     * @since 2022-05-23 23:31:43
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