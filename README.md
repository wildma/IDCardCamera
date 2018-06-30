# WildmaIDCardCamera
Android自定义相机实现身份证拍照，并加入自动对焦与图片不规则裁剪

### 效果图：
![screenshot.jpg](https://github.com/wildma/WildmaIDCardCamera/blob/master/screenshots/screenshot.jpg)

### 功能特点
- 自定义相机界面
- 支持开启闪光灯
- 支持手动触摸屏幕对焦
- 支持自动对焦
- 支持图片自动裁剪
- 支持图片手动不规则裁剪

### 2 使用
使用非常简单，只需要简单的3步即可。
##### 1. 引用本地lib，即将idcardcamera作为module依赖到主module app中，如下：
```
compile project(':idcardcamera')
```
##### 2. 调用CameraActivity类的toCameraActivity方法打开拍照界面，如下：
```
CameraActivity.toCameraActivity(this, CameraActivity.TYPE_IDCARD_FRONT);
```

##### 3. onActivityResult方法中获取裁剪后的图片
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraActivity.REQUEST_CODE && resultCode == CameraActivity.RESULT_CODE) {
            //获取图片路径，显示图片
            final String path = CameraActivity.getImagePath(data);
            if (!TextUtils.isEmpty(path)) {
                imageView.setImageBitmap(BitmapFactory.decodeFile(path));
            }
        }
    }
```

详细介绍请看文章：[Android自定义相机实现身份证拍照，并加入自动对焦与图片不规则裁剪](https://www.jianshu.com/p/5e3cb0c63cd5)

ps:如果对你有帮助，点下star就是对我最大的认可。

### 感谢
- [CertificateCamera](https://github.com/smartown/CertificateCamera) 
- [PhotoPolygonCropper](https://github.com/leanh215/PhotoPolygonCropper)
- [SweetCamera](https://github.com/WellerV/SweetCamera)
