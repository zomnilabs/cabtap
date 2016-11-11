package com.example.alleoindong.cabtap.admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.alleoindong.cabtap.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminActivity extends AppCompatActivity {
    @BindView(R.id.btn_driver_maintenance)
    Button mBtnDriverMaintenance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle("Admin");
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_driver_maintenance) void driverMaintenanceClick() {
        Intent intent = new Intent(this, DriverMaintenanceActivity.class);
        startActivity(intent);
    }
}
