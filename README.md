# IDCardCamera
Android 自定义身份证相机

[![jitpack](https://jitpack.io/v/wildma/IDCardCamera.svg)](https://jitpack.io/#wildma/IDCardCamera)
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![](https://img.shields.io/badge/License-Apache--2.0-brightgreen.svg)](https://github.com/wildma/IDCardCamera/blob/master/LICENSE)
[![](https://img.shields.io/badge/Author-wildma-7AD6FD.svg)](https://github.com/wildma)

## [README of English](https://github.com/wildma/IDCardCamera/blob/master/README_EN.md)

## 效果图
![身份证来源网络虚拟构造](https://github.com/wildma/IDCardCamera/blob/master/screenshots/screenshot.jpg)

## APK
[点击下载APK](https://github.com/wildma/IDCardCamera/raw/master/apk/com.wildma.idcardcamera-1.1.1.apk)

## 功能特点
- 自定义相机界面
- 支持开启闪光灯
- 支持手动触摸屏幕对焦
- 支持自动对焦
- 支持图片自动裁剪
- 支持图片手动不规则裁剪

## 使用
### Step 1. 添加 JitPack 仓库
在项目的 build.gradle 添加 JitPack 仓库
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

### Step 2. 添加依赖
在需要使用的 module 中添加依赖
```
dependencies {
	compile 'com.github.wildma:IDCardCamera:1.1.1'
}
```

### Step 3. 打开拍照界面
- 身份证正面
```
IDCardCamera.create(this).openCamera(IDCardCamera.TYPE_IDCARD_FRONT);
```
- 身份证反面
```
IDCardCamera.create(this).openCamera(IDCardCamera.TYPE_IDCARD_BACK);
```
**注意：** create() 方法的参数传的是上下文，在 Activity 中传 activity.this，在 Fragment 中传 fragment.this

### Step 4. 在 onActivityResult 方法中获取裁剪后的图片
```
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
```

### 清理缓存
实际开发中将图片上传到服务器成功后需要删除全部缓存图片，调用如下方法即可：
```java
FileUtils.clearCache(this);
```

详细介绍请看文章：[Android 自定义相机实现身份证拍照，并加入自动对焦与图片不规则裁剪](https://www.jianshu.com/p/5e3cb0c63cd5)

ps：如果对你有帮助，点下 star 就是对我最大的认可。

## 感谢
- [CertificateCamera](https://github.com/smartown/CertificateCamera) 
- [PhotoPolygonCropper](https://github.com/leanh215/PhotoPolygonCropper)
- [SweetCamera](https://github.com/WellerV/SweetCamera)

## License
```
   Copyright 2018 wildma

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
