package com.henley.permissionhelper.demo;

import android.Manifest;

import java.util.HashMap;

/**
 * @author Henley
 * @date 2017/7/28 13:39
 */
public class PermissionConfig {

    private static final HashMap<String, String> DESC_MAP = new HashMap<>();
    private static final HashMap<String, Integer> ICON_MAP = new HashMap<>();

    static {
        DESC_MAP.put(Manifest.permission.ACCESS_FINE_LOCATION, "位置信息");
        DESC_MAP.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储空间");
        DESC_MAP.put(Manifest.permission.READ_CONTACTS, "通讯录");
        DESC_MAP.put(Manifest.permission.READ_PHONE_STATE, "设备信息");
        DESC_MAP.put(Manifest.permission.RECORD_AUDIO, "麦克风");
        DESC_MAP.put(Manifest.permission.CAMERA, "相机");
        DESC_MAP.put(Manifest.permission.READ_SMS, "短信");

        ICON_MAP.put(Manifest.permission.ACCESS_FINE_LOCATION, R.drawable.icon_location);
        ICON_MAP.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, R.drawable.icon_storage);
        ICON_MAP.put(Manifest.permission.READ_CONTACTS, R.drawable.icon_contacts);
        ICON_MAP.put(Manifest.permission.READ_PHONE_STATE, R.drawable.icon_phone);
        ICON_MAP.put(Manifest.permission.RECORD_AUDIO, R.drawable.icon_microphone);
        ICON_MAP.put(Manifest.permission.CAMERA, R.drawable.icon_camera);
        ICON_MAP.put(Manifest.permission.READ_SMS, R.drawable.icon_sms);

    }

    public static String getDesc(String permission) {
        return DESC_MAP.get(permission);
    }

    public static int getIcon(String permission) {
        return ICON_MAP.get(permission);
    }

}
