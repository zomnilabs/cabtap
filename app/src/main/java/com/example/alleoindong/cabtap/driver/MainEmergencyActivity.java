package com.example.alleoindong.cabtap.driver;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alleoindong.cabtap.BaseActivity;
import com.example.alleoindong.cabtap.DrawerMenuAdapter;
import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.DrawerMenu;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainEmergencyActivity extends BaseActivity {

    private ArrayList<DrawerMenu> mDrawerMenu;
    @BindView(R.id.emergency_toolbar) Toolbar toolbar;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.left_drawer) ListView mDrawerList;
    @BindView(R.id.full_name) TextView mFullName;
    @BindView(R.id.email) TextView mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_emergency);

        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Emergency");
            toolbar.setNavigationIcon(R.drawable.ic_drawer);
        }

        // Menu Items
        mDrawerMenu = new ArrayList<DrawerMenu>();
        mDrawerMenu.add(new DrawerMenu("Notifications", R.drawable.ic_notification));
        mDrawerMenu.add(new DrawerMenu("History", R.drawable.ic_history));
        mDrawerMenu.add(new DrawerMenu("Logout", R.drawable.ic_logout));

        // DrawerLayout
        mDrawerList.setAdapter(new DrawerMenuAdapter(getApplicationContext(), mDrawerMenu));
        mDrawerList.setOnItemClickListener(new MainEmergencyActivity.DrawerItemClickListener());

        initializeProfileInfo();
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

        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeProfileInfo() {
        mFullName.setText(BaseActivity.fullName);
        mEmail.setText(BaseActivity.email);

        Log.i("DRAWER", BaseActivity.fullName);
    }

    public void selectItem(int position) {
//        switch (position) {
//            case 0:
//                break;
//            case 1:
//                break;
//            case 2:
//                break;
//            default:
//
//        }

//        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }
}
