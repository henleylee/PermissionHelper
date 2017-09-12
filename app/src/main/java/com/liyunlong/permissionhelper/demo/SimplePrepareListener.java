package com.liyunlong.permissionhelper.demo;

import android.content.Context;

import com.liyunlong.permissionhelper.OnPrepareListener;
import com.liyunlong.permissionhelper.Rationale;

import java.util.List;

/**
 * @author liyunlong
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
