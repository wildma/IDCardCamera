package com.wildma.idcardcamera.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wildma.idcardcamera.R;
import com.wildma.idcardcamera.cropper.CropImageView;
import com.wildma.idcardcamera.cropper.CropListener;
import com.wildma.idcardcamera.global.Constant;
import com.wildma.idcardcamera.utils.FileUtils;
import com.wildma.idcardcamera.utils.ImageUtils;
import com.wildma.idcardcamera.utils.PermissionUtils;


/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2018/6/24
 * Desc	        ${拍照界面}
 */
public class CameraActivity extends Activity implements View.OnClickListener {

    public final static int    TYPE_IDCARD_FRONT      = 1;//身份证正面
    public final static int    TYPE_IDCARD_BACK       = 2;//身份证反面
    public final static int    REQUEST_CODE           = 0X11;//请求码
    public final static int    RESULT_CODE            = 0X12;//结果码
    public final static int    PERMISSION_CODE_SECOND = 0x13;//权限请求码
    public final static String TAKE_TYPE              = "take_type";//拍摄类型标记
    public final static String IMAGE_PATH             = "image_path";//图片路径标记
    public static int      mType;//拍摄类型
    public static Activity mActivity;
    private boolean isToast = true;//是否弹吐司，为了保证for循环只弹一次

    private CropImageView mCropImageView;
    private Bitmap        mCropBitmap;
    private CameraPreview mCameraPreview;
    private View          mLlCameraCropContainer;
    private ImageView     mIvCameraCrop;
    private ImageView     mIvCameraFlash;
    private View          mLlCameraOption;
    private View          mLlCameraResult;
    private TextView      mViewCameraCropBottom;

    /**
     * 跳转到拍照界面
     *
     * @param activity
     * @param type     拍摄类型
     */
    public static void toCameraActivity(Activity activity, int type) {
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra(TAKE_TYPE, type);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * 获取图片路径
     *
     * @param data
     * @return
     */
    public static String getImagePath(Intent data) {
        if (data != null) {
            return data.getStringExtra(IMAGE_PATH);
        }
        return "";
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*动态请求需要的权限*/
        boolean checkPermissionSecond = PermissionUtils.checkPermissionFirst(this, PERMISSION_CODE_SECOND,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA});
        if (checkPermissionSecond) {
            init();
        }
    }

    /**
     * 处理请求权限的响应
     *
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 请求权限结果数组
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissions = true;
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isPermissions = false;
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) { //用户选择了"不再询问"
                    if (isToast) {
                        Toast.makeText(this, "请手动打开该应用需要的权限", Toast.LENGTH_SHORT).show();
                        isToast = false;
                    }
                }
            }
        }
        isToast = true;
        if (isPermissions) {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "允许所有权限");
            init();
        } else {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "有权限不允许");
            finish();
        }
    }

    private void init() {
        setContentView(R.layout.activity_camera);
        mType = getIntent().getIntExtra(TAKE_TYPE, 0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        initView();
        initListener();
    }

    private void initView() {
        mCameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        mLlCameraCropContainer = findViewById(R.id.ll_camera_crop_container);
        mIvCameraCrop = (ImageView) findViewById(R.id.iv_camera_crop);
        mIvCameraFlash = (ImageView) findViewById(R.id.iv_camera_flash);
        mLlCameraOption = findViewById(R.id.ll_camera_option);
        mLlCameraResult = findViewById(R.id.ll_camera_result);
        mCropImageView = findViewById(R.id.crop_image_view);
        mViewCameraCropBottom = (TextView) findViewById(R.id.view_camera_crop_bottom);

        //获取屏幕最小边，设置为cameraPreview较窄的一边
        float screenMinSize = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        //根据screenMinSize，计算出cameraPreview的较宽的一边，长宽比为标准的16:9
        float maxSize = screenMinSize / 9.0f * 16.0f;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) maxSize, (int) screenMinSize);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mCameraPreview.setLayoutParams(layoutParams);

        float height = (int) (screenMinSize * 0.75);
        float width = (int) (height * 75.0f / 47.0f);
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams((int) width, ViewGroup.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams cropParams = new LinearLayout.LayoutParams((int) width, (int) height);
        mLlCameraCropContainer.setLayoutParams(containerParams);
        mIvCameraCrop.setLayoutParams(cropParams);

        switch (mType) {
            case TYPE_IDCARD_FRONT:
                mIvCameraCrop.setImageResource(R.mipmap.camera_idcard_front);
                break;
            case TYPE_IDCARD_BACK:
                mIvCameraCrop.setImageResource(R.mipmap.camera_idcard_back);
                break;
        }
    }

    private void initListener() {
        mCameraPreview.setOnClickListener(this);
        mIvCameraFlash.setOnClickListener(this);
        findViewById(R.id.iv_camera_close).setOnClickListener(this);
        findViewById(R.id.iv_camera_take).setOnClickListener(this);
        findViewById(R.id.iv_camera_result_ok).setOnClickListener(this);
        findViewById(R.id.iv_camera_result_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.camera_preview) {
            mCameraPreview.focus();
        } else if (id == R.id.iv_camera_close) {
            finish();
        } else if (id == R.id.iv_camera_take) {
            takePhoto();
        } else if (id == R.id.iv_camera_flash) {
            boolean isFlashOn = mCameraPreview.switchFlashLight();
            mIvCameraFlash.setImageResource(isFlashOn ? R.mipmap.camera_flash_on : R.mipmap.camera_flash_off);
        } else if (id == R.id.iv_camera_result_ok) {
            confirm();
        } else if (id == R.id.iv_camera_result_cancel) {
            mCameraPreview.setEnabled(true);
            mCameraPreview.startPreview();
            mIvCameraFlash.setImageResource(R.mipmap.camera_flash_off);
            setTakePhotoLayout();
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        mCameraPreview.setEnabled(false);
        mCameraPreview.takePhoto(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {
                camera.stopPreview();
                //子线程处理图片，防止ANR
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                        /*计算裁剪位置*/
                        float left, top, right, bottom;
                        left = ((float) mLlCameraCropContainer.getLeft() - (float) mCameraPreview.getLeft()) / (float) mCameraPreview.getWidth();
                        top = (float) mIvCameraCrop.getTop() / (float) mCameraPreview.getHeight();
                        right = (float) mLlCameraCropContainer.getRight() / (float) mCameraPreview.getWidth();
                        bottom = (float) mIvCameraCrop.getBottom() / (float) mCameraPreview.getHeight();

