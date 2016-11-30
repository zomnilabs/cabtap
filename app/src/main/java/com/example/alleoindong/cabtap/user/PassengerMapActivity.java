package com.example.alleoindong.cabtap.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.alleoindong.cabtap.BaseActivity;
import com.example.alleoindong.cabtap.DrawerMenuAdapter;
import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.admin.AdminActivity;
import com.example.alleoindong.cabtap.driver.DriverMapActivity;
import com.example.alleoindong.cabtap.models.DrawerMenu;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class PassengerMapActivity extends BaseActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

//    private String[] mDrawerMenu = {"Notifications", "Scheduled", "History", "Logout"};
    private ArrayList<DrawerMenu> mDrawerMenu;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.passenger_book_now) ImageView mBookNow;
    @BindView(R.id.passenger_help) ImageView mHelp;

    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.left_drawer) ListView mDrawerList;
    @BindView(R.id.full_name) TextView mFullName;
    @BindView(R.id.email) TextView mEmail;

    public static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    public GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    public LatLng mlatlng;
    public Marker mCurrentLocation;
    public ArrayList<Marker> mVehicles;
    public LocationRequest mLocationRequest;
    public TextView mLatitude;
    public TextView mLongitude;
    public Bitmap mMarkerIcon;

    public GeoQuery geoQuery;
    private DatabaseReference mGeofireRef;
    private GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_map);

        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Welcome " + BaseActivity.firstName.toUpperCase() + "!");
            toolbar.setNavigationIcon(R.drawable.ic_drawer);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Build Google Api Client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Menu Items
        mDrawerMenu = new ArrayList<DrawerMenu>();
//        mDrawerMenu.add(new DrawerMenu("Notifications", R.drawable.ic_notification));
//        mDrawerMenu.add(new DrawerMenu("Scheduled", R.drawable.ic_schedule));
        mDrawerMenu.add(new DrawerMenu("History", R.drawable.ic_history));
        mDrawerMenu.add(new DrawerMenu("Logout", R.drawable.ic_logout));

        // DrawerLayout
        mDrawerList.setAdapter(new DrawerMenuAdapter(getApplicationContext(), mDrawerMenu));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mVehicles = new ArrayList<Marker>();

        mGeofireRef = FirebaseDatabase.getInstance().getReference("geofire");
        geoFire = new GeoFire(mGeofireRef);

        initializeProfileInfo();
    }

    public void getNearbyVehicles(double lat, double lng) {
        geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lng), 1);
    }

    private void addVehicleMarker(String key, GeoLocation location) {
        LatLng latLng = new LatLng(location.latitude, location.longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cab));
        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(key);

        mVehicles.add(marker);
    }

    private void vehicleMoved(String key, GeoLocation location) {
        int index = findMarker(key);

        Marker marker = mVehicles.get(index);
        LatLng latLng = new LatLng(location.latitude, location.longitude);
        marker.setPosition(latLng);

        mVehicles.set(index, marker);
    }

    private int findMarker(String key) {
        int index = 0;

        for (Marker marker : mVehicles) {
            Log.i("FIND_MARKER", marker.getTag() + " : " + key);

            if (marker.getTag() == key) {
                return mVehicles.indexOf(marker);
            }
        }

        return index;
    }

    private void geoQueryEventListener() {
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.i("VEHICLES", String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
                addVehicleMarker(key, location);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                vehicleMoved(key, location);
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void initializeProfileInfo() {
        mFullName.setText(BaseActivity.fullName);
        mEmail.setText(BaseActivity.email);

        Log.i("DRAWER", BaseActivity.fullName);
    }

    public void selectItem(int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(PassengerMapActivity.this, BookingHistoryActivity.class);
                startActivity(intent);
                break;
            case 1:
                break;
            default:

        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();

        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        mMap = googleMap;

        // Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);

            return;
        }

        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {

                        mMap.setMyLocationEnabled(true);
                    }

                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            mlatlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mlatlng);
            markerOptions.title("My Current Position");
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
            mCurrentLocation = mMap.addMarker(markerOptions);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(mlatlng).zoom(16).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            getNearbyVehicles(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            geoQueryEventListener();
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrentLocation != null) {
            mCurrentLocation.remove();
        }

        mlatlng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mlatlng);
        markerOptions.title("My Current Position");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker));
        mCurrentLocation = mMap.addMarker(markerOptions);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(mlatlng).zoom(16).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        getNearbyVehicles(location.getLatitude(), location.getLongitude());
    }

    @OnClick(R.id.passenger_book_now) void bookClick() {
        Intent intent = new Intent(this, RideBookingActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.passenger_help) void helpClick() {
        Intent intent = new Intent(this, PassengerHelpActivity.class);
        startActivity(intent);
    }


}
