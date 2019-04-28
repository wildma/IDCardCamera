package com.wildma.wildmaidcardcamera;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.wildma.idcardcamera.camera.IDCardCamera;

/**
 * Author   wildma
 * Github   https://github.com/wildma
 * Date     2018/6/24
 * Desc     ${身份证相机使用例子}
 */
public class MainActivity extends AppCompatActivity {
    private ImageView mIvFront;
    private ImageView mIvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIvFront = (ImageView) findViewById(R.id.iv_front);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
    }

    /**
     * 身份证正面
     */
    public void front(View view) {
        IDCardCamera.create(this).openCamera(IDCardCamera.TYPE_IDCARD_FRONT);
    }

    /**
     * 身份证反面
     */
    public void back(View view) {
        IDCardCamera.create(this).openCamera(IDCardCamera.TYPE_IDCARD_BACK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == IDCardCamera.RESULT_CODE) {
            //获取图片路径，显示图片
            final String path = IDCardCamera.getImagePath(data);
            if (!TextUtils.isEmpty(path)) {
                if (requestCode == IDCardCamera.TYPE_IDCARD_FRONT) { //身份证正面
                    mIvFront.setImageBitmap(BitmapFactory.decodeFile(path));
                } else if (requestCode == IDCardCamera.TYPE_IDCARD_BACK) {  //身份证反面
                    mIvBack.setImageBitmap(BitmapFactory.decodeFile(path));
                }
            }
        }
    }
}
