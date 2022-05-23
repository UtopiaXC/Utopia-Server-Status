package com.utopiaxc.serverstatus.Beans;

/**
 * 主页卡片数据
 *
 * @author UtopiaXC
 * @since 2022-05-23 13:34
 */
public class HomePageBean {
    int cardId;
    String title;
    int subTitle;
    int iconId;

    public HomePageBean(int cardId, String title, int subTitle, int iconId) {
        this.cardId = cardId;
        this.title = title;
        this.subTitle = subTitle;
        this.iconId = iconId;
    }

    public HomePageBean() {
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(int subTitle) {
        this.subTitle = subTitle;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
