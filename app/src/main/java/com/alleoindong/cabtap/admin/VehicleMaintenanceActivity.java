package com.alleoindong.cabtap.admin;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alleoindong.cabtap.DividerItemDecoration;
import com.alleoindong.cabtap.R;
import com.alleoindong.cabtap.models.Vehicle;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VehicleMaintenanceActivity extends AppCompatActivity {

    @BindView(R.id.rcv_drivers) RecyclerView mRcvDrivers;
    @BindView(R.id.fab_add_vehicle) FloatingActionButton mFabAddVehicle;

    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter mAdapter;

    private DatabaseReference mDatabase;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_maintenance);
        setTitle("Vehicle Maintenance");

        // Handle search intent
        handleIntent(getIntent());

        mDatabase = FirebaseDatabase.getInstance().getReference("vehicles");

        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRcvDrivers.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRcvDrivers.setLayoutManager(mLayoutManager);
        mRcvDrivers.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));

        mAdapter = new FirebaseRecyclerAdapter<Vehicle, VehicleViewHolder>(Vehicle.class,
                R.layout.vehicle_item, VehicleViewHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(VehicleViewHolder viewHolder, Vehicle model, int position) {
                viewHolder.setMake(model.make);
                viewHolder.setModel(model.model);
                viewHolder.setPlateNumber(model.plateNumber);
                viewHolder.setYear(model.year);
            }

            @Override
            public void cleanup() {
                super.cleanup();
            }
        };

        mRcvDrivers.setAdapter(mAdapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_search_driver, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    @OnClick(R.id.fab_add_vehicle) void addVehicle() {
        Intent intent = new Intent(this, AddVehicleActivity.class);
        startActivity(intent);
    }

    public static class VehicleViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public VehicleViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setPlateNumber(String name) {
            TextView field = (TextView) mView.findViewById(R.id.plate_number);
            field.setText(name);
        }

        public void setMake(String text) {
            TextView field = (TextView) mView.findViewById(R.id.make);
            field.setText(text);
        }

        public void setModel(String text) {
            TextView field = (TextView) mView.findViewById(R.id.model);
            field.setText(text);
        }

        public void setYear(String text) {
            TextView field = (TextView) mView.findViewById(R.id.year);
            field.setText(text);
        }

    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }
}
