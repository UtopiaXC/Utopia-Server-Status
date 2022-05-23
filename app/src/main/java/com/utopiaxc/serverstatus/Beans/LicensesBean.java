package com.utopiaxc.serverstatus.Beans;

/**
 * 开源许可证卡片数据
 *
 * @author UtopiaXC
 * @since 2022-05-23 21:09
 */
public class LicensesBean {
    Integer resourceId;
    String title;
    String subTitle;
    String link;

    public LicensesBean(Integer resourceId, String title, String subTitle, String link) {
        this.resourceId = resourceId;
        this.title = title;
        this.subTitle = subTitle;
        this.link = link;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
