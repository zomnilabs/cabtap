package com.example.alleoindong.cabtap.driver;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alleoindong.cabtap.BaseActivity;
import com.example.alleoindong.cabtap.DrawerMenuAdapter;
import com.example.alleoindong.cabtap.R;
import com.example.alleoindong.cabtap.models.Booking;
import com.example.alleoindong.cabtap.models.BookingRequest;
import com.example.alleoindong.cabtap.models.DrawerMenu;
import com.example.alleoindong.cabtap.models.UserProfile;
import com.example.alleoindong.cabtap.models.Vehicle;
import com.example.alleoindong.cabtap.user.PassengerMapActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
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

public class DriverMapActivity extends BaseActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, DialogInterface.OnDismissListener {

    private ArrayList<DrawerMenu> mDrawerMenu;

    @BindView(R.id.driver_toolbar) Toolbar toolbar;
    @BindView(R.id.btn_emergency) ImageView mEmergency;

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
    public LocationRequest mLocationRequest;

    private DatabaseReference mBookingNotificationsRef;
    private DatabaseReference mGeofireRef;
    private GeoFire geoFire;

    public static String assignedPlateNumber;
    public static Booking mActiveBooking;

    public BookingRequest mCurrentBookingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("NOTIFICATION", "Key: " + key + " Value: " + value);
            }
        }

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
        mDrawerMenu.add(new DrawerMenu("History", R.drawable.ic_history));
        mDrawerMenu.add(new DrawerMenu("Logout", R.drawable.ic_logout));

        // DrawerLayout
        mDrawerList.setAdapter(new DrawerMenuAdapter(getApplicationContext(), mDrawerMenu));
        mDrawerList.setOnItemClickListener(new DriverMapActivity.DrawerItemClickListener());

        mGeofireRef = FirebaseDatabase.getInstance().getReference("geofire");
        geoFire = new GeoFire(mGeofireRef);

        initializeProfileInfo();
        initListenForBookingRequest();
    }

    private void setGeoFireLocation(double lat, double lng) {
        geoFire.setLocation(DriverMapActivity.assignedPlateNumber, new GeoLocation(lat, lng));
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

        if (mCurrentLocation != null) {
            mCurrentLocation.remove();
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

            setGeoFireLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());

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
        setGeoFireLocation(location.getLatitude(), location.getLongitude());

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

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (DriverMapActivity.mActiveBooking != null) {
            Intent intent = new Intent(this, DriverWithBookingMapActivity.class);
            startActivity(intent);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void showBookingRequestDialog(BookingRequest bookingRequest) {
        FragmentManager fm = getSupportFragmentManager();

        final BookingRequestDialogFragment mBookingRequestDialogFragment = BookingRequestDialogFragment
                .newInstance("Passenger Requesting for taxi", bookingRequest.pickup,
                        bookingRequest.destination);

        mBookingRequestDialogFragment.show(fm, "fragment_booking_request");

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBookingRequestDialogFragment.isVisible()) {
                    mBookingRequestDialogFragment.dismiss();

                    rejectBooking();
                }
            }
        };

        handler.postDelayed(runnable, 10000);

    }

    private void initListenForBookingRequest() {
        mBookingNotificationsRef = FirebaseDatabase.getInstance()
                .getReference("booking-requests").child(BaseActivity.uid);

        ValueEventListener bookingRequests = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot request : dataSnapshot.getChildren()) {
                    BookingRequest bookingRequest = request.getValue(BookingRequest.class);

                    Log.i("REQUEST", bookingRequest.getStatus());
                    if (bookingRequest.getStatus().equals("pending")) {
                        Log.i("REQUEST", "new request with pending status");

                        mCurrentBookingRequest = bookingRequest;
                        BookingRequestDialogFragment.mBookingRequest = bookingRequest;

                        showBookingRequestDialog(bookingRequest);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mBookingNotificationsRef.addValueEventListener(bookingRequests);
    }

    private void rejectBooking() {
        mBookingNotificationsRef = FirebaseDatabase.getInstance()
                .getReference("booking-requests").child(BaseActivity.uid);

        mBookingNotificationsRef.child(mCurrentBookingRequest.id).setValue(null);
    }

    @OnClick(R.id.btn_emergency) void bookClick() {
        Intent intent = new Intent(this, MainEmergencyActivity.class);
        startActivity(intent);
    }

}
