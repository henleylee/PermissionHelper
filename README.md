# PermissionHelper —— Android 6.0 运行时权限管理
从Android 6.0开始，Android系统引入了新的权限机制，即运行时权限。具体请查看[运行时权限的变化及特点](./Permission.md)

## 特性 ##
* 链式调用，一句话申请权限；
* 支持申请权限组，兼容Android8.0；
* 不需要判断SDK版本和是否拥有某权限；
* 支持`OnPrepareListener`权限请求准备监听；
* 支持`OnPermissionsCallback`权限请求结果回调；
* 对于某个被拒绝的权限，区分是否勾选了“不再询问”。

## APK Demo ##

下载 [APK-Demo](https://github.com/HenleyLee/PermissionHelper/raw/master/app/app-release.apk)

## 类介绍 ##
#### OnPrepareListener(权限请求准备监听)： ####
```java
    public interface OnPrepareListener {

        /**
         * 当检查有请求的权限未授予时调用该方法(可以用于提示用户需要的权限信息等操作)
         *
         * @param permissions 未授予的权限集合
         * @param rationale   提供给用户选择权限请求如何进行的工具
         * @see Rationale
         */
        void onPermissionsPrepareRequest(int requestCode, List<String> permissions, Rationale rationale);
    }
```

#### OnPermissionsCallback(权限请求结果回调)： ####
```java
    public interface OnPermissionsCallback {

        /**
         * 当Android系统版本号小于6.0或所有权限都被授予时调用该方法
         *
         * @param requestCode 权限请求码
         * @param permissions 被授予的权限
         * @param flag        Android系统版本号小于6.0或所有权限都被授予的标记
         */
        void onPermissionsGranted(int requestCode, List<String> permissions, @PermissionRequest.PermissionFlag int flag);

        /**
         * 当请求的某一项或多项权限被拒绝且选择不再询问时调用该方法
         *
         * @param requestCode 权限请求码
         * @param permissions 被拒绝的权限
         */
        void onPermissionsDenied(int requestCode, List<String> permissions);

        /**
         * 当请求的某一项或多项权限被拒绝且没有选择不再询问时调用该方法
         *
         * @param requestCode 权限请求码
         * @param permissions 被拒绝的权限
         * @param rationale   提供给用户选择权限请求如何进行的工具
         * @see Rationale
         */
        void onShowRequestPermissionRationale(int requestCode, List<String> permissions, Rationale rationale);
    }
```

#### Request(权限请求接口)： ####
```java
    interface Request<T extends Request> {

        /**
         * 设置是否为调试模式
         */
        T debugMode(boolean debug);

        /**
         * 设置需要请求的权限
         */
        T permission(String permission);

        /**
         * 设置需要请求的权限
         */
        T permissions(String... permissions);

        /**
         * 设置需要请求的权限
         */
        T permissions(String[]... permissions);

        /**
         * 设置需要请求的权限
         */
        T permissions(Collection<String> permissions);

        /**
         * 设置权限请求准备监听
         */
        T prepare(OnPrepareListener listener);

        /**
         * 设置权限请求回调
         */
        T callback(OnPermissionsCallback callback);

        /**
         * 设置权限请求码
         */
        T requestCode(int requestCode);

        /**
         * 请求权限
         */
        void request();
    }
```

## 使用方法 ##

[Permission](./Permission.java)封装了一些常用权限

#### 在Activity中使用： ####
```java
    PermissionHelper.with(activity)
            .debugMode(true)
            .permission(Permission.CAMERA)
            .requestCode(100)
            .prepare(...)
            .callback(...)
            .request();
```

#### 在Fragment中使用： ####
```java
    PermissionHelper.with(fragment)
            .debugMode(true)
            .permission(Permission.MICROPHONE)
            .requestCode(101)
            .prepare(...)
            .callback(...)
            .request();
```

#### 请求单独的几个权限： ####
```java
    PermissionHelper.with(activity/fragment)
            .debugMode(true)
            .permissions(Permission.READ_SMS, Permission.READ_PHONE_STATE)
            .requestCode(200)
            .prepare(...)
            .callback(...)
            .request();
```

#### 请求权限组： ####
```java
    PermissionHelper.with(activity/fragment)
            .debugMode(true)
            .permissions(Permission.GROUP_STORAGE, Permission.GROUP_LOCATION)
            .requestCode(200)
            .prepare(...)
            .callback(...)
            .request();
```

## 感谢 ##

1. [AndPermission](https://github.com/yanzhenjie/AndPermission)
2. [PermissionHelper](https://github.com/k0shk0sh/PermissionHelper)
