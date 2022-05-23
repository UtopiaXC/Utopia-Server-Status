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
 * <p>
 *
 * @author UtopiaXC
 * @since 2022-05-23 17:45
 */
public class ServerCardAdapter extends BaseQuickAdapter<ServerCardBean, BaseViewHolder> {
    public ServerCardAdapter(@Nullable List<ServerCardBean> data) {
        super(R.layout.card_server, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ServerCardBean serverCardBean) {
        baseViewHolder.setImageResource(R.id.regionFlag, serverCardBean.getRegionFlag());
        baseViewHolder.setText(R.id.serverName, serverCardBean.getServerName());
        baseViewHolder.setText(R.id.serverType, serverCardBean.getServerType());
        baseViewHolder.setText(R.id.serverLoad, getContext().getString(R.string.server_load_title) + serverCardBean.getServerLoad());
        ProgressBar progressBar = baseViewHolder.getView(R.id.progressLoad);
        progressBar.setProgress((int) (serverCardBean.getServerLoad() * 100));
    }
}
