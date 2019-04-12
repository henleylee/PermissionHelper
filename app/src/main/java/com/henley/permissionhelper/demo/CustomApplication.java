package com.henley.permissionhelper.demo;

import android.app.Application;

import java.util.concurrent.TimeUnit;

/**
 * @author Henley
 * @date 2017/7/31 9:56
 */
public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance().init(this)       // 初始化崩溃日志收集功能
                .setDebugModeEnabled(false)          // 是否启用Debug调试模式
                .setFileCountLimitEnabled(true)     // 是否启用日志文件数量限制检查
                .setMaxFileCountLimit(100)          // 设置日志文件数量最大值
                .setTimeIntervalEnabled(true)       // 是否启用上传时间间隔检查
                .setUploadTimeInterval(24, TimeUnit.HOURS)  // 设置上传时间间隔
                .upload(true);                      // 上传崩溃日志文件
    }
}
