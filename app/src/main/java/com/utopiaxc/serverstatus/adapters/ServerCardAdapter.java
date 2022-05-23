package com.utopiaxc.serverstatus.adapters;

import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.utopiaxc.serverstatus.Beans.ServerCardBean;
import com.utopiaxc.serverstatus.R;

import java.util.List;

/**
 * 服务器卡片适配器
 *
 * @author UtopiaXC
 * @since 2022-05-23 17:45
 */
public class ServerCardAdapter extends BaseQuickAdapter<ServerCardBean, BaseViewHolder> {
    public ServerCardAdapter(@Nullable List<ServerCardBean> data) {
        super(R.layout.card_server, data);
    }

    /**
     * 适配器数据绑定
     *
     * @param baseViewHolder 数据设置器
     * @param serverCardBean 数据实体
     * @author UtopiaXC
     * @since 2022-05-23 23:27:41
     */
    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ServerCardBean serverCardBean) {
        baseViewHolder.setImageResource(R.id.regionFlag, serverCardBean.getRegionFlag());
        baseViewHolder.setText(R.id.serverName, serverCardBean.getServerName());
        baseViewHolder.setText(R.id.serverType, serverCardBean.getServerType());
        if (serverCardBean.getServerLoad() != null && serverCardBean.getServerLoad() != null) {
            baseViewHolder.setText(R.id.serverLoad, getContext().getString(R.string.server_load_title) + serverCardBean.getServerLoad());
            ProgressBar progressBar = baseViewHolder.getView(R.id.progressLoad);
            progressBar.setProgress((int) (serverCardBean.getServerLoad() * 100));
        } else {
            baseViewHolder.setText(R.id.serverLoad, getContext().getString(R.string.home_offline));
            ProgressBar progressBar = baseViewHolder.getView(R.id.progressLoad);
            progressBar.setProgress(0);
        }
    }

    /**
     * 注册卡片监听ID
     *
     * @author UtopiaXC
     * @since 2022-05-23 23:27:23
     */
    public void registerItemClickID() {
        this.addChildClickViewIds(R.id.cardServer);
    }
}
