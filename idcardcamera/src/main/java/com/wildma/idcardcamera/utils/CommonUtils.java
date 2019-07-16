package com.wildma.idcardcamera.utils;

/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2019/07/16
 * Desc	        ${公用工具类}
 */
public class CommonUtils {

    private static long lastClickTime;

    /**
     * 判断是否是快速点击
     *
     * @return true：是，false：否
     */
    public static boolean isFastClick() {
        return isFastClick(1000);
    }

    /**
     * 判断是否是快速点击
     *
     * @param intervalTime 间隔时间，单位毫秒。
     * @return true：是，false：否
     */
    public static boolean isFastClick(long intervalTime) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < intervalTime) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
