package com.gdlinkjob.game;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import io.rong.imkit.RongIM;

/**
 *
 * @author hwhong
 * @创建日期 2018/1/26/ 0026 下午 02:14
 */

public class App extends MultiDexApplication {
    //须在AndroidManifest添加android：nane=".AppController"重要！！！！
    public static final String TAG = App.class.getSimpleName();
    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        RongIM.init(this);
        mInstance=this;
    }



    public static synchronized App getInstance() {
        return mInstance;
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}