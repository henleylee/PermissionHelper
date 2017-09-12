package com.liyunlong.permissionhelper.demo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 崩溃日志收集(单例模式)
 * <br/>使用方法：
 * <ul>
 * <li>在{@link Application#onCreate()}中调用{@link CrashHandler#getInstance()}方法得到{@link CrashHandler}实例
 * <li>得到{@link CrashHandler}实例后调用{@link CrashHandler#init(Context)}进行初始化
 * <li>得到{@link CrashHandler}实例后调用{@link CrashHandler#setFileCountLimitEnabled(boolean)}设置是否启用日志文件数量限制检查
 * <li>得到{@link CrashHandler}实例后调用{@link CrashHandler#setTimeIntervalEnabled(boolean)}设置是否启用上传时间间隔检查
 * <li>得到{@link CrashHandler}实例后调用{@link CrashHandler#upload(boolean)}上传崩溃日志文件
 * </ul>
 *
 * @author liyunlong
 * @date 2016/6/21 10:59
 */
public class CrashHandler implements UncaughtExceptionHandler {

    /** TAG */
    private static final String TAG = "CrashHandler";
    /** 崩溃日志目录名称 */
    private static final String DIR_NAME = "crash";
    /** 崩溃日志路径分隔符 */
    private static final String DEFAULT_FILE_SEPARATOR = ",";
    /** 崩溃信息最大长度(默认为4000字符) */
    private static final int CRASH_INFO_MAX_LENTH = 4000 - 100;
    /** 保存上次上传崩溃日志时间的key */
    private static final String LAST_UPLOAD_CRASH_LOG_TIME = "LAST_UPLOAD_CRASH_LOG_TIME";
    /** 保存所有日志文件路径的key */
    private static final String ALL_CRASH_LOG_FILE_PATH = "ALL_CRASH_LOG_FILE_PATH";
    /** 用于格式化日期 */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /** CrashHandler实例 */
    private static CrashHandler INSTANCE = new CrashHandler();
    /** 是否初始化 */
    private boolean isInit = false;
    /** 是否启用Debug调试模式 */
    private boolean debugModeEnabled = false;
    /** 上传时间间隔(单位：毫秒，默认为24小时) */
    private long uploadTimeInterval = 24 * 60 * 60 * 1000;
    /** 是否启用上传时间间隔检查(默认为true) */
    private boolean timeIntervalEnabled = true;
    /** 日志文件数量限制(默认为100) */
    private int maxFileCountLimit = 100;
    /** 是否启用日志文件数量限制检查(默认为true) */
    private boolean fileCountLimitEnabled = true;
    /** 用来存储APP信息和设备信息 */
    private Map<String, String> infoMap = new HashMap<>();
    /** 用来存储日志文件路径信息 */
    private List<String> fileList = new ArrayList<>();
    /** 程序的Context对象 */
    private Context mContext;
    /** 系统默认的UncaughtException处理类 */
    private UncaughtExceptionHandler mDefaultHandler;
    /** 崩溃日志保存目录 */
    private String logFileDir;
    /** 崩溃时间 */
    private String crashTime;
    /** SharedPreferences对象 */
    private SharedPreferences mSharedPreferences;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler() {

    }

    /**
     * 获取CrashHandler实例
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化崩溃日志收集功能
     */
    public CrashHandler init(Context context) {
        if (context == null) { // context不能为空
            throw new RuntimeException("The context cannot be empty!");
        }
        if (isInit) { // 如果已经初始化则返回
            return this;
        }
        this.mContext = context;
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器
        mSharedPreferences = mContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        logFileDir = getLogFileDir(); // 初始化崩溃日志保存目录
        isInit = true;
        Log.i(TAG, "崩溃日志收集功能初始化完成！");
        return this;
    }

    /**
     * 当UncaughtException发生时会转入该重写的方法来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex); // 自定义错误处理,收集错误信息
        if (mDefaultHandler != null) {
            // 如果系统提供了默认的异常处理器，则交给系统去结束我们的程序
            Log.i(TAG, "系统处理异常");
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // 结束当前进程，并重启应用
            Log.i(TAG, "自定义处理异常");
            restartApp(); // 1秒之后重启应用（在程序退出之前调用）
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1); // 0表示正常退出，1表示异常退出(只要是非0的都为异常退出)
        }
    }

    /**
     * 1秒之后重启应用（在程序退出之前调用）
     */
    private void restartApp() {
        String packageName = mContext.getPackageName();
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(packageName);
            List<ResolveInfo> resolveinfoList = packageManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo resolveinfo = resolveinfoList.iterator().next();
            if (resolveinfo != null) {
                String packname = resolveinfo.activityInfo.packageName;
                String className = resolveinfo.activityInfo.name;
                ComponentName componentName = new ComponentName(packname, className);
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setComponent(componentName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        }
        PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
    }

    /**
     * 自定义错误处理,收集错误信息，发送错误报告等操作均在此完成
     *
     * @param throwable 异常信息
     * @return true 如果处理了该异常信息则返回true，反之返回false
     */
    private boolean handleException(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        if (BuildConfig.DEBUG)
            throwable.printStackTrace();
        Log.i(TAG, "程序崩溃，开始收集错误信息并保存到SD卡crash目录下...");
        crashTime = dateFormat.format(new Date(System.currentTimeMillis()));// 崩溃时间
        collectAppInfo();// 收集APP参数信息
        collectDeviceInfo();// 收集设备参数信息
        saveCrashInfo2File(throwable);// 保存日志文件
        return true;
    }

    /**
     * 收集APP主要参数信息
     */
    private void collectAppInfo() {
        String packageName = mContext.getPackageName();// 获得包名
        infoMap.put("appName", mContext.getString(R.string.app_name)); // App名称
        try {
            PackageManager pm = mContext.getPackageManager();// 获得包管理器
            PackageInfo packageInfo = pm.getPackageInfo(packageName,
                    PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
            if (packageInfo != null) {
                infoMap.put("appVersion", packageInfo.versionName); // 版本名称
            }
        } catch (NameNotFoundException e) {
            throw new RuntimeException("NameNotFoundException occurred. ", e);
        }
    }

    /**
     * 收集设备主要参数信息
     */
    private void collectDeviceInfo() {
        infoMap.put("errorTime", crashTime); // 崩溃时间
        infoMap.put("appPlatform", "android"); // 平台
        infoMap.put("osVersion", android.os.Build.VERSION.RELEASE); // 设备系统版本号
        infoMap.put("mobileType", android.os.Build.MANUFACTURER.trim() + "_" +
                android.os.Build.MODEL.trim()); // 设备品牌型号
    }

    /**
     * 保存日志文件
     */
    private String saveCrashInfo2File(Throwable throwable) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        Throwable cause = throwable.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();// 记得关闭
        String crashInfo = writer.toString();
        Log.i(TAG, "崩溃信息：" + crashInfo);
        Log.i(TAG, "崩溃信息长度：" + crashInfo.length());
        // 如果崩溃信息长度大于默认值，则截取
        if (!TextUtils.isEmpty(crashInfo) && crashInfo.length() > CRASH_INFO_MAX_LENTH) {
            crashInfo = crashInfo.substring(0, CRASH_INFO_MAX_LENTH);
            Log.i(TAG, "崩溃信息长度大于默认最大值，截取后长度为：" + crashInfo.length());
        }
        infoMap.put("errorDesc", crashInfo); // 错误描述
        String crashMessage = mapToJson(infoMap); // 崩溃日志信息(Json格式)
        Log.i(TAG, "崩溃日志信息：" + crashMessage);
        if (debugModeEnabled) {
            Log.i(TAG, "调试模式已启用，不保存崩溃日志！");
            return null;
        }
        // 保存文件
        String fileName = "crash " + crashTime + ".log";
        try {
            File dir = new File(logFileDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String filePath = logFileDir + File.separator + fileName;
            Log.i(TAG, "日志保存路径：" + filePath);
            FileOutputStream fos = new FileOutputStream(new File(filePath));
            fos.write(crashMessage.getBytes());
            fos.close();
            putFileIntoFileList(filePath);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        }
    }

    /**
     * 将Map转换为Json格式
     *
     * @param map
     * @return
     */
    private String mapToJson(Map<String, String> map) {
        if (map == null || map.size() == 0) {
            return null;
        }
        return new JSONObject(map).toString();
    }

    /**
     * 获取日志保存目录
     */
    private String getLogFileDir() {
        String dir = mContext.getExternalFilesDir(DIR_NAME).getAbsolutePath();
        if (TextUtils.isEmpty(dir)) {
            dir = mContext.getFilesDir().getAbsolutePath() + File.separator + DIR_NAME;
        }
        return dir;
    }

    /**
     * 是否启用Debug调试模式(默认为false)
     */
    public CrashHandler setDebugModeEnabled(boolean debugModeEnabled) {
        this.debugModeEnabled = debugModeEnabled;
        return this;
    }

    /**
     * 设置日志文件数量最大值
     *
     * @param maxCount 日志文件数量最大值
     */
    public CrashHandler setMaxFileCountLimit(int maxCount) {
        if (maxCount <= 0) {
            throw new IllegalArgumentException("maxCount <= 0");
        }
        this.maxFileCountLimit = maxCount;
        return this;
    }

    /**
     * 是否启用日志文件数量限制检查(默认为true)
     */
    public CrashHandler setFileCountLimitEnabled(boolean fileCountLimitEnabled) {
        this.fileCountLimitEnabled = fileCountLimitEnabled;
        initFileList(); // 初始化日志文件集合
        return this;
    }

    /**
     * 设置上传时间间隔
     *
     * @param timeMillis 以毫秒为单位的上传时间间隔
     */
    public CrashHandler setUploadTimeInterval(long timeMillis) {
        if (timeMillis < 0) {
            throw new IllegalArgumentException("timeMillis < 0");
        }
        this.uploadTimeInterval = timeMillis;
        return this;
    }

    /**
     * 设置上传时间间隔
     *
     * @param time 上传时间间隔
     * @param unit 时间单位
     */
    public CrashHandler setUploadTimeInterval(long time, TimeUnit unit) {
        if (time < 0) {
            throw new IllegalArgumentException("time < 0");
        }
        if (unit == null) {
            throw new NullPointerException("unit == null");
        }
        long millis = unit.toMillis(time);
        if (millis > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("The time too large.");
        }
        if (millis == 0 && time > 0) {
            throw new IllegalArgumentException("The time too small.");
        }
        this.uploadTimeInterval = millis;
        return this;
    }

    /**
     * 是否启用上传时间间隔检查(默认为true)
     */
    public CrashHandler setTimeIntervalEnabled(boolean timeIntervalEnabled) {
        this.timeIntervalEnabled = timeIntervalEnabled;
        return this;
    }

    /**
     * 上传崩溃日志文件
     * <p/>注意：
     * <br/>如果需要设置上传时间间隔，调用该方法之前需要调用{@link CrashHandler#setUploadTimeInterval(long)}
     * 或{@link CrashHandler#setUploadTimeInterval(long, TimeUnit)}设置上传时间间隔(默认为24小时)。
     * <br/>如果每次启动应用都进行崩溃日志上传，则需要调用{@link CrashHandler#setTimeIntervalEnabled(boolean)}关闭上传时间间隔检查。
     *
     * @param isWifiOnly true代表只在wifi情况下发送，false代表有网的情况下就发送
     */
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE})
    public void upload(boolean isWifiOnly) {
        if (debugModeEnabled) {
            Log.i(TAG, "调试模式已启用，不进行崩溃日志上传！");
            return;
        }
        if (timeIntervalEnabled) { // 如果启用上传时间间隔检查
            Log.i(TAG, "上传时间间隔检查已启用！");
            long timeMillis = System.currentTimeMillis();
            long lastUploadTime = mSharedPreferences.getLong(LAST_UPLOAD_CRASH_LOG_TIME, 0); // 获取上次上传时间
            if (timeMillis - lastUploadTime < uploadTimeInterval) { // 如果距离上次上传时间小于设置的时间间隔，则返回
                Log.e(TAG, "上传崩溃日志时间间隔小于" + formatTimeInterval(uploadTimeInterval) + "！");
                return;
            }
        } else {
            Log.i(TAG, "上传时间间隔检查已关闭！");
        }
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isAvailable()) { // 如果网络不可用，则返回
            Log.e(TAG, "当前网络不可用！");
            return;
        }
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);// 得到与WiFi相关的网络信息
        if (isWifiOnly && (networkInfo == null || !networkInfo.isAvailable())) { // 如果只在WIFI情况下上传且WIFI不可用，则返回
            Log.e(TAG, "只在WIFI情况下上传且WIFI不可用！");
            return;
        }
        checkLogFiles(); // 检查崩溃日志是否存在
    }

    /**
     * 格式化上传时间间隔
     */
    private String formatTimeInterval(long uploadTimeInterval) {
        long days = TimeUnit.MILLISECONDS.toDays(uploadTimeInterval);
        if (days > 1) {
            return days + "天";
        }
        long hours = TimeUnit.MILLISECONDS.toHours(uploadTimeInterval);
        if (hours > 1) {
            return hours + "小时";
        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uploadTimeInterval);
        if (minutes > 1) {
            return minutes + "分钟";
        }
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uploadTimeInterval);
        if (seconds > 1) {
            return seconds + "秒";
        }
        return uploadTimeInterval + "毫秒";
    }

    /**
     * 检查崩溃日志文件是否存在
     */
    private void checkLogFiles() {
        File logFile = new File(logFileDir);
        if (!logFile.exists() || !logFile.isDirectory()) { // 如果崩溃日志目录不存在或不是一个目录，则返回
            Log.e(TAG, "崩溃日志目录不存在或不是一个目录！");
            return;
        }
        File[] listFiles = logFile.listFiles();
        if (listFiles == null || listFiles.length <= 0) { // 如果崩溃日志目录中没有日志文件，则返回
            Log.e(TAG, "崩溃日志目录中没有日志文件！");
            return;
        }
        uploadLogFiles(listFiles);
    }

    /**
     * File转换成字符串
     */
    public static String file2String(File file) {
        StringBuilder result = new StringBuilder();
        FileInputStream fileInputStream;
        BufferedReader bufferedReader;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            fileInputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "读取文件" + file + " 发生异常！");
        }
        return result.toString();
    }

    /**
     * 上传崩溃日志文件
     * <p/>注意：上传完成需要完成如下操作：
     * <br/>调用{@link CrashHandler#saveUploadTime()}方法保存上传时间
     * <br/>调用{@link CrashHandler#deleteLogFiles(File[])}方法删除日志文件
     *
     * @param listFiles 崩溃日志文件数组
     */
    @RequiresPermission(Manifest.permission.INTERNET)
    private void uploadLogFiles(final File[] listFiles) {
        Log.i(TAG, "开始进行崩溃日志上传...");
        new ReadLogMessageTask().execute(listFiles); // 开启异步任务读取文件并进行上传
    }

    /**
     * 保存上传时间
     */
    private void saveUploadTime() {
        if (timeIntervalEnabled) { // 如果启用上传时间间隔检查
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putLong(LAST_UPLOAD_CRASH_LOG_TIME, System.currentTimeMillis()); // 保存上传时间
            edit.commit();
            Log.i(TAG, "上传时间已保存！");
        }
    }

    /**
     * 初始化日志文件集合
     */
    private void initFileList() {
        if (fileCountLimitEnabled) {
            Log.i(TAG, "日志文件数量限制检查已启用！");
            if (mSharedPreferences != null) { // 如果启用日志文件数量限制检查
                String allFilePath = mSharedPreferences.getString(ALL_CRASH_LOG_FILE_PATH, null);
                if (allFilePath != null && allFilePath.length() > 0) {
                    String[] filePaths = allFilePath.split(DEFAULT_FILE_SEPARATOR);
                    if (fileList.size() > 0) {
                        fileList.clear();
                    }
                    fileList.addAll(Arrays.asList(filePaths));
                }
            }
        } else {
            Log.i(TAG, "日志文件数量限制检查已关闭！");
        }
    }

    /**
     * 将文件存入日志路径集合
     *
     * @param filePath 文件路径
     */
    private void putFileIntoFileList(String filePath) {
        if (fileCountLimitEnabled && !TextUtils.isEmpty(filePath)) {
            fileList.add(filePath);
            if (fileList.size() > maxFileCountLimit) {
                String removeFilePath = fileList.remove(0);
                if (new File(removeFilePath).delete()) {
                    Log.e(TAG, "日志文件数量达到最大值，" + removeFilePath + "被删除！");
                }
            }
            saveAllFilePath(); // 保存日志文件路径
        }
    }

    /**
     * 将文件从日志路径集合中移除
     *
     * @param filePath 文件路径
     */
    private void removeFileFromFileList(String filePath) {
        if (fileCountLimitEnabled && !TextUtils.isEmpty(filePath)) {
            fileList.remove(filePath);
        }
    }

    /**
     * 保存日志文件路径
     */
    private void saveAllFilePath() {
        if (fileCountLimitEnabled && fileList.size() > 0) {
            StringBuilder fileBuilder = new StringBuilder();
            boolean firstTime = true;
            for (String filePath : fileList) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    fileBuilder.append(DEFAULT_FILE_SEPARATOR);
                }
                fileBuilder.append(filePath);
            }
            String allFilePath = fileBuilder.toString();
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.putString(ALL_CRASH_LOG_FILE_PATH, allFilePath);
            edit.commit();
            Log.i(TAG, "所有日志文件路径已保存！");
        }
    }

    /**
     * 清除保存的日志文件路径
     */
    private void clearAllFilePath() {
        if (fileCountLimitEnabled) {
            SharedPreferences.Editor edit = mSharedPreferences.edit();
            edit.remove(ALL_CRASH_LOG_FILE_PATH);
            edit.commit();
            Log.i(TAG, "所有日志文件路径已清除！");
        }
    }

    /**
     * 删除日志文件
     */
    private void deleteLogFiles(File[] listFiles) {
        Log.i(TAG, "开始进行崩溃日志删除...");
        for (File file : listFiles) { // 循环遍历删除日志文件
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    removeFileFromFileList(file.getAbsolutePath());
                    Log.i(TAG, "崩溃日志文件" + file + "已删除！");
                }
            }
        }
        clearAllFilePath(); // 清除保存的日志文件路径
    }

    /**
     * 读取所有崩溃日志文件，并拼接成Json格式的字符串
     */
    class ReadLogMessageTask extends AsyncTask<File[], Integer, String> {

        private File[] listFiles;

        @Override
        protected String doInBackground(File[]... params) {
            listFiles = params[0];
            Log.i(TAG, "崩溃日志文件数量为：" + listFiles.length);
            JSONArray jsonArray = new JSONArray();
            for (File file : listFiles) {
                if (file.exists() && file.isFile() && file.length() > 0) {
                    String fileContent = file2String(file);
                    try {
                        jsonArray.put(new JSONObject(fileContent));
                    } catch (JSONException e) {
                        continue;
                    }
                }
            }
            String result = jsonArray.toString();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(TAG, "上传日志内容：" + result);
        }
    }

}
