package com.henley.permissionhelper.demo;

import android.Manifest;
import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.henley.permissionhelper.PermissionHelper;

/**
 * 在Fragment中请求权限
 *
 * @author Henley
 * @date 2017/7/28 11:59
 */
public class PermissionFragment extends Fragment implements View.OnClickListener {

    private TextView tvResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_permission_permission_request, container, false);
        rootView.findViewById(R.id.permission_single).setOnClickListener(this);
        rootView.findViewById(R.id.permission_multi).setOnClickListener(this);
        TextView tvLocation = (TextView) rootView.findViewById(R.id.permission_location);
        tvLocation.setText("在Fragment中请求权限");
        tvResult = (TextView) rootView.findViewById(R.id.permission_result);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.permission_single:
                PermissionHelper.with(this)
                        .debugMode(true)
                        .permission(Manifest.permission.READ_CONTACTS)
                        .requestCode(200)
                        .prepare(new SimplePrepareListener(getActivity()))
                        .callback(new SimplePermissionsCallback(getActivity()))
                        .request();
                break;
            case R.id.permission_multi:
                PermissionHelper.with(this)
                        .debugMode(true)
                        .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                        .requestCode(201)
                        .prepare(new SimplePrepareListener(getActivity()))
                        .callback(new SimplePermissionsCallback(getActivity()))
                        .request();
                break;
        }
    }
}
