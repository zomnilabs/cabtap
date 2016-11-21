package com.example.alleoindong.cabtap.admin;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alleoindong.cabtap.DividerItemDecoration;
import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.UserProfile;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DriverMaintenanceActivity extends AppCompatActivity {

    @BindView(R.id.rcv_drivers) RecyclerView mRcvDrivers;
    @BindView(R.id.fab_add_driver) FloatingActionButton mFabAddDriver;

    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter mAdapter;

    private DatabaseReference mDatabase;

    @Override
    protected void onStart() {
        super.onStart();

//        ValueEventListener usersListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot userProfiles : dataSnapshot.getChildren()) {
//                    UserProfile userProfile = userProfiles.getValue(UserProfile.class);
//                    Log.i("USERPROFILE", userProfile.firstName);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//
//        mDatabase.addValueEventListener(usersListener);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_maintenance);
        setTitle("Driver Maintenance");

        // Handle search intent
        handleIntent(getIntent());

        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        Query mDrivers = mDatabase.orderByChild("role").equalTo("driver");

        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRcvDrivers.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRcvDrivers.setLayoutManager(mLayoutManager);
        mRcvDrivers.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider));

        mAdapter = new FirebaseRecyclerAdapter<UserProfile, UserProfileHolder>(UserProfile.class,
                R.layout.driver_item, UserProfileHolder.class, mDrivers) {

            @Override
            protected void populateViewHolder(UserProfileHolder viewHolder, UserProfile model, int position) {
                viewHolder.setName(model.firstName + " " + model.lastName);
                viewHolder.setContactNnumber(model.contactNumber);
                viewHolder.setAddress(model.address);
                viewHolder.setEmail(model.email);
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

    @OnClick(R.id.fab_add_driver) void addDriver() {
        Intent intent = new Intent(this, AddDriverActivity.class);
        startActivity(intent);
    }

    public static class UserProfileHolder extends RecyclerView.ViewHolder {
        View mView;

        public UserProfileHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setName(String name) {
            TextView field = (TextView) mView.findViewById(R.id.full_name);
            field.setText(name);
        }

        public void setAddress(String text) {
            TextView field = (TextView) mView.findViewById(R.id.address);
            field.setText(text);
        }

        public void setContactNnumber(String text) {
            TextView field = (TextView) mView.findViewById(R.id.contact_number);
            field.setText(text);
        }

        public void setEmail(String text) {
            TextView field = (TextView) mView.findViewById(R.id.email);
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
