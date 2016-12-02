package com.example.alleoindong.cabtap.driver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.alleoindong.cabtap.BaseActivity;
import com.example.alleoindong.cabtap.R;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DriverWithBookingMapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    public LatLng mlatlng;
    public Marker mCurrentLocation;
    public Marker mPassengerLocation;
    public LocationRequest mLocationRequest;

    @BindView(R.id.btn_cancel_booking) Button mCancelBooking;
    @BindView(R.id.btn_start_booking) Button mStartBooking;
    @BindView(R.id.btn_complete_booking) Button mCompleteBooking;
    @BindView(R.id.lbl_pickup_location) TextView mPickup;
    @BindView(R.id.lbl_destination_location) TextView mDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_with_booking_map);
        setTitle("Picking up passenger");

        ButterKnife.bind(this);

        // initialize
        init();

        mPickup.setText(DriverMapActivity.mActiveBooking.passengerLocationName);
        mDestination.setText(DriverMapActivity.mActiveBooking.destinationName);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            this.logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, DriverMapActivity.MY_PERMISSION_ACCESS_FINE_LOCATION);

            return;
        }

        googleMap.setMyLocationEnabled(true);

        // Add Marker to Passenger Location
        LatLng passengerLocation = new LatLng(DriverMapActivity.mActiveBooking.passengerLocation.latitude,
                DriverMapActivity.mActiveBooking.passengerLocation.longitude);

        if (mPassengerLocation != null) {
            mPassengerLocation.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(passengerLocation);
        markerOptions.title("Passenger Location");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_passenger));
        mPassengerLocation = mMap.addMarker(markerOptions);
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
    }

    @OnClick(R.id.btn_cancel_booking) void cancelBookingClick() {
        updateBookingStatus("cancelled");
        finish();
    }

    @OnClick(R.id.btn_start_booking) void startBookingClick() {
        updateBookingStatus("started");

        mCancelBooking.setVisibility(View.GONE);
        mStartBooking.setVisibility(View.GONE);
        mCompleteBooking.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_complete_booking) void completeBookingClick() {
        updateBookingStatus("completed");

        finish();
    }

    private void init() {
        initMap();
        initGoogleApiClient();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    private void initGoogleApiClient() {
        // Build Google Api Client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void updateBookingStatus(String status) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("bookings");

        DriverMapActivity.mActiveBooking.setStatus(status);

        bookingRef.child(DriverMapActivity.mActiveBooking.passengerId)
                .child(DriverMapActivity.mActiveBooking.id)
                .setValue(DriverMapActivity.mActiveBooking);
    }
}
