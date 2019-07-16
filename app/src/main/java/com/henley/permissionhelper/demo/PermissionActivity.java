package com.henley.permissionhelper.demo;

import android.Manifest;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.henley.permissionhelper.PermissionHelper;

/**
 * 在Activity中请求权限
 *
 * @author Henley
 * @date 2017/7/28 11:59
 */
public class PermissionActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_permission_permission_request);
        findViewById(R.id.permission_single).setOnClickListener(this);
        findViewById(R.id.permission_multi).setOnClickListener(this);
        TextView tvLocation = (TextView) findViewById(R.id.permission_location);
        tvLocation.setText("在Activity中请求权限");
        tvResult = (TextView) findViewById(R.id.permission_result);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.permission_single:
                PermissionHelper.with(this)
                        .debugMode(true)
                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .requestCode(100)
                        .prepare(new SimplePrepareListener(this))
                        .callback(new SimplePermissionsCallback(this))
                        .request();
                break;
            case R.id.permission_multi:
                PermissionHelper.with(this)
                        .debugMode(true)
                        .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .requestCode(101)
                        .prepare(new SimplePrepareListener(this))
                        .callback(new SimplePermissionsCallback(this))
                        .request();
                break;
        }
    }
}
