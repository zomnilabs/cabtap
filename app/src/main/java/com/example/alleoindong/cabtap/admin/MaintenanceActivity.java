package com.example.alleoindong.cabtap.admin;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.alleoindong.cabtap.DividerItemDecoration;
import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.Maintenance;
import com.example.alleoindong.cabtap.models.UserProfile;
import com.example.alleoindong.cabtap.models.Vehicle;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MaintenanceActivity extends AppCompatActivity {

    @BindView(R.id.rcv_maintenance) RecyclerView mRcvMaintenance;
    @BindView(R.id.fab_add_maintenance) FloatingActionButton mFabAddMaintenance;

    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter mAdapter;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);
        setTitle("Maintenance");

        mDatabase = FirebaseDatabase.getInstance().getReference("maintenance");

        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRcvMaintenance.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, 4);
        mRcvMaintenance.setLayoutManager(mLayoutManager);

        mAdapter = new FirebaseRecyclerAdapter<Maintenance, MaintenanceActivity.MaintenanceHolder>(Maintenance.class,
                R.layout.maintenance_item, MaintenanceActivity.MaintenanceHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(MaintenanceActivity.MaintenanceHolder viewHolder, Maintenance model, int position) {
                viewHolder.setPlateNumber(model.vehicle_id);
            }

            @Override
            public void cleanup() {
                super.cleanup();
            }
        };

        mRcvMaintenance.setAdapter(mAdapter);
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

    @OnClick(R.id.fab_add_maintenance) void addMaintenance() {
        Intent intent = new Intent(this, AddMaintenanceActivity.class);
        startActivity(intent);
    }

    public static class MaintenanceHolder extends RecyclerView.ViewHolder {
        View mView;

        public MaintenanceHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setPlateNumber(String name) {
            TextView field = (TextView) mView.findViewById(R.id.plate_number);
            field.setText(name);
        }

    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }

}
