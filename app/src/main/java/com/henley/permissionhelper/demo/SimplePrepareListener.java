package com.henley.permissionhelper.demo;

import android.content.Context;

import com.henley.permissionhelper.OnPrepareListener;
import com.henley.permissionhelper.Rationale;

import java.util.List;

/**
 * @author Henley
 * @date 2017/7/31 15:34
 */
public class SimplePrepareListener implements OnPrepareListener {

    private Context context;

    public SimplePrepareListener(Context context) {
        this.context = context;
    }

    @Override
    public void onPermissionsPrepareRequest(int requestCode, List<String> permissions, final Rationale rationale) {
        PermissionRationaleDialog dialog = new PermissionRationaleDialog(context);
        dialog.setPermissions(permissions);
        dialog.setCallback(new PermissionRationaleDialog.Callback() {
            @Override
            public void callback() {
                rationale.resume(); // 继续请求权限
            }
        });
        dialog.show();
    }
}
