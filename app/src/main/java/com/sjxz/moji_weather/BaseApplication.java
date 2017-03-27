package com.sjxz.moji_weather;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * @author WYH_Healer
 * @email 3425934925@qq.com
 * Created by xz on 2016/9/19.
 * Role:启动时首先运行的注册类
 */
public class BaseApplication extends Application {

    private static BaseApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    public static BaseApplication getApplication() {
        return INSTANCE;
    }
}

