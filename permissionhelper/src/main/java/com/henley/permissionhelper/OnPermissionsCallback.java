package com.henley.permissionhelper;

import java.util.List;

/**
 * 权限请求结果回调
 *
 * @author Henley
 * @date 2017/7/31 14:24
 */
public interface OnPermissionsCallback {

    /**
     * 当Android系统版本号小于6.0或所有权限都被授予时调用该方法
     *
     * @param requestCode 权限请求码
     * @param permissions 被授予的权限
     * @param flag        Android系统版本号小于6.0或所有权限都被授予的标记
     */
    void onPermissionsGranted(int requestCode, List<Permission> permissions, @PermissionRequest.PermissionFlag int flag);

    /**
     * 当请求的某一项或多项权限被拒绝且选择不再询问时调用该方法
     *
     * @param requestCode 权限请求码
     * @param permissions 被拒绝的权限
     */
    void onPermissionsDenied(int requestCode, List<Permission> permissions);

    /**
     * 当请求的某一项或多项权限被拒绝且没有选择不再询问时调用该方法
     *
     * @param requestCode 权限请求码
     * @param permissions 被拒绝的权限
     * @param rationale   提供给用户选择权限请求如何进行的工具
     * @see Rationale
     */
    void onShowRequestPermissionRationale(int requestCode, List<Permission> permissions, Rationale rationale);

    /**
     * 当权限请求操作被取消时回调该方法
     *
     * @param requestCode 权限请求码
     * @param permissions 未被授予的权限
     */
    void onPermissionsRequestCancel(int requestCode, List<Permission> permissions);

}
