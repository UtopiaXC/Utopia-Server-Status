package com.utopiaxc.serverstatus.fragments;

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
import com.utopiaxc.serverstatus.utils.Constants;

public class AboutFragment extends MaterialAboutFragment {

    @Override
    protected MaterialAboutList getMaterialAboutList(final Context activityContext) {
        MaterialAboutCard.Builder appCardBuilder = new MaterialAboutCard.Builder();

        appCardBuilder.addItem(new MaterialAboutTitleItem.Builder()
                .text(R.string.app_name)
                .desc(R.string.rights)
                .icon(R.mipmap.ic_launcher)
                .build());

        appCardBuilder.addItem(ConvenienceBuilder.createVersionActionItem(activityContext,
                new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_code_array)
                        .sizeDp(18),
                requireActivity().getString(R.string.version),
                true));

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.changelog)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_content_paste)
                        .sizeDp(18))
                .setOnClickAction(ConvenienceBuilder.createWebViewDialogOnClickAction(activityContext, requireActivity().getString(R.string.changelog_title), Constants.GITHUB_RELEASE_URL, true, false))
                .build());

        appCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.licenses)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_book)
                        .sizeDp(18))
                .setOnClickAction(() -> {
                    Intent intent = new Intent(activityContext, LicencesActivity.class);
                    startActivity(intent);
                })
                .build());

        MaterialAboutCard.Builder authorCardBuilder = new MaterialAboutCard.Builder();
        authorCardBuilder.title(R.string.author);

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(Constants.AUTHOR_NAME)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_account)
                        .sizeDp(18))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.follow_on_github)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_github_circle)
                        .sizeDp(18))
                .setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(activityContext, Uri.parse(Constants.GITHUB_URL)))
                .build());

        authorCardBuilder.addItem(new MaterialAboutActionItem.Builder()
                .text(R.string.follow_on_gitlab)
                .icon(new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_gitlab)
                        .sizeDp(18))
                .setOnClickAction(ConvenienceBuilder.createWebsiteOnClickAction(activityContext, Uri.parse(Constants.GITLAB_URL)))
                .build());

        authorCardBuilder.addItem(ConvenienceBuilder.createEmailItem(activityContext,
                new IconicsDrawable(activityContext)
                        .icon(CommunityMaterial.Icon.cmd_email)
                        .sizeDp(18),
                requireActivity().getString(R.string.feedback_by_email),
                true,
                Constants.AUTHOR_EMAIL,
                requireActivity().getString(R.string.feedback_subject)));


        MaterialAboutCard.Builder convenienceCardBuilder = new MaterialAboutCard.Builder();
        convenienceCardBuilder.title(R.string.settings);
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


        return new MaterialAboutList(appCardBuilder.build(), authorCardBuilder.build(), convenienceCardBuilder.build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}