package com.example.alleoindong.cabtap.admin;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.alleoindong.cabtap.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MaintenanceActivity extends AppCompatActivity {

    @BindView(R.id.rcv_maintenance) RecyclerView mRcvMaintenance;
    @BindView(R.id.fab_add_maintenance) FloatingActionButton mFabAddMaintenance;

    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter mAdapter;

    private DatabaseReference mMaintenanceRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);
        setTitle("Maintenance");

        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMaintenanceRef = FirebaseDatabase.getInstance().getReference("maintenance");

        mRcvMaintenance.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 4);
        mRcvMaintenance.setLayoutManager(mLayoutManager);

        // Data Adapter

    }
}
