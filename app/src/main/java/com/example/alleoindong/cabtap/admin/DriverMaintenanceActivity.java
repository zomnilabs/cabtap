package com.example.alleoindong.cabtap.admin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.alleoindong.cabtap.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DriverMaintenanceActivity extends AppCompatActivity {

    @BindView(R.id.rcv_drivers) private RecyclerView mRcvDrivers;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_maintenance);
        setTitle("Driver Maintenance");

        ButterKnife.bind(this);

        // Recycler view
//        mLayoutManager = new LinearLayoutManager(this);
//        mRcvDrivers.setLayoutManager(mLayoutManager);

    }
}
