package com.utopiaxc.serverstatus.utils;

/**
 * @author UtopiaXC
 * @date 2022-05-22 14:31
 */
public class Constants {
    /**
     * GitHub地址
     */
    public static final String GITHUB_URL = "https://github.com/UtopiaXC/Utopia-Server-Status";
    /**
     * GitHub发布地址
     */
    public static final String GITHUB_RELEASE_URL = "https://github.com/UtopiaXC/Utopia-Server-Status/releases";
    /**
     * GitLab地址
     */
    public static final String GITLAB_URL = "https://git.utopiaxc.cn/UtopiaXC/utopia-server-status";
    /**
     * 作者名
     */
    public static final String AUTHOR_NAME = "UtopiaXC";
    /**
     * 作者邮箱
     */
    public static final String AUTHOR_EMAIL = "utopiaxc@utopiaxc.com";

    /**
     * 主题枚举
     * <p>用于保存主题映射
     *
     * @author UtopiaXC
     * @since 2022-05-22 22:21:48
     */
    public enum ThemeModeEnum {
        /**
         * 自动
         */
        AUTO_MODE("auto", "Auto Mode"),
        /**
         * 日间主题
         */
        DAY_MODE("day", "Day Mode"),
        /**
         * 夜间主题
         */
        NIGHT_MODE("night", "Night Mode");

        private final String mode;
        private final String description;

        /**
         * 主题枚举构造函数
         * <p>用于设置枚举的值与描述
         *
         * @param mode        主题枚举值
         * @param description 主题枚举描述
         * @author UtopiaXC
         * @since 2022-05-22 22:22:33
         */
        ThemeModeEnum(String mode, String description) {
            this.mode = mode;
            this.description = description;
        }

        /**
         * 获取主题枚举值
         *
         * @return java.lang.String
         * @author UtopiaXC
         * @since 2022-05-22 22:23:16
         */
        public String getMode() {
            return mode;
        }

        /**
         * 获取主题枚举描述
         *
         * @return java.lang.String
         * @author UtopiaXC
         * @since 2022-05-22 22:23:16
         */
        public String getDescription() {
            return description;
        }
    }


}
