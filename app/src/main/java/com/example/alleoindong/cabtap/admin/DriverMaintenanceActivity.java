package com.example.alleoindong.cabtap.admin;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.alleoindong.cabtap.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DriverMaintenanceActivity extends AppCompatActivity {

    @BindView(R.id.rcv_drivers) RecyclerView mRcvDrivers;
    @BindView(R.id.fab_add_driver) FloatingActionButton mFabAddDriver;

    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_maintenance);
        setTitle("User Maintenance");

        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recycler view
//        mLayoutManager = new LinearLayoutManager(this);
//        mRcvDrivers.setLayoutManager(mLayoutManager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.fab_add_driver) void addDriver() {
        Intent intent = new Intent(this, AddDriverActivity.class);
        startActivity(intent);
    }
}
