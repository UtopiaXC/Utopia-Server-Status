package com.utopiaxc.serverstatus.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.utopiaxc.serverstatus.Beans.LicensesBean;
import com.utopiaxc.serverstatus.R;

import java.util.List;

/**
 * <p>
 *
 * @author UtopiaXC
 * @since 2022-05-23 21:15
 */
public class LicenseAdapter extends BaseQuickAdapter<LicensesBean, BaseViewHolder> {
    public LicenseAdapter(@Nullable List<LicensesBean> data) {
        super(R.layout.card_license, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, LicensesBean licensesBean) {
        baseViewHolder.setText(R.id.licenseTitle, licensesBean.getTitle());
        baseViewHolder.setText(R.id.licenseSubTitle, licensesBean.getSubTitle());
        baseViewHolder.setImageResource(R.id.licenseImage, licensesBean.getResourceId());
    }

    public void registerItemClickID() {
        this.addChildClickViewIds(R.id.cardLicense);
    }
}
