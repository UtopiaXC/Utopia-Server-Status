package com.utopiaxc.serverstatus.utils;

import com.utopiaxc.serverstatus.R;

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

    /**
     * 地区旗帜映射
     *
     * @author UtopiaXC
     * @since 2022-05-23 20:09:22
     */
    public enum RegionFlagEnum {
        /**
         * 美国国旗
         */
        FLAG_AMERICA("US", R.drawable.flag_america, "America"),
        /**
         * 阿根廷国旗
         */
        FLAG_ARGENTINA("AR", R.drawable.flag_argentina, "Argentina"),
        /**
         * 澳大利亚国旗
         */
        FLAG_AUSTRALIA("AU", R.drawable.flag_australia, "Australia"),
        /**
         * 巴西国旗
         */
        FLAG_BRAZIL("BR", R.drawable.flag_brazil, "Brazil"),
        /**
         * 英国国旗
         */
        FLAG_BRITAIN("GB", R.drawable.flag_britain, "Great Britain"),
        /**
         * 加拿大国旗
         */
        FLAG_CANADA("CA", R.drawable.flag_canada, "Canada"),
        /**
         * 中国国旗
         */
        FLAG_CHINA("CN", R.drawable.flag_china, "China"),
        /**
         * 法国国旗
         */
        FLAG_FRANCE("FR", R.drawable.flag_france, "France"),
        /**
         * 德国国旗
         */
        FLAG_GERMANY("DE", R.drawable.flag_germany, "Germany"),
        /**
         * 香港区旗
         */
        FLAG_HK("HK", R.drawable.flag_hk, "Hong Kong"),
        /**
         * 印度国旗
         */
        FLAG_INDIA("IN", R.drawable.flag_india, "India"),
        /**
         * 意大利国旗
         */
        FLAG_ITALY("IT", R.drawable.flag_italy, "Italy"),
        /**
         * 日本国旗
         */
        FLAG_JAPAN("JP", R.drawable.flag_japan, "Japan"),
        /**
         * 南韩国旗
         */
        FLAG_KOREA("KR", R.drawable.flag_korea, "South Korea"),
        /**
         * 澳门区旗
         */
        FLAG_MACAO("MO", R.drawable.flag_macao, "Macao"),
        /**
         * 挪威国旗
         */
        FLAG_NORWAY("NO", R.drawable.flag_norway, "Norway"),
        /**
         * 新加坡国旗
         */
        FLAG_SINGAPORE("SG", R.drawable.flag_singapore, "Singapore"),
        /**
         * 瑞士国旗
         */
        FLAG_SWITZERLAND("CH", R.drawable.flag_switzerland, "Switzerland"),
        /**
         * 土耳其国旗
         */
        FLAG_TURKEY("TR", R.drawable.flag_turkey, "Turkey"),
        /**
         * 越南国旗
         */
        FLAG_VIETNAM("VN", R.drawable.flag_vietnam, "Vietnam"),
        /**
         * 联合国旗
         */
        FLAG_UN("UN", R.drawable.flag_un, "United Nations");

        private final String key;
        private final int sourceId;
        private final String description;

        /**
         * 枚举构造
         *
         * @param key         地区标识
         * @param sourceId    图片资源
         * @param description 描述
         * @author UtopiaXC
         * @since 2022-05-23 23:21:05
         */
        RegionFlagEnum(String key, int sourceId, String description) {
            this.key = key;
            this.sourceId = sourceId;
            this.description = description;
        }


        public String getKey() {
            return key;
        }

        public int getSourceId() {
            return sourceId;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 通过地区标识获取枚举对象
         *
         * @param key 地区标识
         * @return com.utopiaxc.serverstatus.utils.Constants.RegionFlagEnum
         * @author UtopiaXC
         * @since 2022-05-23 23:21:42
         */
        public static RegionFlagEnum getByKey(String key) {
            for (RegionFlagEnum regionFlagEnum : RegionFlagEnum.values()) {
                if (regionFlagEnum.key.equals(key)) {
                    return regionFlagEnum;
                }
            }
            //如不在列表中则返回联合国旗
            return FLAG_UN;
        }
    }

    /**
     * 主页类型枚举
     *
     * @author UtopiaXC
     * @since 2022-05-23 23:22:24
     */
    public enum CardFlag {NORMAL, OFFLINE, OVERLOAD, CPU_OVERLOAD, MEMORY_OVERLOAD, DISK_OVERLOAD}
}
