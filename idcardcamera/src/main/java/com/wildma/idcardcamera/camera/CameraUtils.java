package com.wildma.idcardcamera.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;

/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2018/6/24
 * Desc	        ${相机工具类}
 */
public class CameraUtils {
    /**
     * 检查是否有相机
     *
     * @param context
     * @return
     */
    public static boolean hasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * 打开相机
     *
     * @return
     */
    public static Camera openCamera() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
