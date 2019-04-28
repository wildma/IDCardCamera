package com.wildma.wildmaidcardcamera;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2019/04/28
 * Desc	        ${MyApplication}
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLeakCanary();
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
