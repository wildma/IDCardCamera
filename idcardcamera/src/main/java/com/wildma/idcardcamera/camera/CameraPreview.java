package com.wildma.idcardcamera.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wildma.idcardcamera.utils.ScreenUtils;

import java.util.List;

/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2018/6/24
 * Desc	        ${相机预览}
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static String TAG = CameraPreview.class.getName();

    private Camera           camera;
    private AutoFocusManager mAutoFocusManager;
    private SensorControler  mSensorControler;
    private Context          mContext;
    private SurfaceHolder    mSurfaceHolder;

    public CameraPreview(Context context) {
        super(context);
        init(context);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSensorControler = SensorControler.getInstance(context.getApplicationContext());
    }

    public void surfaceCreated(SurfaceHolder holder) {
        camera = CameraUtils.openCamera();
        if (camera != null) {
            try {
                camera.setPreviewDisplay(holder);

                Camera.Parameters parameters = camera.getParameters();
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //竖屏拍照时，需要设置旋转90度，否者看到的相机预览方向和界面方向不相同
                    camera.setDisplayOrientation(90);
                    parameters.setRotation(90);
                } else {
                    camera.setDisplayOrientation(0);
                    parameters.setRotation(0);
                }
                List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();//获取所有支持的预览大小
                Camera.Size bestSize = getOptimalPreviewSize(sizeList, ScreenUtils.getScreenWidth(mContext), ScreenUtils.getScreenHeight(mContext));
                parameters.setPreviewSize(bestSize.width, bestSize.height);//设置预览大小
                camera.setParameters(parameters);
                camera.startPreview();
                focus();//首次对焦
                //mAutoFocusManager = new AutoFocusManager(camera);//定时对焦
            } catch (Exception e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        //竖屏拍照时，需要设置旋转90度，否者看到的相机预览方向和界面方向不相同
                        camera.setDisplayOrientation(90);
                        parameters.setRotation(90);
                    } else {
                        camera.setDisplayOrientation(0);
                        parameters.setRotation(0);
                    }
                    camera.setParameters(parameters);
                    camera.startPreview();
                    focus();//首次对焦
                    //mAutoFocusManager = new AutoFocusManager(camera);//定时对焦
                } catch (Exception e1) {
                    e.printStackTrace();
                    camera = null;
                }
            }
        }
    }

    /**
     * 获取最佳预览大小
     *
     * @param sizes 所有支持的预览大小
     * @param w     SurfaceView宽
     * @param h     SurfaceView高
     * @return
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        //因为设置了固定屏幕方向，所以在实际使用中不会触发这个方法
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        holder.removeCallback(this);
        //回收释放资源
        release();
    }

    /**
     * 释放资源
     */
    private void release() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;

            if (mAutoFocusManager != null) {
                mAutoFocusManager.stop();
                mAutoFocusManager = null;
            }
        }
    }

    /**
     * 对焦，在CameraActivity中触摸对焦或者自动对焦
     */
    public void focus() {
        if (camera != null) {
            try {
                camera.autoFocus(null);
            } catch (Exception e) {
                Log.d(TAG, "takePhoto " + e);
            }
        }
    }

    /**
     * 开关闪光灯
     *
     * @return 闪光灯是否开启
     */
    public boolean switchFlashLight() {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            if (parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(parameters);
                return true;
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                return false;
            }
        }
        return false;
    }

    /**
     * 拍摄照片
     *
     * @param pictureCallback 在pictureCallback处理拍照回调
     */
    public void takePhoto(Camera.PictureCallback pictureCallback) {
        if (camera != null) {
            try {
                camera.takePicture(null, null, pictureCallback);
            } catch (Exception e) {
                Log.d(TAG, "takePhoto " + e);
            }
        }
    }

    public void startPreview() {
        if (camera != null) {
            camera.startPreview();
        }
    }

    public void onStart() {
        addCallback();
        if (mSensorControler != null) {
            mSensorControler.onStart();
            mSensorControler.setCameraFocusListener(new SensorControler.CameraFocusListener() {
                @Override
                public void onFocus() {
                    focus();
                }
            });
        }
    }

    public void onStop() {
        if (mSensorControler != null) {
            mSensorControler.onStop();
        }
    }

    public void addCallback() {
        if (mSurfaceHolder != null) {
            mSurfaceHolder.addCallback(this);
        }
    }
}
