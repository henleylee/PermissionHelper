package com.liyunlong.permissionhelper.demo;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.liyunlong.permissionhelper.OnPermissionsCallback;
import com.liyunlong.permissionhelper.PermissionHelper;
import com.liyunlong.permissionhelper.Rationale;

import java.util.List;

/**
 * @author liyunlong
 * @date 2017/7/31 15:48
 */
public class SimplePermissionsCallback implements OnPermissionsCallback {

    private Context context;
    private Toast toast;

    public SimplePermissionsCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> permissions, int flag) {
        showToast("所有权限都已授予");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> permissions) {
        showToast("被拒绝的权限：" + permissions.toString());
        new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("当前应用缺少必要权限，为了您能正常使用所有功能，请点击“设置”-“权限”-打开所需权限")
                .setNegativeButton("取消", null)
                .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionHelper.startPackageSettings(context);
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onShowRequestPermissionRationale(int requestCode, List<String> permissions, final Rationale rationale) {
        showToast("被拒绝的权限：" + permissions.toString());
        new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage("为了您能正常使用所有功能，需要如下权限：" + getDesc(permissions))
                .setNegativeButton("取消", null)
                .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rationale.resume();
                    }
                })
                .create()
                .show();
    }

    private String getDesc(List<String> permissions) {
        int size = permissions.size();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            builder.append(PermissionConfig.getDesc(permissions.get(i)));
            if (i < size - 1) {
                builder.append("、");
            }
        }
        return builder.toString();
    }

    private void showToast(String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }
}
