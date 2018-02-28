package com.gdlinkjob.baselibrary.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * android系统工具类
 */
public class AndroidUtils {
    private static final String TAG = "AndroidUtils";

    private AndroidUtils() {

    }

    /**
     * 检查权限
     *
     * @param permission android.permission.WRITE_EXTERNAL_STORAGE
     * @return manifest 已经定义了则返回true
     */
    public static boolean checkPermission(@NonNull Context context, @NonNull String permission) {
        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 申请root权限
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean getRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定包名的包信息
     */
    public static PackageInfo getAppInfo(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取未安装软件包的包名
     */
    public static String getApkPackageName(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            return appInfo.packageName;
        }
        return "";
    }

    /**
     * 判断是否安装
     */
    public static boolean apkIsInstall(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                pm.getPackageInfo(appInfo.packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
                return true;
            } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
                return false;
            }
        }
        return false;
    }

    /**
     * 判断服务是否后台运行
     *
     * @param context Context
     * @param clazz
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRun(Context context, Class<?> clazz) {
        boolean isRun = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            if (serviceList.get(i).service.getClassName().equals(clazz.getName())) {
                isRun = true;
                break;
            }
        }
        return isRun;
    }

    /**
     * 启动另外一个App
     */
    public static void startOtherApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        Intent launcherIntent = pm.getLaunchIntentForPackage(packageName);
        context.startActivity(launcherIntent);
    }

    /**
     * 判断手机是否拥有Root权限。
     *
     * @return 有root权限返回true，否则返回false。
     */
    public static boolean isRoot() {
        String binPath = "/system/bin/su";
        String xBinPath = "/system/xbin/su";
        return new File(binPath).exists() && isExecutable(binPath) || new File(xBinPath).exists() && isExecutable(xBinPath);
    }

    private static boolean isExecutable(String filePath) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("ls -l " + filePath);
            // 获取返回内容
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String str = in.readLine();
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x')
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return false;
    }

    /**
     * 静默卸载
     */
    public static boolean uninstallInBackground(String packageName) {
        String command = "pm uninstall -k " + packageName + "\n";
        ShellUtils.CommandResult result = ShellUtils.execCommand(command, true);
        String errorMsg = result.errorMsg;
        int res = result.result;
        return res == 0 && !errorMsg.contains("Failure");
    }

    /**
     * 静默安装APk
     * 执行具体的静默安装逻辑，需要手机ROOT。
     *
     * @param apkPath 要安装的apk文件的路径
     * @return 安装成功返回true，安装失败返回false。
     */
    public static boolean installInBackground(String apkPath) {
        String command = "pm install -r " + apkPath + "\n";
        ShellUtils.CommandResult result = ShellUtils.execCommand(command, true);
        String errorMsg = result.errorMsg;
        int res = result.result;
        return res == 0 && !errorMsg.contains("Failure");
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 获取当前窗口的高度， 该高度是不包含导航栏和状态栏的
     */
    public static int getWindowHeight(Activity activity) {
        return activity.getWindow().getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 获取当前窗口的宽度
     *
     * @param activity
     * @return
     */
    public static int getWindowWidth(Activity activity) {
        return activity.getWindow().getWindowManager().getDefaultDisplay().getWidth();
    }

    /**
     * 获取屏幕参数，这个获取到的高是包含导航栏和状态栏的
     *
     * @param context
     * @return
     */
    public static int[] getScreenParams(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return new int[]{wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight()};
    }

    /**
     * 检查手机是否安装了指定的APK
     *
     * @param packageName 该APK的包名
     * @return
     */
    public static boolean checkApkExists(Context context, String packageName) {
        List<PackageInfo> packageInfos = getAllApps(context);
        for (PackageInfo pi : packageInfos) {
            if (pi.packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询手机内所有应用
     *
     * @param context
     * @return
     */
    public static List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            apps.add(pak);
        }
        return apps;
    }

    /**
     * 查询手机内非系统应用
     *
     * @param context
     * @return
     */
    public static List<PackageInfo> getAllNoSystemApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }
        return apps;
    }

    /**
     * 获取目录
     */
    public static String getSourcePath(Context context, String packageName) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            return appInfo.sourceDir;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取目录
     */
    public static String getFilesDir(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 获取目录
     */
    public static String getCacheDir(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * 获取包名
     */
    public static String getPackageName(Context context) {
        return getPackageInfo(context).packageName;
    }

    /**
     * 获取应用名
     */
    public static String getAppName(Context context) {
        try {
            return context.getString(context.getApplicationInfo().labelRes);
        } catch (Resources.NotFoundException e) {
            return "";
        }
    }

    /**
     * 获取设备的唯一ID
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 返回版本名称
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    /**
     * 返回版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    /**
     * 打开一个隐藏了图标的APP
     *
     * @param context
     */
    public static void openAppWithAction(Context context, String packageName, String activity) {
        ComponentName componentName = new ComponentName(packageName, activity);
        try {
            Intent intent = new Intent();
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {

        }
    }

    /**
     * 应用是否被安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isInstall(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            //            e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 安装APP
     *
     * @param file
     */
    public static void install(Context context, File file) {
        context.startActivity(getInstallIntent(file));
    }

    /**
     * 卸载APk
     *
     * @param context
     * @param packageName 包名
     */
    public static void uninstall(Context context, String packageName) {
        Uri packageURI = Uri.parse("package:" + packageName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(uninstallIntent);
    }

    /**
     * 获取安装应用的Intent
     *
     * @param file
     * @return
     */
    public static Intent getInstallIntent(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }

    /**
     * 拷贝Assets中的文件到指定目录
     *
     * @param context
     * @param fileName
     * @param path
     * @return
     */
    public static boolean copyFileFromAssets(Context context, String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = createFile(path);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }

    /**
     * 创建文件 当文件不存在的时候就创建一个文件，否则直接返回文件
     *
     * @param path
     * @return
     */
    public static File createFile(String path) {
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            if (!createDir(file.getParent())) {
            }
        }
        // 创建目标文件
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                }
                return file;
            } else {
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建目录 当目录不存在的时候创建文件，否则返回false
     *
     * @param path
     * @return
     */
    public static boolean createDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 获取版本信息
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo pkg = null;
        if (context == null) {
            return null;
        }
        try {
            pkg = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pkg;
    }

    /**
     * 获取设备的显示属性
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    /**
     * 获取电话号码
     *
     * @param context
     * @return
     */
    public static String getLocalPhoneNumber(Context context) {
        String line1Number = getTelephonyManager(context).getLine1Number();
        return line1Number == null ? "" : line1Number;
    }

    /**
     * 获取设备型号(Nexus5)
     *
     * @return
     */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取电话通讯管理（可以通过这个对象获取手机号码等）
     */
    public static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * 获取版本号
     */
    public static String getSystemVersion() {
        // 获取android版本号
        return Build.VERSION.RELEASE;
    }

    /**
     * 返回ApplicationInfo（可以通过这个读取meta-data等等）
     */
    public static ApplicationInfo getApplicationInfo(Context context) {
        if (context == null) {
            return null;
        }
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return applicationInfo;
    }

    /**
     * 获取MetaData的Bundle
     */
    public static Bundle getMetaData(Context context) {
        ApplicationInfo applicationInfo = getApplicationInfo(context);
        if (applicationInfo == null) {
            return new Bundle();
        }
        return applicationInfo.metaData;
    }

    /**
     * 应用是否启动
     */
    public static boolean appIsRunning(Context context) {
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        String packageName = getPackageInfo(context).packageName;
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) {
                isAppRunning = true;
                //find it, break
                break;
            }
        }
        return isAppRunning;
    }

    /**
     * 检查系统是否有这个Intent，在启动Intent的时候需要检查，因为启动一个没有的Intent程序会Crash
     */
    public static boolean isIntentSafe(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        return activities.size() > 0;
    }


    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

}
