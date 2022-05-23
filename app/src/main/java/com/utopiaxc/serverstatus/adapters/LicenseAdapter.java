package com.utopiaxc.serverstatus.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.utopiaxc.serverstatus.Beans.LicensesBean;
import com.utopiaxc.serverstatus.R;

import java.util.List;

/**
 * 开源许可证适配器
 *
 * @author UtopiaXC
 * @since 2022-05-23 21:15
 */
public class LicenseAdapter extends BaseQuickAdapter<LicensesBean, BaseViewHolder> {
    public LicenseAdapter(@Nullable List<LicensesBean> data) {
        super(R.layout.card_license, data);
    }

    /**
     * 适配器数据绑定
     *
     * @author UtopiaXC
     * @since 2022-05-23 23:27:41
     * @param baseViewHolder 数据设置器
     * @param licensesBean 数据实体
     */
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, LicensesBean licensesBean) {
        baseViewHolder.setText(R.id.licenseTitle, licensesBean.getTitle());
        baseViewHolder.setText(R.id.licenseSubTitle, licensesBean.getSubTitle());
        baseViewHolder.setImageResource(R.id.licenseImage, licensesBean.getResourceId());
    }

    /**
     * 注册卡片监听ID
     *
     * @author UtopiaXC
     * @since 2022-05-23 23:27:23
     */
    public void registerItemClickID() {
        this.addChildClickViewIds(R.id.cardLicense);
    }
}
