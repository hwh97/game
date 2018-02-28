package com.gdlinkjob.baselibrary.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by legendmohe on 16/3/29.
 */
public class CommonUtils {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static <T> boolean isEmpty(T[] t) {
        return !(t != null && t.length != 0);
    }

    public static boolean isEmpty(Collection collection) {
        return !(collection != null && collection.size() != 0);
    }

    public static boolean isEmpty(WeakReference weakReference) {
        return !(weakReference != null && weakReference.get() != null);
    }

    public static boolean validateEmail(String email) {
        if (email == null)
            return false;
        if (email.length() > 30) {
            return false;
        }
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public static boolean validatePhoneNumber(String number) {
        if (number == null)
            return false;
        if (number.length() != 11) {
            return false;
        }

        for (char c : number.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public static void copyToClipboard(Context context, String content, String label) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(content);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(label, content);
            clipboard.setPrimaryClip(clip);
        }
    }

    public static String pasteFromClipboard(Context context) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.getText() != null) {
                return clipboard.getText().toString().trim();
            }
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            if (item.getText() != null) {
                return item.getText().toString().trim();
            }
        }
        return null;
    }

    public static boolean equals(short[] array1, short[] array2) {
        if (array1 == null && array2 == null)
            return true;
        if (array1 == null) {
            array1 = array2;
            array2 = null;
        }
        if (array2 == null)
            return false;
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[i])
                return false;
        }
        return true;
    }

    public static boolean equals(int[] array1, int[] array2) {
        if (array1 == null && array2 == null)
            return true;
        if (array1 == null) {
            array1 = array2;
            array2 = null;
        }
        if (array2 == null)
            return false;
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[i])
                return false;
        }
        return true;
    }

    public static boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }


    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean SetStatusBarLightMode(Window window, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (dark) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
            return true;
        }

        if (CommonUtils.isXiaomiRom()) {
            return CommonUtils.setMiuiStatusBarDarkMode(window, dark);
        }
        if (CommonUtils.isMeizuRom()) {
            return CommonUtils.setMeizuStatusBarDarkIcon(window, dark);
        }
        return true;
    }

    public static boolean isLightColor(@ColorInt int color) {
        return Color.red(color) + Color.blue(color) + Color.green(color) > 382;
    }


    public static boolean setMiuiStatusBarDarkMode(Window window, boolean darkmode) {
        Class<? extends Window> clazz = window.getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setMeizuStatusBarDarkIcon(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(
                    new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ignore) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignore) {
                }
            }
        }
        return line;
    }

    public static String dumpDeviceRomInfo() {
        String handSetInfo = "MODEL:" + android.os.Build.MODEL
                + "\nRELEASE:" + android.os.Build.VERSION.RELEASE
                + "\nPRODUCT:" + android.os.Build.PRODUCT
                + "\nDISPLAY:" + android.os.Build.DISPLAY
                + "\nBRAND:" + android.os.Build.BRAND
                + "\nDEVICE:" + android.os.Build.DEVICE
                + "\nCODENAME:" + android.os.Build.VERSION.CODENAME
                + "\nSDK_INT:" + android.os.Build.VERSION.SDK_INT
                + "\nCPU_ABI:" + android.os.Build.CPU_ABI
                + "\nHARDWARE:" + android.os.Build.HARDWARE
                + "\nHOST:" + android.os.Build.HOST
                + "\nID:" + android.os.Build.ID
                + "\nMANUFACTURER:" + android.os.Build.MANUFACTURER // 这行返回的是rom定制商的名称
                ;
        return handSetInfo;
    }

    public static String getRomManufacturer() {
        return android.os.Build.MANUFACTURER;
    }

    public static boolean isXiaomiRom() {
        return getRomManufacturer().equalsIgnoreCase("xiaomi");
    }

    public static boolean isMeizuRom() {
        return getRomManufacturer().equalsIgnoreCase("meizu"); // TODO - check this!
    }

    public static Bundle bundleForPair(String key, Object value) {
        Bundle data = new Bundle();
        if (value instanceof String) {
            data.putString(key, (String) value);
        } else if (value instanceof Integer) {
            data.putInt(key, (Integer) value);
        } else if (value instanceof byte[]) {
            data.putByteArray(key, (byte[]) value);
        } else if (value instanceof Parcelable) {
            data.putParcelable(key, (Parcelable) value);
        } else if (value instanceof Serializable) {
            data.putSerializable(key, (Serializable) value);
        } else {
            throw new UnsupportedOperationException();
        }
        return data;
    }

    public static <T> List<T> toList(T[] objects) {
        List<T> result = new ArrayList<>();
        for (T t :
                objects) {
            result.add(t);
        }
        return result;
    }

    public static <T> boolean in(T index, T[] excludePointInPowerOff) {
        for (T t :
                excludePointInPowerOff) {
            if (index.equals(t)) {
                return true;
            }
        }
        return false;
    }
}
