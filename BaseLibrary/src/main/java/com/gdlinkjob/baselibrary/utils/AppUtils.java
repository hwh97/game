package com.gdlinkjob.baselibrary.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Created by MYFLY on 2017/3/18.
 */

public class AppUtils {
    private static Boolean isDebug = null;

    public static boolean isDebug() {
        return isDebug == null ? false : isDebug.booleanValue();
    }

    public static void syncIsDebug(Context context) {
        if (isDebug == null) {
            isDebug = context.getApplicationInfo() != null && (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }
}
