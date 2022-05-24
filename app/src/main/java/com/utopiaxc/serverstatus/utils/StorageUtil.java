package com.utopiaxc.serverstatus.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * <p>
 *
 * @author UtopiaXC
 * @since 2022-05-24 13:04
 */
public class StorageUtil {
    public static String formatMbToString(int mb) {
        DecimalFormat df = new DecimalFormat("0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        if (mb < 1024.0) {
            return mb + "MB";
        } else if (mb < 1024.0 * 1024.0) {
            double mem = ((double) mb) / 1024.0;
            return df.format(mem) + "GB";
        } else {
            double mem = ((double) mb) / 1024.0 / 1024.0;
            return df.format(mem) + "TB";
        }
    }

    public static String formatByteToString(long b) {
        DecimalFormat df = new DecimalFormat("0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        if (b < 1024) {
            return b + "B";
        } else if (b < 1024.0 * 1024.0) {
            double mem = ((double) b) / 1024.0;
            return df.format(mem) + "KB";
        } else if (b < 1024.0 * 1024.0 * 1024.0) {
            double mem = ((double) b) / 1024.0 / 1024.0;
            return df.format(mem) + "MB";
        } else if (b < 1024.0 * 1024.0 * 1024.0 * 1024.0) {
            double mem = ((double) b) / 1024.0 / 1024.0 / 1024.0;
            return df.format(mem) + "GB";
        } else {
            double mem = ((double) b) / 1024.0 / 1024.0 / 1024.0 / 1024.0;
            return df.format(mem) + "TB";
        }
    }

    public static String formatKbToString(long kb) {
        DecimalFormat df = new DecimalFormat("0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        if (kb < 1024) {
            return kb + "KB";
        } else if (kb < 1024.0 * 1024.0) {
            double mem = ((double) kb) / 1024.0;
            return df.format(mem) + "MB";
        } else if (kb < 1024.0 * 1024.0 * 1024.0) {
            double mem = ((double) kb) / 1024.0 / 1024.0;
            return df.format(mem) + "GB";
        } else {
            double mem = ((double) kb) / 1024.0 / 1024.0 / 1024.0;
            return df.format(mem) + "TB";
        }
    }

    public static String formatByteSpeedToString(long b) {
        DecimalFormat df = new DecimalFormat("0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        if (b < 1024) {
            return b + "B/s";
        } else if (b < 1024.0 * 1024.0) {
            double mem = ((double) b) / 1024.0;
            return df.format(mem) + "KB/s";
        } else if (b < 1024.0 * 1024.0 * 1024.0) {
            double mem = ((double) b) / 1024.0 / 1024.0;
            return df.format(mem) + "MB/s";
        } else if (b < 1024.0 * 1024.0 * 1024.0 * 1024.0) {
            double mem = ((double) b) / 1024.0 / 1024.0 / 1024.0;
            return df.format(mem) + "GB/s";
        } else {
            double mem = ((double) b) / 1024.0 / 1024.0 / 1024.0 / 1024.0;
            return df.format(mem) + "TB/s";
        }
    }
}
