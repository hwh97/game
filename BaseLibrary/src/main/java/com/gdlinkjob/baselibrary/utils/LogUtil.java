package com.gdlinkjob.baselibrary.utils;

import com.orhanobut.logger.Logger;

import java.util.Map;
import java.util.Set;

/**
 * Created by MYFLY on 2016/9/8.
 */

public class LogUtil {
    public static boolean sIsLogEnable = true;

    private LogUtil() {
    }

    public static void init(){
        Logger.init("SmartApp").methodOffset(1).setMethodCount(3);
    }

    public static void enableLog() {
        sIsLogEnable = true;
    }

    public static void disableLog() {
        sIsLogEnable = false;
    }

//    public static void d(String tag, String msg) {
//        if (sIsLogEnable) {
//            Logger.d("%s %s", tag, msg);
//        }
//    }

    public static void d(String msg, Object... args) {
        if (sIsLogEnable) {
            Logger.d(msg, args);
        }
    }

    public static void i(String msg, Object... args) {
        if (sIsLogEnable) {
            Logger.i(msg, args);
        }
    }

    public static void e(String msg, Object... args) {
        if (sIsLogEnable) {
            Logger.e(msg, args);
        }
    }

    public static void e(Throwable throwable) {
        if (sIsLogEnable) {
            Logger.e("Exception: %s", throwable);
        }
    }

    public static void w(String msg, Object... args) {
        if (sIsLogEnable) {
            Logger.w(msg, args);
        }
    }


    public static void v(String msg, Object... args) {
        if (sIsLogEnable) {
            Logger.v(msg, args);
        }
    }

    public static void json(String json) {
        if (sIsLogEnable) {
            Logger.json(json);
        }
    }

    /**
     * 打印MAp
     */
    public static void m(Map map) {
        if (sIsLogEnable) {
            Set set = map.entrySet();
            if (set.size() < 1) {
                i("map: %s", "[]");
                return;
            }

            int i = 0;
            String[] s = new String[set.size()];
            for (Object aSet : set) {
                Map.Entry entry = (Map.Entry) aSet;
                s[i] = entry.getKey() + " = " + entry.getValue() + ",\n";
                i++;
            }
            i("map: %s", s.toString());
        }
    }

}
