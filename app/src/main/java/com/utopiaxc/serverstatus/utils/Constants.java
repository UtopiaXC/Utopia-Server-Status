package com.utopiaxc.serverstatus.utils;

/**
 * @author UtopiaXC
 * @date 2022-05-22 14:31
 */
public class Constants {
    public static final String GITHUB_URL = "https://github.com/UtopiaXC/Utopia-Server-Status";
    public static final String GITHUB_RELEASE_URL = "https://github.com/UtopiaXC/Utopia-Server-Status/releases";
    public static final String GITLAB_URL = "https://git.utopiaxc.cn/UtopiaXC/utopia-server-status";
    public static final String AUTHOR_NAME = "UtopiaXC";
    public static final String AUTHOR_EMAIL = "utopiaxc@utopiaxc.com";

    public enum ThemeModeEnum {
        AUTO_MODE("auto", "Auto Mode"),
        DAY_MODE("day", "Day Mode"),
        NIGHT_MODE("night", "Night Mode");

        private String mode;
        private String description;


        ThemeModeEnum(String mode, String description) {
            this.mode = mode;
            this.description = description;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }


}
