package com.utopiaxc.serverstatus.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.utopiaxc.serverstatus.Beans.HomePageBean;
import com.utopiaxc.serverstatus.R;

import java.util.List;

/**
 * <p>
 *
 * @author UtopiaXC
 * @since 2022-05-23 13:33
 */
public class HomePageAdapter extends BaseQuickAdapter<HomePageBean, BaseViewHolder> {
    public HomePageAdapter(@Nullable List<HomePageBean> data) {
        super(R.layout.card_home_page, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, HomePageBean homePageBean) {
        baseViewHolder.setText(R.id.homePageCardTitle, homePageBean.getTitle());
        baseViewHolder.setText(R.id.homePageCardCount, String.valueOf(homePageBean.getSubTitle()));
        baseViewHolder.setImageResource(R.id.homePageCardIcon, homePageBean.getIconId());
    }
}
