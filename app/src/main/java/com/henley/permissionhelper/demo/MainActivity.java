package com.henley.permissionhelper.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.request_in_activity).setOnClickListener(this);
        findViewById(R.id.request_in_fragment).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_in_activity:
                startActivity(new Intent(this, PermissionActivity.class));
                break;
            case R.id.request_in_fragment:
                startActivity(new Intent(this, FragmentActivity.class));
                break;
        }
    }
}