                        /*自动裁剪*/
                        mCropBitmap = Bitmap.createBitmap(bitmap,
                                (int) (left * (float) bitmap.getWidth()),
                                (int) (top * (float) bitmap.getHeight()),
                                (int) ((right - left) * (float) bitmap.getWidth()),
                                (int) ((bottom - top) * (float) bitmap.getHeight()));

                        /*手动裁剪*/
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //将裁剪区域设置成与扫描框一样大
                                mCropImageView.setLayoutParams(new LinearLayout.LayoutParams(mIvCameraCrop.getWidth(), mIvCameraCrop.getHeight()));
                                setCropLayout();
                                mCropImageView.setImageBitmap(mCropBitmap);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    /**
     * 设置裁剪布局
     */
    private void setCropLayout() {
        mIvCameraCrop.setVisibility(View.GONE);
        mCameraPreview.setVisibility(View.GONE);
        mLlCameraOption.setVisibility(View.GONE);
        mCropImageView.setVisibility(View.VISIBLE);
        mLlCameraResult.setVisibility(View.VISIBLE);
        mViewCameraCropBottom.setText("");
    }

    /**
     * 设置拍照布局
     */
    private void setTakePhotoLayout() {
        mIvCameraCrop.setVisibility(View.VISIBLE);
        mCameraPreview.setVisibility(View.VISIBLE);
        mLlCameraOption.setVisibility(View.VISIBLE);
        mCropImageView.setVisibility(View.GONE);
        mLlCameraResult.setVisibility(View.GONE);
        mViewCameraCropBottom.setText(getString(R.string.touch_to_focus));

        mCameraPreview.focus();
    }

    /**
     * 点击确认，返回图片路径
     */
    private void confirm() {
        /*裁剪图片*/
        mCropImageView.crop(new CropListener() {
            @Override
            public void onFinish(Bitmap bitmap) {

                /*保存图片到sdcard并返回图片路径*/
                if (FileUtils.createOrExistsDir(Constant.DIR_ROOT)) {
                    StringBuffer buffer = new StringBuffer();
                    String imagePath = "";
                    if (mType == TYPE_IDCARD_FRONT) {
                        imagePath = buffer.append(Constant.DIR_ROOT).append(Constant.APP_NAME).append(".").append("idCardFrontCrop.jpg").toString();
                    } else if (mType == TYPE_IDCARD_BACK) {
                        imagePath = buffer.append(Constant.DIR_ROOT).append(Constant.APP_NAME).append(".").append("idCardBackCrop.jpg").toString();
                    }

                    if (ImageUtils.save(bitmap, imagePath, Bitmap.CompressFormat.JPEG)) {
                        Intent intent = new Intent();
                        intent.putExtra(CameraActivity.IMAGE_PATH, imagePath);
                        setResult(RESULT_CODE, intent);
                        finish();
                    }
                }
            }
        }, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraPreview != null) {
            mCameraPreview.onStart();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraPreview != null) {
            mCameraPreview.onStop();
        }
    }
}