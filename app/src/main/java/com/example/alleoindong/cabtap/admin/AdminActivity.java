package com.example.alleoindong.cabtap.admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.alleoindong.cabtap.BaseActivity;
import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.user.PassengerMapActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdminActivity extends BaseActivity {
    @BindView(R.id.btn_driver_maintenance) Button mBtnDriverMaintenance;
    @BindView(R.id.btn_vehicle_maintenance) Button mBtnVehicleMaintenance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        setTitle("Admin");
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_right, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            this.logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_driver_maintenance) void driverMaintenanceClick() {
        Intent intent = new Intent(this, DriverMaintenanceActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_vehicle_maintenance) void vehicleMaintenanceClick() {
        Intent intent = new Intent(this, VehicleMaintenanceActivity.class);
        startActivity(intent);
    }
}
