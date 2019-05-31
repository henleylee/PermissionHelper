# 运行时权限的变化及特点

## 介绍 ##
Android 6.0，代号棉花糖，自发布伊始，其主要的特征运行时权限就很受关注。因为这一特征不仅改善了用户对于应用的使用体验，还使得应用开发者在实践开发中需要做出改变。
随着Android 6.0发布以及普及，我们开发者所要应对的主要就是新版本SDK带来的一些变化，首先关注的就是权限机制的变化。
Android 6.0(API 级别 23)除了提供诸多新特性和功能外，还对系统和 API 行为做出了各种变更。对于[Android 6.0 变更](http://developer.android.com/intl/zh-cn/about/versions/marshmallow/android-6.0-changes.html)，包含Runtime Permissions。
在6.0以前的系统，都是权限一刀切的处理方式，只要用户安装，Manifest申请的权限都会被赋予，并且安装后权限也撤销不了。
从Android 6.0开始，Android系统引入了新的权限机制，即运行时权限。新的权限机制更好的保护了用户的隐私，Google将权限分为两类，一类是[Normal Permissions](http://developer.android.com/intl/zh-cn/guide/topics/security/normal-permissions.html)，这类权限一般不涉及用户隐私，是不需要用户进行授权的，比如手机震动、访问网络等；另一类是Dangerous Permission，一般是涉及到用户隐私的，需要用户进行授权，比如读取sdcard、访问通讯录等。

## 权限的分组 ##

Android中有很多权限，但并非所有的权限都是敏感权限，于是6.0系统就对权限进行了分类，一般为下述几类
 * 正常权限(Normal Permissions)
 * 危险权限(Dangerous Permissions)
 * 特殊权限(Particular Permissions)
 * 其他权限（一般很少用到）

#### 正常权限： ####
###### 正常权限具有如下的几个特点 ######
 * 对用户隐私没有较大影响或者不会打来安全问题。
 * 安装后就赋予这些权限，不需要显示提醒用户，用户也不能取消这些权限。

###### 正常权限列表 ######
```
android.permission.ACCESS_LOCATION_EXTRA_COMMANDS
android.permission.ACCESS_NETWORK_STATE
android.permission.ACCESS_NOTIFICATION_POLICY
android.permission.ACCESS_WIFI_STATE
android.permission.BLUETOOTH
android.permission.BLUETOOTH_ADMIN
android.permission.BROADCAST_STICKY
android.permission.CHANGE_NETWORK_STATE
android.permission.CHANGE_WIFI_MULTICAST_STATE
android.permission.CHANGE_WIFI_STATE
android.permission.DISABLE_KEYGUARD
android.permission.EXPAND_STATUS_BAR
android.permission.GET_PACKAGE_SIZE
android.permission.INTERNET
android.permission.KILL_BACKGROUND_PROCESSES
android.permission.MODIFY_AUDIO_SETTINGS
android.permission.NFC
android.permission.READ_SYNC_SETTINGS
android.permission.READ_SYNC_STATS
android.permission.RECEIVE_BOOT_COMPLETED
android.permission.REORDER_TASKS
android.permission.REQUEST_INSTALL_PACKAGES
android.permission.SET_TIME_ZONE
android.permission.SET_WALLPAPER
android.permission.SET_WALLPAPER_HINTS
android.permission.TRANSMIT_IR
android.permission.USE_FINGERPRINT
android.permission.VIBRATE
android.permission.WAKE_LOCK
android.permission.WRITE_SYNC_SETTINGS
android.permission.SET_ALARM
android.permission.INSTALL_SHORTCUT
android.permission.UNINSTALL_SHORTCUT
```
上述的权限基本设计的是关于网络，蓝牙，时区，快捷方式等方面，只要在Manifest指定了这些权限，就会被授予，并且不能撤销。

#### 危险权限： ####
危险权限实际上才是运行时权限主要处理的对象，这些权限可能引起隐私问题或者影响其他程序运行。Android中的危险权限可以归为以下几个分组：
 * CALENDAR
 * CAMERA
 * CONTACTS
 * LOCATION
 * MICROPHONE
 * PHONE
 * SENSORS
 * SMS
 * STORAGE

###### 各个权限分组与其具体的权限，具体如下： ######
```
group:com.google.android.gms.permission.CAR_INFORMATION
  permission:com.google.android.gms.permission.CAR_VENDOR_EXTENSION
  permission:com.google.android.gms.permission.CAR_MILEAGE
  permission:com.google.android.gms.permission.CAR_FUEL

group:android.permission-group.CONTACTS
  permission:android.permission.WRITE_CONTACTS
  permission:android.permission.READ_CONTACTS

group:android.permission-group.PHONE
  permission:android.permission.READ_CALL_LOG
  permission:android.permission.READ_PHONE_STATE
  permission:android.permission.CALL_PHONE
  permission:android.permission.WRITE_CALL_LOG
  permission:android.permission.USE_SIP
  permission:android.permission.PROCESS_OUTGOING_CALLS
  permission:com.android.voicemail.permission.ADD_VOICEMAIL

group:android.permission-group.CALENDAR
  permission:android.permission.READ_CALENDAR
  permission:android.permission.WRITE_CALENDAR

group:android.permission-group.CAMERA
  permission:android.permission.CAMERA

group:android.permission-group.SENSORS
  permission:android.permission.BODY_SENSORS

group:android.permission-group.LOCATION
  permission:android.permission.ACCESS_FINE_LOCATION
  permission:com.google.android.gms.permission.CAR_SPEED
  permission:android.permission.ACCESS_COARSE_LOCATION

group:android.permission-group.STORAGE
  permission:android.permission.READ_EXTERNAL_STORAGE
  permission:android.permission.WRITE_EXTERNAL_STORAGE

group:android.permission-group.MICROPHONE
  permission:android.permission.RECORD_AUDIO

group:android.permission-group.SMS
  permission:android.permission.READ_SMS
  permission:android.permission.RECEIVE_WAP_PUSH
  permission:android.permission.RECEIVE_MMS
  permission:android.permission.RECEIVE_SMS
  permission:android.permission.SEND_SMS
  permission:android.permission.READ_CELL_BROADCASTS

```
###### 未进行分组的权限，具体如下： ######
```
ungrouped:
  permission:com.huawei.pushagent.permission.RICHMEDIA_PROVIDER
  permission:com.huawei.permission.ACCESS_FM
  permission:com.huawei.motion.permission.MOTION_EX
  permission:org.fidoalliance.uaf.permissions.FIDO_CLIENT
  permission:com.huawei.contacts.permission.CHOOSE_SUBSCRIPTION
```

#### 特殊权限： ####
特殊权限，顾名思义，就是一些特别敏感的权限，在Android系统中，主要由两个 * 对用户隐私没有较大影响或者不会打来安全问题。
 * SYSTEM_ALERT_WINDOW，设置悬浮窗，进行一些黑科技
 * WRITE_SETTINGS 修改系统设置
关于上面两个特殊权限的授权，做法是使用startActivityForResult启动授权界面来完成。

###### 1. 请求SYSTEM_ALERT_WINDOW ######
```java
    private static final int REQUEST_CODE = 1;
    private  void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Log.i(LOGTAG, "onActivityResult granted");
            }
        }
    }
```
上述代码需要注意的是
 * 使用Action Settings.ACTION_MANAGE_OVERLAY_PERMISSION启动隐式Intent
 * 使用"package:" + getPackageName()携带App的包名信息
 * 使用Settings.canDrawOverlays方法判断授权结果

###### 2. 请求WRITE_SETTINGS ######
```java
    private static final int REQUEST_CODE_WRITE_SETTINGS = 2;
    private void requestWriteSettings() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS );
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                Log.i(LOGTAG, "onActivityResult write settings granted" );
            }
        }
    }
```
上述代码需要注意的是
 * 使用Action Settings.ACTION_MANAGE_WRITE_SETTINGS 启动隐式Intent
 * 使用"package:" + getPackageName()携带App的包名信息
 * 使用Settings.System.canWrite方法检测授权结果

注意：关于这两个特殊权限，一般不建议应用申请。

## 注意事项 ##
#### API问题 ####

由于checkSelfPermission和requestPermissions从API 23才加入，低于23版本，需要在运行时判断 或者使用Support Library v4中提供的方法
 * ContextCompat.checkSelfPermission
 * ActivityCompat.requestPermissions
 * ActivityCompat.shouldShowRequestPermissionRationale

#### 多系统问题 ####
当我们支持了6.0必须也要支持4.4，5.0这些系统，所以需要在很多情况下，需要有两套处理。比如Camera权限
```java
    if (isMarshmallow()) {
        requestPermission();//然后在回调中处理
    } else {
        useCamera();//低于6.0直接使用Camera
    }
```

#### 两个权限 ####
运行时权限对于应用影响比较大的权限有两个，他们分别是：
 * READ_PHONE_STATE
 * WRITE_EXTERNAL_STORAGE/READ_EXTERNAL_STORAGE

其中READ_PHONE_STATE用来获取deviceID，即IMEI号码。这是很多统计依赖计算设备唯一ID的参考。如果新的权限导致读取不到，避免导致统计的异常。建议在完全支持运行时权限之前，将对应的值写入到App本地数据中，对于新安装的，可以采取其他策略减少对统计的影响。
WRITE_EXTERNAL_STORAGE/READ_EXTERNAL_STORAGE这两个权限和外置存储（即sdcard）有关，对于下载相关的应用这一点还是比较重要的，我们应该尽可能的说明和引导用户授予该权限。
#### 些许建议 ####
 * 不要使用多余的权限，新增权限时要慎重
 * 使用Intent来替代某些权限，如拨打电话（和你的产品经理PK去吧）
 * 对于使用权限获取的某些值，比如deviceId，尽量本地存储，下次访问直接使用本地的数据值
 * 注意，由于用户可以撤销某些权限，所以不要使用应用本地的标志位来记录是否获取到某权限。

## 相关文章 ##

1. [Working with System Permissions](http://developer.android.com/intl/zh-cn/training/permissions/index.html)
2. [Permissions Best Practices](http://developer.android.com/intl/zh-cn/training/permissions/best-practices.html#testing)

