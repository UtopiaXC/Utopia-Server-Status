package com.utopiaxc.serverstatus.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.danielstone.materialaboutlibrary.ConvenienceBuilder;
import com.danielstone.materialaboutlibrary.MaterialAboutFragment;
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem;
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard;
import com.danielstone.materialaboutlibrary.model.MaterialAboutList;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.utopiaxc.serverstatus.R;
import com.utopiaxc.serverstatus.activities.LicencesActivity;
import com.utopiaxc.serverstatus.activities.SettingsActivity;
import com.utopiaxc.serverstatus.database.model.NotificationBean;
import com.utopiaxc.serverstatus.database.model.ServerBean;
import com.utopiaxc.serverstatus.database.model.StatusBean;
import com.utopiaxc.serverstatus.utils.Constants;
import com.utopiaxc.serverstatus.utils.Variables;

import java.util.List;

/**
 * 关于页面Fragment
 *
 * @author UtopiaXC
 * @since 2022-05-22 22:53:08
 */
public class AboutFragment extends MaterialAboutFragment {
    /**
     * 设置关于卡片
     *
     * @param activityContext 调用活动上下文
     * @return com.danielstone.materialaboutlibrary.model.MaterialAboutList
     * @author UtopiaXC
     * @since 2022-05-22 22:53:33
     */
    @Override
    protected MaterialAboutList getMaterialAboutList(final Context activityContext) {
        //获取卡片构造器
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();

        //添加版权卡片
        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(R.string.app_name)
                .desc(R.string.rights)
                .icon(R.mipmap.ic_launcher)
                .build());

        //添加版本卡片
        appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(activityContext,
                new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_code_array)
                        .sizeDp(18),
                requireActivity().getString(R.string.version),
                true));

        //添加更新内容卡片
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.changelog)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_content_paste)
                        .sizeDp(18))
                .setOnClickAction(ConvenienceBuilder.createWebViewDialogOnClickAction(activityContext, requireActivity().getString(R.string.changelog_title), Constants.GITHUB_RELEASE_URL, true, false))
                .build());

        //添加开源许可证卡片
        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.licenses)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_code_tags)
                        .sizeDp(18))
                .setOnClickAction(() -> {
                    Intent intent = new Intent(activityContext, LicencesActivity.class);
                    startActivity(intent);
                })
                .build());

        //获取卡片构造器
        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title(R.string.author);

        //添加作者卡片
        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(Constants.AUTHOR_NAME)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_account)
                        .sizeDp(18))
                .build());

        //添加GitHub卡片
        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.follow_on_github)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_github_circle)
                        .sizeDp(18))
                .setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(activityContext, Uri.parse(Constants.GITHUB_URL)))
                .build());

        //添加GitLab卡片
        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.follow_on_gitlab)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_gitlab)
                        .sizeDp(18))
                .setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(activityContext, Uri.parse(Constants.GITLAB_URL)))
                .build());

        //添加反馈邮件卡片
        authorCardBuilder.addItem(ConvenienceBuilder.createEmailItem(activityContext,
                new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_email)
                        .sizeDp(18),
                requireActivity().getString(R.string.feedback_by_email),
                true,
                Constants.AUTHOR_EMAIL,
                requireActivity().getString(R.string.feedback_subject)));


        //获取卡片构造器
        MaterialAboutCard.Builder convenienceCardBuilder = new MaterialAboutCard.Builder();
        convenienceCardBuilder.title(R.string.settings);

        //添加设置卡片
        convenienceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.settings)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_cellphone_settings_variant)
                        .sizeDp(18))
                .setOnClickAction(() -> {
                    Intent intent = new Intent(requireActivity(), SettingsActivity.class);
                    startActivity(intent);
                })
                .build());

        //清空数据缓存
        convenienceCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.clear_cache)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_delete)
                        .sizeDp(18))
                .setOnClickAction(() -> {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.warning)
                            .setMessage(R.string.cache_clear_confirm)
                            .setPositiveButton(R.string.confirm, (dialogInterface, i) -> {
                                new Thread(() -> {
                                    //删除全部数据库数据
                                    List<ServerBean> serverBeans = Variables.database.serverDao().getAll();
                                    Variables.database.serverDao().deleteServer(serverBeans.toArray(new ServerBean[0]));
                                    List<StatusBean> statusBeans = Variables.database.statusDao().getAll();
                                    Variables.database.statusDao().deleteStatus(statusBeans.toArray(new StatusBean[0]));
                                    List<NotificationBean> notificationBeans = Variables.database.notificationDao().getAll();
                                    Variables.database.notificationDao().deleteNotifications(notificationBeans.toArray(new NotificationBean[0]));
                                }).start();
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .create()
                            .show();

                })
                .build());


        return new MaterialAboutList(appCardBuilder.build(), authorCardBuilder.build(), convenienceCardBuilder.build());
    }

    /**
     * 关于页面Fragment销毁
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:56:41
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}