package com.example.alleoindong.cabtap.admin;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.alleoindong.cabtap.DividerItemDecoration;
import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.Maintenance;
import com.example.alleoindong.cabtap.models.MaintenanceMenu;
import com.example.alleoindong.cabtap.models.UserProfile;
import com.example.alleoindong.cabtap.models.Vehicle;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MaintenanceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.rcv_maintenance) RecyclerView mRcvMaintenance;
    @BindView(R.id.fab_add_maintenance) FloatingActionButton mFabAddMaintenance;

    @BindView(R.id.select_maintenance_filter) AppCompatSpinner mSpinnerMaintenance;

    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter mAdapter;

    public ArrayList<MaintenanceMenu> mMaintenanceMenuList;
    public DatabaseReference mMaintenanceMenuRef;
    public MaintenanceMenuSpinnerAdapter mMaintenanceMenuSpinnerAdapter;

    public MaintenanceMenu mSelectedMaintenanceMenu;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);
        setTitle("Maintenance");

        mDatabase = FirebaseDatabase.getInstance().getReference("maintenance");

        ButterKnife.bind(this);

        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRcvMaintenance.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRcvMaintenance.setLayoutManager(mLayoutManager);
        mRcvMaintenance.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));

        mAdapter = new FirebaseRecyclerAdapter<Maintenance, MaintenanceActivity.MaintenanceHolder>(Maintenance.class,
                R.layout.maintenance_item, MaintenanceActivity.MaintenanceHolder.class, mDatabase) {

            @Override
            protected void populateViewHolder(MaintenanceActivity.MaintenanceHolder viewHolder, Maintenance model, final int position) {
                viewHolder.setPlateNumber(model.plateNumber);
                viewHolder.setCost(model.cost);
                viewHolder.setMaintenanceType(model.maintenance);
                viewHolder.setScheduleDate(model.maintenanceDate);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Maintenance maintenance = (Maintenance) mAdapter.getItem(position);
                        Log.i("Maintenance", maintenance.plateNumber);
                        showMaintenanceInfoDialog(maintenance);
                    }
                });
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AppCompatSpinner spinner = (AppCompatSpinner) parent;
        if (spinner.getId() == R.id.select_maintenance_filter) {
            mSelectedMaintenanceMenu = mMaintenanceMenuList.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

        public void setScheduleDate(String name) {
            TextView field = (TextView) mView.findViewById(R.id.maintenance_schedule);
            field.setText(name);
        }

        public void setCost(Double name) {
            TextView field = (TextView) mView.findViewById(R.id.maintenance_cost);
            field.setText(String.format("%.2f",name));
        }

        public void setMaintenanceType(String name) {
            TextView field = (TextView) mView.findViewById(R.id.maintenance_type);
            field.setText(name);
        }

    }

    private void showMaintenanceInfoDialog(Maintenance maintenance) {
        FragmentManager fm = getSupportFragmentManager();

        MaintenanceInformationDialog maintenanceInformationDialog = MaintenanceInformationDialog
                .newInstance(maintenance);

        maintenanceInformationDialog.show(fm, "fragment_maintenance_info");
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }
    }

    private void init() {
        initMaintenanceData();
    }

    private void initMaintenanceData() {
        mMaintenanceMenuList = new ArrayList<MaintenanceMenu>();

        mMaintenanceMenuRef = FirebaseDatabase.getInstance().getReference("maintenance-menu");

        ValueEventListener maintenanceMenuListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot maintenanceMenus : dataSnapshot.getChildren()) {
                    MaintenanceMenu maintenanceMenu = maintenanceMenus.getValue(MaintenanceMenu.class);
                    mMaintenanceMenuList.add(maintenanceMenu);
                }

                initMaintenanceMenuSpinner();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mMaintenanceMenuRef.addValueEventListener(maintenanceMenuListener);
    }

    private void initMaintenanceMenuSpinner() {
        mMaintenanceMenuSpinnerAdapter = new MaintenanceMenuSpinnerAdapter(this, mMaintenanceMenuList);
        mMaintenanceMenuSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerMaintenance.setAdapter(mMaintenanceMenuSpinnerAdapter);
        mSpinnerMaintenance.setOnItemSelectedListener(this);
    }

    public class MaintenanceMenuSpinnerAdapter extends ArrayAdapter<MaintenanceMenu> {

        public MaintenanceMenuSpinnerAdapter(Context context, ArrayList<MaintenanceMenu> objects) {
            super(context, 0,  objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MaintenanceMenu maintenanceMenu = this.getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_spinner_item, parent, false);
            }

            TextView spinnerItem = (TextView) convertView.findViewById(R.id.tvSpinnerItem);
            spinnerItem.setText(maintenanceMenu.name);
            spinnerItem.setTag(maintenanceMenu.name);

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            MaintenanceMenu maintenanceMenu = this.getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.driver_spinner_item, parent, false);
            }

            TextView spinnerItem = (TextView) convertView.findViewById(R.id.tvSpinnerItem);
            spinnerItem.setText(maintenanceMenu.name);
            spinnerItem.setTag(maintenanceMenu.name);

            return convertView;
        }
    }
}
