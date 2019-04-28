# Android custom ID card camera

[![jitpack](https://jitpack.io/v/wildma/IDCardCamera.svg)](https://jitpack.io/#wildma/IDCardCamera)
[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![](https://img.shields.io/badge/License-Apache--2.0-brightgreen.svg)](https://github.com/wildma/IDCardCamera/blob/master/LICENSE)
[![](https://img.shields.io/badge/Author-wildma-7AD6FD.svg)](https://github.com/wildma)

### [README of Chinese](https://github.com/wildma/IDCardCamera/blob/master/README.md)

### Design sketch
![](https://github.com/wildma/IDCardCamera/blob/master/screenshots/screenshot.jpg)

### APK
[download APK](https://github.com/wildma/IDCardCamera/raw/master/apk/com.wildma.idcardcamera-1.1.0.apk)

### Features
- Custom camera interface
- Support to turn on flash
- Support touch screen for focusing
- Support automatic focus
- Support automatic clipping of images
- Support manual irregular clipping of images

### Usage
##### Step 1. Add a JitPack repertory
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

##### Step 2. Add a gradle dependency
```
dependencies {
	compile 'com.github.wildma:IDCardCamera:1.1.0'
}
```

##### Step 3. Open photographic interface
- front
```
IDCardCamera.create(this).openCamera(IDCardCamera.TYPE_IDCARD_FRONT);
```
- back
```
IDCardCamera.create(this).openCamera(IDCardCamera.TYPE_IDCARD_BACK);
```
**notice：** In the Fragment,Replace "this" with "fragment.this".

##### Step 4. Get a picture
```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == IDCardCamera.RESULT_CODE) {
            final String path = IDCardCamera.getImagePath(data);
            if (!TextUtils.isEmpty(path)) {
                if (requestCode == IDCardCamera.TYPE_IDCARD_FRONT) {
                    mIvFront.setImageBitmap(BitmapFactory.decodeFile(path));
                } else if (requestCode == IDCardCamera.TYPE_IDCARD_BACK) {
                    mIvBack.setImageBitmap(BitmapFactory.decodeFile(path));
                }
            }
        }
    }
```

Blog：[Android custom ID card camera](https://www.jianshu.com/p/5e3cb0c63cd5)

### Thanks
- [CertificateCamera](https://github.com/smartown/CertificateCamera) 
- [PhotoPolygonCropper](https://github.com/leanh215/PhotoPolygonCropper)
- [SweetCamera](https://github.com/WellerV/SweetCamera)

### License
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
